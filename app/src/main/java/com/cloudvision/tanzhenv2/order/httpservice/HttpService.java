package com.cloudvision.tanzhenv2.order.httpservice;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.cloudvision.tanzhenv2.order.http.CustomHttpClient;
import com.cloudvision.tanzhenv2.order.http.CustomHttpConstant;


public class HttpService {
    final int LOG_LEVEL = Log.WARN;
    final String TAG = "HttpPostData";

    AsyncTaskPool abTaskPool;
    Context context;
    AsyncTaskCallback taskGetVideoDataItemCallback = new AsyncTaskCallback() {

        @Override
        public boolean get(Object oparam) {
            HttpCallBackObj param = (HttpCallBackObj) oparam;

            String jsonString;

            long tick1 = System.currentTimeMillis();
            jsonString = CustomHttpClient.requestForString(context, param.url,
                    param.submitMap, param.method, "utf-8",
                    System.currentTimeMillis());
            long tick2 = System.currentTimeMillis();
            Log.println(LOG_LEVEL, TAG, "yused: " + param.url + ", " + (tick2 - tick1));

            param.strResult = jsonString;
            return false;
        }

        @Override
        public void update(Object oparam) {
            HttpCallBackObj param = (HttpCallBackObj) oparam;
            String jsonString = (String) param.strResult;

            param.callbackItr.getResult(jsonString, param.callbackParam);
        }
    };
    
    AsyncTaskCallback taskGetVideoDataItemCallbackForWarning = new AsyncTaskCallback() {

        @Override
        public boolean get(Object oparam) {
            HttpCallBackObj param = (HttpCallBackObj) oparam;

            String jsonString;

            long tick1 = System.currentTimeMillis();
            jsonString = CustomHttpClient.requestForStringWarning(context, param.url,
                    param.submitMap, param.method, "utf-8",
                    System.currentTimeMillis());
            long tick2 = System.currentTimeMillis();
            Log.println(LOG_LEVEL, TAG, "yused: " + param.url + ", " + (tick2 - tick1));

            param.strResult = jsonString;
            return false;
        }

        @Override
        public void update(Object oparam) {
            HttpCallBackObj param = (HttpCallBackObj) oparam;
            String jsonString = (String) param.strResult;

            param.callbackItr.getResult(jsonString, param.callbackParam);
        }
    };

    public HttpService(Context context) {
        this.context = context;
        abTaskPool = AsyncTaskPool.getInstance();
    }

    public static String getCaller() {
        String str = "" + new Throwable().getStackTrace()[2].getMethodName()
                + ":" + new Throwable().getStackTrace()[2].getLineNumber();

        return str;
    }

    public void get(String url, HttpServiceInterface getItr, Object getParam) {
        String tag = getCaller();
        Log.println(LOG_LEVEL, tag, "httpget: " + url);

        HttpCallBackObj param = new HttpCallBackObj();
        param.url = url;
        param.callbackItr = getItr;
        param.callbackParam = getParam;
        param.submitMap = new HashMap<String, String>();
        param.method = CustomHttpConstant.GET;

        AsyncTaskItem taskItem = new AsyncTaskItem();
        taskItem.param = param;
        taskItem.callback = taskGetVideoDataItemCallback;
        abTaskPool.execute(taskItem);
    }
    
    public void getForWarning(String url, HttpServiceInterface getItr, Object getParam) {
        String tag = getCaller();
        Log.println(LOG_LEVEL, tag, "httpget: " + url);

        HttpCallBackObj param = new HttpCallBackObj();
        param.url = url;
        param.callbackItr = getItr;
        param.callbackParam = getParam;
        param.submitMap = new HashMap<String, String>();
        param.method = CustomHttpConstant.GET;

        AsyncTaskItem taskItem = new AsyncTaskItem();
        taskItem.param = param;
        taskItem.callback = taskGetVideoDataItemCallbackForWarning;
        abTaskPool.execute(taskItem);
    }

    public void post(String url, Map<String, String> submitMap, HttpServiceInterface getItr, Object getParam) {
        String tag = getCaller();
        Log.println(LOG_LEVEL, tag, "httppost: " + url);

        HttpCallBackObj param = new HttpCallBackObj();
        param.url = url;
        param.callbackItr = getItr;
        param.callbackParam = getParam;
        param.submitMap = submitMap;
        param.method = CustomHttpConstant.POST;

        AsyncTaskItem taskItem = new AsyncTaskItem();
        taskItem.param = param;
        taskItem.callback = taskGetVideoDataItemCallback;
        abTaskPool.execute(taskItem);
    }

    public class HttpCallBackObj {
        public HttpServiceInterface callbackItr;
        public String url;
        public String method;
        public String strResult;
        public Object callbackParam;
        Map<String, String> submitMap;
    }
}
