package com.cloudvision.tanzhenv2.order.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @Description: 获取HttpPost??
 * @date 2013-10-10 下午2:28:13
 */
public class CustomHttpPost {
	
	/**
	 * 获得httpPost
	 * @param url 请求url
	 * @param params 请求提交参数
	 * @return httpPost
	 */
	public static synchronized HttpPost getHttpPost(String url, Map<String, String> params){
		return getHttpPost(url, params, HTTP.UTF_8, CustomHttpConstant.CONNECTION_TIMEOUT, CustomHttpConstant.SOTIMEOUT);
	}
	
	/**
	 * 获得httpPost
	 * @param url 请求url
	 * @param params 请求提交参数
	 * @param encoding 请求参数编码
	 * @param connectionTimeout 连接超时时间
	 * @param soTimeout socket超时时间
	 * @return httpPost
	 */
	public static synchronized HttpPost getHttpPost(String url, Map<String, String> params, String encoding, int connectionTimeout, int soTimeout){
		ArrayList<BasicNameValuePair> postData = new ArrayList<BasicNameValuePair>();
		if (params != null) {
			Set<Entry<String, String>> set=params.entrySet();
			for (Entry<String, String> m : set) {
				postData.add(new BasicNameValuePair(m.getKey(), m.getValue()));
			}
		}
		return getHttpPost(url, postData, encoding, connectionTimeout, soTimeout);
	}
	
	/**
	 * 获取httpPost
	 * @param url 请求url
	 * @param params 请求提交参数
	 * @return httpPost
	 */
	public static synchronized HttpPost getHttpPost(String url, List<BasicNameValuePair> params){
		return getHttpPost(url, params, HTTP.UTF_8, CustomHttpConstant.CONNECTION_TIMEOUT, CustomHttpConstant.SOTIMEOUT);
	}
	
	/**
	 * 获取httpPost
	 * @param url 请求url
	 * @param params 请求提交参数
	 * @param encoding 请求参数编码
	 * @param connectionTimeout 连接超时时间
	 * @param soTimeout socket超时时间
	 * @return httpPost
	 */
	public static synchronized HttpPost getHttpPost(String url, List<BasicNameValuePair> params, String encoding,int connectionTimeout, int soTimeout){
		HttpPost httpPost = new HttpPost(url);
		HttpParams httpParams = httpPost.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
		HttpConnectionParams.setSoTimeout(httpParams, soTimeout);
		httpPost.setParams(httpParams);
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, encoding);
			httpPost.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return httpPost;
	}
	
	/**
	 * 获取httpPost
	 * @param url 请求url
	 * @param httpEntity 请求提交参数
	 * @return httpPost
	 */
	public static synchronized HttpPost getHttpPost(String url, HttpEntity httpEntity){
		HttpPost httpPost = new HttpPost(url);
		HttpParams httpParams = httpPost.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CustomHttpConstant.CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, CustomHttpConstant.SOTIMEOUT);
		httpPost.setParams(httpParams);
		httpPost.setEntity(httpEntity);
		return httpPost;
	}
	
}
