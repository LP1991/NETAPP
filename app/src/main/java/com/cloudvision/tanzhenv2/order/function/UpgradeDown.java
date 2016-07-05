package com.cloudvision.tanzhenv2.order.function;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpDownFile;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.util.MyLog;

/**
 * 探针升级文件下载请求
 *
 * Created by 谭智文
 */
public class UpgradeDown implements HttpServiceInterface {
	
	private static final String TAG = "UpgradeDown";
	
	private String[] deviceTypeStrings =  new String[]{"EOC","ROUTER","CATV","MCU"};
	
	private String probeMac = "CC:AA:CC:AA:CC:AA";
	
	private String fileName = Environment.getExternalStorageDirectory().getPath()+"/upgrade";

	private Context context;
	
	public UpgradeDown(Context context) {
		this.context = context;
		http();
	}
	
	private void http() {
		
		StringBuilder data = new StringBuilder(256);
		data.append("userName=");
        data.append(MySharedPreferencesUtils.get("userName", ""));
        data.append("&deviceType=");			//设备类型
        data.append(deviceTypeStrings[3]);
        data.append("&probeMac=");			//探针MAC
        data.append(probeMac);
        data.append("&deviceSw=");			//设备软件版本
        data.append(Constants.McuSw);
        
        MyLog.i(TAG, "upgrade data: " + data.toString());
        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_UPGRADEDOWN;
        url = url + data03;
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败
        MyLog.i(TAG, "upgrade url :" + url);

//        HttpService httpService = new HttpService(context);
//        httpService.get(url, this, null);
        HttpDownFile httpDownFile = new HttpDownFile(context);
        MyLog.e(TAG,fileName);
        httpDownFile.get(url, fileName, this, null);
        
	}
	
	@Override
	public void getResult(String result, Object objParam) {

        MyLog.e(TAG, "result: " + result);
        if(result.equals("code404")){
        	Toast.makeText(context, "获取失败，此版本不存在", Toast.LENGTH_SHORT).show();
        }else{
        	Toast.makeText(context, "文件已下载于"+fileName, Toast.LENGTH_SHORT).show();
        }
    }
}
