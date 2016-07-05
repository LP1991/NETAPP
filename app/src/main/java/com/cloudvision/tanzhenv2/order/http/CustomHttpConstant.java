package com.cloudvision.tanzhenv2.order.http;

/**
 * @Description: Http请求常量??

 */
public class CustomHttpConstant {
	
	/** 请求方法post  */
	public final static String POST="post";
	
	/** 请求方法get */
	public final static String GET="get";
	
	/** 连接池超时时??*/
	public static int CONNMANAGER_TIMEOUT=5000;
	
	/**  请求连接超时时间 */
	public static int CONNECTION_TIMEOUT=5000;
	
	/** socket响应超时时间 */
	public static int SOTIMEOUT=5000;
	
	/** socket响应超时时间告警信息使用 */
	public static int SOTIMEOUTWARNING=15000;
	
//	/** 不设置默认端??*/
//	public final static int NO_PORT=-1;
	
	public static int defaultHttpPort=80;
	public static int defaultHttpsPort=443;
	
	
	
}
