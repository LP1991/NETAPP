package com.cloudvision.tanzhenv2.order.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 
 * @Description: 获取单实例httpClient, 线程安全, 可用于多线程访问
 * 
 */
public class CustomHttpClient {
	public static final String ERROR_MARK="status error:";
	
	private static HttpClient customHttpClient;
	public final static String USER_AGENT= "Mozilla/5.0 (Linux; U; Android 2.2.1; en-us; Nexus One Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";

	private CustomHttpClient() {
	}
	
	static String getAgentString()
	{
		return "hofia_phone";
	}
	
	/**
	 * @serialData version 协议版本:HttpVersion.HTTP_1_1
	 * @serialData contentCharset 字符编码:HTTP.DEFAULT_CONTENT_CHARSET
	 * @serialData useExpectContinue:true
	 * @serialData useragent:CustomHttpClient.userAgent
	 * @serialData timeout 连接池超时时??1000
	 * @serialData connectionTimeout 连接超时时间:5000
	 * @serialData soTimeout socket超时时间:10000
	 * @serialData httpPort http请求默认端口:??
	 * @serialData httpsPort https请求默认端口:??
	 * @return httpClient
	 */
	/*public static synchronized HttpClient getHttpClient(){
		if(customHttpClient==null){
			customHttpClient=getHttpClient(HttpVersion.HTTP_1_1, HTTP.DEFAULT_CONTENT_CHARSET, true, USER_AGENT, 
					CustomHttpConstant.CONNMANAGER_TIMEOUT, CustomHttpConstant.CONNECTION_TIMEOUT, CustomHttpConstant.SOTIMEOUT, 
					CustomHttpConstant.defaultHttpPort, CustomHttpConstant.defaultHttpsPort);
		}
		return customHttpClient;
	}*/
	public static synchronized HttpClient getHttpClient(){
		if(customHttpClient==null){
			customHttpClient=getHttpClient(HttpVersion.HTTP_1_1, HTTP.DEFAULT_CONTENT_CHARSET, true, getAgentString(), 
					CustomHttpConstant.CONNMANAGER_TIMEOUT, CustomHttpConstant.CONNECTION_TIMEOUT, CustomHttpConstant.SOTIMEOUT, 
					CustomHttpConstant.defaultHttpPort, CustomHttpConstant.defaultHttpsPort);
		}
		return customHttpClient;
	}

