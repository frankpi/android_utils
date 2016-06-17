package com.frankpi.video99.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.util.Log;

public class HttpReqUtils {

	private static final String TAG = "ljp";

	public static JSONObject iqiyiTargetUrl(String url) {
		String reString = getResponse(url);
		String pt1 = "jsonp.*\\((.*?)\\);";
		Pattern p = Pattern.compile(pt1);
		Matcher m = p.matcher(reString);
		String targetjson = "";
		if (m.find()) {
			targetjson = m.group(1);
		}
		JSONObject json = JSON.parseObject(targetjson);
		Log.i(TAG, json.toString());
		return json;
	}

	public static String getResponse(String url) {

		HashMap<String, String> head = new HashMap<String, String>();
		head.put("Connection", "keep-alive");
		head.put("Cache-Control", "max-age=0");
		head.put("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		head.put(
				"User-Agent",
				"Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/KRT16M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.105 Mobile Safari/537.36");
		head.put("Accept-Encoding", "zh-CN,zh;q=0.8");
		head.put("Accept-Encoding", "zh-CN,zh;q=0.8");
		String res = httpsGet(url, head, true);
		Log.i(TAG, res);
		return res;
	}

	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			// TODO Auto-generated method stub
			return true;
		}
	};

	public static String httpsGet(String httpUrl, Map<String, String> heads, boolean isPrint) {
		BufferedReader input = null;
		StringBuilder sb = null;
		URL url = null;
		HttpURLConnection con = null;
		try {
			url = new URL(httpUrl);
			try {
				if (url.getProtocol().toLowerCase().equals("https")) {
					HttpsURLConnection https = (HttpsURLConnection) url
							.openConnection();
					https.setHostnameVerifier(DO_NOT_VERIFY);
					con = https;
				} else {
					con = (HttpURLConnection) url.openConnection();
				}
				Iterator<Map.Entry<String, String>> it = heads.entrySet()
						.iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> entry = it.next();
					String key = entry.getKey();
					String val = entry.getValue();
					con.setRequestProperty(key, val);
				}
				input = new BufferedReader(new InputStreamReader(
						con.getInputStream(), "utf8"));
				sb = new StringBuilder();
				String s;
				while ((s = input.readLine()) != null) {
					sb.append(s);
				}
				input.close();
				con.disconnect();
				return sb.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} finally {
			// close buffered
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// disconnecting releases the resources held by a connection so they
			// may be closed or reused
			if (con != null) {
				con.disconnect();
			}
		}
		return sb == null ? "" : sb.toString();
	}
}
