package com.cloudvision.tanzhenv2.order.function;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.widget.Toast;

import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.util.MyLog;
import com.cloudvision.util.SPUtils;

import java.util.Map;

/**
 * 签到签出功能
 * 
 * Created by 谭智文
 */
public class SignInAndOut {
	
	private static final String TAG = "SignF";
	private String probeMac = "AA:BB:CC:CC:BB:DD";
	
	public String getProbeMac() {
		return probeMac;
	}
	public void setProbeMac(String probeMac) {
		this.probeMac = probeMac;
	}

	private Context context;
	private Thread updateCurrentTime = null;	//信息上报
	private Handler handler = new Handler();
	
	private boolean upFlag = true;      //默认上传
	private boolean signFlag = false;   //默认未签到
	
	public refreshCallbackImpl bCallback;

    public void setCallfuc(refreshCallbackImpl bCallback){
        this.bCallback= bCallback;
        sign();
    }
    public void call(){
        this.bCallback.refreshTip();
    }

	public SignInAndOut(Context context){
		this.context = context;
	}
	
	/**
     * 签到签出
     */
    private void sign() {
    	
    	signFlag = (Boolean) MySharedPreferencesUtils.get("signFlag", false);
        if (signFlag) {
            showTips();
        } else {
            signHttp();
        }    
    }
    
