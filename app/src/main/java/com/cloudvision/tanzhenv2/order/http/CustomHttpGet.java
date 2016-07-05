package com.cloudvision.tanzhenv2.order.http;

import android.util.Log;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


//import android.util.Log;



/**
 * @Description: 获取HttpGet??
 */
public class CustomHttpGet {
	final static int LOG_LEVEL = Log.WARN;
	final static String TAG = "CustomHttpGet";
	
	/**
	 * 获得httpGet
	 * @param url 请求url
	 * @return httpPost
	 */
	public static synchronized HttpGet getHttpGet(String url){
		return getHttpGet(url, null, CustomHttpConstant.CONNECTION_TIMEOUT, CustomHttpConstant.SOTIMEOUT);
	}
	
	/**
	 * 获取httpGet
	 * @param url 请求url
	 * @param connectionTimeout 连接超时时间
	 * @param soTimeout socket超时时间
	 * @return httpPost
	 */
	public static synchronized HttpGet getHttpGet(String url, int connectionTimeout, int soTimeout){
		return getHttpGet(url, null, connectionTimeout, soTimeout);
	}
	
	/**
	 * 获取httpGet
	 * @param url 请求url
	 * @param params 请求提交参数
	 * @param connectionTimeout 连接超时时间
	 * @param soTimeout socket超时时间
	 * @return httpPost
	 */
	public static synchronized HttpGet getHttpGet(String url, Map<String, String> params, int connectionTimeout, int soTimeout){
		if(params!=null && params.size()>0){
			StringBuffer strBuffer=new StringBuffer(url);
			strBuffer.append("?");
			Set<Entry<String, String>> set=params.entrySet();
			for(Entry<String, String> entry:set){
				strBuffer.append(entry.getKey());
				strBuffer.append("=");
				strBuffer.append(entry.getValue());
				strBuffer.append("&");
			}
			strBuffer.delete(strBuffer.length()-1, strBuffer.length());
			url=strBuffer.toString();
		}
		Log.println(LOG_LEVEL, TAG, "get===url=="+url);
		HttpGet httpGet = new HttpGet(url);
		HttpParams httpParams = httpGet.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
		HttpConnectionParams.setSoTimeout(httpParams, soTimeout);
		httpGet.setParams(httpParams);
		
		return httpGet;
	}
	
}
