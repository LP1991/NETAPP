package com.cloudvision.tanzhenv2.order.wifi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.wifi.ScanResultAdapter;
import com.cloudvision.tanzhenv2.order.wifi.SetApPwdDialog;
import com.cloudvision.tanzhenv2.order.wifi.SetApPwdDialog.IConnectWifi;
import com.cloudvision.tanzhenv2.order.wifi.WifiUtil;
import com.cloudvision.tanzhenv2.order.wifi.WifiUtil.IWifiOpen;
import com.cloudvision.tanzhenv2.order.wifi.WifiUtil.WifiCipherType;
import com.cloudvision.ui.tabitem.LoadingProgressDialog;
import com.cloudvision.util.MyLog;
import com.cloudvision.util.WifiAdmin;

import java.util.Comparator;
import java.util.List;

/**
 * 连接其他WIFI热点
 * @author zhangyun
 *
 */
public class WifiTestActivity extends FragmentActivity implements IWifiOpen {
	
	private static final String TAG = "WifiTestActivity";

	private Context context = this;
	private List<ScanResult> ScanResults;
	private WifiAdmin wifiAdmin;
	private ListView mListView;
//	private ProgressDialog  progressDialog;
	private LoadingProgressDialog  progressDialog;
	private ScanResultAdapter mAdapter = new ScanResultAdapter(WifiTestActivity.this) ;
	
	private IntentFilter mFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
	private WifiActionReceiver mWifiActionReceiver = new WifiActionReceiver();
	private WifiManager wifiManager = null ;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case 0:
				new ScanResultTask().execute((Void)null);
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifitest);
		
		initView();

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiAdmin = new WifiAdmin(WifiTestActivity.this);
		 
		progressDialog = LoadingProgressDialog.createDialog(WifiTestActivity.this);
		progressDialog.setMessage("正在打开WIFI");
		
		//关闭wifi热点
		WifiUtil.closeWifiAp(context);

		//如果WIFI没打开，则打开
		if(!WifiUtil.isWifiOpen(WifiTestActivity.this))
		{
			progressDialog.show();
			WifiUtil.openWifi(WifiTestActivity.this, WifiTestActivity.this);
		}
		else
		{
			progressDialog.setMessage("正在扫描WIFI");
			progressDialog.show();
			mHandler.post(mCallBack);
		}
	} 
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		
		MyLog.e("wifiActivity","onDestroy");
		if(mCallBack != null){
			mHandler.removeCallbacks(mCallBack);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mWifiActionReceiver != null && mFilter != null)
			registerReceiver(mWifiActionReceiver, mFilter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(mWifiActionReceiver != null)
			unregisterReceiver(mWifiActionReceiver);
	}
	
	@Override
	public void onWifiOpen(final int state) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(state == WifiManager.WIFI_STATE_DISABLED){
					
				}else{
					mHandler.post(mCallBack);
				}
			}
		});
	}
	
	private void initView() {
		
		TextView tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText("热点列表");
		TextView tv_back = (TextView) findViewById(R.id.top_back);
		tv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mListView = (ListView) findViewById(R.id.list_other_wifi);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mAdapter.refreshList(changeFirst(ScanResults));
				ScanResult mTemp = (ScanResult) mListView.getAdapter().getItem(position);
				WifiInfo mInfo = WifiUtil.getConnectedWifiInfo(WifiTestActivity.this);
				if( mTemp != null){
					if(mInfo != null){
						if(mInfo.getSSID() != null && (mInfo.getSSID().equals(mTemp.SSID)||mInfo.getSSID().equals("\"" + mTemp.SSID+"\""))){
							//显示信息页
							String encrypt = WifiUtil.getEncryptString(mTemp.capabilities);
							goDetail(encrypt,mInfo.getSSID());
							}else{
							connectAp(mTemp);
						}
					}else{
						connectAp(mTemp);
					}
				}
			}
		});
		
