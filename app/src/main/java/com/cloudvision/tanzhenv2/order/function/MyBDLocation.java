package com.cloudvision.tanzhenv2.order.function;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.util.MyLog;

/**
 * Baidu地图的定位类，主线程中持续运行
 * 
 * Created by 谭智文.
 */
public class MyBDLocation implements BDLocationListener {
	
	private static final String TAG = "MyBDLocation";
	
	private static MyBDLocation myBDLocation;
	private static Context context;
	
    private MyBDLocation(Context context){
    	MyBDLocation.context = context;
    };
     
    public synchronized static MyBDLocation getInstance(Context context) {
    	if (null == myBDLocation) {
    		myBDLocation = new MyBDLocation(context);
        }
        return myBDLocation;
    }
	
    private static float radius;
	private static double latitude ;
    private static double longtitude ;
    private static LocationClient mLocationClient;

    @Override
    public void onReceiveLocation(BDLocation location) {

//        Constants.userLatitude = location.getLatitude();
//        Constants.userLongtitude = location.getLongitude();

    	setRadius(location.getRadius());
    	setLatitude(location.getLatitude());
    	setLongtitude(location.getLongitude());

        if(latitude != 0 && longtitude != 0){
            MySharedPreferencesUtils.put("latitude",latitude);
            MySharedPreferencesUtils.put("longtitude",longtitude);
        }

        //Receive Location
        StringBuilder sb = new StringBuilder(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeGpsLocation) {
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\ndirection : ");
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append(location.getDirection());
        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
        }
//        MyLog.i("location", "百度定位\n" + sb.toString());
    }

    public static synchronized void startBaidu() {
    	MyLog.e(TAG,"baidu Locatioin start...");
        if (null == mLocationClient) {
        	mLocationClient = new LocationClient(context);
            //定位的配置
            LocationClientOption option = new LocationClientOption();
            //定位模式选择，高精度、省电、仅设备
            option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
            //定位坐标系类型选取, gcj02、bd09ll、bd09
            option.setCoorType("bd09ll");
            //定位时间间隔
            option.setScanSpan(Constants.MAP_LOCATION_TIME);
            //选择定位到地址
            option.setIsNeedAddress(true);
            option.setIgnoreKillProcess(true);
            mLocationClient.setLocOption(option);
            //注册定位的成功的回调
//            MyBDLocation mBDLocationListenerImpl = new MyBDLocation();
            mLocationClient.registerLocationListener(myBDLocation);
        }
        mLocationClient.start();
    }

    public static float getRadius() {
        return radius;
    }

    public static double getLongtitude() {
        return longtitude;
    }

    public static double getLatitude() {
        return latitude;
    }
    
    public static void setRadius(float radius) {
		MyBDLocation.radius = radius;
	}

	public static void setLatitude(double latitude) {
		MyBDLocation.latitude = latitude;
	}

	public static void setLongtitude(double longtitude) {
		MyBDLocation.longtitude = longtitude;
	}

    public static void stopLocation(){
    	MyLog.e(TAG,"baidu Locatioin stop...");
    	mLocationClient.stop();
    }
}