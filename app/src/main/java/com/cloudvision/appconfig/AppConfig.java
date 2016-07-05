package com.cloudvision.appconfig;

import android.os.Environment;


/**
 * 程序配置类
 *
 */
public class AppConfig {

	/**
	 * 推送轮询检测时间
	 * 默认30秒
	 */
	public static final int pushCheckTime = 1 * 30 *1000;
	
	
	/**
	 * 发送设备状态
	 * 默认1分钟
	 */
	public static final int updateStatusTime = 1 * 60 *1000;
	
	
	/**
	 * SD卡的缓存文件夹名字
	 */
//	public static final String CacheDir = Environment.getExternalStorageDirectory() + "/" + "tanzhenV2";
	public static final String CacheDir = Environment.getExternalStorageDirectory() + "/" + "emsapp";
	
	/**
	 * SD卡的Config目录
	 */
	public static final String CacheConfigDir = CacheDir + "/" + "Config";
	/**
	 * SD卡的Update目录
	 */
	public static final String CacheUpdateDir = CacheDir + "/" + "Update_file";

	public static final String EMSWAP_DEPLOY_NAME = "EMSWAP";
	
}
