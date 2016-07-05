package com.cloudvision.tanzhenv2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudvision.listview.adapter.SetListViewAdapter;
import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.WebViewActivity;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.function.AppUpdate;
import com.cloudvision.tanzhenv2.order.infoedit.EditSuggestReturn;
import com.cloudvision.ui.tabitem.DialogAlert_one_btn;
import com.cloudvision.util.MyLog;
import com.cloudvision.util.SPUtils;

import java.util.ArrayList;
import java.util.List;

public class SetActivity extends FragmentActivity implements OnClickListener,OnItemClickListener{
	
	private String TAG = "SetActivity";
	private TextView titltView;
	private TextView backTextView;
	
	private List<String>  list = null;
	private String[] nameList = {"tag","设备配置","tag","检测更新","关于我们","意见反馈","用户帮助","FAQ","tag","APP版本","设备版本"};
    private ListView listview;
    public DialogAlert_one_btn tipDialog;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        
        titltView = (TextView)findViewById(R.id.top_title);
        titltView.setText("设置");
        backTextView = (TextView)findViewById(R.id.top_back);
        backTextView.setOnClickListener(this);
        
        listview = (ListView) findViewById(R.id.set_list);  
        initData(); 
        listview.setAdapter(new SetListViewAdapter(SetActivity.this, list));
        listview.setOnItemClickListener(this);
    }
	
	public void initData(){  
        list = new ArrayList<String>(); 
        for(int i =0;i<nameList.length;i++)
        {
        	list.add(nameList[i]);
        }
         
    }  
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int viewId = arg0.getId();
		switch (viewId) {
		case R.id.top_back:
			this.finish();
			break;
		default:
			break;
		}
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		return false;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		int position =Integer.parseInt(arg1.getTag().toString());
		String name = nameList[position];
		MyLog.e(TAG, "点击了"+name);
		if(arg2 == 1)
		{
//			Intent intent = new Intent(this,WifiListActivicty.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//			startActivity(intent);
		}
		if(arg2 == 3)
		{
			AppUpdate appUpdate = new AppUpdate(SetActivity.this);
			// 检查软件更新
			appUpdate.checkUpdate();
		}
		if(arg2 == 4)
		{
			Intent intent = new Intent();
			intent.putExtra("webAddress", Constants.URL_ABOUT_US);
		    intent.setClass(this, WebViewActivity.class);	
			this.startActivity(intent);
		}
		if(arg2 == 5)
		{
			Intent intent = new Intent(this,EditSuggestReturn.class);
			startActivity(intent);
		}
		if(arg2 == 6)
		{
			Intent intent = new Intent();
			intent.putExtra("webAddress", Constants.URL_USER_HELP);
		    intent.setClass(this, WebViewActivity.class);	
			this.startActivity(intent);
		}
		if(arg2 == 7)
		{
			Intent intent = new Intent();
			intent.putExtra("webAddress", Constants.URL_USER_FAQ);
		    intent.setClass(this, WebViewActivity.class);	
			this.startActivity(intent);
		}
		if(arg2 == 9)
		{
			String ver = getVersionName(SetActivity.this);
			CommonUtils.showTips(SetActivity.this, "提示", "版本号:" + ver);
		}
		if(arg2 == 10)
		{
			Boolean version = (Boolean)SPUtils.get(SetActivity.this, "versionFlag",false);
			if(!version)
			{
				Toast.makeText(SetActivity.this, "未获取设备版本号", Toast.LENGTH_SHORT).show();
			}
			else {
				String rouerStr = (String)SPUtils.get(SetActivity.this, "routerSw","");
				String eocStr = (String)SPUtils.get(SetActivity.this, "eocSw","");
				String catvStr = (String)SPUtils.get(SetActivity.this, "catvSw","");
				String mcuStr = (String)SPUtils.get(SetActivity.this, "mcuSw","");
					
				CommonUtils.showTips(SetActivity.this, "提示", "ROUTER:"+rouerStr+"\n"+"EOC:"+eocStr+"\n"+
						"CATV:"+catvStr+"\n"+"MCU:"+mcuStr);
			}
		}

	}
	
	private String getVersionName(Context context){
	    String versionName = "";
	    try{
	        // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
	    	versionName = context.getPackageManager().getPackageInfo(SetActivity.this.getPackageName(), 0).versionName;
	    } catch (NameNotFoundException e){
	        e.printStackTrace();
	    }
	    return versionName;
	}
}
