package com.cloudvision.tanzhenv2.order.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.cloudvision.util.MyLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * WIFI工具类
 * 用来扫描周边wifi，清除wifi记录等操作
 * @author zhangyun
 *
 */
public class WifiUtil {
	
	private static final String TAG = "WifiUtil";
	
	private static WifiManager wifiManager;
	
	public enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}
	
	public enum WIFI_AP_STATE {
		WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING,  WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
	}

	public static android.net.DhcpInfo getDhcpInfo(Context mContext){
		
		wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);  
		
		DhcpInfo di = wifiManager.getDhcpInfo();  
		  
		return di;
	}
	
	public static String long2ip(long ip){ 
		
		StringBuffer sb=new StringBuffer();  
		sb.append(String.valueOf((int)(ip&0xff)));  
		sb.append('.');  
		sb.append(String.valueOf((int)((ip>>8)&0xff)));  
		sb.append('.');  
		sb.append(String.valueOf((int)((ip>>16)&0xff)));  
		sb.append('.');  
		sb.append(String.valueOf((int)((ip>>24)&0xff)));  
		return sb.toString();  
	} 
	
	public static WifiCipherType getWifiCipher(String capability){
		
		String cipher = getEncryptString(capability);
		
		if(cipher.contains("WEP")){
			
			return WifiCipherType.WIFICIPHER_WEP;
		}else if(cipher.contains("WPA") || cipher.contains("WPA2") || cipher.contains("WPS")){
			
			return WifiCipherType.WIFICIPHER_WPA;
		}else if(cipher.contains("unknow")){
			
			return WifiCipherType.WIFICIPHER_INVALID;
		}else{
			return WifiCipherType.WIFICIPHER_NOPASS;
		}
	}
	
	public static String getEncryptString(String capability){
		
		
		StringBuilder sb = new StringBuilder();
		
		if(TextUtils.isEmpty(capability))
			return "unknow";
		
		if(capability.contains("WEP")){
			
			sb.append("WEP");
			
			return sb.toString();
		}
		
		if(capability.contains("WPA")){
			
			sb.append("WPA");
			
		}
		if(capability.contains("WPA2")){
			
			sb.append("/");
			
			sb.append("WPA2");
			
		}
		
		if(capability.contains("WPS")){
			
			sb.append("/");
			
			sb.append("WPS");
			
		}
		
		if(TextUtils.isEmpty(sb))
			return "OPEN";
		
		return sb.toString();
	}
	
	public static List<WifiConfiguration> getConfigurations(Context mContext){
		
		WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		
		List<WifiConfiguration> mList = wm.getConfiguredNetworks();
		
		return mList;
	}
	
	public static boolean removeWifi(Context mContext ,int networkId){
		WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		
		return wm.removeNetwork(networkId);
		
	}
	public static boolean addNetWork(WifiConfiguration cfg ,Context mContext){
		
		WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo mInfo = wm.getConnectionInfo();
		
		if(mInfo != null){
			
			wm.disableNetwork(mInfo.getNetworkId());
//			wm.disconnect();
		}

		boolean flag = false;
		
		if(cfg.networkId > 0){
			
			MyLog.i(TAG, "cfg networkId = " + cfg.networkId);
			flag = wm.enableNetwork(cfg.networkId,true);
			wm.updateNetwork(cfg);
		}else{
			
			int netId = wm.addNetwork(cfg);
			MyLog.i(TAG, "after adding netId = " + netId);
			
			if(netId > 0){
				wm.saveConfiguration();
				flag = wm.enableNetwork(netId, true);
			}
			else{
				
				Toast.makeText(mContext, "创建连接失败", Toast.LENGTH_SHORT).show();
			}
		}
		
		return flag;
	}
	
	public static WifiConfiguration createWifiConfig(String SSID, String Password,

	WifiCipherType Type) {

		WifiConfiguration config = new WifiConfiguration();

		config.allowedAuthAlgorithms.clear();

		config.allowedGroupCiphers.clear();

		config.allowedKeyManagement.clear();

		config.allowedPairwiseCiphers.clear();

		config.allowedProtocols.clear();

		if(!SSID.startsWith("\"")){
			
			SSID = "\"" + SSID + "\"";
		}
		config.SSID =  SSID ;
		
		MyLog.i(TAG, config.SSID );

		// 无密码

		if (Type == WifiCipherType.WIFICIPHER_NOPASS) {

			config.wepKeys[0] = "\"" + "\"";

			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

			config.wepTxKeyIndex = 0;

		}

		// WEP加密

		if (Type == WifiCipherType.WIFICIPHER_WEP) {

			config.preSharedKey = "\"" + Password + "\"";

			config.hiddenSSID = true;

			config.allowedAuthAlgorithms

			.set(WifiConfiguration.AuthAlgorithm.SHARED);

			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

			config.allowedGroupCiphers

			.set(WifiConfiguration.GroupCipher.WEP104);

			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

			config.wepTxKeyIndex = 0;

		}

		// WPA加密

		if (Type == WifiCipherType.WIFICIPHER_WPA) {

			config.preSharedKey = "\"" + Password + "\"";

			config.hiddenSSID = true;

//			 config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

//			 config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

			 config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

//			 config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

//			 config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

			 config.status = WifiConfiguration.Status.ENABLED;

		}

		return config;

	}
	
	public static WifiInfo getConnectedWifiInfo(Context mContext) {
		
		WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		
		return wm.getConnectionInfo();
		
	}
	
	/**
	 * 获取扫描结果
	 * @param mContext
	 * @return
	 */
	public static List<ScanResult> getWifiScanResult(Context mContext){
		
		
		List<ScanResult> mResult = new ArrayList<ScanResult>();
		
		WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		
//		wm.startScan();
		
		mResult = wm.getScanResults();
		
//		if(mResult != null){
//			
//			for(ScanResult mRs : mResult){
//				
//				MyLog.i(TAG, mRs.toString());
//			}
//		}
		
		return mResult;
	}
	
	public static boolean isWifiOpen(Context mContext){
		
		WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		
		return  wm.isWifiEnabled() ;
			
	}
	
	public static void openWifi(final Context mContext , final IWifiOpen mCallBack){
		
		new Thread(
				new Runnable(){

					@Override
					public void run() {
						
						WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
						
						wm.setWifiEnabled(true);
						
						while(wm.getWifiState() == WifiManager.WIFI_STATE_ENABLING){
							
						}
						
						MyLog.i(TAG, "openWifi finish... " + wm.getWifiState());
						
						if(mCallBack != null){
							
							mCallBack.onWifiOpen(wm.getWifiState());
						}
					}
					
				}).start();
		
	}
	
	public interface IWifiOpen{
		
		public void onWifiOpen(int state);
	}

	
	
	/**
	 * 根据ssid获取对应的配置
	 * @param mContext
	 * @param SSID
	 * @return
	 */
	public static WifiConfiguration isExsits(Context mContext,String SSID)   
    {   
		WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> existingConfigs = wm.getConfiguredNetworks();   
           for (WifiConfiguration existingConfig : existingConfigs)    
           {   
             if (existingConfig.SSID.equals("\""+SSID+"\""))  
             {   
                 return existingConfig;   
             }   
           }   
        return null;    
    } 
	
	public static boolean isWifiConnected(Context context){
	 
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    
		if(wifiNetworkInfo.isConnected()){
			return true;
		}
		return false;
	}
	
	
	/**判断热点开启状态*/
	public static boolean isWifiApEnabled() {    
        try {    
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");    
            method.setAccessible(true);    
            return (Boolean) method.invoke(wifiManager);    
        } catch (NoSuchMethodException e) {    
            e.printStackTrace();    
        } catch (Exception e) {    
            e.printStackTrace();    
        }    
        return false;    
    }   
	
	/** 
     * 关闭WiFi热点
     * 
     *  by 谭智文
     */  
    public static void closeWifiAp(Context mContext) {    
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);     
        if (isWifiApEnabled()) {    
            try {    
                Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");    
                method.setAccessible(true);    
                WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);    
                Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);    
                method2.invoke(wifiManager, config, false);    
            } catch (NoSuchMethodException e) {    
                e.printStackTrace();    
            } catch (IllegalArgumentException e) {    
                e.printStackTrace();    
            } catch (IllegalAccessException e) {    
                e.printStackTrace();    
            } catch (InvocationTargetException e) {    
                e.printStackTrace();    
            }    
        }   
    } 
    
    /** 
     * 通过频率获取信道 
     * 
     * by 谭智文
     */ 
    public static int frequency2Channel(int frequency) {
		int channel = 0;
    	
		switch (frequency) {
		case 2412:
			channel = 1;
			break;
		case 2417:
			channel = 2;
			break;
		case 2422:
			channel = 3;
			break;
		case 2427:
			channel = 4;
			break;
		case 2432:
			channel = 5;
			break;
		case 2437:
			channel = 6;
			break;
		case 2442:
			channel = 7;
			break;
		case 2447:
			channel = 8;
			break;
		case 2452:
			channel = 9;
			break;
		case 2457:
			channel = 10;
			break;
		case 2462:
			channel = 11;
			break;
		case 2467:
			channel = 12;
			break;
		case 2472:
			channel = 13;
			break;
		case 2477:
			channel = 14;
			break;
		default:
			break;
		}
			
		return channel;
	}

}