    /**
     * 签出警告提示
     */
    private void showTips() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("签出");
        builder.setMessage("确定签出？");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                signHttp();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }
    
    /**
     * 发送前到签出GET请求
     */
    private void signHttp() {

        StringBuilder data = new StringBuilder(256);
        data.append("userName=");
        data.append(MySharedPreferencesUtils.get("userName", ""));
        
        Boolean version = (Boolean)SPUtils.get(context, "versionFlag",false);
		if(!version)
		{
			CommonUtils.showTips(context, "提示", "未获取设备信息");
			return;
		}

        if (!signFlag) {
        	
        	data.append("&probeMac=");
//            data.append(probeMac);
        	data.append(SPUtils.get(context, "probeMac",""));

            MyLog.i(TAG, "sign in data: " + data.toString());
            String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
            String url = Constants.URL_SIGNIN;
            url = url + data03;
            MyLog.i(TAG, "sign in url :" + url);
            url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

            HttpService httpService = new HttpService(context);
            httpService.get(url, signInResult, null);

        } else {

            MyLog.i(TAG, "sign out data: " + data.toString());
            String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
            String url = Constants.URL_SIGNOUT;
            url = url + data03;
            MyLog.i(TAG, "sign out url :" + url);

            HttpService httpService = new HttpService(context);
            httpService.get(url, signOutResult, null);
        }
    }
    
    /**
     * 获取签到返回值
     */
    private HttpServiceInterface signInResult = new HttpServiceInterface() {
        @Override
        public void getResult(String result, Object objParam) {
            MyLog.e(TAG, "result: " + result);

            String value;
            Map<String, String> map = null;
            try {
                map = MapUtils.parseData(result);
            } catch (Exception e) {
                Toast.makeText(context, "网络问题，签到失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            if (map != null) {
                value = map.get("returnCode");
                if (value.equals("SUCCESS")) {
                    upData();		//定时数据上传线程开始
                    MySharedPreferencesUtils.put("upFlag", true);
                    Toast.makeText(context, "签到成功", Toast.LENGTH_SHORT).show();
                    MySharedPreferencesUtils.put("signFlag", true);
                    MyBDLocation.startBaidu();
                    call();			//callback更新UI
                } else {
                    Toast.makeText(context,  map.get("returnMsg"), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    
    /**
     * 获取签出返回值
     */
    private HttpServiceInterface signOutResult = new HttpServiceInterface() {
        @Override
        public void getResult(String result, Object objParam) {
            MyLog.e(TAG, "result: " + result);

            String value;
            Map<String, String> map = null;
            try {
                map = MapUtils.parseData(result);
            } catch (Exception e) {
                Toast.makeText(context, "网络问题，签出失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            if (map != null) {
                value = map.get("returnCode");
                if (value.equals("SUCCESS")) {
                    Toast.makeText(context, "签出成功", Toast.LENGTH_SHORT).show();
                    MySharedPreferencesUtils.put("upFlag", false);
                    handler.removeCallbacks(updateCurrentTime);
                    MySharedPreferencesUtils.put("signFlag", false);
                    MyBDLocation.stopLocation();
                    call();			//callback更新UI
                } else {
                    Toast.makeText(context,  map.get("returnMsg"), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    
    /**
     * 定时信息上报
     * （签到时开始，签出时结束，是否退出APP无关）
     */
    public void upData() {

        updateCurrentTime = new Thread() {
            @Override
            public void run() {

//            	while(probe.ssm.bGetVersion)
//            	{
            		double longtitude = MyBDLocation.getLongtitude();
                    double Latitude = MyBDLocation.getLatitude();

//                    StringBuilder data = new StringBuilder(256);
//                    data.append("userName=");
//                    data.append(MySharedPreferencesUtils.get("userName", ""));
//                    data.append("&probeMac=");
//                    data.append(probeMac);
//                    data.append("&version=");
//                    data.append(Constants.version);
//                    data.append("&gpsX=");
//                    data.append(longtitude);
//                    data.append("&gpsY=");
//                    data.append(Latitude);
//                    data.append("&EocSw=");
//                    data.append(Constants.EocSw);
//                    data.append("&CatvSw=");
//                    data.append(Constants.CatvSw);
//                    data.append("&McuSw=");
//                    data.append(Constants.McuSw);
//                    data.append("&RouterSw=");
//                    data.append(Constants.RouterSw);
                    
                    StringBuilder data = new StringBuilder(256);
                    data.append("userName=");
                    data.append(MySharedPreferencesUtils.get("userName", ""));

                    data.append("&version=");
                    data.append(Constants.version);
                    data.append("&gpsX=");
                    data.append(longtitude);
                    data.append("&gpsY=");
                    data.append(Latitude);

                    MyLog.e(TAG, "UP data: " + data.toString());
                    
                    upFlag = (Boolean) MySharedPreferencesUtils.get("upFlag", false);
                    if (upFlag)
                    {
                    	MyLog.e(TAG, "开始上报探针信息");
                        if (longtitude != 0 && Latitude != 0) {
                            String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
                            String url = Constants.URL_UPDATA;
                            url = url + data03;
                            MyLog.i(TAG, "sign in url :" + url);

                            url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败
                            HttpService httpService = new HttpService(context);
                            httpService.get(url, updataResult, null);
                            handler.postDelayed(this, Constants.INFO_UPDATA_TIME);
                        } else {
//                            handler.postDelayed(this, 10 * 1000); //失败则10s重试
                        	MyLog.e(TAG, "未获取GPS信息，无法上报");
                        }
                    }
                    else {
//                    	MyLog.e(TAG, "信息不全无法上报");
//                    	handler.postDelayed(this, Constants.INFO_UPDATA_TIME);
					}
                }
//            	}
//            	double longtitude = MyBDLocation.getLongtitude();
//                double Latitude = MyBDLocation.getLatitude();
//
//                StringBuilder data = new StringBuilder(256);
//                data.append("userName=");
//                data.append(MySharedPreferencesUtils.get("userName", ""));
//                data.append("&probeMac=");
//                data.append(probeMac);
//                data.append("&version=");
//                data.append(Constants.version);
//                data.append("&gpsX=");
//                data.append(longtitude);
//                data.append("&gpsY=");
//                data.append(Latitude);
//                data.append("&EocSw=");
//                data.append(Constants.EocSw);
//                data.append("&CatvSw=");
//                data.append(Constants.CatvSw);
//                data.append("&McuSw=");
//                data.append(Constants.McuSw);
//                data.append("&RouterSw=");
//                data.append(Constants.RouterSw);
//                
//                MyLog.i(TAG, "UP data: " + data.toString());
//                
//                upFlag = (Boolean) MySharedPreferencesUtils.get("upFlag", false);
//                if (upFlag) {
//                    if (longtitude != 0 && Latitude != 0) {
//                        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
//                        String url = Constants.URL_UPDATA;
//                        url = url + data03;
//                        MyLog.i(TAG, "sign in url :" + url);
//
//                        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败
//                        HttpService httpService = new HttpService(context);
//                        httpService.get(url, updataResult, null);
//
//                        handler.postDelayed(this, Constants.INFO_UPDATA_TIME);
//                    } else {
//                        handler.postDelayed(this, 10 * 1000); //失败则10s重试
//                    }
//                }
//            }
        };
        updateCurrentTime.start();
    }
    
    private HttpServiceInterface updataResult = new HttpServiceInterface() {
        @Override
        public void getResult(String result, Object objParam) {
            MyLog.e(TAG, "result: " + result);
        }
    };
    
}
