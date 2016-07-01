package com.ljp.utils.log;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LogObserverActivity extends Activity {
	private String TAG = "LogObserverActivity";
	public static String LOG_ACTION = "com.isoft.log.LOG_ACTION";
	private TextView logContent = null;
	private Button start = null;
	private Intent logObserverIntent = null;
	private LogBroadcastReceiver mLogBroadcastReceiver = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new LinearLayout(this));
		//初始化视图
		initView();
		//注册log广播接收者
		registerLogBroadcastReceiver();
	}

	private void initView() {
		logContent = new TextView(this);
		logContent.setText("show log");
		start = new Button(this);
		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startLogObserverService();
				start.setEnabled(false);
			}
		});
	}

	private void startLogObserverService() {
		logObserverIntent = new Intent(this, LogObserverService.class);
		startService(logObserverIntent);
	}

	/**
	 * 注册log广播接收者
	 */
	private void registerLogBroadcastReceiver(){
		mLogBroadcastReceiver = new LogBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(LOG_ACTION);
		registerReceiver(mLogBroadcastReceiver, filter);
	}
	
	/**
	 * log 广播接收者
	 * @author zhangyg
	 *
	 */
	private class LogBroadcastReceiver extends BroadcastReceiver{
		private String action = null;
		private Bundle mBundle = null;
		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			if(LOG_ACTION.equals(action)){
				mBundle = intent.getExtras();
				logContent.setText(mBundle.getString("log"));
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(logObserverIntent);
		unregisterReceiver(mLogBroadcastReceiver);
	}
}