//		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
//		{
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				final ScanResult mTemp = (ScanResult) mListView.getAdapter().getItem(arg2);
//				String[] strs = new String[] {"删除"+mTemp.SSID+"的配置"};
//				Builder alertDialog = new AlertDialog.Builder(context);
//				alertDialog.setTitle("请选择以下操作");
//				alertDialog.setItems(strs, new DialogInterface.OnClickListener()
//				{
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						if(which == 0)
//						{
//							//删除该热点的配置
//							WifiConfiguration config = WifiUtil.isExsits(context, mTemp.SSID);
//							if(config!=null)
//							{
//								if(WifiUtil.removeWifi(context, config.networkId))
//								{
//									//移除成功
//									Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show();
//								}
//								else
//								{
//									//移除失败
//									Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
//								}
//								goScanResult();
//							}
//						}
//					}
//				});
//				alertDialog.create().show();
//				return true;
//			}
//		});
	}
	
	private void goDetail(String encrypt,String ssid) {
		Intent intent = new Intent(WifiTestActivity.this,WifiDetailActivity.class);
		intent.putExtra("encrypt", encrypt);
		intent.putExtra("ssid", ssid);
		WifiTestActivity.this.startActivity(intent);
	}

	private void goScanResult() {
		mHandler.sendEmptyMessage(0);
	}
	
	private Runnable mCallBack = new Runnable() {
		@Override
		public void run() {
			MyLog.i(TAG,"mCallBack...");
			wifiManager.startScan();		
			mHandler.postDelayed(this, 3000);
		}
	};
	
	private void connectAp(ScanResult mResult){
		
		MyLog.i(TAG,WifiUtil.getWifiCipher(mResult.capabilities).toString());
		MyLog.i(TAG,mResult.capabilities);
		MyLog.i(TAG,WifiUtil.getEncryptString(mResult.capabilities));
		
		List<WifiConfiguration> mList = WifiUtil.getConfigurations(WifiTestActivity.this);
		
		if(mList == null || mList.isEmpty()){
			if(WifiUtil.getEncryptString(mResult.capabilities).equals("OPEN")){
				
				wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(mResult.SSID, "", 1));
//				WifiUtil.addNetWork(WifiUtil.createWifiConfig(mResult.SSID,"", WifiUtil.getWifiCipher(mResult.capabilities)), WifiTestActivity.this);
			}else{
				//连接当前ap,弹出密码输入框，设置密码 
				SetApPwdDialog.show(WifiTestActivity.this, new IConnectWifi(){

					@Override
					public void onConnectClick(String SSID, String pwd,
							WifiCipherType mType) {
						WifiUtil.addNetWork(WifiUtil.createWifiConfig(SSID, pwd, mType), WifiTestActivity.this);
					}
				}, mResult.SSID, WifiUtil.getWifiCipher(mResult.capabilities));
			}
			
		}else{
			
			boolean flag = false;
			for(int i = 0 ; i< mList.size(); i++){
				if(mList.get(i).SSID.equals("\"" + mResult.SSID + "\"")){
					MyLog.i(TAG," ssid = " + mResult.SSID);
//					WifiUtil.addNetWork(mList.get(i), WifiTestActivity.this);
					wifiAdmin.addNetwork(mList.get(i));
					flag = true;
					break;
				}
			}
			
			if(!flag){
				if(WifiUtil.getEncryptString(mResult.capabilities).equals("OPEN")){
					WifiUtil.addNetWork(WifiUtil.createWifiConfig(mResult.SSID,"", WifiUtil.getWifiCipher(mResult.capabilities)), WifiTestActivity.this);
				}else{
					//连接当前ap,弹出密码输入框，设置密码 
					SetApPwdDialog.show(WifiTestActivity.this, new IConnectWifi(){

						@Override
						public void onConnectClick(String SSID,String pwd, WifiCipherType mType) {
							WifiUtil.addNetWork(WifiUtil.createWifiConfig(SSID, pwd, mType), WifiTestActivity.this);
						}
					}, mResult.SSID, WifiUtil.getWifiCipher(mResult.capabilities));
				}
			}
		}
		goScanResult();
	}
	
	class WifiActionReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			if(intent.getAction() != null && intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
				if(progressDialog!=null)
				{
					if(progressDialog.isShowing())
					{
						progressDialog.dismiss();
					}
				}
				goScanResult() ;
			}
		}
	}

	class ScanResultTask extends AsyncTask<Void, Void, List<ScanResult>>{
		@Override
		protected List<ScanResult> doInBackground(Void... params) {
			return WifiUtil.getWifiScanResult(WifiTestActivity.this);
		}
		@Override
		protected void onPostExecute(List<ScanResult> result) {
			if(result != null ){
				if(mListView.getVisibility() == View.GONE)
					mListView.setVisibility(View.VISIBLE);
				ScanResults = result;
				mAdapter.refreshList(changeFirst(ScanResults));
			}else{
				mListView.setVisibility(View.GONE);
			}
			super.onPostExecute(result);
		}
	}
	
	/**
	 * 列表排序，将已连接和正在连接的wifi设于最前
	 * 
	 * by 谭智文
	 */
	private List<ScanResult> changeFirst(List<ScanResult> mScanResult) {
		
//		MyCompartor mc = new MyCompartor();
//	    Collections.sort(mScanResult,mc);     //按字母排序
	    
	    for (int i = 0; i < mScanResult.size(); i++){
	    	int channelValue = WifiUtil.frequency2Channel(mScanResult.get(i).frequency);
	    	if (mScanResult.get(i).SSID.equals("") || 0 == channelValue) {
	    		mScanResult.remove(i);
	    		continue;
			}
            for (int j = mScanResult.size() - 1 ; j > i; j--){
                if (mScanResult.get(i).SSID.equals(mScanResult.get(j).SSID)){
                	mScanResult.remove(j);
                }
            }
        }
		
		WifiInfo mInfo = WifiUtil.getConnectedWifiInfo(context);
		for (int i = 0; i < mScanResult.size(); i++) {
			ScanResult mTemp = mScanResult.get(i);
			if(mInfo.getSSID() != null && 
					(mInfo.getSSID().equals(mTemp.SSID)||mInfo.getSSID().equals("\"" + mTemp.SSID+"\""))){
				mScanResult.remove(i);
				mScanResult.add(0, mTemp);
			}
		}
		return mScanResult;
	} 
	
	class MyCompartor implements Comparator<Object>{
	     @Override
	     public int compare(Object o1, Object o2){

	    	 ScanResult sResult1= (ScanResult) o1;
	    	 ScanResult sResult2= (ScanResult) o2;
	         return sResult1.SSID.compareTo(sResult2.SSID);
	    }
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN && 
			event.getKeyCode() == KeyEvent.KEYCODE_BACK)
		{
			return false;
		}
		else {
			return super.dispatchKeyEvent(event);
		}
	}
}
