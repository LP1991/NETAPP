package com.cloudvision.tanzhenv2.order.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ZoomControls;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.function.MyBDLocation;

import java.lang.ref.WeakReference;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    protected WeakReference<View> mRootView;
    // TODO: Rename and change types of parameters
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private RoutePlanSearch mSearch;
    private PlanNode stNode;
    private PlanNode enNode;
//    private boolean firstflag = true;
    private OnFragmentInteractionListener mListener;
    private OnGetRoutePlanResultListener onGetRoutePlanResultListener = new OnGetRoutePlanResultListener() {

        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//                Toast.makeText(getActivity().getApplicationContext(), "抱歉，路线查询失败", Toast.LENGTH_SHORT).show();
                enNode = PlanNode.withLocation(new LatLng(Constants.pointLatitude, Constants.pointLongtitude));
                mSearch.drivingSearch((new DrivingRoutePlanOption())
                        .from(stNode)
                        .to(enNode));
                userLocationInit();
            }
            if (result != null && result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                //result.getSuggestAddrInfo()
                return;
            }
            if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
//            RouteLine route = result.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
//            routeOverlay = overlay;
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                mBaiduMap.setMyLocationEnabled(false);
            }
        }
    };

    public MapTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapTabFragment newInstance(String param1, String param2) {
        MapTabFragment fragment = new MapTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SDKInitializer.initialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.workdetails_fragment_map, container, false);
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();



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
//        moveUserPoint();

        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(onGetRoutePlanResultListener);
        stNode = PlanNode.withLocation(new LatLng(MyBDLocation.getLatitude(), MyBDLocation.getLongtitude()));
        enNode = PlanNode.withLocation(new LatLng(Constants.pointLatitude, Constants.pointLongtitude));

        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));

        return view;
    }

//    /**
//     * 界面移动到当前位置
//     */
//    private void moveUserPoint() {
//
//        double userlatitude = Constants.userLatitude;
//        double userlongtitude = Constants.userLongtitude;
//        if (firstflag && (userlatitude != 0) && (userlongtitude != 0)) {
//            firstflag = false;
//            LatLng userPoint = new LatLng(userlatitude, userlongtitude);
//            //定义地图状态
//            MapStatus mMapStatus = new MapStatus.Builder()
//                    .target(userPoint)
//                    .zoom(14)
//                    .build();
//            //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
//            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
//            //改变地图状态
//            mBaiduMap.setMapStatus(mMapStatusUpdate);
//        }
//    }

    /**
     * 显示当前位置
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_userpoint);
//            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker);
//            }
            return null;
        }
    }

}
