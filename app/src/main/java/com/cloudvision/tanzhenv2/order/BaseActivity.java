package com.cloudvision.tanzhenv2.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.KeyEvent;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class BaseActivity extends Activity{
	
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
}
