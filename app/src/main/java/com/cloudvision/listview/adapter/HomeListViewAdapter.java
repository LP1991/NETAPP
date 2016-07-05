package com.cloudvision.listview.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.activity.NetManagerActivity;
import com.cloudvision.tanzhenv2.order.WorkListActivity;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.function.SignInAndOut;
import com.cloudvision.tanzhenv2.order.function.refreshCallbackImpl;
import com.cloudvision.util.MyLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeListViewAdapter extends BaseAdapter{
	
	//添加function的点击Tag -- 谭智文
//	private static final int HISTORY = 1;
	private static final int WORKLIST = 1;
	private static final int SIGN = 2;
	
	private String[] nameList;
	private Context context;
	private List<String>  list = null;
	private ListGridView gView = null;
	private List<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();
	private SimpleAdapter sim_adapter = null;
	
	private int[] icon;
	private String[] iconName;
	private String [] from;
	private int [] to;
	
	public HomeListViewAdapter(Context context,String[] strList){  
		this.nameList = strList;
		this.context = context;
		
		list = new ArrayList<String>(); 
        for(int i =0;i<nameList.length;i++)
        {
        	list.add(nameList[i]);
        }
        
//        this.icon = imageList;
//        this.iconName = nameList;
        initData();		//初始化数据
    } 
	
	private void initData() {
		
		iconName = new String[]{"网管","工单","签到"};
        icon = new int[]{R.drawable.research,R.drawable.workorder,R.drawable.login};
//		iconName = new String[]{"网管",};
//        icon = new int[]{R.drawable.research};
//		iconName = new String[]{"网管"};
//        icon = new int[]{R.drawable.research};
        from = new String[]{"image","text"};
		to = new int[]{R.id.image,R.id.text};
		
		//载入data_list
		getData();
    	//新建适配器
		sim_adapter = new SimpleAdapter(context, data_list, R.layout.main_list_grid_item, from, to);
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
        ImageView imageView;  
    } 
	
	public List<Map<String, Object>> getData(){		
		//cion和iconName的长度是相同的，这里任选其一都可以
		for(int i=0;i<icon.length;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", icon[i]);
			map.put("text", iconName[i]);
			data_list.add(map);
		}
			
		return data_list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = convertView; 
		ViewHolder viewHolder = new ViewHolder();
        String nameString = list.get(position);
        if(nameString.equals("shottag"))
        {
        	view = LayoutInflater.from(context).inflate(R.layout.set_list_item_shot_tag, null);
        }
        else if(nameString.equals("tag"))
        {
        	view = LayoutInflater.from(context).inflate(R.layout.set_list_item_tag, null);
        }
        else if(nameString.equals("notice"))
        {
        	view = LayoutInflater.from(context).inflate(R.layout.main_list_notice_item, null);
        	viewHolder.imageView = (ImageView) view.findViewById(R.id.main_list_notice_icon);
        	viewHolder.imageView.setBackgroundResource((R.drawable.activity));
        	
        	//添加公告内容显示 -- 谭智文
        	viewHolder.textDetail = (TextView) view.findViewById(R.id.main_list_notice_text);
        	//viewHolder.textDetail.setText(Bullet.getBulletInfo());
        	viewHolder.textDetail.setSelected(true);  //text滚动的必须设置。
		}
        else {
        	view = LayoutInflater.from(context).inflate(R.layout.main_list_funtion_item, null);
        	gView = (ListGridView) view.findViewById(R.id.gview);

        	//更改为initData初始化sim_adapter 。  -- 谭智文
			gView.setAdapter(sim_adapter);
    		//增加点击监听--谭智文
    		gView.setOnItemClickListener(new homeOnItemClickListener());
		}
        return view;
	}
	
	/**
	 * 更新适配器（为了更新签到签出按钮状态）
	 * 
	 * by 谭智文
	 */
	public void refreshSimAdapter() {
		
//		if((Boolean) MySharedPreferencesUtils.get("signFlag", false)){
//			icon[SIGN] = R.drawable.login;
//			iconName[SIGN] = "签出";
//		}else {
//			icon[SIGN] = R.drawable.login_1;
//			iconName[SIGN] = "签到";
//		}
//		
//		//重新获取数据
//		data_list.clear();
//		getData();
//		sim_adapter.notifyDataSetChanged();
	}
	
	/**
	 * 主界面item点击监听
	 * 
	 * by 谭智文
	 */
	private class homeOnItemClickListener implements AdapterView.OnItemClickListener
    {
		@Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        	
        	Intent intent;
        	switch (arg2) {
        	case 0:
        		MyLog.e("", "网管");
        		intent = new Intent(context, NetManagerActivity.class);
				context.startActivity(intent);
        		break;
			case WORKLIST:	//工单
				clickWorkList();
				break;
//			case SIGN:		//签到
//				clickSignBtn();
//				break;
//			case HISTORY:	//历史工单
//				intent = new Intent(context, HistoryActivity.class);
//				context.startActivity(intent);
//				break;
//			case 3:
//				MyLog.e("test", "升级");
//				new UpgradeDown(context);
//				break;
//			case 4:
//				MyLog.e("test", "查询");
//				new UpgradeQuery(context);
//				break;
			default:
				break;
			}
        }
    }
	
	/**
	 * 点击工单按钮处理
	 * 
	 * by 谭智文
	 */
	private void clickWorkList() {
		
		boolean signFlag = (Boolean) MySharedPreferencesUtils.get("signFlag", false);
		if(signFlag){
			Intent intent = new Intent(context, WorkListActivity.class);
			context.startActivity(intent);
		}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
	        builder.setIcon(android.R.drawable.ic_dialog_alert);
	        builder.setTitle("提示");
	        builder.setMessage("在查看工单之前请签到！");
	        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.cancel();
	            }
	        });
	        builder.create();
	        builder.show();
        }
	}
	
	/**
	 * 点击签到/签出按钮处理
	 * 
	 * by 谭智文
	 */
	private void clickSignBtn() {
		
		boolean loginFlag = (Boolean) MySharedPreferencesUtils.get("loginFlag", true);
		if(loginFlag){
			
			SignInAndOut signInAndOut = new SignInAndOut(context);
			signInAndOut.setCallfuc(new refreshCallbackImpl() {
				@Override
				public void refreshTip() {
					refreshSimAdapter();
				}
			});
		}else {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
	        builder.setIcon(android.R.drawable.ic_dialog_alert);
	        builder.setTitle("提示");
	        builder.setMessage("请登录后再进行签到！");
	        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.cancel();
	            }
	        });
	        builder.create();
	        builder.show();
		}
	}
	
}
