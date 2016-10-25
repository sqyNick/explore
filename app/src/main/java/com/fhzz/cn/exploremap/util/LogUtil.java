package com.fhzz.cn.exploremap.util;

import android.util.Log;

public class LogUtil {

	private static final String TAG = "ExploreMap";
	private static boolean allowLog=true;
	
	/**
	 * 控制Log输出
	 * @param isAllow
	 */
	public static void setLogAllow(boolean isAllow){
		allowLog=isAllow;
	}
	
	public static void i(String tag,String msg){
		if (allowLog) {
			Log.i(tag, msg);
		}
	}
	public static void i(String msg){
		if (allowLog) {
			Log.i(TAG, msg);
		}
	}
	public static void e(String tag,String msg){
		if (allowLog) {
			Log.e(tag, msg);
		}
	}
	public static void e(String msg){
		if (allowLog) {
			Log.e(TAG, msg);
		}
	}
	public static void v(String tag,String msg){
		if (allowLog) {
			Log.v(tag, msg);
		}
	}
	public static void v(String msg){
		if (allowLog) {
			Log.v(TAG, msg);
		}
	}
	public static void d(String tag,String msg){
		if (allowLog) {
			Log.d(tag, msg);
		}
	}
	public static void d(String msg){
		if (allowLog) {
			Log.d(TAG, msg);
		}
	}
	public static void w(String tag,String msg){
		if (allowLog) {
			Log.w(tag, msg);
		}
	}
	public static void w(String msg){
		if (allowLog) {
			Log.w(TAG, msg);
		}
	}
	
	
}
