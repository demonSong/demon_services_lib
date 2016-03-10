package com.demon.services;

import android.util.Log;

public class LogCatTool {

	public enum Type {
		DEBUG, ERRO, INFORMATION
	}
	
	private static final String TAG ="demon_debug";

	public static void show(String TAG, String msg, boolean d, Type type) {
		if (d) {
			switch (type) {
			case DEBUG:
				Log.d(TAG, msg);
				break;
			case ERRO:
				Log.e(TAG, msg);
				break;
			case INFORMATION:
				Log.i(TAG, msg);
				break;
			}
		}
	}
	
	public static void showDefault(String msg,boolean d){
		if(d){
			Log.d(TAG,msg);
		}
	}
	

}
