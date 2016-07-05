package com.cloudvision.tanzhenv2.order.wifi.activity;

import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.BaseActivity;
import com.cloudvision.tanzhenv2.order.wifi.WifiUtil;
import com.cloudvision.util.MyLog;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Locale;

/**
 * 已连接的ap详情
 *
 * Created by 谭智文
 */
public class WifiDetailActivity extends BaseActivity{
	
	private static final String TAG = "WifiDetailActivity";
	
	private WifiInfo mConnectedInfo;
	private String encrypt;
	
	private TextView mSsidTv;
	private TextView mStateTv;
	private TextView mSafetyTv;
	private TextView mLevelTv;
	private TextView mSpeedTv;
	private TextView mIpTv;
	private TextView mApMacTv;
	private TextView mNetMacTv;
	private TextView mMaskTv;
	private TextView mGateWayTv;
	private TextView mDns1Tv;
	private TextView mDns2Tv; 
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case 0:
				setTextParms();
				break;
			default:
				break;
			}
		};
	};
	
	private Runnable mCallBack = new Runnable() {
		@Override
		public void run() {
			mConnectedInfo = WifiUtil.getConnectedWifiInfo(WifiDetailActivity.this);
			mHandler.sendEmptyMessage(0);
			mHandler.postDelayed(this, 2000);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_detail);
		
		initView();
		mHandler.post(mCallBack);
	}
	
	private void initView() {
		
		//title设置
		TextView tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText("热点详情");
		TextView tv_back = (TextView) findViewById(R.id.top_back);
		tv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		Button btnPing = (Button) findViewById(R.id.btn_ping);
		btnPing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WifiDetailActivity.this,PingActivity.class);
				WifiDetailActivity.this.startActivity(intent);
			}
		});
		
		Intent intent = getIntent();
		encrypt = intent.getStringExtra("encrypt");
		String ssid = intent.getStringExtra("ssid");
		mConnectedInfo = WifiUtil.getConnectedWifiInfo(WifiDetailActivity.this);
		
		mSsidTv = (TextView) findViewById(R.id.ssid_tv);
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion > 17) {
			ssid = ssid.substring(1, ssid.length()-1);
		}
		mSsidTv.setText(ssid);
		
		mStateTv = (TextView) findViewById(R.id.state_tv);
		mSafetyTv = (TextView) findViewById(R.id.safety_tv);
		mLevelTv = (TextView) findViewById(R.id.level_tv);
		mSpeedTv = (TextView) findViewById(R.id.speed_tv);
		mIpTv = (TextView) findViewById(R.id.ip_tv);
		mApMacTv = (TextView) findViewById(R.id.ap_mac_tv);
		mNetMacTv = (TextView) findViewById(R.id.netcard_mac_tv);
		mMaskTv = (TextView) findViewById(R.id.mask_tv);
		mGateWayTv = (TextView) findViewById(R.id.maskway_tv);
		mDns1Tv = (TextView) findViewById(R.id.dns1_tv);
		mDns2Tv = (TextView) findViewById(R.id.dns2_tv);
		
	}
	
	private void setTextParms() {
		
		if (null == mConnectedInfo) {
			return;
		}
		
		int Ip = mConnectedInfo.getIpAddress() ;
		MyLog.i(TAG, "ip = " + Ip);
		String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
		if(mConnectedInfo.getSSID() != null && mConnectedInfo.getBSSID() != null && !strIp.equals("0.0.0.0")){
			
			mStateTv.setText("已连接");
		}else{
			mStateTv.setText("正在连接...");
		}
		mSafetyTv.setText(encrypt);
		mLevelTv.setText(mConnectedInfo.getRssi() + "db");
		mSpeedTv.setText(mConnectedInfo.getLinkSpeed() + " Mbps");	
		mIpTv.setText(WifiUtil.long2ip(mConnectedInfo.getIpAddress()));
		
		if (null != mConnectedInfo.getBSSID() || "".equals(mConnectedInfo.getBSSID())) {
			mApMacTv.setText(mConnectedInfo.getBSSID().toUpperCase(Locale.US));
		}

		try {	
			Field mField = mConnectedInfo.getClass().getDeclaredField("mIpAddress");
			mField.setAccessible(true);
			InetAddress mInetAddr = (InetAddress) mField.get(mConnectedInfo);
			NetworkInterface mInterface = NetworkInterface.getByInetAddress(mInetAddr);
			
			byte[] mac = mInterface.getHardwareAddress();
			
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i<mac.length;i++){
				
				sb.append(String.format("%02X%s", mac[i],(i<mac.length-1)?":":""));
			}
			mNetMacTv.setText(sb.toString());
			
		}catch (SocketException e){
			
			e.printStackTrace();
		}catch(NoSuchFieldException e){
			e.printStackTrace();
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}catch(NullPointerException e){
			e.printStackTrace();
		}
	
		DhcpInfo mDhcpInfo = WifiUtil.getDhcpInfo(this);
		mMaskTv.setText(WifiUtil.long2ip(mDhcpInfo.netmask));
		mGateWayTv.setText(WifiUtil.long2ip(mDhcpInfo.gateway));
		mDns1Tv.setText(WifiUtil.long2ip(mDhcpInfo.dns1));
		mDns2Tv.setText(WifiUtil.long2ip(mDhcpInfo.dns2));
	}

}
