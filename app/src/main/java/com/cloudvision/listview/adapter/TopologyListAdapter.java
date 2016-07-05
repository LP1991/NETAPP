package com.cloudvision.listview.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.util.MyLog;

public class TopologyListAdapter extends BaseAdapter{
	private String TAG = "TopologyListAdapter";
	private Context context;
	private List<String>  list = null;
	
	public TopologyListAdapter(Context context,List<String> list){  
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
        if(position==0)
        {
        	return false;
        }
        return super.isEnabled(position);  
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView; 
        if(position == 0)
        {
        	view = LayoutInflater.from(context).inflate(R.layout.topology_list_tag, null);
        }
        else {
        	view = LayoutInflater.from(context).inflate(R.layout.topology_list_info, null);
        	TextView ccoTeiTextView = (TextView)view.findViewById(R.id.topology_list_tei);
        	TextView ccoMacTextView = (TextView)view.findViewById(R.id.topology_list_mac);
        	TextView ccoTxTextView = (TextView)view.findViewById(R.id.topology_list_tx);
        	TextView ccoRxTextView = (TextView)view.findViewById(R.id.topology_list_rx);
        	
        	String str = list.get(position-1);
        	MyLog.e(TAG,str);
    		String []infoArr= str.split(";");
        	if(infoArr.length ==4)
        	{
        		ccoTeiTextView.setText(infoArr[1]);
        		ccoMacTextView.setText(infoArr[0]);
        		ccoTxTextView.setText(infoArr[2]);
        		ccoRxTextView.setText(infoArr[3]);
        	}

		}
        return view;
	}
}
