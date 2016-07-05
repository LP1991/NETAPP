package com.cloudvision.tanzhenv2;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cloudvision.tanzhenv2.order.WorkListDetailsActivity;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.deal.OnlyToast;
import com.cloudvision.tanzhenv2.order.function.MyBDLocation;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.tanzhenv2.order.model.WorkListJson;
import com.cloudvision.tanzhenv2.order.model.WorkListRoot;
import com.cloudvision.util.MyLog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment {
	
	private static final String TAG = "Map_order";

//    private Toast mToast;

    private MapView mMapView = null;//MapView 是地图主控件
    private BaiduMap mBaiduMap = null;
    private List<Marker> markers = new ArrayList<Marker>();

    private boolean firstflag = true;
    private List<WorkListJson> jsons;
    private List<String> orderLevels = new ArrayList<String>();		//存储工单紧急程度
    private int numberrderLevel = 0;								//工单紧急程度标记
    private int numberJson = 0;		//记录点击的是哪个标记
    
    private Runnable runnable = new Runnable() {
        public void run() {

            Gson gson = new Gson();
//            HashMap<Integer, String> mapId = new HashMap<>();

            for (int i = 0; i < jsons.size(); i++) {
                WorkListJson json = jsons.get(i);
//                mapId.put(i, String.valueOf(json.getId()));

                String city = json.getAddresspath();
                String[] temp = city.split(" ");
                String address = json.getCustomeraddress();
                reverseGeoCode(temp[0], address);
                
                //存储工单紧急程度，等待反编码得到结果后，依次处理
                orderLevels.add(json.getOrderlevel());

                if (!MySharedPreferencesUtils.contains(String.valueOf(json.getId()))) {
                    String sjson = gson.toJson(json);
                    MySharedPreferencesUtils.put(String.valueOf(json.getId()), sjson);
                }
            }
//            MySharedPreferencesUtils.put("order_id", mapId);
        }
    };

    private HttpServiceInterface httpOrder = new HttpServiceInterface() {
        @Override
        public void getResult(String result, Object objParam) {
        	       	
        	MyLog.e(TAG, "result: " + result);
            System.out.println(result);
            
            Gson gson = new Gson();
            WorkListRoot workListData = null;
            try {
                workListData = gson.fromJson(result, WorkListRoot.class);
            } catch (JsonSyntaxException e) {
            	OnlyToast.showToast("网络问题，周边工单获取失败");
                e.printStackTrace();
            }
            if ((workListData != null) && workListData.getReturnCode().equals("SUCCESS")) {
                jsons = workListData.getJson();
//                String returnMsg = workListData.getReturnMsg();
                if (jsons != null) {
//                	showToast(returnMsg);
                    Handler myHandler = new Handler();
                    myHandler.post(runnable);
                } else {
                	OnlyToast.showToast("周边暂无工单");
                }
            }
        }
    };
    
    private HttpServiceInterface httpAccept = new HttpServiceInterface() {
        @Override
        public void getResult(String result, Object objParam) {
        	
        	MyLog.e(TAG, "result: " + result);
            System.out.println(result);
                
            String value = "";
            Map<String, String> map;
            try {
                map = MapUtils.parseData(result);
                value = map.get("returnCode");
                System.out.println(value);
            } catch (Exception e) {
            	OnlyToast.showToast("网络问题，接单失败");
                e.printStackTrace();
            }
            if (value.equals("SUCCESS")) {
            	OnlyToast.showToast("接单成功！");
                markers.get(numberJson).remove();
            } else {
            	OnlyToast.showToast("接单失败。");
            }           
        }
    };
   
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
//		SDKInitializer.initialize(getActivity().getApplicationContext());
		View view = inflater.inflate(R.layout.fragment_map, container, false);

        //获取地图控件引用
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        TextView textView = (TextView) view.findViewById(R.id.top_refresh);
        textView.setOnClickListener(new RefreshOnClickListener());

        //此段代码消去自带的缩放按钮
        int childCount = mMapView.getChildCount();
        View zoom = null;
        for (int i = 0; i < childCount; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                zoom = child;
                break;
            }
        }
        assert zoom != null;
        zoom.setVisibility(View.GONE);
        
        //缓存
        MySharedPreferencesUtils.getInstance(getActivity().getApplicationContext(), "share_data");
        //地图显示移动到用户当前的位置
        moveUserPoint();
        //本机定位显示
        userLocationInit();
        //访问获取未指派工工单数据(需要签到)
        if((Boolean) MySharedPreferencesUtils.get("signFlag", false)){
        	orderHttp();
        }

        //设置Mark的点击监听
        mBaiduMap.setOnMarkerClickListener(new MarkOnClickListener());
        //设置点击变化监听
        mBaiduMap.setOnMapStatusChangeListener(new ChangeOnClickListener());
        //设置图定位图标点击事件监听
        mBaiduMap.setOnMyLocationClickListener(new LocationOnClickListener());
        
		return view;
	}
	
	 /**
     * 界面移动到当前位置
     */
    private void moveUserPoint() {

        double latitude = MyBDLocation.getLatitude();
        double longtitude = MyBDLocation.getLongtitude();
        if (firstflag && (latitude != 0) && (longtitude != 0)) {
            firstflag = false;
            LatLng userPoint = new LatLng(latitude, longtitude);
            //定义地图状态
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(userPoint)
                    .zoom(14)
                    .build();
            //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            //改变地图状态
            mBaiduMap.setMapStatus(mMapStatusUpdate);
        }else{
        	new Handler().postDelayed(new Runnable(){
        	      @Override
        	      public void run() {
        	    	  moveUserPoint();
        	      }
        	    }, 1000);
        }
    }
    
    /**
     * 显示当前位置
     *
     */
    private void userLocationInit() {

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        double latitude = MyBDLocation.getLatitude();
        double longtitude = MyBDLocation.getLongtitude();
        if (latitude == 0 || longtitude == 0) {
            String slatitude = (String) MySharedPreferencesUtils.get("latitude", "31.308715");
            String slongtitude = (String) MySharedPreferencesUtils.get("longtitude", "121.523597");
            if (slatitude != null) {
                latitude = Double.valueOf(slatitude);
            }
            if (slongtitude != null) {
                longtitude = Double.valueOf(slongtitude);
            }
        }
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(MyBDLocation.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100)
                .latitude(latitude)
                .longitude(longtitude)
                .build();
        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);
        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_map_userpoint);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                true, mCurrentMarker);
        mBaiduMap.setMyLocationConfigeration(config);
        // 当不需要定位图层时关闭定位图层
        mBaiduMap.setMyLocationEnabled(true);
    }
    
    /**
     * 反地理编码得到地址信息
     */
    private void reverseGeoCode(String city, String address) {
        // 创建地理编码检索实例
        final GeoCoder geoCoder = GeoCoder.newInstance();

        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            // 反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            }

            // 地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    MyLog.i("order", "抱歉，未能找到结果");
                } else {
                    //设置mark
                	String orderLevel = orderLevels.get(numberrderLevel++);
                    setPoint(result.getLocation().latitude, result.getLocation().longitude,orderLevel);
                    // 释放地理编码检索实例
                    geoCoder.destroy();
                }
            }
        };
        // 设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(listener);
        geoCoder.geocode(new GeoCodeOption()
                .city(city)
                .address(address));
    }
    
    /**
     * 网络获取未指派工单信息
     */
    private void orderHttp() {

        StringBuilder data = new StringBuilder(256);
        data.append("userName=");
        data.append(MySharedPreferencesUtils.get("userName", ""));
        data.append("&orderStatus=");
        data.append("1");

        MyLog.i(TAG, "order data: " + data.toString());
        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_WORKLIST;
        url = url + data03;
        MyLog.i(TAG, "order url :" + url);
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        HttpService httpService = new HttpService(getActivity().getApplicationContext());
        httpService.get(url, httpOrder, null);
    }
    
    /**
     * 添加Mark到指定坐标
     *
     * @param latitude  纬度 
     * @param longtitude  精度
     */
    private void setPoint(double latitude, double longtitude,String orderLevel) {
        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longtitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker);
        //更具工单紧急程度选择图片
    	if(orderLevel.equals("高")){
    		bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker);
    	}else if(orderLevel.equals("中")){
    		bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_mid);
    	}else if(orderLevel.equals("低")){
    		bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_low);
    	}
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        Marker marker = (Marker) (mBaiduMap.addOverlay(option));
        markers.add(marker);
    }
    
    /**
     * 请求接受工单
     */
    private void acceptHttp() {

        StringBuilder data = new StringBuilder(256);
        data.append("id=");
        data.append(jsons.get(numberJson).getId());
        data.append("&userName=");
        data.append(MySharedPreferencesUtils.get("userName", ""));
        data.append("&orderStatus=4");

        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_WORKDEAL;
        url = url + data03;
        MyLog.i(TAG, "accept data:" + data);

        HttpService httpService = new HttpService(getActivity().getApplicationContext());
        httpService.get(url, httpAccept, null);
    }
    
    @Override
	public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        markers.clear();
        orderLevels.clear();
    	numberrderLevel = 0;
        MyLog.e(TAG,"Map onDestry...");
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        firstflag = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    
    /**
     * 地图点击变化监听
     */
    private class ChangeOnClickListener implements BaiduMap.OnMapStatusChangeListener {

        @Override
        public void onMapStatusChangeStart(MapStatus arg0) {
            mBaiduMap.hideInfoWindow();
        }

        @Override
        public void onMapStatusChangeFinish(MapStatus arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onMapStatusChange(MapStatus arg0) {
            // TODO Auto-generated method stub
        }
    }
    
    /**
     * 当前位置点击监听
     */
    private class LocationOnClickListener implements BaiduMap.OnMyLocationClickListener {

        @Override
        public boolean onMyLocationClick() {

            LayoutInflater inflate = LayoutInflater.from(getActivity().getApplicationContext());
            int layoutId = R.layout.window_orders_location;
            View view = inflate.inflate(layoutId, null);
            
            double userlatitude = MyBDLocation.getLatitude();
            double userlongtitude = MyBDLocation.getLongtitude();
            LatLng myLL = new LatLng(userlatitude, userlongtitude);
            MapStatusUpdate m = MapStatusUpdateFactory.newLatLngZoom(myLL, 14);
            mBaiduMap.setMapStatus(m);
            myLL = new LatLng(userlatitude + 0.001, userlongtitude);
            Point point = mBaiduMap.getProjection().toScreenLocation(myLL);
            LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(point);
            InfoWindow mInfoWindow = new InfoWindow(view, llInfo, Constants.MAP_TAG_HIGH);
            mBaiduMap.showInfoWindow(mInfoWindow);

            //设置InfoWindow的工单信息显示
            TextView textView = (TextView) view.findViewById(R.id.tv_myLocation);
            textView.setText("您的位置");

            return true;
        }
    }

    /**
     * 地图标记物点击监听
     */
    private class MarkOnClickListener implements BaiduMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {

            LayoutInflater inflate = LayoutInflater.from(getActivity().getApplicationContext());
            int layoutId = R.layout.window_orders_mark;
            View view = inflate.inflate(layoutId, null);
            
            String info = null;
            for (int i = 0; i < markers.size(); i++) {
                if (marker == markers.get(i)) {
                    WorkListJson json = jsons.get(i);
                    info = json.getCustomeraddress();  
                    if(null != info){
                    	info = info.replaceAll(".{16}(?!$)", "$0\n");
                    }
                    info = info + "\n" ;
                    String stemp = json.getTroubledesc();
                    if(null != stemp){
                    	stemp = stemp.replaceAll(".{16}(?!$)", "$0\n");
                    }
                    info = info + stemp;
                    numberJson = i;
                    break;
                }
            }

            //设置中心点
            LatLng markLL = marker.getPosition();
            MapStatusUpdate m = MapStatusUpdateFactory.newLatLng(markLL);
            if (mBaiduMap.getMapStatus().zoom < 15) {
                m = MapStatusUpdateFactory.newLatLngZoom(markLL, 14);
            }
            mBaiduMap.setMapStatus(m);

            //设置infoWindow位置和显示
            markLL = new LatLng(marker.getPosition().latitude + 0.005 / Math.pow(2, mBaiduMap.getMapStatus().zoom - 14), marker.getPosition().longitude);
            if (mBaiduMap.getMapStatus().zoom < 15) {
                markLL = new LatLng(marker.getPosition().latitude + 0.005, marker.getPosition().longitude);
            }
            Point point = mBaiduMap.getProjection().toScreenLocation(markLL);
            LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(point);
            InfoWindow mInfoWindow = new InfoWindow(view, llInfo, Constants.MAP_TAG_HIGH);
            mBaiduMap.showInfoWindow(mInfoWindow);

            //设置InfoWindow的工单信息显示
            TextView textView = (TextView) view.findViewById(R.id.tv_infoAddress);
            textView.setText(info);

            //为接单按钮注册监听事件
            Button btnAccept = (Button) view.findViewById(R.id.btn_accept);
            btnAccept.setOnClickListener(new OrderOnClickListener());
            //为接单按钮注册监听事件
            Button btnDetails = (Button) view.findViewById(R.id.btn_details);
            btnDetails.setOnClickListener(new DetailsOnClickListener());

            return true;
        }
    }
    
    /**
     * 显示详情按钮监听
     */
    private class DetailsOnClickListener implements View.OnClickListener {
        public void onClick(View v) {

            Intent intent = new Intent(getActivity().getApplicationContext(), WorkListDetailsActivity.class);
            Constants.workId = String.valueOf(jsons.get(numberJson).getId());
            intent.putExtra("id", String.valueOf(jsons.get(numberJson).getId()));
            intent.putExtra("order", "true");
            startActivity(intent);
        }
    }

    /**
     * 接单按钮监听
     */
    private class OrderOnClickListener implements View.OnClickListener {
        public void onClick(View v) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("接单");
            dialog.setMessage("是否马上抢下此单？");
            //为“确定”按钮注册监听事件
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    acceptHttp();
                    mBaiduMap.hideInfoWindow();
                }
            });
            //为“取消”按钮注册监听事件
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.create();
            dialog.show();
        }
    }
    
    /**
     * 刷新按钮监听
     */
    private class RefreshOnClickListener implements View.OnClickListener {
        public void onClick(View v) {
        	try {
        		markers.clear();
            	orderLevels.clear();
            	numberrderLevel = 0;
			} catch (Exception e) {
			}
        	if((Boolean) MySharedPreferencesUtils.get("signFlag", false)){
            	orderHttp();
            }else {
				CommonUtils.showTips(getActivity(), "提示", "签到后才能获取未指派工单的信息。");
			}
        }
    }
	
}
