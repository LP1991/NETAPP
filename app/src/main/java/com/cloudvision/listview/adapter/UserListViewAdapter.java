package com.cloudvision.listview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CircleImageView;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class UserListViewAdapter extends BaseAdapter {
	
	private String[] nameList;
	private Context context;
	private List<String>  list = null;
	private MyClickListener mListener;		//用户头像图片点击监听--谭智文
	
	public UserListViewAdapter(Context context,String[] strList,MyClickListener mListener){  
		this.nameList = strList;
		this.context = context;
		this.mListener = mListener;
		
		list = new ArrayList<String>(); 
        for(int i =0;i<nameList.length;i++)
        {
        	list.add(nameList[i]);
        }
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
	
	public class ViewHolder{  
        TextView textName;  
        TextView textDetail;   
        CircleImageView imageView;  
    }  

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView; 
//		Bitmap bitmap = UserActivity.facebitmap;
		ViewHolder viewHolder = new ViewHolder();
        String nameString = list.get(position);
        if(nameString.equals("tag"))
        {
        	view = LayoutInflater.from(context).inflate(R.layout.set_list_item_tag, null);
        }
        else if(nameString.equals("user"))
        {
        	view = LayoutInflater.from(context).inflate(R.layout.user_list_item_logo, null);
        	viewHolder.imageView = (CircleImageView) view.findViewById(R.id.user_list_item_icon);
        	viewHolder.imageView.setImageResource(R.drawable.login_1);
        	viewHolder.imageView.setBackgroundResource(R.color.face_background);
//        	修改显示的图片为用户选择的图片 -- 谭智文
//        	if (bitmap != null){
//            	viewHolder.imageView.setImageBitmap(bitmap);
//        	}
        	String urlString = (String) MySharedPreferencesUtils.get("headImage", "");
        	boolean isLocation = (Boolean) MySharedPreferencesUtils.get("isLocation", false);
            if(urlString.equals("") || isLocation){
            	urlString = "file:///"+Constants.CACHE_PATH + MySharedPreferencesUtils.get("userName", "");
            	ImageLoader.getInstance().displayImage(urlString, viewHolder.imageView);
            }else {
            	ImageLoader.getInstance().displayImage(urlString, viewHolder.imageView,
            			new DisplayImageOptions.Builder()
            			.cacheInMemory(true)
            	        .cacheOnDisk(true)
            	        .build());
			}
        	viewHolder.imageView.setOnClickListener(mListener);	//设置图像点击监听
        	viewHolder.textDetail = (TextView) view.findViewById(R.id.user_list_item_username);
        	//更改显示内容（真名） -- 谭智文
        	String trueName = (String) MySharedPreferencesUtils.get("trueName", "");
        	viewHolder.textDetail.setText(trueName);
		}
        else {
        	view = LayoutInflater.from(context).inflate(R.layout.user_list_item, null);
        	viewHolder.textName = (TextView) view.findViewById(R.id.user_list_item_text);
        	viewHolder.textDetail = (TextView) view.findViewById(R.id.user_list_item_text_detail);
        	view.setTag(position);
        	viewHolder.textName.setText(nameString);
        	if(position == 3)
        	{
        		//更改显示内容（用户名） -- 谭智文
        		String userName = (String) MySharedPreferencesUtils.get("userName", "");
        		viewHolder.textDetail.setText(userName);
        	}
        	if(position == 4)
        	{
        		viewHolder.textDetail.setText("");
        	}
        	if(position == 5)
        	{
        		
        	}
		}
        return view;
	}
	
	/**
	* 用于回调的抽象类
	*/
	public static abstract class MyClickListener implements OnClickListener {
	/**
	* 基类的onClick方法
	*/
		@Override
	    public void onClick(View v) {
	        myOnClick(v);
	    }
	    public abstract void myOnClick(View v);
	}
}
