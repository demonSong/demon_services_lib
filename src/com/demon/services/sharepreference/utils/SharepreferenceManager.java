package com.demon.services.sharepreference.utils;

import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/**
 * @author DELL
 *
 */
@SuppressLint("CommitPrefEdits")
public class SharepreferenceManager {
	
	//debug
	public static final String TAG="SharepreferenceManager";
	public static final boolean D=true;
	
	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;
	
	private String autoIndex;
	
	public static final String LENGTH ="length";
	
	/**
	 * @param activity
	 * @param sharePreferenceName
	 */
	public SharepreferenceManager(Activity activity,String sharePreferenceName){
		
		this(activity,sharePreferenceName,Context.MODE_WORLD_READABLE);
	}
	
	public SharepreferenceManager(Activity activity,String sharePreferenceName,int mode){
		mPreferences =activity.getSharedPreferences(sharePreferenceName, mode);
		mEditor = mPreferences.edit();
	}
	
	public void clear(){
		autoIndex="#";
		mEditor.clear();
		mEditor.commit();
	}
	
	public void submit(){
		mEditor.putInt(LENGTH, autoIndex.toCharArray().length);
		mEditor.commit();
	}
	
	public void commit(StringSerializable serializableData){
		
		mEditor.putString(autoIndex, serializableData.toStringSerializable());
		autoIndex +="#";
		mEditor.commit();
	}
	
	
	@SuppressWarnings("rawtypes")
	private class SharepreferenceIterator implements Iterator{
		int returnCount=0;
		final int length=mPreferences.getInt("length", 0);
		String command[] =new String[length];
		String autoIndex ="#";
		{
			for(int i=0;i<length-1;i++){
				if(D)
					Log.d(TAG, mPreferences.getString(autoIndex,"e"));
				command[i]=mPreferences.getString(autoIndex, "e");
				autoIndex+="#";
			}
		}
		@Override
		public boolean hasNext() {
			return ( returnCount == length-1 )? false : true;
		}

		@Override
		public Object next() {
			if(D)
				Log.d(TAG, returnCount +"");
			return command[returnCount++];
		}

		@Override
		public void remove() {
			
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Iterator getIterator(){
		return new SharepreferenceIterator();
	}
	
}
