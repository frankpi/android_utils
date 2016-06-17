package com.frankpi.video99.util.a.en;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by serious on 2015-10-10.
 */
public class NetworkRequest {

	private static final String end = "\r\n";
	private static final String twoHyphens = "--";
	private static final String boundary = "******";
	private static final int BLOCK_SIZE = 4096;
	public static final String UPLOAD_FILE_NAME="file";

	private class UploadPart {
		String name;
		String fileName;
		byte[] content;
		String fullFilePath;

		
		public UploadPart(String name, String fileName, byte[] content) {
			this.name = name;
			this.fileName = fileName;
			this.content = content;
			this.fullFilePath = null;
		}

		public UploadPart(String name, String fileName, String fullFilePath) {
			this.name = name;
			this.fileName = fileName;
			this.fullFilePath = fullFilePath;
			this.content = null;
		}
	}

	protected Context context;
	protected URL url;
	protected ArrayList<UploadPart> partList;

	protected boolean canceled;
	protected int timeout = 60 * 1000;

	public NetworkRequest(Context context, URL url) {
		this.context = context;
		this.url = url;
		this.partList = new ArrayList<UploadPart>();
		this.canceled = false;
	}

	
	public NetworkRequest addUploadPart(String name, String fileName, byte[] content) {
		partList.add(new UploadPart(name, fileName, content));
		return this;
	}

	public NetworkRequest addUploadPartWithDataZipAndEncrypt(String name, String fileName, byte[] content) throws Exception{
		partList.add(new UploadPart(name, fileName, Util.zipAndEncrypt(content)));
		return this;
	}
	
	public NetworkRequest addUploadPartWithDataZipAnd(String name, String fileName, byte[] content) throws Exception{
		partList.add(new UploadPart(name, fileName, Zip.zipBytes(content)));
		return this;
	}
	
	public NetworkRequest addUploadPart(String name, String fileName, String fullFilePath) {
		partList.add(new UploadPart(name, fileName, fullFilePath));
		return this;
	}

	public NetworkRequest setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public byte[] doRequestWithDecryptAndUnzipResult() throws Exception {
		return Util.decryptAndUnzip(doRequest(true));
	}
	
	public byte[] doRequestWithUnzipResult() throws Exception {
		return Zip.unzipBytes(doRequest(true));
	}
	
	public byte[] doRequest() throws ConnectionException {
		return doRequest(false);
	}
	
	public byte[] doRequest(boolean compress) throws ConnectionException {
		DataOutputStream dos = null;
		InputStream is = null;
		byte[] result = new byte[0];
		try {
			HttpURLConnection connection = null;
			if (isWapNetwork(context)) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
				connection = (HttpURLConnection) url.openConnection(proxy);
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}
			final HttpURLConnection httpURLConnection = connection;
//			if (TextUtils.equals("https", url.getProtocol())) {
//				((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(CAKeyStoreSingleton.getInstance(context).getSSLSocketFactory());
//			}
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("X-Compress", compress?"true":"false");
			httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			httpURLConnection.setRequestProperty("User-Agent", getUserAgent());
			httpURLConnection.setConnectTimeout(timeout);// 设置超时
			httpURLConnection.setReadTimeout(timeout);// 设置读取数据超时

			final CountDownLatch cdl = new CountDownLatch(1);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						//DNS LOOKUP 无超时，会卡住很久很久……
						httpURLConnection.connect();
					} catch (Exception e) {
						e.printStackTrace();
					}
					cdl.countDown();
				}
			}).start();

			try {
				if( !cdl.await(30, TimeUnit.SECONDS) ){
					throw new ConnectionException("timeout");
				}
			} catch (Exception e) {
				return null;
			}
			
			dos = new DataOutputStream(httpURLConnection.getOutputStream());
			for (UploadPart part : partList) {
				dos.writeBytes(twoHyphens + boundary + end);
				dos.writeBytes("Content-Disposition: form-data; name=\"" + part.name + "\"; filename=\"" + part.fileName + "\"" + end);
				dos.writeBytes("Content-Type:application/octet-stream;" + end + end);

				InputStream in = null;
				long contentLength = 0;
				if (part.content != null) {
					in = new ByteArrayInputStream(part.content);
					contentLength = part.content.length;
				} else if (!TextUtils.isEmpty(part.fullFilePath) && new File(part.fullFilePath).exists()) {
					in = new FileInputStream(part.fullFilePath);
					contentLength = new File(part.fullFilePath).length();
				}
				if (contentLength > 0) {
					byte[] buffer = new byte[BLOCK_SIZE];
					int size;
					while ((size = in.read(buffer)) >= 0) {
						dos.write(buffer, 0, size);
					}
				}
//				Log.e("dump", "post part: "+part.name + " = " + contentLength);
				dos.writeBytes(end);

			}
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
			// /////////////////
			dos.flush();
			// 上传文件成功之后返回
//			MyLog.d(" <NetworkRequest.doRequest> result " + httpURLConnection.getResponseCode());
			Log.e("gameassist"," <NetworkRequest.doRequest> result " + httpURLConnection.getResponseCode());
			is = httpURLConnection.getInputStream();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] buffer = new byte[BLOCK_SIZE];
			int size;
			while ((size = is.read(buffer)) >= 0) {
				stream.write(buffer, 0, size);
			}
			is.close();
			result = stream.toByteArray();
//			MyLog.e(" <NetworkRequest.doRequest> " + compress + " result="+ result.length);
//			Log.e("gameassist"," <NetworkRequest.doRequest> " + compress + " result="+ result.length);
			
		} catch (ProtocolException e) {
			throw new ConnectionException(e);
		} catch (FileNotFoundException e) {
			throw new ConnectionException(e);
		} catch (IOException e) {
			throw new ConnectionException(e);
		}finally {
			try {
				if (dos != null)
					dos.close();
				if (is != null)
					is.close();
			} catch (Exception ignore) {
			}
		}
		return result;
	}
	
	public static String getUserAgent() {
		String version = Build.VERSION.RELEASE;
		String model = Build.MODEL;
		String display = Build.DISPLAY;
		String userAgent = String.format("Mozilla/5.0 (Linux; Android %1$s; %2$s Build/%3$s) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19", version, model, display);
		return userAgent;
	}
	
	public static boolean isWapNetwork(Context context) {
		try {
			ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connMgr.getActiveNetworkInfo();
			if (info.getType() == ConnectivityManager.TYPE_WIFI || info.getType() == 9) {
				return false;
			}
			String currentAPN = info.getExtraInfo();
			if (currentAPN == null)
				return false;
			return currentAPN.equalsIgnoreCase("cmwap") || currentAPN.equalsIgnoreCase("ctwap") || currentAPN.equalsIgnoreCase("3gwap") || currentAPN.equalsIgnoreCase("uniwap");
		} catch (Exception e) {
			return false;
		}
	}
	
	public final class ConnectionException extends Exception {
	    public ConnectionException(final String detailMessage) {
	        super(detailMessage);
	    }
	    public ConnectionException(final Throwable throwable) {
	        super(throwable);
	    }
	}
}
