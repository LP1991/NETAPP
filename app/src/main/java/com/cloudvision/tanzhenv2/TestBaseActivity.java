package com.cloudvision.tanzhenv2;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cloudvision.ui.tabitem.DialogAlert_one_btn;
import com.cloudvision.ui.tabitem.LoadingProgressDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestBaseActivity extends Activity{
	
	public TextView titltView;
	public TextView backTextView;
	public ImageButton device_state_btn;
	public GridView gView;
	public List<Map<String, Object>> data_list;
	public SimpleAdapter sim_adapter;
	public int[] icon;
	public String[] iconName;
	public String [] from;
	public int [] to;
	public LoadingProgressDialog progressDialog;
	public LocalBroadcastManager localBroadcastManager;
	public IntentFilter intentFilter;
	public DialogAlert_one_btn tipDialog;

//	public final int DEVICE_CONNECT_SUCCEED = 0;
//	public final int DEVICE_CONNECT_FAILED = 1;
//	public final int DEVICE_UNFIND = 2;
//	public final int DISCONNECT_DEVICE_SUCCEED = 3;
//	public final int DEVICE_UNCONNECTED = 4;
//	public final int GETDEVCIDEINFO_SUCCEED = 5;
//	public final int GETDEVCIDEINFO_FAILED = 6;
//	public final int SETDEVCIDEINFO_SUCCEED = 7;
//	public final int SETDEVCIDEINFO_FAILED = 8;
//	public final int SOCKET_DISCONNECTED = 20;

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
	
	public List<Map<String, Object>> getData(){		
		data_list.clear();
		for(int i=0;i<icon.length;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", icon[i]);
			map.put("text", iconName[i]);
			data_list.add(map);
		}
			
		return data_list;
	}
	
	public void showTip(Context context,String str)
	{
		tipDialog = new DialogAlert_one_btn(context, "提示", str, "确定", new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				tipDialog.dismiss();
			}
		});
	}
	
}

