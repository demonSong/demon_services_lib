package com.demon.services.customviews;

import com.google.android.gms.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class CustomviewFactory {
	
	public static final int POPUP_VIEW= 0;
	public static final int POPUP_TEXT =1;
	
	//私有化 构造器
	private CustomviewFactory(){};
	
	@SuppressLint("InflateParams") 
	public static final View getCustomview(Context context,int type){
		View view =null;
		LayoutInflater inflater =LayoutInflater.from(context);
		
		switch(type){
		case POPUP_VIEW:
			view =inflater.inflate(R.layout.host_view, null);
			break;
			
		case POPUP_TEXT:
			view =new PopupText(context);
			break;
		}
		return view;
		
	}

}
