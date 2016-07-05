package com.cloudvision.listview.adapter;

import java.util.List;

import android.R.string;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.model.NetAlarmsInfo;
import com.cloudvision.util.MyLog;

public class WarnigListAdapter extends BaseAdapter{
	private String TAG = "WarnigListAdapter";
	private Context context;
	private List<NetAlarmsInfo>  list = null;
	
	public WarnigListAdapter(Context context,List<NetAlarmsInfo> list){  
		this.list = list;
		this.context = context;
    } 
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size()+1;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	
	@Override  
    public boolean isEnabled(int position) {  
        // TODO Auto-generated method stub  
//        if(position==0)
//        {
//        	return false;
//        }
//        return super.isEnabled(position);  
		return false;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView; 
        if(position == 0)
        {
        	view = LayoutInflater.from(context).inflate(R.layout.warning_list_tag, null);
        }
        else {
        	view = LayoutInflater.from(context).inflate(R.layout.warning_list_info, null);
        	TextView countTextView = (TextView)view.findViewById(R.id.warning_list_count);
        	TextView timeTextView = (TextView)view.findViewById(R.id.warning_list_time);
        	TextView adviseTextView = (TextView)view.findViewById(R.id.warning_list_advise);
        	        	
        	NetAlarmsInfo alarmsInfo = list.get(position-1);
        	countTextView.setText(String.valueOf(position));
        	timeTextView.setText(alarmsInfo.time);
        	adviseTextView.setText(alarmsInfo.advice);
        	

		}
        return view;
	}
}
