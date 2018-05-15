package com.gameassist.plugin.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Process;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;

public class CommonUtil {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    public static String currentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(System.currentTimeMillis());



    }


    public void isForgound(){
        new Thread(new Runnable() {
            public boolean start;
            public boolean end;
            public String isFront;

            @Override
            public void run() {
                while (true) {

                    try {
                        CommonUtil.getCommandLineOutput(String.format("cat /proc/%1$s/cmdline", Process.myPid()));
                        isFront = CommonUtil.getCommandLineOutput("cat /proc/self/oom_adj").trim();
                        if (!TextUtils.isEmpty(isFront)) {
                            if (end && Integer.parseInt(isFront) <= 0) {
//                                coreLogic.putGameTime(false);
                                start = true;
                                end = false;
                            }
                            if (start && Integer.parseInt(isFront) >= 1) {
//                                coreLogic.putGameTime(true);
                                end = true;
                                start = false;
                            }
                        }
                        Thread.currentThread().sleep(1000 * 60);
//                        Log.i(TAG, isFront);
                    } catch (InterruptedException e) {
//                        Log.i(TAG, "eee");
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static String getCommandLineOutput(String cmdLine) {
        String output = "";
        try {
            java.lang.Process p = Runtime.getRuntime().exec(cmdLine);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                output += (line + '\n');
            }
            input.close();
        } catch (Exception e) {
        }
        return output;
    }

    public static int getGGVersionCode(Context context){
        int  ggvercode=0;
        List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(0);
        for (PackageInfo pInfo : packageInfos) {
            if (TextUtils.equals("com.iplay.assistant", pInfo.packageName)) {
                ggvercode = pInfo.versionCode;
            }
        }
        return  ggvercode;
    }

}
