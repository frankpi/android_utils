package com.ljp.utils.log;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
	private static final String TAG = "gameassist";

	// 监听时间变化的 这个receiver只能动态创建
	private TimeTickReceiver mTickReceiver;
	private IntentFilter mFilter;

	class TimeTickReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// 判断它是否是为电量变化的Broadcast Action
			if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
				Log.i(TAG, "触发");
			}
		}
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "ExampleService-onCreate");

		mFilter = new IntentFilter();
		mFilter.addAction(Intent.ACTION_TIME_TICK); // 每分钟变化的action
		mFilter.addAction(Intent.ACTION_TIME_CHANGED); // 设置了系统时间的action
		mTickReceiver = new TimeTickReceiver();
		registerReceiver(mTickReceiver, mFilter);
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "ExampleService-onStart");
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 执行文件的下载或者播放等操作
		Log.i(TAG, "ExampleService-onStartCommand");
		/*
		 * 这里返回状态有三个值，分别是:
		 * 1、START_STICKY：当服务进程在运行时被杀死，系统将会把它置为started状态，但是不保存其传递的Intent对象
		 * ，之后，系统会尝试重新创建服务;
		 * 2、START_NOT_STICKY：当服务进程在运行时被杀死，并且没有新的Intent对象传递过来的话，
		 * 系统将会把它置为started状态， 但是系统不会重新创建服务，直到startService(Intent intent)方法再次被调用;
		 * 3、START_REDELIVER_INTENT：当服务进程在运行时被杀死，它将会在隔一段时间后自动创建，
		 * 并且最后一个传递的Intent对象将会再次传递过来。
		 */
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "ExampleService-onBind");
		return null;
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "ExampleService-onDestroy");
		unregisterReceiver(mTickReceiver);
		super.onDestroy();
	}

}