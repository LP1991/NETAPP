package com.cloudvision.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 程序级的SP
 * @author zhangyun
 *
 */
public class AppSP {

	private SharedPreferences prefs;

	public AppSP(Context context)
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(context);	
	}
	
	/**
	 * 是否是第一次启动App
	 */
	private String firstRun = "firstRun";
	public boolean getFirstRun() {		
		return prefs.getBoolean(firstRun, true);
	}
	public void setFirstRun(boolean isFirstRun) {
		Editor editor = prefs.edit();     
		editor.putBoolean(firstRun,isFirstRun);
		editor.commit();
	}
	
}
