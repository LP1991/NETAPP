package com.cloudvision.tanzhenv2.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudvision.appconfig.AppConfig;
import com.cloudvision.listview.adapter.WarnigListAdapter;
import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.TestBaseActivity;
import com.cloudvision.tanzhenv2.model.NetAlarmsInfo;
import com.cloudvision.tanzhenv2.model.NetDeviceInfo;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.util.MyLog;
import com.cloudvision.util.MyProgressDialog;
import com.cloudvision.util.SPUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetManagerActivity extends TestBaseActivity implements OnClickListener ,HttpServiceInterface{
	
	private String TAG = "NetManagerActivity";
	private Button more1, more2, more3, more4;
	private PopupWindow popupWindow;
	private View view;
	private TextView scan;
	private ListView warning_list;
	private List<NetAlarmsInfo> warningInfoList;
	//begin add defind for master warning search by caiming
	private List<NetAlarmsInfo> master_warningInfoList;
	private List<NetAlarmsInfo> slave_warningInfoList;
	//end add defind for master warning search by caiming
	private WarnigListAdapter warnigListAdapter;
	private int currentMore = 0;
	private Button scanMacBtn;
	private EditText macAdress;
	private NetDeviceInfo netDeviceInfo;
	private List<String> portList;
	private Spinner portSpinner;
	private ArrayAdapter<String> portAdapter;

	private TextView end_pop_mac;
	private TextView end_pop_version;
	private TextView end_pop_outRFLevel;
	private TextView end_pop_portStat;
	private TextView end_pop_portMode;
	private TextView end_pop_portVlan;
	private TextView end_pop_portMacCount;//终端端口正在使用mac数目
	private TextView portMacCountLabel;
	private TextView endOutRFLevelLabel;
	private String currentMac;
	
	private TextView end_mac,end_version,head_name,head_ip,link_rssi,link_speed;
	
	private final int GET_DEVICEINFO_SUCCEED = 0;
	private final int GET_DEVICEINFO_FAILED = 1;
	private boolean getInfo;
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_DEVICEINFO_SUCCEED:
				getInfo = true;

				SPUtils.put(NetManagerActivity.this, "netMac", macAdress
						.getText().toString());

				end_mac.setText(netDeviceInfo.endInfo.mac);
				end_version.setText(netDeviceInfo.endInfo.verInfo);
				head_name.setText(netDeviceInfo.headInfo.host);
				head_ip.setText(netDeviceInfo.headInfo.ip);
				link_rssi.setText(netDeviceInfo.linkInfo.rssi);
				link_speed.setText(netDeviceInfo.linkInfo.upDownSpeed);
				//modfiy begin for serach master warning by caiming
//				if (warningInfoList.size() > 0) {
//					warningInfoList.clear();
//				}
//				for (int i = 0; i < netDeviceInfo.alarms.length; i++) {
//					warningInfoList.add(netDeviceInfo.alarms[i]);
//				}
				if (warningInfoList.size() > 0) {
					warningInfoList.clear();
					slave_warningInfoList.clear();
					master_warningInfoList.clear();
				}
				for (int i = 0; i < netDeviceInfo.alarms.length; i++) {
					warningInfoList.add(netDeviceInfo.alarms[i]);	
					
					if(netDeviceInfo.alarms[i].mac!=null){
						if(netDeviceInfo.alarms[i].mac.equals(currentMac)){
							slave_warningInfoList.add(netDeviceInfo.alarms[i]);
						} else {
							master_warningInfoList.add(netDeviceInfo.alarms[i]);
						}						
					}

				}
				//modfiy end for serach master warning by caiming
				warnigListAdapter.notifyDataSetChanged();

				break;
			case GET_DEVICEINFO_FAILED:
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_netmanager);

		initData();
		initView();
	}
	
	public void initData()
	{
		getInfo = false;
		portList = new ArrayList<String>();
		warningInfoList = new ArrayList<NetAlarmsInfo>();
		//begin add init for master warning search by caiming
		master_warningInfoList = new ArrayList<NetAlarmsInfo>();
		slave_warningInfoList = new ArrayList<NetAlarmsInfo>();
		//end add defind for master warning search by caiming	

		warning_list = (ListView) findViewById(R.id.warning_list);
		warnigListAdapter = new WarnigListAdapter(this, slave_warningInfoList);
		warning_list.setAdapter(warnigListAdapter);
	}
	
	public void initView()
	{
		titltView = (TextView)findViewById(R.id.top_title);
        titltView.setText("终端实时信息查询");
        backTextView = (TextView)findViewById(R.id.top_back);
        backTextView.setOnClickListener(this);
        scan = (TextView)findViewById(R.id.top_esc);
        scan.setText("");
		scan.setBackgroundResource(R.drawable.scanning);
        scan.setOnClickListener(this);
        
        more1 = (Button)findViewById(R.id.more1);
        more1.setOnClickListener(this);
        more2 = (Button)findViewById(R.id.more2);
        more2.setOnClickListener(this);
        more3 = (Button)findViewById(R.id.more3);
        more3.setOnClickListener(this);
        more4 = (Button)findViewById(R.id.more4);
        more4.setOnClickListener(this);
        
        scanMacBtn = (Button)findViewById(R.id.scanMacBtn);
        scanMacBtn.setOnClickListener(this);
        
        macAdress = (EditText)findViewById(R.id.macAdress);
        macAdress.setText((String)SPUtils.get(NetManagerActivity.this, "netMac",""));
        end_mac = (TextView)findViewById(R.id.end_mac);
        end_version = (TextView)findViewById(R.id.end_version);
        head_name = (TextView)findViewById(R.id.head_name);
        head_ip = (TextView)findViewById(R.id.head_ip);
        link_rssi = (TextView)findViewById(R.id.link_rssi);
        link_speed = (TextView)findViewById(R.id.link_speed);
        
        
	}
	private void showPopupWindow(View parent) {  
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        if(currentMore ==1)
        {
			int portNum = netDeviceInfo.endInfo.portNum;
			if(portNum>0)
			{
				for(int n=0;n<portNum;n++)
				{
					portList.add(String.valueOf(n));
				}
			}
        	view = layoutInflater.inflate(R.layout.netmanager_1, null);
			endOutRFLevelLabel = (TextView)view.findViewById(R.id.endRFLevelLabel);
        	end_pop_mac = (TextView)view.findViewById(R.id.end_pop_mac);
			end_pop_mac.setText(netDeviceInfo.endInfo.mac);
			end_pop_version = (TextView)view.findViewById(R.id.end_pop_version);
			end_pop_version.setText(netDeviceInfo.endInfo.verInfo);
			end_pop_outRFLevel =(TextView)view.findViewById(R.id.end_pop_outRFLevel);
			if (netDeviceInfo.endInfo.outRFLevel != null && !netDeviceInfo.endInfo.outRFLevel.equals("")){
				endOutRFLevelLabel.setVisibility(View.VISIBLE);
				end_pop_outRFLevel.setVisibility(View.VISIBLE);
				end_pop_outRFLevel.setText(netDeviceInfo.endInfo.outRFLevel);

			}else {
				endOutRFLevelLabel.setVisibility(View.INVISIBLE);
				end_pop_outRFLevel.setVisibility(View.INVISIBLE);
			}

			end_pop_portStat = (TextView)view.findViewById(R.id.end_pop_portStat);
			end_pop_portMode = (TextView)view.findViewById(R.id.end_pop_portMode);
			end_pop_portVlan = (TextView)view.findViewById(R.id.end_pop_portVlan);
			portMacCountLabel = (TextView)view.findViewById(R.id.portMacCountLabel);
			end_pop_portMacCount = (TextView)view.findViewById(R.id.end_pop_portMacCount);
			if(portNum>0)
			{
				end_pop_portStat.setText(netDeviceInfo.endInfo.port[0].stat);
				end_pop_portMode.setText(netDeviceInfo.endInfo.port[0].mode);
				end_pop_portVlan.setText(netDeviceInfo.endInfo.port[0].pvid);

				if (netDeviceInfo.endInfo.port[0].macCount != null){
					portMacCountLabel.setVisibility(View.VISIBLE);
					end_pop_portMacCount.setVisibility(View.VISIBLE);
					end_pop_portMacCount.setText(netDeviceInfo.endInfo.port[0].macCount);
				}else {
					portMacCountLabel.setVisibility(View.INVISIBLE);
					end_pop_portMacCount.setVisibility(View.INVISIBLE);
				}
			}
      	
        	portSpinner = (Spinner)view.findViewById(R.id.spinner_port);
        	portAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, portList);
        	portSpinner.setAdapter(portAdapter); 
        	portSpinner.setOnItemSelectedListener(
        			new OnItemSelectedListener() {
        				public void onItemSelected(AdapterView<?> parent, 
        					View view, int position, long id) {
        					end_pop_portStat.setText(netDeviceInfo.endInfo.port[position].stat);
        					end_pop_portMode.setText(netDeviceInfo.endInfo.port[position].mode);
        					end_pop_portVlan.setText(netDeviceInfo.endInfo.port[position].pvid);
							if (netDeviceInfo.endInfo.port[0].macCount != null){
								end_pop_portMacCount.setText(netDeviceInfo.endInfo.port[position].macCount);
							}
        				}
        				public void onNothingSelected(AdapterView<?> parent) {
        				}
        			});

        }
        if(currentMore ==2)
        {
        	view = layoutInflater.inflate(R.layout.netmanager_2, null);
        	TextView head_pop_host = (TextView)view.findViewById(R.id.head_pop_host);
        	TextView head_pop_ip = (TextView)view.findViewById(R.id.head_pop_ip);
        	TextView head_pop_version = (TextView)view.findViewById(R.id.head_pop_version);
        	TextView head_pop_mask = (TextView)view.findViewById(R.id.head_pop_mask);
        	TextView head_pop_gateway = (TextView)view.findViewById(R.id.head_pop_gateway);
        	TextView head_pop_vlan = (TextView)view.findViewById(R.id.head_pop_vlan);
        	TextView head_pop_runtime = (TextView)view.findViewById(R.id.head_pop_runtime);
        	
        	head_pop_host.setText(netDeviceInfo.headInfo.host);
        	head_pop_ip.setText(netDeviceInfo.headInfo.ip);
        	head_pop_version.setText(netDeviceInfo.headInfo.verInfo);
        	head_pop_mask.setText(netDeviceInfo.headInfo.mask);
        	head_pop_gateway.setText(netDeviceInfo.headInfo.gateway);
        	head_pop_vlan.setText(netDeviceInfo.headInfo.vlan);
        	head_pop_runtime.setText(netDeviceInfo.headInfo.runDuration);
        	
        	
        }
        if(currentMore ==3)
        {
        	view = layoutInflater.inflate(R.layout.netmanager_3, null);
        	TextView link_rssi = (TextView)view.findViewById(R.id.link_rssi);
        	TextView link_snr = (TextView)view.findViewById(R.id.link_snr);
        	TextView link_speed = (TextView)view.findViewById(R.id.link_speed);
        	TextView link_decay = (TextView)view.findViewById(R.id.link_decay);
        	
        	link_rssi.setText(netDeviceInfo.linkInfo.rssi);
        	link_snr.setText(netDeviceInfo.linkInfo.snr);
        	link_speed.setText(netDeviceInfo.linkInfo.upDownSpeed);
        	link_decay.setText(netDeviceInfo.linkInfo.decay);
        	
        	if(netDeviceInfo.endInfo.frequencyType.equals("L74"))
			{
        		if(isNumeric(netDeviceInfo.linkInfo.snr))
        		{
        			int snr = Integer.parseInt(netDeviceInfo.linkInfo.snr);
    				if(snr>=20)
    				{
    					
    				}
    				else if(snr>=15 && snr<20)
    				{
    					link_snr.setTextColor(Color.YELLOW);
    				}
    				else {
    					link_snr.setTextColor(Color.RED);
    				}
        		}
        		if(isNumeric(netDeviceInfo.linkInfo.decay))
        		{
        			int decay = Integer.parseInt(netDeviceInfo.linkInfo.decay);
    				if(decay<=40)
    				{
    					
    				}
    				else if(decay>40 && decay<60)
    				{
    					link_decay.setTextColor(Color.YELLOW);
    				}
    				else {
    					link_decay.setTextColor(Color.RED);
    				}
        		}
				if(netDeviceInfo.linkInfo.upDownSpeed.contains(","))
				{
					String[] ints = netDeviceInfo.linkInfo.upDownSpeed.split(",");
					int up = 0;
					int down = 0;
					try {
						if (isNumeric(ints[0])){
							up = Integer.parseInt(ints[0]);
						}
						if (isNumeric(ints[1])){
							down = Integer.parseInt(ints[1]);
						}
					}catch (NumberFormatException e){
						up = 0;
						down = 0;
					}
					if(up<down)
					{
						if(up>=400)
						{
							
						}
						else if(up>=175 && up<400)
						{
							link_speed.setTextColor(Color.YELLOW);
						}
						else{
							link_speed.setTextColor(Color.RED);
						}
					}
					else {
						if(down>=400)
						{
							
						}
						else if(down>=175 && down<400)
						{
							link_speed.setTextColor(Color.YELLOW);
						}
						else{
							link_speed.setTextColor(Color.RED);
						}
					}
				}
	
			}
			else if(netDeviceInfo.endInfo.frequencyType.equals("L64"))
			{
				if(isNumeric(netDeviceInfo.linkInfo.snr))
        		{
					int snr = Integer.parseInt(netDeviceInfo.linkInfo.snr);
					if(snr>=20)
					{
						
					}
					else if(snr>=15 && snr<20)
					{
						link_snr.setTextColor(Color.YELLOW);
					}
					else {
						link_snr.setTextColor(Color.RED);
					}
        		
        		}
				if(isNumeric(netDeviceInfo.linkInfo.decay))
				{
					int decay = 0;
					try {
						decay = Integer.parseInt(netDeviceInfo.linkInfo.decay);
					}catch (NumberFormatException e){
						decay = 0;
					}
					if(decay<=40)
					{
						
					}
					else if(decay>40 && decay<60)
					{
						link_decay.setTextColor(Color.YELLOW);
					}
					else {
						link_decay.setTextColor(Color.RED);
					}
				}
				
				if(netDeviceInfo.linkInfo.upDownSpeed.contains(","))
				{
					String[] ints = netDeviceInfo.linkInfo.upDownSpeed.split(",");
					int up = 0;
					int down = 0;
					try {
						if (isNumeric(ints[0])){
							up = Integer.parseInt(ints[0]);
						}
						if (isNumeric(ints[1])){
							down = Integer.parseInt(ints[1]);
						}
					}catch (NumberFormatException e){
						up = 0;
						down = 0;
					}
					if(up<down)
					{
						if(up>=160)
						{
							
						}
						else if(up>=70 && up<160)
						{
							link_speed.setTextColor(Color.YELLOW);
						}
						else{
							link_speed.setTextColor(Color.RED);
						}
					}
					else {
						if(down>=160)
						{
							
						}
						else if(down>=70 && down<160)
						{
							link_speed.setTextColor(Color.YELLOW);
						}
						else{
							link_speed.setTextColor(Color.RED);
						}
					}
				}
				
			}
			else if(netDeviceInfo.endInfo.frequencyType.equals("H"))
			{
				if(isNumeric(netDeviceInfo.linkInfo.rssi))
				{
					int rssi = Integer.parseInt(netDeviceInfo.linkInfo.rssi);
					if(rssi>=30 && rssi<=70)
					{
						
					}
					else if((rssi>=20 && rssi<30) || (rssi>70 && rssi<=80))
					{
						link_rssi.setTextColor(Color.YELLOW);
					}
					else {
						link_rssi.setTextColor(Color.RED);
					}
				}
				
				if(isNumeric(netDeviceInfo.linkInfo.decay))
				{
					int decay = 0;
					try {
						decay = Integer.parseInt(netDeviceInfo.linkInfo.decay);
					}catch (NumberFormatException e){
						decay = 0;
					}
					if(decay>=25 && decay<=65)
					{
						
					}
					else if((decay>=65 && decay<=75) || (decay>=15 && decay<=25))
					{
						link_decay.setTextColor(Color.YELLOW);
					}
					else {
						link_decay.setTextColor(Color.RED);
					}
				}
				if(netDeviceInfo.linkInfo.upDownSpeed.contains(","))
				{
					String[] ints = netDeviceInfo.linkInfo.upDownSpeed.split(",");
					int up = 0;
					int down = 0;
					try {
						if (isNumeric(ints[0])){
							up = Integer.parseInt(ints[0]);
						}
						if (isNumeric(ints[1])){
							down = Integer.parseInt(ints[1]);
						}
					}catch (NumberFormatException e){
						up = 0;
						down = 0;
					}

					if(up<down)
					{
						if(up>=121)
						{
							
						}
						else if(up>=81 && up<121)
						{
							link_speed.setTextColor(Color.YELLOW);
						}
						else{
							link_speed.setTextColor(Color.RED);
						}
					}
					else {
						if(down>=121)
						{
							
						}
						else if(down>=81 && down<121)
						{
							link_speed.setTextColor(Color.YELLOW);
						}
						else{
							link_speed.setTextColor(Color.RED);
						}
					}
				}
			}
        	
        }
        if(currentMore ==4)
        {
        	view = layoutInflater.inflate(R.layout.netmanager_4, null);
        }
            
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);  
        popupWindow.setOutsideTouchable(true);  
        Button miss = (Button)view.findViewById(R.id.dismiss);
        miss.setOnClickListener(this);
        popupWindow.showAsDropDown(parent, 0, 10);  
   
    } 
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int viewId = arg0.getId();  
		switch (viewId) {
		case R.id.top_back:
			this.finish();
			break;
		case R.id.scanMacBtn:
			MyLog.e(TAG, "scan");
			MyProgressDialog.show(this, progressDialog, "正在查找", false);
			getNetManagerInfo();
			break;
		case R.id.more1:
			if (getInfo) 
			{
				currentMore = 1;
				LinearLayout layout1 = (LinearLayout)findViewById(R.id.macInput);
				showPopupWindow(layout1);
			}
			else {
				Toast.makeText(NetManagerActivity.this, "未获取网管信息", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.more2:
			if (getInfo) 
			{
				currentMore = 2;
				LinearLayout layout2 = (LinearLayout)findViewById(R.id.net_1);
				showPopupWindow(layout2);
			}
			else {
				Toast.makeText(NetManagerActivity.this, "未获取网管信息", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.more3:
			if (getInfo) 
			{
				currentMore = 3;
				LinearLayout layout3 = (LinearLayout)findViewById(R.id.net_2);
				showPopupWindow(layout3);
			}
			else {
				Toast.makeText(NetManagerActivity.this, "未获取网管信息", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.more4:
			if (getInfo) 
			{
				Intent intent2 = new Intent(this, NetWarningActivity.class);
				intent2.putExtra("mac", currentMac);
				startActivity(intent2);
			}
			else {
				Toast.makeText(NetManagerActivity.this, "未获取网管信息", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.dismiss:
			if(portList.size()>0)
			{
				portList.clear();
			}
			popupWindow.dismiss();
			break;
		case R.id.top_esc:
			Intent intent = new Intent(this,CaptureActivity.class);
			startActivityForResult(intent, 0);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {	
		if(resultCode == RESULT_OK){	
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");				
			macAdress.setText(scanResult);
		}
	}
	
	public void getNetManagerInfo()
	{
		//构造MySharedPreferencesUtils
//		Boolean loginFlag =(Boolean)MySharedPreferencesUtils.get("loginFlag", false);
//		if(!loginFlag)
//		{
//			MyLog.e(TAG, "未登录");
//			MyProgressDialog.dismiss();
//			CommonUtils.showTips(NetManagerActivity.this, "提示", "未登入");
//			return;
//		}
		String mac = macAdress.getText().toString();
		if(isChineseChar(mac) || !checkMac(mac))
		{
			MyLog.e(TAG, "MAC地址错误");
			MyProgressDialog.dismiss();
			CommonUtils.showTips(NetManagerActivity.this, "提示", "MAC地址错误");
			return;
		}
		
        MySharedPreferencesUtils.getInstance(NetManagerActivity.this, "share_data");
  
        //设备升级文件
        StringBuilder data = new StringBuilder(256);
        data.append("userName=");
        data.append(MySharedPreferencesUtils.get("userName", "root")); // TODO: 2016/4/27 用户名获取
		data.append("&mac=");
        data.append(currentMac);
        MyLog.e(TAG, "work data: " + data.toString());
        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
//        String url = Constants.URL_NMS_INFO;
        
        String ip = (String)SPUtils.get(NetManagerActivity.this, "serverIp","");
		String port = (String)SPUtils.get(NetManagerActivity.this, "serverPort","");
		String url = "http://"+ip+":"+port+"/"+ AppConfig.EMSWAP_DEPLOY_NAME+"/"+"app?queryDeviceInfo&ct=";
        
        url = url + data03;
        MyLog.e(TAG, "work url :" + url);
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        HttpService httpService = new HttpService(this);
//        httpService.get(url, this, null);
        httpService.getForWarning(url, this, null);
	}
	
//	public void getWarningInfo()
//	{
//		//构造MySharedPreferencesUtils
//		Boolean loginFlag =(Boolean)MySharedPreferencesUtils.get("loginFlag", false);
//		if(!loginFlag)
//		{
//			MyLog.e(TAG, "未登录");
//			MyProgressDialog.dismiss();
//			CommonUtils.showTips(NetManagerActivity.this, "提示", "未登入");
//			return;
//		}
//		String mac = macAdress.getText().toString();
//		if(isChineseChar(mac) || !checkMac(mac))
//		{
//			MyLog.e(TAG, "MAC地址错误");
//			MyProgressDialog.dismiss();
//			CommonUtils.showTips(NetManagerActivity.this, "提示", "MAC地址错误");
//			return;
//		}
//		
//        MySharedPreferencesUtils.getInstance(NetManagerActivity.this, "share_data");
//        //设备升级文件
//        StringBuilder data = new StringBuilder(256);
//        data.append("userName=");
//        data.append(MySharedPreferencesUtils.get("userName", ""));
//        data.append("&mac=");
//        data.append(currentMac);
//        data.append("&startTime=");
//        data.append("2015-08-17 13:00:00");
//        data.append("&endTime=");
//        data.append("2015-10-17 13:00:00");
//        data.append("&num=");
//        data.append("");
//        MyLog.e(TAG, "work data: " + data.toString());
//        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
//        String url = Constants.URL_WARNING_INFO;
//        url = url + data03;
//        MyLog.e(TAG, "work url :" + url);
//        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败
//
//        HttpService httpService = new HttpService(this);
//        httpService.get(url, this, null);
//	}

	@Override
	public void getResult(String result, Object objParam) {
		// TODO Auto-generated method stub
		MyLog.e(TAG, result);
		MyProgressDialog.dismiss();
		 Map<String, Object> map = null;
         try {
             map = MapUtils.parseObjectData(result);
         } catch (Exception e) {
             e.printStackTrace();
         } 
          
         if(map != null)
         {
         	if(map.containsKey("returnCode"))
         	{
         		String returnCode =(String)map.get("returnCode");
         		if(returnCode.equals("SUCCESS"))
         		{
         			parseImageData(result);
         		}
//         		else if(returnCode.equals("WAIT"))
//         		{
//         			getNetManagerInfo();
//         		}
         		else {
         			CommonUtils.showTips(NetManagerActivity.this, "提示", (String)map.get("returnMsg"));
				}
         		
         		
         	}
         	else {
         		CommonUtils.showTips(NetManagerActivity.this, "提示", "查找失败");
         	}
         }
         else {
         	CommonUtils.showTips(NetManagerActivity.this, "提示", "查找失败");
         }
	}
	
	
	private void parseImageData(String result) {
    	
    	Gson gson = new Gson();
		try {
			netDeviceInfo = gson.fromJson(result, NetDeviceInfo.class);
        } catch (JsonSyntaxException e) {
        	e.printStackTrace();
        }
		if (null != netDeviceInfo) {
			MyLog.e("parseImageData", "一层JSON解析成功");
			handler.sendEmptyMessage(GET_DEVICEINFO_SUCCEED);
		}
		else {
			MyLog.e("parseImageData", "一层JSON解析失败");
			handler.sendEmptyMessage(GET_DEVICEINFO_FAILED);
		}
	}
	
	public boolean isChineseChar(String str){
	       boolean temp = false;
	       Pattern p=Pattern.compile("[\u4e00-\u9fa5]");
	       Matcher m=p.matcher(str);
	       if(m.find()){
	           temp =  true;
	       }
	       return temp;
	   }
	
	public boolean checkMac(String str){
		str = str.toUpperCase();
	    boolean temp = false;
	    if(str.length() == 12)
	    {
		   String str1 = str.substring(0, 2);
		   String str2 = "-"+str.substring(2, 4);
		   String str3 = "-"+str.substring(4, 6);
		   String str4 = "-"+str.substring(6, 8);
		   String str5 = "-"+str.substring(8, 10);
		   String str6 = "-"+str.substring(10, 12);
		   currentMac = str1+str2+str3+str4+str5+str6;
		   temp = true;
	    }
	    return temp;
	}
	
	public boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}
		
}

