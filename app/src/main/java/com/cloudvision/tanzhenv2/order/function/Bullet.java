package com.cloudvision.tanzhenv2.order.function;

import android.content.Context;

import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.util.MyLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * 公告获取并保存功能
 *
 * Created by 谭智文
 */
public class Bullet implements HttpServiceInterface {

    private static final String TAG = "Bullet";
    private static String bulletInfo = "";

    public refreshCallbackImpl bCallback;

    public void setCallfuc(refreshCallbackImpl bCallback){
        this.bCallback= bCallback;
        bulletinHttp();
    }
    public void call(){
        this.bCallback.refreshTip();
    }

    public static String getBulletInfo() {
        bulletInfo = (String) MySharedPreferencesUtils.get("bulletInfo", bulletInfo);
        return bulletInfo;
    }

    private Context context;

    public Bullet(Context context) {
        this.context = context;
    }

    /**
     * 发送前到公告GET请求
     */
    private void bulletinHttp() {

        long currentTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        Date currentDate = new Date(currentTime);
        String sCurrentDate = format.format(currentDate);
        StringBuilder data = new StringBuilder(256);
        data.append("currentDate=");
        data.append(sCurrentDate);

        MyLog.i(TAG, "currentDate: " + data.toString());
        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_BULLETIN;
        url = url + data03;
        MyLog.i(TAG, "currentDate url :" + url);
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        HttpService httpService = new HttpService(context);
        httpService.get(url, this , null);
    }

    @Override
    public void getResult(String result, Object objParam) {

        MyLog.e(TAG, "result: " + result);

        Gson gson = new Gson();
        List<Map<String, String>> maps = null;
        StringBuilder bulletinInfo = new StringBuilder(1024);

        try {
//            maps = new ArrayList<>();
            maps = gson.fromJson(result, new TypeToken<List<Map<String, String>>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (maps != null) {
            for (Map<String, String> map : maps) {
                bulletinInfo.append("[");
                bulletinInfo.append(map.get("bulletinTitle"));
                bulletinInfo.append("]");
                bulletinInfo.append(map.get("bulletinContent"));
                bulletinInfo.append("      ");
            }
            bulletInfo = bulletinInfo.toString();
            MySharedPreferencesUtils.put("bulletInfo", bulletInfo);
            MyLog.i(TAG, "bulletinInfo data: " + bulletInfo);
            call();
        }
    }
}
