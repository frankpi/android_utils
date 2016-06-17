package com.frankpi.video99.util;

import java.io.File;
import java.io.FileOutputStream;

import android.os.Environment;

public class SaveJson2sdcardUtils {

	public static boolean saveJson2sdcard(String json, String name) {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + name);
		FileOutputStream fop;
		try {
			fop = new FileOutputStream(file);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			// get the content in bytes
			byte[] contentInBytes = json.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
