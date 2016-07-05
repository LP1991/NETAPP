package com.cloudvision.tanzhenv2.order.wifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.wifi.WifiUtil.WifiCipherType;



public class SetApPwdDialog extends DialogFragment {

	private FragmentActivity mActivity;
	private IConnectWifi mConnectWifi = null;
	private String SSID; 
//	private String pwd; 
	private WifiCipherType mType;
	
	public interface IConnectWifi{
		public void onConnectClick(String SSID ,String pwd , WifiCipherType mType);
	}
	
	public SetApPwdDialog() {
		// TODO Auto-generated constructor stub
	};
	 
	public static SetApPwdDialog newInstance(IConnectWifi mConnectWifi , String SSID , WifiCipherType mType){
		
		SetApPwdDialog mFragment = new SetApPwdDialog();
		mFragment.SSID = SSID;
		mFragment.mType = mType;
		mFragment.mConnectWifi = mConnectWifi;

		return mFragment;
	}

	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		try {
			mActivity = (FragmentActivity) activity;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		LayoutInflater inflate = LayoutInflater.from(mActivity);
        int layoutId = R.layout.set_ap_pwd_page;
        View view = inflate.inflate(layoutId, null);
//		View view = LayoutInflater.from(mActivity).inflate(R.layout.set_ap_pwd_page, null);
		
		final EditText et = (EditText) view.findViewById(R.id.editText1);
		
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
		
		mBuilder.setView(view)
		.setTitle("请输入密码")
		.setNegativeButton("返回", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				
			}
		})
		.setPositiveButton("连接", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String pass = et.getText().toString().trim();
				
				if(mConnectWifi != null){
					mConnectWifi.onConnectClick(SSID, pass, mType);
				}
			}
		});
		
		return mBuilder.create();
	}
	
	public static void show(FragmentActivity mActivity,IConnectWifi mConnectWifi , String SSID , WifiCipherType mType){
		
		FragmentTransaction fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
		Fragment mBefore = mActivity.getSupportFragmentManager().findFragmentByTag(SetApPwdDialog.class.getSimpleName());
		
		if(mBefore != null){
			
			((DialogFragment)mBefore).dismiss();
			
			fragmentTransaction.remove(mBefore);
		}
		fragmentTransaction.addToBackStack(null);
		
		DialogFragment mNow =  SetApPwdDialog.newInstance(mConnectWifi , SSID ,  mType);
		
		mNow.show(fragmentTransaction, SetApPwdDialog.class.getSimpleName());
	}
}
