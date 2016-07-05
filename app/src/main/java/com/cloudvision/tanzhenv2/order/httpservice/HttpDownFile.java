package com.cloudvision.tanzhenv2.order.httpservice;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.cloudvision.tanzhenv2.order.http.CustomHttpConstant;


public class HttpDownFile {
    final int LOG_LEVEL = Log.WARN;

    AsyncTaskPool abTaskPool;
    Context context;
    AsyncTaskCallback taskGetVideoDataItemCallback = new AsyncTaskCallback() {

        @Override
        public boolean get(Object oparam) {
            HttpCallBackObj param = (HttpCallBackObj) oparam;

            String len = HttpDownFileSync.downloadFile(param.url, param.fileName);
            param.strResult = "" + len;
            return false;
        }

        @Override
        public void update(Object oparam) {
            HttpCallBackObj param = (HttpCallBackObj) oparam;
            String jsonString = (String) param.strResult;

            param.callbackItr.getResult(jsonString, param.callbackParam);
        }
    };

    public HttpDownFile(Context context) {
        this.context = context;
        abTaskPool = AsyncTaskPool.getInstance();
    }

    public static String getCaller() {
        String str = "" + new Throwable().getStackTrace()[2].getMethodName()
                + ":" + new Throwable().getStackTrace()[2].getLineNumber();

        return str;
    }

    public void get(String url, String fileName, HttpServiceInterface getItr, Object getParam) {
        String tag = getCaller();
        Log.println(LOG_LEVEL, tag, "httpget: " + url);

        HttpCallBackObj param = new HttpCallBackObj();
        param.url = url;
        param.fileName = fileName;
        param.callbackItr = getItr;
        param.callbackParam = getParam;
        param.submitMap = new HashMap<String, String>();
        param.method = CustomHttpConstant.GET;

        AsyncTaskItem taskItem = new AsyncTaskItem();
        taskItem.param = param;
        taskItem.callback = taskGetVideoDataItemCallback;
        abTaskPool.execute(taskItem);
    }

    public class HttpCallBackObj {
        public HttpServiceInterface callbackItr;
        public String url;
        public String fileName;
        public String method;
        public String strResult;
        public Object callbackParam;
        Map<String, String> submitMap;
    }
}
