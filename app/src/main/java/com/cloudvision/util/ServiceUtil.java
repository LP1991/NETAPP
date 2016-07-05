package com.cloudvision.util;


import android.content.Context;
import android.content.Intent;

import com.cloudvision.service.ClientService;
import com.cloudvision.service.SocketServerService;



public class ServiceUtil {
	
	private static String TAG = "ServiceUtil";
	
	public static void connectDevice(Context context)
	{
		Object modeStr = SPUtils.get(context, "mode","String");
		if(modeStr.equals("ap"))
		{
			MyLog.e(TAG, "apmode");
			startServerService(context);
		}
		else {
			MyLog.e(TAG, "clientmode");
			startClientService(context);
		}
	
	}
	
	public static void  stopConnectDevice(Context context)
	{
		
		Object modeStr = SPUtils.get(context, "mode","String");
		if(modeStr.equals("ap"))
		{
			MyLog.e(TAG, "apmode");
			stopServerService(context);
		}
		else {
			MyLog.e(TAG, "clientmode");
			stopClientService(context);
		}
	}
	
	private static void startServerService(Context context)
	{
		Intent intent = new Intent();
  	    intent.setClass(context, SocketServerService.class);
  	    context.startService(intent);
	}
	
	private static void stopServerService(Context context)
	{
		Intent intent = new Intent();
  	    intent.setClass(context, SocketServerService.class);
  	    context.stopService(intent);
	}
	
	private static void startClientService(Context context)
	{
		Intent intent = new Intent();
  	    intent.setClass(context, ClientService.class);
  	    context.startService(intent);
	}
	
	private static void stopClientService(Context context)
	{
		Intent intent = new Intent();
  	    intent.setClass(context, ClientService.class);
  	    context.stopService(intent);
	}
	

}
