package com.cloudvision.tanzhenv2.order.function;

import android.content.Context;
import android.widget.Toast;

import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.util.MyLog;
import com.google.gson.JsonSyntaxException;

import java.util.Map;

public class UpgradeQuery implements HttpServiceInterface{

private static final String TAG = "UpgradeQuery";
	
	private String[] deviceTypeStrings = new String[]{"EOC","ROUTER","CATV","MCU"};
	
	private String probeMac = "CC:AA:CC:AA:CC:AA";

	private Context context;
	
	private String deviceSw;		//查询到的最新软件版本
	
	public String getDeviceSw() {
		return deviceSw;
	}

	public void setDeviceSw(String deviceSw) {
		this.deviceSw = deviceSw;
	}
	
	public UpgradeQuery(Context context) {
		this.context = context;
		http();
	}
	
	private void http() {
		
		StringBuilder data = new StringBuilder(256);
		data.append("userName=");
        data.append(MySharedPreferencesUtils.get("userName", ""));
        data.append("&deviceType=");			//设备类型
        data.append(deviceTypeStrings[0]);
        data.append("&probeMac=");			//探针MAC
        data.append(probeMac);				
        
        MyLog.i(TAG, "upgrade data: " + data.toString());
        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_UPGRADEQUERY;
        url = url + data03;
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败
        MyLog.i(TAG, "upgrade url :" + url);

        HttpService httpService = new HttpService(context);
        httpService.get(url, this, null);
	}
	
	@Override
	public void getResult(String result, Object objParam) {

        MyLog.e(TAG, "result: " + result);
        
        Map<String, String> map = null;
        try {
        	map = MapUtils.parseData(result);
        } catch (JsonSyntaxException e) {
            Toast.makeText(context, "网络问题，获取失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        
        if (map != null) {
        	deviceSw = map.get("returnCode");
        	Toast.makeText(context, "最新版本为"+deviceSw, Toast.LENGTH_SHORT).show();
        }        
    }
}
