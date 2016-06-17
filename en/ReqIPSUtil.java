package com.frankpi.video99.util.a.en;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class ReqIPSUtil {

	private static final String VIDEO = "video";
	protected static final String TAG = "video2ips";
	private  Context context;

	public ReqIPSUtil(Context context) {
		super();
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	private static List<String> baseIPS = new ArrayList<>();

	private static void initIPS() {
		baseIPS.add(new String("http://52.79.155.233:80/api/gs/video"));
		baseIPS.add(new String("http://52.79.128.27:80/api/gs/video"));
		baseIPS.add(new String("http://52.9.43.92:80/api/gs/video"));
		baseIPS.add(new String("http://52.79.155.254:80/api/gs/video"));
		baseIPS.add(new String("http://52.77.140.1:80/api/gs/video"));
		baseIPS.add(new String("http://52.77.56.172:80/api/gs/video"));
	};

	private NetworkRequest networkRequest;
	private String content;
	private static String POST_FILENAME = "file";
	public static final String RESULT = "result";
	public static final String CODE = "code";
	public static final String IPS = "ips";
	public static final String GS = "gs";


	public static void changeBaseUrl(String sp) {
		try {
			baseIPS.clear();

			JSONObject jsonObject = new JSONObject(sp);
			JSONArray jsonArray = jsonObject.getJSONArray(GS);
			for (int x = 0; x < jsonArray.length(); x++) {
				String url = (String) jsonArray.get(x);
				if (!TextUtils.isEmpty(url)) {
					baseIPS.add(url);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			initIPS();
		}
	}

	public JSONObject reqVideosIPS() {
		String sp = getBaseUrl(context);
		if (!TextUtils.equals(ONKNOWN, sp)) {
			changeBaseUrl(sp);
		}
		JSONObject result = null;
		content = getContent(context, null);
		for (String aurl : baseIPS) {
			try {
				networkRequest = new NetworkRequest(context, new URL(aurl));
				networkRequest.addUploadPartWithDataZipAnd(POST_FILENAME,
						POST_FILENAME, content.getBytes());
				byte[] res = networkRequest.doRequestWithUnzipResult();
				result = new JSONObject(new String(res));
				Log.i(TAG, result.toString());
				if (result.getJSONObject(RESULT).optInt(CODE) == 0) {
					saveIPSBaseURL(context, result.getJSONObject(IPS)
							.toString());
					saveVideosURL(result.getJSONObject(IPS).optJSONArray(VIDEO));
					break;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void saveIPSBaseURL(Context context, String url) {
		if (!TextUtils.isEmpty(url))
			context.getSharedPreferences("IPSBaseUrl", 0).edit()
					.putString("IPSBaseUrl", url).commit();
	}

	public static final String ONKNOWN = "onknown";

	public static String getBaseUrl(Context context) {
		return context.getSharedPreferences("IPSBaseUrl", 0).getString(
				"IPSBaseUrl", ONKNOWN);
	}

	// 请求视频信息
	public static List<String> videoUrlList = new ArrayList<String>();

	public static void saveVideosURL(JSONArray jsonArray) {
		for (int x = 0; x < jsonArray.length(); x++) {
			try {
				videoUrlList.add((String) jsonArray.get(x));
			} catch (Exception e) {

			}
		}
	}

	public  String reqVideos( String data) {
		String content = getContent(context, data);
		String result = null;
		for (String base : videoUrlList) {
			try {
				NetworkRequest request = new NetworkRequest(context, new URL(
						base));
				request.addUploadPartWithDataZipAnd(POST_FILENAME,
						POST_FILENAME, content.getBytes());
				JSONObject jsonObject = new JSONObject(new String(
						request.doRequestWithDecryptAndUnzipResult()));

				if (!TextUtils.isEmpty(jsonObject.getString("msg"))) {
					result = jsonObject.toString();
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	private static String getContent(Context context, String data) {
		JSONObject jsonObject = new JSONObject();

		try {
			if (!TextUtils.isEmpty(data)) {
				jsonObject = new JSONObject(data);
			}
			jsonObject.put("device", DeviceInfo.getDeviceInfo(context));
			jsonObject.put("caller", DeviceInfo.getCallerInfo(context));
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(jsonObject.toString());

		return stringBuilder.toString();
	}
}
