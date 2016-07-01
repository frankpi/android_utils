package com.ljp.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.os.Environment;
import android.widget.EditText;

public class Log2 {

	private static StringBuilder log = new StringBuilder();

	public static void displayLog(final EditText tvLog, final String logtext) {
		log.append(logtext).append('\n');
		tvLog.post(new Runnable() {

			@Override
			public void run() {
				tvLog.setText(log.toString());
				tvLog.setSelection(tvLog.length());// 调整光标到最后一行
			}
		});
	}

	public static void displaystat(final EditText textview1,
			final String logtext) {
		textview1.post(new Runnable() {

			@Override
			public void run() {
				textview1.setText(logtext);
			}
		});
	}

	public static void clearLog(final EditText textview) {
		textview.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				log.append("Log:\n");
				log.delete(0, log.length());
				textview.setText("Log:\n");
			}
		});
	}

	public static String getCurrentTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");// 可以方便地修改日期格式
		return dateFormat.format(System.currentTimeMillis());
	}

	public static boolean saveLog(String name) {
		// sendMailMessage("采集日志");
		name = name + getCurrentTime();
		try {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				String path = "/sdcard/leLog/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + name);
				fos.write(log.toString().getBytes());
				fos.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static String getSystemLog() {
		System.out.println("--------func start--------"); // 方法启动
		String str = null;
		try {
			ArrayList<String> cmdLine = new ArrayList<String>(); // 设置命令 logcat
																	// -d 读取日志
			cmdLine.add("logcat");
			cmdLine.add("-d");

			ArrayList<String> clearLog = new ArrayList<String>(); // 设置命令 logcat
																	// -c 清除日志
			clearLog.add("logcat");
			clearLog.add("-c");

			Process process = Runtime.getRuntime().exec(
					cmdLine.toArray(new String[cmdLine.size()])); // 捕获日志
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream())); // 将捕获内容转换为BufferedReader

			// Runtime.runFinalizersOnExit(true);

			while ((str = bufferedReader.readLine()) != null) // 开始读取日志，每次读取一行
			{
				Runtime.getRuntime().exec(
						clearLog.toArray(new String[clearLog.size()])); // 清理日志....这里至关重要，不清理的话，任何操作都将产生新的日志，代码进入死循环，直到bufferreader满
				System.out.println(str); // 输出，在logcat中查看效果，也可以是其他操作，比如发送给服务器..
			}
			if (str == null) {
				System.out.println("--   is null   --");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("--------func end--------");
		return str;
	}

}
