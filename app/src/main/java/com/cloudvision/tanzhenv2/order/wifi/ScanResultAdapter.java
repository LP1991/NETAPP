package com.cloudvision.tanzhenv2.order.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.util.MyLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanResultAdapter extends BaseAdapter{
	
	private static final String TAG = "ScanResultAdapter";
	
	private Context context;
	
	private List<ScanResult> mScanResult = new ArrayList<ScanResult>();
	
	private List<WifiPwdUtil.WifiInfo> mWifiPwdInfo = new ArrayList<WifiPwdUtil.WifiInfo>();
	
	private Map<String , String> mWifiPwdMap = new HashMap<String, String>();
	
	public ScanResultAdapter(Context context) {
		this.context = context;
	}
	
	public void refreshList(List<ScanResult> mResult){
				
		mScanResult.clear();
		mScanResult.addAll(mResult);
		notifyDataSetChanged();
	}
	
	public void refreshPwdList(List<WifiPwdUtil.WifiInfo> mResult){
		
		if(mResult == null)
			return;
	
		mWifiPwdInfo.clear();	
		mWifiPwdInfo.addAll(mResult);
		mWifiPwdMap.clear();
		
		for(int i = 0 ; i < mWifiPwdInfo.size() ; i++){	
//			MyLog.i(TAG, "ssid = " + mWifiPwdInfo.get(i).ssid + " pwd = " + mWifiPwdInfo.get(i).password);
			if(mWifiPwdInfo.get(i) != null){
				mWifiPwdMap.put(mWifiPwdInfo.get(i).ssid, mWifiPwdInfo.get(i).password);
			}
		}
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return this.mScanResult.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mScanResult.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			LayoutInflater inflate = LayoutInflater.from(context);
            int layoutId = R.layout.scan_result_item;
            convertView = inflate.inflate(layoutId, null);
//			convertView = LayoutInflater.from(context).inflate(R.layout.scan_result_item, null);
		}

		TextView mSsidTv = ViewHolder.getView(convertView, R.id.ssid_tv);
		TextView mBssidTv = ViewHolder.getView(convertView, R.id.bssid_tv);
		TextView mConnectTv = ViewHolder.getView(convertView, R.id.connect_tv);
		TextView mChannelTv = ViewHolder.getView(convertView, R.id.channel_tv);
		TextView mLevelTv = ViewHolder.getView(convertView, R.id.level_tv);
		TextView mEncryptTv = ViewHolder.getView(convertView, R.id.encrypt_tv);
		TextView mPwdTv = ViewHolder.getView(convertView, R.id.pwd_tv);
		
		WifiInfo mInfo = WifiUtil.getConnectedWifiInfo(context);
		
		if(getItem(position) != null){
			
			ScanResult mResult = (ScanResult) getItem(position);
			
			mSsidTv.setText(mResult.SSID);
			mBssidTv.setText("("+mResult.BSSID+")");
			int channel = WifiUtil.frequency2Channel(mResult.frequency);
			if (channel > 0) {
				mChannelTv.setText("信道："+ WifiUtil.frequency2Channel(mResult.frequency));
			}else {
				mChannelTv.setText("信道：  ");
			}
			mLevelTv.setText("信号:"+ mResult.level + "dbm");
			
			
			if(mWifiPwdMap.containsKey(mResult.SSID)){
				
				mPwdTv.setVisibility(View.VISIBLE);
				mPwdTv.setText(mWifiPwdMap.get(mResult.SSID));
			}else{
				mPwdTv.setVisibility(View.GONE);
			}
			
			if(mInfo != null){
				if(mInfo.getSSID() != null && (mInfo.getSSID().equals(mResult.SSID)||mInfo.getSSID().equals("\"" + mResult.SSID+"\""))){
					
					mConnectTv.setVisibility(View.VISIBLE);
					
					int Ip = mInfo.getIpAddress() ;
					
					MyLog.i(TAG, "ip = " + Ip);
					
					String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
					
					if(mInfo.getBSSID() != null && mInfo.getSSID() != null && strIp != null && !strIp.equals("0.0.0.0")){
						
						mConnectTv.setText("已连接");
					}else{
						
						mConnectTv.setText("正在连接...");
					}
				}else{
					mConnectTv.setVisibility(View.GONE);
				}
			}else{
				mConnectTv.setVisibility(View.GONE);
			}
			mEncryptTv.setText(WifiUtil.getEncryptString(mResult.capabilities));
		}
		return convertView;
	}
}
