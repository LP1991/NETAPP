package com.cloudvision.listview.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;

public class SetListViewAdapter extends BaseAdapter {
	
	private Context context;
	private List<String>  list = null;
	
	public SetListViewAdapter(Context context,List<String> list){  
		this.list = list;
		this.context = context;
    } 
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
    	String nameString = list.get(position);
        if(nameString.equals("tag"))
        {
        	return false;
        }
        return super.isEnabled(position);  
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView; 
        String nameString = list.get(position);
        if(nameString.equals("tag"))
        {
        	view = LayoutInflater.from(context).inflate(R.layout.set_list_item_tag, null);
        }
        else {
        	view = LayoutInflater.from(context).inflate(R.layout.set_list_item, null);
        	TextView text = (TextView) view.findViewById(R.id.set_list_item_text);
        	view.setTag(position);
        	text.setText(nameString);
		}
        return view;
	}
	
}
