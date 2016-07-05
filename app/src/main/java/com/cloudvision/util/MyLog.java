package com.cloudvision.util;

import android.util.Log;

public class MyLog {
	
	private static boolean isLogShow = true;
	
	public static void i(String TAG,String msg)  
    {  
        if (isLogShow)  
            Log.i(TAG, msg);  
    } 
	
	public static void e(String TAG,String msg)  
    {  
        if (isLogShow)  
            Log.e(TAG, msg);  
    } 
}
