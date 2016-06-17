package com.frankpi.video99.util.a.en;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 网络请求中的设备信息
 * Created by serious on 2/22/16.
 */
public class DeviceInfo {
    private static JSONObject _deviceInfo = null;
    public static JSONObject getDeviceInfo(Context context){
        if(_deviceInfo == null){
            try {
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                DisplayMetrics dm = new DisplayMetrics();
                display.getMetrics(dm);

                _deviceInfo = new JSONObject();
                _deviceInfo.put("product", Build.PRODUCT);
                _deviceInfo.put("vendor", Build.MANUFACTURER);
                _deviceInfo.put("density", dm.densityDpi);
                _deviceInfo.put("gpuVendor", PreferenceManager.getDefaultSharedPreferences(context).getString("gpuVendor", ""));
                _deviceInfo.put("mac", getWifiMacAddress(context));
                _deviceInfo.put("imei", getGlobalDeviceId(context));
                _deviceInfo.put("model", Build.MODEL);
                _deviceInfo.put("sdk", Build.VERSION.SDK_INT);
                _deviceInfo.put("androidId", getAndroidId(context));
                if(Build.FINGERPRINT.split("/").length < 2){
                    _deviceInfo.put("fingerprint", Build.BRAND + "/" + Build.PRODUCT + "/" + Build.DEVICE + ":" + Build.VERSION.RELEASE + "/" + Build.ID + "/" + Build.VERSION.INCREMENTAL + ":" + Build.TYPE + "/" + Build.TAGS);
                }else{
                    _deviceInfo.put("fingerprint", Build.FINGERPRINT);
                }
                _deviceInfo.put("widthPixels", dm.widthPixels);
                _deviceInfo.put("heightPixels", dm.heightPixels);
            } catch (Exception e) {
                _deviceInfo = null;
            }
        }
        return _deviceInfo;
    }

    private static JSONObject _callerInfo = null;
    public static JSONObject getCallerInfo(Context context){
        if(_callerInfo == null){
            try {
                _callerInfo = new JSONObject();
                _callerInfo.put("vercode", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
                _callerInfo.put("pkgName",context.getPackageName());
                _callerInfo.put("sigMd5", getSignatureMd5(context));
                _callerInfo.put("apkMd5", getApkMd5(context));
                //TODO
                _callerInfo.put("channel", "sdfadf");
            } catch (Exception e) {
                _callerInfo = null;
            }
        }
        return _callerInfo;
    }

    private static String getWifiMacAddress(Context context){
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo != null){
                return wifiInfo.getMacAddress();
            }

            BluetoothAdapter bAdapt= BluetoothAdapter.getDefaultAdapter();
            if(bAdapt != null){
                return bAdapt.getAddress();
            }
        } catch (Exception ignore) {
        }

        return "";
    }

    public static String getAndroidId(Context context) {
        String androidId = "0";
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidId;
    }

    private static void putValueToMap(Map<String,String> map,String key,String val){
        key=key.replace("[","").replace("]","").trim();
        val=val.replace("[","").replace("]","").trim();
        Iterator iterator=map.keySet().iterator();
        if (iterator!=null&&iterator.hasNext()){
            while (iterator.hasNext()){
                String lKey= (String) iterator.next();
                if (lKey.equals(key)){
                    map.put(lKey,val);
                    break;
                }
            }
        }
    }

    private static String getStringFromInputStream(InputStream is, Map formatter) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        boolean isMapInitPut=(formatter==null||formatter.size()<1)?false:true;
        try {
            while ((line = br.readLine()) != null) {
                if (formatter != null && line.contains(":")) {
                    String[] values = line.split(":");
                    //解析getprop,/proc/meminfo, 按需索取数据，所以map先预定义
                    if (isMapInitPut) {
                        putValueToMap(formatter,values[0],values[1]);
                    }
                    //解析CPUInfo
                    else {
                        //区分cpuinfo中的 processor(cpu个数)->{0 or 1 or 2 or 3} 和 Processor(cpu名称)->{"ARMv7 Processor rev3(v7I)"}
                        if (values[0].trim().equals("processor")) {
                            formatter.put("processorcnt", values[1].trim());
                        } else {
                            formatter.put(values[0].trim(), values[1].trim());
                        }
                    }
                }
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }

        }

        return sb.toString();
    }

    @SuppressWarnings("deprecation")
    public static String getGlobalDeviceId(final Context appContext) {
        String number = null;
        try {
            TelephonyManager tm = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
            number = tm.getDeviceId();
        } catch (Exception e) {
        }

        if (number == null || number.length() == 0) {
            try {
                number = Settings.System.getString(appContext.getContentResolver(), Settings.System.ANDROID_ID);
            } catch (Exception e) {
            }
        }

        if ((number == null || number.length() == 0) && Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            try {
                Class<Build> cls = Build.class;
                Field field = cls.getDeclaredField("SERIAL");
                field.setAccessible(true);
                number = (String) field.get(null);
            } catch (Exception e) {
            }
        }

        if (number == null | number.length() == 0)
            return "UNKNOWN";
        else
            return number;
    }

    private static String getSignatureMd5(Context context) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(getSignature(context).getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private static String getSignature(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        Iterator<PackageInfo> iter = apps.iterator();
        while (iter.hasNext()) {
            PackageInfo packageinfo = iter.next();
            String packageName = packageinfo.packageName;
            if (packageName.equals(context.getPackageName())) {
                return packageinfo.signatures[0].toCharsString();
            }
        }
        return null;
    }

    private static String getApkMd5(Context context) {
        PackageManager packageManager = context.getPackageManager();

        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);

            return getFileMd5(applicationInfo.sourceDir);
        } catch (Exception e) {
            return "";
        }
    }

    private static String getFileMd5(String path){
        try {
            File file = new File(path);
            MessageDigest digest = MessageDigest.getInstance("md5");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] result = digest.digest();
            StringBuffer sb  = new StringBuffer();
            for (byte b : result) {
                int number = b & 0xff;
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
