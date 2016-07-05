package com.cloudvision.tanzhenv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudvision.appconfig.AppConfig;
import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.TestBaseActivity;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.util.MyLog;
import com.cloudvision.util.SPUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerIpPortActivity extends TestBaseActivity implements OnClickListener{
	
	private String TAG = "ServerIpPortActivity";
	private EditText ipEditText,portEditText;
	private Button saveButton;
	private String ip,port,webAddress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_server_ip);
		initData();
		initView();
		Intent intent = getIntent();
		if (intent != null){
			String title = intent.getStringExtra("title");
			String msg = intent.getStringExtra("msg");
			if (title != null && msg != null){
				CommonUtils.showTips(ServerIpPortActivity.this, title, msg);
			}
		}
	}
	
	public void initData()
	{
		ip = (String)SPUtils.get(ServerIpPortActivity.this, "serverIp","");
		port = (String)SPUtils.get(ServerIpPortActivity.this, "serverPort","");
	}
	
	public void initView()
	{
		titltView = (TextView)findViewById(R.id.top_title);
        titltView.setText("服务器设置");
        backTextView = (TextView)findViewById(R.id.top_back);
        backTextView.setOnClickListener(this);
        device_state_btn = (ImageButton)findViewById(R.id.device_state);
        device_state_btn.setVisibility(View.GONE);
        
        ipEditText = (EditText)findViewById(R.id.ip);
        ipEditText.setText(ip);
        portEditText = (EditText)findViewById(R.id.port);
        portEditText.setText(port);
        saveButton = (Button)findViewById(R.id.saveBtn);
        saveButton.setOnClickListener(this);
	}
	
	public boolean saveIpPort()
	{
		ip = ipEditText.getText().toString();
		port = portEditText.getText().toString();
		if(!isIPAddress(ip))
		{
			Toast.makeText(ServerIpPortActivity.this, "请输入正确的IP", Toast.LENGTH_SHORT).show();
			return false;

		}else if (!checkPort(port)){
			return false;
		}
		else {
			webAddress = "http://"+ip+":"+port+"/"+AppConfig.EMSWAP_DEPLOY_NAME+"/";
			MyLog.e("error", webAddress);
			if (testServerConnect()){
				SPUtils.put(ServerIpPortActivity.this, "serverIp", ip);
				SPUtils.put(ServerIpPortActivity.this, "serverPort", port);
				MyLog.e("", webAddress);
				Intent intent = new Intent();
				intent.putExtra("webAddress",webAddress);
				setResult(RESULT_OK,intent);
				this.finish();
				return true;
			}else {
				return false;
			}

		}
	}

	/**
	 * 检查端口号
	 * @param port String类型端口号
	 * @return true or false
     */
	private boolean checkPort(String port){
		if (port == null || "".equals(port)){
			return false;
		}
		int lPort = Integer.valueOf(port);
		if (lPort > 65535 || lPort < 0){
			Toast.makeText(ServerIpPortActivity.this, "端口号范围0-65535", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int viewId = arg0.getId();  
		switch (viewId) {
		case R.id.top_back:
			setResult(RESULT_CANCELED);
			this.finish();
			break;
		case R.id.saveBtn:
			saveIpPort();
//			Intent intent = new Intent(ServerIpPortActivity.this,NetManagerActivity.class);
//			webAddress = "http://"+ip+":"+port+"/"+ AppConfig.EMSWAP_DEPLOY_NAME+"/";
//			intent.putExtra("webAddress",webAddress);
//			startActivity(intent);
			break;
		default:
			break;
		}
	}
	/**
	 *
	 *isIPAddress <br/>
	 *检查是否是ip <br/>
	 *@param ipaddr String
	 *@return boolean true:是ip；false:不是ip
	 */
	public static boolean isIPAddress(String ipaddr) {
		if (ipaddr == null || ipaddr.trim().isEmpty()){
			return false;
		}
		boolean flag = false;
		Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher m = pattern.matcher(ipaddr);
		flag = m.matches();
		return flag;
	}

	/**
	 * 检查服务器连接
	 * @return true or false
     */
	private boolean testServerConnect(){
		//CommonUtils.showTips(ServerIpPortActivity.this, "连接服务器失败", "网络错误");
		return true;
	}
}


