package com.cloudvision.tanzhenv2.order.wifi;

import com.cloudvision.util.MyLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 通过反射获取wifi接口名
 *
 * Created by 谭智文
 */
public class SystemInfo {
	
	private static final String TAG = "SystemInfo";

	private Method methodGetProperty;
    private static SystemInfo instance = new SystemInfo();

    public static SystemInfo getInstance() {
        return instance;
      }

    private SystemInfo(){
        Class<?> classSystemProperties = null;
		try {
			classSystemProperties = Class.forName("android.os.SystemProperties");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         try {
			methodGetProperty = classSystemProperties.getMethod("get", String.class);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public String getProperty(String property) {
        if(methodGetProperty == null) return null;
        try {

          return (String)methodGetProperty.invoke(null, property);

        } catch(IllegalAccessException e) {
          MyLog.e(TAG, "Failed to get property");
        } catch(InvocationTargetException e) {
        MyLog.e(TAG, "Exception thrown while getting property");
        }
        return null;
   }
}
