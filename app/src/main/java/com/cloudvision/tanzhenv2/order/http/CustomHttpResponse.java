package com.cloudvision.tanzhenv2.order.http;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;





/**
 * @Description: 获取httpResponse??
 */
public class CustomHttpResponse {
	private static Map<Long, HttpRequestBase> requestMap=new Hashtable<Long, HttpRequestBase>();
	
	private CustomHttpResponse() {
	}

	/**
	 * 请求服务器获取HttpResponse
	 * @param url 请求url
	 * @param params 请求参数
	 * @param method 请求方法,use CustomHttpConstant.POST or CustomHttpConstant.GET
	 * @param requestEncoding 请求编码格式
	 * @param connectionTimeout 连接超时时间
	 * @param soTimeout socket超时时间
	 * @param requestTimeStamp 当前请求时间??可用于终止请??
	 * @return 响应HttpResponse对象
	 */
	public static synchronized HttpResponse getHttpResponse(Context context, String url, Map<String, String> params, String method, String requestEncoding, 
			int connectionTimeout, int soTimeout, Long requestTimeStamp){
		HttpResponse httpResponse =null;
		
		HttpClient httpClient=CustomHttpClient.getHttpClient();
		HttpRequestBase request=null;
		if(CustomHttpConstant.POST.equalsIgnoreCase(method)){
			request=CustomHttpPost.getHttpPost(url, params, requestEncoding, connectionTimeout, soTimeout);
		}else if(CustomHttpConstant.GET.equalsIgnoreCase(method)){
			request=CustomHttpGet.getHttpGet(url, params, connectionTimeout, soTimeout);
		}else{
			throw new IllegalArgumentException("param 'method' must used either CustomHttpConstant.POST or CustomHttpConstant.GET");
		}
		httpResponse=getHttpResponse(context, httpClient, request, requestTimeStamp);
		return httpResponse;
	}
	
	/**
	 * 请求服务器获取HttpResponse
	 * @param url 请求url
	 * @param params 请求参数
	 * @param method 请求方法,use CustomHttpConstant.POST or CustomHttpConstant.GET
     * @param requestTimeStamp 当前请求时间??可用于终止请??
	 * @return 响应HttpResponse对象
	 */
	public static synchronized HttpResponse getHttpResponse(Context context, String url, Map<String, String> params, String method, Long requestTimeStamp){
		HttpResponse httpResponse =null;
		
		HttpClient httpClient=CustomHttpClient.getHttpClient();
		HttpRequestBase request=null;
		if(CustomHttpConstant.POST.equalsIgnoreCase(method)){
			request=CustomHttpPost.getHttpPost(url, params);
		}else if(CustomHttpConstant.GET.equalsIgnoreCase(method)){
			request=CustomHttpGet.getHttpGet(url, params, CustomHttpConstant.CONNECTION_TIMEOUT, CustomHttpConstant.SOTIMEOUT);
		}else{
			throw new IllegalArgumentException("param 'method' must used either CustomHttpConstant.POST or CustomHttpConstant.GET");
		}
		httpResponse=getHttpResponse(context, httpClient, request, requestTimeStamp);
		return httpResponse;
	}
	
	public static synchronized HttpResponse getHttpResponseForWarning(Context context, String url, Map<String, String> params, String method, Long requestTimeStamp){
		HttpResponse httpResponse =null;
		
		HttpClient httpClient=CustomHttpClient.getHttpClient();
		HttpRequestBase request=null;
		if(CustomHttpConstant.POST.equalsIgnoreCase(method)){
			request=CustomHttpPost.getHttpPost(url, params);
		}else if(CustomHttpConstant.GET.equalsIgnoreCase(method)){
			request=CustomHttpGet.getHttpGet(url, params, CustomHttpConstant.CONNECTION_TIMEOUT, CustomHttpConstant.SOTIMEOUTWARNING);
		}else{
			throw new IllegalArgumentException("param 'method' must used either CustomHttpConstant.POST or CustomHttpConstant.GET");
		}
		httpResponse=getHttpResponse(context, httpClient, request, requestTimeStamp);
		return httpResponse;
	}
	
	public static synchronized HttpResponse getHttpResponse(Context context, HttpClient httpClient, HttpRequestBase request, Long requestTimeStamp){
		HttpResponse httpResponse =null;
		if(requestTimeStamp==null){
			requestTimeStamp=System.currentTimeMillis();
		}
		requestMap.put(requestTimeStamp, request);
		try {
			httpResponse = httpClient.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			requestMap.remove(requestTimeStamp);
		}
		return httpResponse;
	}
	
	/**
	 * 根据请求标志终止请求的连??
	 * @param requestMark @param requestTimeStamp 当前请求时间??可用于终止请??
	 */
	protected static void abort(Long requestMark){
		HttpRequestBase request=requestMap.remove(requestMark);
		if(request!=null){
			request.abort();
		}
	}
	
	/**
	 * 终止????正在请求的连??
	 */
	protected static void abortAll(){
		Set<Long> requestSet=requestMap.keySet();
		HttpRequestBase request;
		for(Long key: requestSet){
			request=requestMap.remove(key);
			if(request!=null){
				request.abort();
			}
		}
	}
	
}
