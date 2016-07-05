package com.cloudvision.tanzhenv2.order.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;

import com.cloudvision.tanzhenv2.order.function.refreshCallbackImpl;
import com.cloudvision.tanzhenv2.order.wifi.WifiUtil.IWifiOpen;
import com.cloudvision.ui.tabitem.LoadingProgressDialog;

import java.util.List;


/**
 * 扫描附近的wifi
 *
 * Created by 谭智文
 */
public class WifiScanResult {

	private int ScanTime;
	private Context context;	
	private static List<ScanResult> result;
	private LoadingProgressDialog  progressDialog;
	private WifiManager wifiManager;
	private IntentFilter mFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
	private WifiActionReceiver mWifiActionReceiver = new WifiActionReceiver();

	public refreshCallbackImpl bCallback;
	public boolean isAutoMiss = true;
	
    public void setAutoMiss(boolean isAutoMiss) {
		this.isAutoMiss = isAutoMiss;
	}

	public void setCallfuc(refreshCallbackImpl bCallback){
        this.bCallback= bCallback;
    }
    
    public void call(){
        this.bCallback.refreshTip();
    }
    
    public WifiScanResult(Context context,WifiManager wifiManager ,int ScanTime) {
		this.context = context;
		this.wifiManager = wifiManager;
		this.ScanTime = ScanTime;
		initScan();
	}
    
    private Handler mHandler = new Handler();
    private Runnable mCallBack = new Runnable() {
		@Override
		public void run() {
			wifiManager.startScan();		
			mHandler.postDelayed(this, ScanTime);
		}
	};
    
    /**
     * 开始扫描
     * 
     * by 谭智文
     */
    private void initScan() {
    		
    	StartReceiver();
    		
		progressDialog = LoadingProgressDialog.createDialog(context);
		progressDialog.setMessage("正在打开WIFI");
		
		//关闭wifi热点
		WifiUtil.closeWifiAp(context);

		//如果WIFI没打开，则打开
		if(!WifiUtil.isWifiOpen(context))
		{
			progressDialog.show();
			WifiUtil.openWifi(context, new IWifiOpen(){
				@Override
				public void onWifiOpen(final int state) {
					new Runnable() {
						@Override
						public void run() {
							if(state == WifiManager.WIFI_STATE_DISABLED){
								
								
							}else{
								mHandler.post(mCallBack);
							}
						}
					};
				}
			});
			
		}
		else
		{
			progressDialog.setMessage("正在扫描WIFI");
			progressDialog.show();
			mHandler.post(mCallBack);
		} 
    }
    
    private void goScanResult() {
    	new ScanResultTask().execute((Void)null);
	}
    
	public static List<ScanResult> getResult() {
		return result;
	}
	
	public void StartReceiver() {
		if(mWifiActionReceiver != null && mFilter != null){
			context.registerReceiver(mWifiActionReceiver, mFilter);
    	}
	}
	
	public void stopReceiver() {
		if(mWifiActionReceiver != null)
			context.unregisterReceiver(mWifiActionReceiver);
	}
	
	public void missProgress() {
		if(progressDialog.isShowing())
		{
			progressDialog.dismiss();
		}
	}
	
	class WifiActionReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			
			if(intent.getAction() != null && intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
				if(progressDialog!=null)
				{
					if(progressDialog.isShowing() && isAutoMiss)
					{
						progressDialog.dismiss();
					}
				}
				goScanResult();
			}
		}
	}
	
	class ScanResultTask extends AsyncTask<Void, Void, List<ScanResult>>{
		@Override
		protected List<ScanResult> doInBackground(Void... params) {
			return WifiUtil.getWifiScanResult(context);
		}
		
		@Override
		protected void onPostExecute(List<ScanResult> result) {
			WifiScanResult.result = result;
			call();
			super.onPostExecute(result);
		}
	}
}
