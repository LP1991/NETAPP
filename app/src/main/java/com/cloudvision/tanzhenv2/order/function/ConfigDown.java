package com.cloudvision.tanzhenv2.order.function;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.util.MyLog;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 探针的配置文件下载类
 * Http上传探针的MAC后，的到探针配置文件存于本地根目录下。
 *
 * Created by 谭智文
 */
public class ConfigDown implements HttpServiceInterface {
	
	private static final String TAG = "ConfigDown";
	
	private Context context;
	private String fileName = "config111.xml";
	private String configURL = Environment.getExternalStorageDirectory().getPath()+ fileName;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ConfigDown(Context context) {
		this.context = context;
		http();
	}

	private void http() {
		
		StringBuilder data = new StringBuilder(256);
        data.append("probeMac=");
//        data.append(Constants.probeMac);

        MyLog.i(TAG, "config data: " + data.toString());
        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_CONFIG;
        url = url + data03;
        MyLog.i(TAG, "config url :" + url);
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        HttpService httpService = new HttpService(context);
        httpService.get(url, this, null);
	}
	
	@Override
	public void getResult(String result, Object objParam) {

        MyLog.e(TAG, "result: " + result);
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        try{

            File file = new File(configURL);
            if(!file.exists()){//判断文件是否存在（不存在则创建这个文件）
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte [] bytes = result.getBytes();
            fos.write(bytes);
            fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
	
}
