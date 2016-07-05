package com.cloudvision.service;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import com.cloudvision.tanzhenv2.application.ContextUtil;
import com.cloudvision.tanzhenv2.order.wifi.WifiUtil;
import com.cloudvision.util.MyLog;
import com.cloudvision.util.SPUtils;
import com.cloudvision.util.WifiAdmin;

import java.util.List;


public class ClientService extends Service {

	private String TAG = "ClientService";
	
	private WifiManager wifiManager;
	private WifiAdmin wifiAdmin;
	private int connectTime;
	public ContextUtil contextUtil;
	private String defaultSSID;
	private String defaultPwd;
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		contextUtil = ContextUtil.getInstance_o();
		wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        wifiAdmin = new WifiAdmin(getBaseContext());
        wifiAdmin.openWifi();
        connectTime = 0;
        contextUtil.ifClientService = true;
        
        defaultSSID = (String)SPUtils.get(ClientService.this, "routerSSID","String");
        defaultPwd = (String)SPUtils.get(ClientService.this, "routerPwd","String");
        
        new Thread(new ClientThread()).start();
		return START_NOT_STICKY;
	}
	
	public void checkDeviceSSID()
	{
		wifiManager.startScan();
		List<ScanResult> scanResults = wifiManager.getScanResults();
		if(scanResults != null)
		{
			if(scanResults.size() > 0)
	        {
	        	for(int i=0; scanResults != null && i<scanResults.size(); i++) {
	                ScanResult scanRet = scanResults.get(i);
	                String ssid = scanRet.SSID;
	                MyLog.e("扫描到的SSID", ssid);
	                if(ssid.contains(defaultSSID))
	                {
	                	MyLog.e(TAG,"发现SSID,开始连接");
	                	MyLog.e(TAG,defaultPwd);
	                    wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(scanRet.SSID, defaultPwd, 3));
	                    checkIfWifiConnected();
	                    break;
	                }
	                else
	                {
	                	MyLog.e(TAG,"未发现SSID,继续扫描");
	                }
	                if(i == scanResults.size()-1)
	                {
	                	MyLog.e(TAG,"列表未发现SSID,再次扫描");
	                	try {
	        				Thread.sleep(500);
	        				connectTime++;
	        				if(connectTime == 20)
	        				{
	        					MyLog.e(TAG,"周围未发现设备,通知调用页面");
	        					
	        					Intent intent = new Intent();
	    						intent.setAction("nossid");
	    						contextUtil.localBroadcastManager.sendBroadcast(intent);
	    						
	        					
	        					connectTime = 0;
	        					return;
	        				}
	        				scanResults.clear();
	        				checkDeviceSSID();
	        			} catch (InterruptedException e) {
	        				// TODO Auto-generated catch block
	        				e.printStackTrace();
	        			}
	                }
	                
	            }
	        }
	        else {
	        	try {
					Thread.sleep(1000);
					checkDeviceSSID();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void checkIfWifiConnected()
	{		
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
		// Do whatever
			MyLog.e(TAG,"wifi已连接");
			connectTime = 0;
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			WifiInfo mConnectedInfo =WifiUtil.getConnectedWifiInfo(ClientService.this);
			if(mConnectedInfo.getSSID().contains(defaultSSID))
			{
//				myProbe.startConnectRouter(ClientService.this);
			}
			else {
				MyLog.e(TAG,"周围未发现设备,通知调用页面");
				
				Intent intent = new Intent();
				intent.setAction("nossid");
				contextUtil.localBroadcastManager.sendBroadcast(intent);
				
				
				connectTime = 0;
				return;
			}
		}
		else
		{
			MyLog.e(TAG,"wifi未连接");
			try {
				Thread.sleep(500);
				connectTime++;
				if(connectTime == 30)
				{
					MyLog.e(TAG,"设备无法连接,通知调用页面");
					Intent intent = new Intent();
					intent.setAction("connectfailed");
					contextUtil.localBroadcastManager.sendBroadcast(intent);
					
					connectTime = 0;
					return;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			checkIfWifiConnected();
		}
		
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyLog.e(TAG, "onDestroy");
		contextUtil.ifClientService = false;
	}
	
public class ClientThread implements Runnable{
		
//		private Socket acceptSocket;

		public ClientThread(){

		}		
		
		public void run(){	
			checkDeviceSSID();
		}

	}

}