	/**
	 * 获得HttpClient单实??
	 * @param version 协议
	 * 版本:HttpVersion.HTTP_0_9 / HttpVersion.HTTP_1_0 / HttpVersion.HTTP_1_1
	 * @param contentCharset 字符编码:HTTP.DEFAULT_CONTENT_CHARSET / HTTP.UTF_8 / HTTP.ISO_8859_1 etc.
	 * @param useExpectContinue
	 * see void org.apache.http.params.HttpProtocolParams.setUseExpectContinue(HttpParams params, boolean b)
	 * @param useragent
	 * see void org.apache.http.params.HttpProtocolParams.setUserAgent(HttpParams params, String useragent)
	 * @param timeout 连接池超时时??
	 * @param connectionTimeout 连接超时时间
	 * @param soTimeout socket超时时间
	 * @param httpPort http请求端口,-1时不设此端口
	 * @param httpsPort https请求端口,-1时不设此端口
	 * @return httpClient
	 */
	public static synchronized HttpClient getHttpClient(ProtocolVersion version, String contentCharset, boolean useExpectContinue, String useragent, 
			 long timeout, int connectionTimeout, int soTimeout, int httpPort, int httpsPort){
		if(customHttpClient==null){
			HttpParams params=new BasicHttpParams();
			HttpProtocolParams.setVersion(params, version);
			HttpProtocolParams.setContentCharset(params, contentCharset);
			HttpProtocolParams.setUseExpectContinue(params, useExpectContinue);
			HttpProtocolParams.setUserAgent(params, useragent);
			
			ConnManagerParams.setTimeout(params, timeout);
			HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
			HttpConnectionParams.setSoTimeout(params, soTimeout);
			
			SchemeRegistry schreg=new SchemeRegistry();
			schreg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), httpPort));
			schreg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), httpsPort));
			ClientConnectionManager conMgr=new ThreadSafeClientConnManager(params, schreg);
			
			customHttpClient=new DefaultHttpClient(conMgr, params);
		}
		return customHttpClient;
	}
	
	/**
	 * 请求服务器获取输入流
	 * @param url 请求url
	 * @param params 请求参数
	 * @param method 请求方法,use CustomHttpClient.POST or CustomHttpClient.GET
	 * @param requestTimeStamp 当前请求时间??可用于终止请??
	 * @return 响应输入??
	 */
	public static synchronized InputStream requestForInputStream(Context context, String url, Map<String, String> params, String method, Long requestTimeStamp){
		ByteArrayInputStream bais=null;
		try {
			HttpResponse httpResponse = CustomHttpResponse.getHttpResponse(context, url, params, method, requestTimeStamp);
			if(httpResponse!=null){
				InputStream is=httpResponse.getEntity().getContent();
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				byte[] buffer=new byte[1024];
				int len=0;
				if((len=is.read(buffer))!=-1){
					baos.write(buffer, 0, len);
				}
				bais=new ByteArrayInputStream(baos.toByteArray());
				baos.close();
				is.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bais;
	}
	
	/**
	 * 请求服务器获取字符串
	 * @param url 请求url
	 * @param params 请求参数
	 * @param method 请求方法,use CustomHttpClient.POST or CustomHttpClient.GET
	 * @param responseEncoding 响应编码格式  ??HTTP.UTF_8
	 * @param requestTimeStamp 当前请求时间??可用于终止请??
	 * @return 响应字符??
	 */
	public static synchronized String requestForString(Context context, String url, Map<String, String> params, String method, String responseEncoding, Long requestTimeStamp){
		String result=ERROR_MARK+"网络错误";
	/*	if(NetworkUtil.getAPNType(context)==NetworkUtil.NO_NETWORK){
			return result;
		}*/
		
		try {
			HttpResponse httpResponse = CustomHttpResponse.getHttpResponse(context, url, params, method, requestTimeStamp);
			if(httpResponse!=null){
				if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					result=EntityUtils.toString(httpResponse.getEntity(), responseEncoding);
				}else{
					result=ERROR_MARK+httpResponse.getStatusLine().getStatusCode();
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static synchronized String requestForStringWarning(Context context, String url, Map<String, String> params, String method, String responseEncoding, Long requestTimeStamp){
		String result=ERROR_MARK+"网络错误";
	/*	if(NetworkUtil.getAPNType(context)==NetworkUtil.NO_NETWORK){
			return result;
		}*/
		
		try {
			HttpResponse httpResponse = CustomHttpResponse.getHttpResponseForWarning(context, url, params, method, requestTimeStamp);
			if(httpResponse!=null){
				if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					result=EntityUtils.toString(httpResponse.getEntity(), responseEncoding);
				}else{
					result=ERROR_MARK+httpResponse.getStatusLine().getStatusCode();
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 请求服务器获取Bitmap
	 * @param url 请求url
	 * @param params 请求参数
	 * @param method 请求方法,use CustomHttpClient.POST or CustomHttpClient.GET
	 * @param requestTimeStamp 当前请求时间??可用于终止请??
	 * @return 响应返回图片Bitmap
	 */
	public static synchronized Bitmap requestForBitmap(Context context, String url, Map<String, String> params, String method, Long requestTimeStamp){
		InputStream is=requestForInputStream(context, url, params, method, requestTimeStamp);
		Bitmap bmp=BitmapFactory.decodeStream(is);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmp;
	}
	
	/**
	 * 请求服务器获取Drawable
	 * @param url 请求url
	 * @param params 请求参数
	 * @param method 请求方法,use CustomHttpClient.POST or CustomHttpClient.GET
	 * @param requestTimeStamp 当前请求时间??可用于终止请??
	 * @param srcName 图片名称
	 * @return 响应返回图片Drawable
	 */
	public static synchronized Drawable requestForDrawable(Context context, String url, Map<String, String> params, String method, Long requestTimeStamp, String srcName){
		InputStream is=requestForInputStream(context, url, params, method, requestTimeStamp);
		Drawable drawable=BitmapDrawable.createFromStream(is, srcName);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return drawable;
	}
	
	/**
	 * 根据请求时间戳终止请求的连接
	 * @param requestTimeStamp 当前请求时间??可用于终止请??
	 */
	public static void abort(Long requestTimeStamp){
		CustomHttpResponse.abort(requestTimeStamp);
	}
	
	/**
	 * 终止????正在请求的连??
	 */
	public static void abort(){
		CustomHttpResponse.abortAll();
	}
	
	/**
	 * ????requestForString()方法是否成功
	 * @param result requestForString()返回??
	 * @return
	 */
	public static boolean isResultSucceed(String result){
		boolean re=(result!=null);
		int error=JSONUtil.optError(result);
		//re&=ETCloudApplication.validate(error);
		re&=(result.contains(ERROR_MARK) ? false:true);
		
		return re;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
