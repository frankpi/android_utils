package com.frankpi.video99.util.a.en;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class ReqPUTUtil {

	private static final String MSG = "msg";
	private static final String RC_CODE = "rc_code";
	private static final String POST_FILENAME = "file";
	private static final String TAG = "video2put";
	private Context context;
	private NetworkRequest networkRequest;

	public ReqPUTUtil(Context context) {
		super();
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	private static List<String> videoPUTURLs = new ArrayList<>();

	private static void initIPS() {
			videoPUTURLs.add(new String("http://52.77.56.172:80/api/video/put"));
        	videoPUTURLs.add(new String("http://52.79.128.27:80/api/video/put"));
        	videoPUTURLs.add(new String("http://52.79.155.233:80/api/video/put"));
        	videoPUTURLs.add(new String("http://52.79.155.254:80/api/video/put"));
        	videoPUTURLs.add(new String("http://52.77.140.1:80/api/video/put"));
        	videoPUTURLs.add(new String("http://52.9.43.92:80/api/video/put"));
        	

	};

	public String reqPUTVideos(String data) {
		initIPS();
		JSONObject result = null;
		String ret = "error";
		String content = getContent(context, data);
		for (String aurl : videoPUTURLs) {
			try {
				networkRequest = new NetworkRequest(context, new URL(aurl));
				networkRequest.addUploadPartWithDataZipAnd(POST_FILENAME,
						POST_FILENAME, content.getBytes());
				byte[] res = networkRequest.doRequestWithUnzipResult();
				result = new JSONObject(new String(res));
				Log.i(TAG, result.toString());
				if (result.optInt(RC_CODE) == 0) {
					ret = result.optString(MSG);
					break;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;
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
