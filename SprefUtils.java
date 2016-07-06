package com.iplay.coresdk.util;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

@SuppressLint("NewApi") public class SprefUtils {
	
	private static SharedPreferences sp;
	
	
	 public static boolean getBool(String key, boolean defValue) {
	        return SprefUtils.sp.getBoolean(key, defValue);
	    }

	    public static long getLong(String key, long defValue) {
	        return SprefUtils.sp.getLong(key, defValue);
	    }

	    public static String getString(String key, String defValue) {
	        return SprefUtils.sp.getString(key, defValue);
	    }
	    
	    public static int getInt(String key, int defValue) {
	        return SprefUtils.sp.getInt(key, defValue);
	    }

	    public static void init(Context context,String spname) {
	        if(SprefUtils.sp == null) {
	        	SprefUtils.sp = context.getSharedPreferences(spname, 0);
	        }
	    }

	    @SuppressLint("NewApi") public static void putBool(String key, boolean value) {
	    	SprefUtils.sp.edit().putBoolean(key, value).apply();
	    }

	    @SuppressLint("NewApi") public static void putLong(String key, long value) {
	    	SprefUtils.sp.edit().putLong(key, value).apply();
	    }

	    @SuppressLint("NewApi") public static void putString(String key, String value) {
	    	SprefUtils.sp.edit().putString(key, value).apply();
	    }
	    
	    public static void putInt(String key, int value) {
	    	SprefUtils.sp.edit().putInt(key, value).apply();
	    }

}