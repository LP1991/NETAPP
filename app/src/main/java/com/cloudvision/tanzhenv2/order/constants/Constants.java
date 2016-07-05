package com.cloudvision.tanzhenv2.order.constants;


import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.model.WorkListJson;

public abstract class Constants {

	public static double pointLatitude = 0;
	public static double pointLongtitude = 0;

	public static final String probeMac = "AA:BB:CC:CC:BB:DD";		//探针MAC，随后使用获取的值
	public static final String deviceType = "EOC";				//设备类型probe,router,catv,mcu
	public static final String version = "A1000";						//探针产品型号
	public static final String EocSw = "v1.0.0.4";				//软件版本
	public static final String CatvSw = "v1.4.53";				//软件版本
	public static final String McuSw = "v1.0.0.4";				//软件版本
	public static final String RouterSw = "v1.4.53";				//软件版本
	public static final String upFileURL = "/storage/sdcard0/tanzhenV2/Catv测试/smart_jni.txt";	//上传文件的本地路径（暂时写死）

	public static String workId;
	public static WorkListJson json;
	
	public static final String CACHE_PATH = CommonUtils.getSDPath() + java.io.File.separator + "TanZhen" + java.io.File.separator;
	
	public static final String ALIAS_TYPE = "PROBE";	//推送的TYPE

	public static final String PWD_KEY = "cvnchina";	//数据加密的key
	public static final String CHARSET = "UTF-8";

	public static final String IP_ADDRESS_test = "http://192.168.14.123:8080/mns/";
	public static final String IP_ADDRESS_C = "http://10.9.200.11:8280/cpms/";
	public static final String IP_ADDRESS_HANGZHOU = "http://192.168.14.104:8080/cpms/";//实际使用的地址
	public static final String IP_ALI = "http://121.40.184.131:8080/cpms/";//实际使用的地址
	
	public static String IP_ADDRESS = IP_ADDRESS_test;

	public static String URL_LOGIN = IP_ADDRESS+"app.do?login&ct=";		//登录
	public static String URL_SIGNIN = IP_ADDRESS+"app.do?signin&ct=";	//签到
	public static String URL_SIGNOUT = IP_ADDRESS+"app.do?signout&ct=";	//签出
	public static String URL_UPDATA = IP_ADDRESS+"app.do?probeinfoReport&ct=";	//信息上报

	public static String URL_WORKLIST =  IP_ADDRESS+"app.do?queryTroubleOrder&ct=";		//工单查询
	public static String URL_WORKDEAL =  IP_ADDRESS+"app.do?notifyOrderResult&ct=";		//工单处理完成
	public static String URL_WORKHISTORY =  IP_ADDRESS+"app.do?queryHistoryTroubleOrder&ct=";	//历史工单查询
	public static String URL_UPLOAD =  IP_ADDRESS+"app.do?upload&ct=";		//文件上传

	public static String URL_CONFIG =  IP_ADDRESS+"app.do?probeConfigDownload&ct=";		//配置文件下载
	public static String URL_UPGRADEDOWN =  IP_ADDRESS+"app.do?softVersionDownload&ct=";	//升级文件下载
	public static String URL_UPGRADEQUERY =  IP_ADDRESS+"app.do?queryLastVersion&ct=";		//升级版本查询
	public static String URL_BULLETIN =  IP_ADDRESS+"app.do?queryBulletinInformation&ct=";	//公告查询

	public static String URL_UPDATE_USER =  IP_ADDRESS+"app.do?updateUser&ct=";		//用户信息更改
	public static String URL_UPDATE_FACE =  IP_ADDRESS+"app.do?upUserHeadImage&ct=";		//上传用户头像
	public static String URL_START_IMAGE =  IP_ADDRESS+"app.do?queryAppImg&ct=";	//获取启动页和主页图片
	public static String URL_TEST_SPEED =  IP_ADDRESS+"app.do?appWifiSpeed";		//获取测速地址
	public static String URL_ABOUT_US =  IP_ADDRESS+"app.do?aboutUs";				//关于我们
	public static String URL_USER_HELP =  IP_ADDRESS+"app.do?userHelp";				//用户帮助
	public static String URL_USER_FAQ =  IP_ADDRESS+"app.do?userFAQ";				//FAQ
	public static String URL_SUGGEST_RETURN =  IP_ADDRESS+"app.do?saveFeedback&ct=";	//用户反馈
	public static String URL_APP_UPDATE =  IP_ADDRESS+"app.do?appUpdate&ct=";	//用户反馈
	public static String URL_DEVICE_UPDATE =  IP_ADDRESS+"app.do?probeUpdate&ct=";	//设备升级
	public static String URL_TEST_CONFIG =  IP_ADDRESS+"app.do?probeConfigUpdate&ct=";	//测试配置文件
	public static String URL_NMS_INFO =  IP_ADDRESS+"app.do?queryDeviceInfo&ct=";	//获取网管信息
	public static String URL_WARNING_INFO =  IP_ADDRESS+"app.do?queryAlarmInfo&ct=";	//获取告警信息
	
	public static final int TOTAL_TABS = 3;

	public static final int MAP_TAG_HIGH = -20;		//地图的信息Window的偏移量
	public static final int MAP_LOCATION_TIME = 100*1000;		//地图定位一次的时间间隔
	public static final int INFO_UPDATA_TIME = 100*1000;		//信息上报一次的时间间隔
	
	public static String TEST_CONFIG_1 = "1";//EOC快速测试
	public static String TEST_CONFIG_2 = "2";//CATV频点
	public static String TEST_CONFIG_3 = "3";//CATV快速测试
}
