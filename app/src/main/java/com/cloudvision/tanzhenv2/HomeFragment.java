package com.cloudvision.tanzhenv2;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudvision.listview.adapter.HomeListViewAdapter;
import com.cloudvision.tanzhenv2.activity.ServerIpPortActivity;
import com.cloudvision.tanzhenv2.activity.UserActivity;
import com.cloudvision.tanzhenv2.order.WebViewActivity;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CircleImageView;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.deal.OnlyToast;
import com.cloudvision.tanzhenv2.order.function.Bullet;
import com.cloudvision.tanzhenv2.order.function.YoumengPush;
import com.cloudvision.tanzhenv2.order.function.refreshCallbackImpl;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.tanzhenv2.order.model.ImageJson;
import com.cloudvision.tanzhenv2.order.model.ImageRoot;
import com.cloudvision.ui.viewpager.AutoScrollViewPager;
import com.cloudvision.util.MyLog;
import com.cloudvision.util.SPUtils;
import com.cloudvision.viewpager.adapter.AdvertImagePagerAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements OnClickListener{

	private String TAG = "HomeFragment";
	
	private AutoScrollViewPager viewPager;
	private HomeListViewAdapter adapter = null;
		
	private TextView userTextView;
	private CircleImageView imageFace;
	private ImageView setView;
	private ListView listview;
//	private Bitmap bitmap = null;		//用户头像图片
	
//	private List<Integer> imageIdList;
	private String[] titleList; 
      
//    private Handler faceHandler = new Handler(){
//        public void handleMessage(Message msg) {  
//            	if (null != imageFace && null != bitmap){
//            		imageFace.setImageBitmap(bitmap);
//            	}else {
//					this.postDelayed(runnable, 1000);
//				}
//            super.handleMessage(msg);
//        }
//    };
   
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_home, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
				
		initData();
		initView();
		initAutoLogin();		
	}
	
	public void initData()
	{
		titleList = new String[]{"shottag","notice","shottag","funtion"};
	}
	
	public void initView()
	{
		//头像点击监听（登录成功则出现） -- 谭智文
		imageFace = (CircleImageView) this.getActivity().findViewById(R.id.top_user);
		imageFace.setOnClickListener(this);
		
		userTextView = (TextView)this.getActivity().findViewById(R.id.top_login); // 改为登录 -- 谭智文
		userTextView.setOnClickListener(this);
		setView = (ImageView)this.getActivity().findViewById(R.id.top_setting);
		setView.setOnClickListener(this);
		
		viewPager = (AutoScrollViewPager)this.getActivity().findViewById(R.id.view_pager_advert);
		
//		List<String> imageIdList = parseImageData();		//改为网络获取 -- 谭智文
//		if (imageIdList.isEmpty()) {
//			imageIdList.add("drawable://"+R.drawable.ad1);
//			imageIdList.add("drawable://"+R.drawable.ad2);
//			imageIdList.add("drawable://"+R.drawable.ad3);
//		}else if (imageIdList.size() < 3) {
//			for (int i = 0; i < 3 - imageIdList.size(); i++) {
//				imageIdList.add("drawable://"+R.drawable.ad3);
//			}
//		}
	
		List<String> imageIdList = new ArrayList<String>();		//改为网络获取 -- 谭智文
		imageIdList.add("drawable://"+R.drawable.ad1);
		imageIdList.add("drawable://"+R.drawable.ad2);
		imageIdList.add("drawable://"+R.drawable.ad3);
		
		//增加全局变量adapter，接收到公告后刷新内容  -- 谭智文
		viewPager.setAdapter(new AdvertImagePagerAdapter(this.getActivity(), imageIdList).setInfiniteLoop(true));
		
		viewPager.setInterval(3000);
		viewPager.startAutoScroll();
		viewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 %imageIdList.size());
		viewPager.setBorderAnimation(true);
		
		listview = (ListView) this.getActivity().findViewById(R.id.main_list);
		adapter = new HomeListViewAdapter(this.getActivity(),titleList);
		listview.setAdapter(adapter);
		listview.setDividerHeight(0);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		// stop auto scroll when onPause
		viewPager.stopAutoScroll();
	}

	@Override
	public void onResume() {
		MyLog.e(TAG, "HomeFragment onResume ....");
		super.onResume();
		// start auto scroll when onResume
		viewPager.startAutoScroll();
		//每次启动检测当时的登录状态并改变 -- 谭智文
		changeStatus();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		 int viewId = arg0.getId();
		 switch (viewId) {
		 case R.id.top_login:	//增加登录 -- 谭智文
             MyLog.e(TAG, "点击了登录");
            // startActivity(new Intent(this.getActivity(), LoginActivity.class));
			 Intent intent = new Intent(this.getActivity(), WebViewActivity.class);
			 intent.putExtra("webAddress","http://192.168.1.139:8080/wap/");
			 startActivity(intent);
             break;
		case R.id.top_user:
			MyLog.e(TAG, "点击了用户");
			startActivity(new Intent(this.getActivity(),UserActivity.class));
			break;
		case R.id.top_setting:
			MyLog.e(TAG, "点击了设置");
//			startActivity(new Intent(this.getActivity(),SetActivity.class));
			startActivity(new Intent(this.getActivity(),ServerIpPortActivity.class));
			break;
		default:
			break;
		}
	}
	
	/**
	 * 初始化自动登录 和 公告内容加载
	 * 
	 * by 谭智文
	 */
	private void initAutoLogin() {
		
		//初始化自动登录（第一次加载homeFragment时启动）
		boolean flag = (Boolean) MySharedPreferencesUtils.get("autoLoginFlag", true);
		if(flag){
			String userName = (String) MySharedPreferencesUtils.get("userName", "");
			String password = (String) MySharedPreferencesUtils.get("password", "");
			if((!userName.isEmpty()) && (!password.isEmpty()) ){
				loginHttp();
				MySharedPreferencesUtils.put("autoFrag", false);
			}
		}
			
		//获取公告内容
        Bullet bullet = new Bullet(getActivity());
        bullet.setCallfuc(new refreshCallbackImpl() {
            @Override
            public void refreshTip() {
                MyLog.e(TAG,"refreshTip Bullet...");
                adapter.notifyDataSetChanged();
            }
        });
        
	}
	
	/**
	 * 获取本地头像资源（改用联网获取）
	 * 
	 * by 谭智文
	 */
//	private Runnable runnable = new Runnable() {
//        public void run() {
//        	
//        	Object userName = MySharedPreferencesUtils.get("userName", "");
//        	if (CommonUtils.checkSdCard()) {
//                String tempImgPath = Constants.CACHE_PATH + userName;
//                int lastSlastPos = tempImgPath.lastIndexOf('/');
//                String dir = tempImgPath.substring(0, lastSlastPos);
//                File dirFile = new File(dir);
//                if (dirFile.exists()) {
//                    bitmap = BitUtils.decodeSampledBitmapFromFile(tempImgPath, 
//                    		UserActivity.IMAGE_SIZE_WIDTH, UserActivity.IMAGE_SIZE_HEIGHT);
//                }
//                faceHandler.sendMessage(new Message());
//            }
//        }
//	};
	
	/**
	 * 验证状态，若是登录状态则显示头像
	 * 
	 * by 谭智文
	 */
	private void changeStatus() {
		boolean loginFlag = (Boolean) MySharedPreferencesUtils.get("loginFlag", false);
		imageFace = (CircleImageView) this.getActivity().findViewById(R.id.top_user);
		userTextView = (TextView)this.getActivity().findViewById(R.id.top_login);
		
		if(loginFlag){
			
			userTextView.setVisibility(View.GONE);
    		imageFace.setVisibility(View.VISIBLE);
//    		faceHandler.post(runnable);
    		
    		String urlString = (String) MySharedPreferencesUtils.get("headImage", "");
            if(urlString.equals("")){
            	urlString = "file:///"+Constants.CACHE_PATH + MySharedPreferencesUtils.get("userName", "");
            }
        	ImageLoader.getInstance().displayImage(urlString, imageFace,
        			new DisplayImageOptions.Builder()
        			.cacheInMemory(true)
        	        .cacheOnDisk(true)
        	        .build());

		}else{
			MySharedPreferencesUtils.put("signFlag", false);
			userTextView.setVisibility(View.VISIBLE);
    		imageFace.setVisibility(View.GONE);
		}
		adapter.refreshSimAdapter();
	}
	
	/**
	 * 解析获取到的主页广告数据
	 * 
	 * by 谭智文
	 */
	private List<String> parseImageData() {
    	
		List<String> urlString = new ArrayList<String>();
    	String result = (String) MySharedPreferencesUtils.get("MainimageData", "");
    	Gson gson = new Gson();
    	if (!"".equals(result)) {
			
    		ImageRoot imageData = null;
            try {
            	imageData = gson.fromJson(result, ImageRoot.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            
            if (null != imageData) {
            	List<ImageJson> imageJsons = imageData.getImgs();
            	for (int i = 0; i < imageJsons.size(); i++) {
            		urlString.add(imageData.getImgs().get(i).getUrl());
				}
            }
		}
		return urlString;
	}
	
	/**
	 * 自动登录的网络请求
	 * 
	 * by 谭智文
	 */
	private void loginHttp() {

//        String url = Constants.URL_LOGIN;
		String ip = (String)SPUtils.get(HomeFragment.this.getActivity(), "serverIp","");
		String port = (String)SPUtils.get(HomeFragment.this.getActivity(), "serverPort","");
		String url = "http://"+ip+":"+port+"/mns/"+"app.do?login&ct=";

        String userName = (String) MySharedPreferencesUtils.get("userName", "");
        String password = (String) MySharedPreferencesUtils.get("password", "");
        String data = "loginType=Android&userName=" + userName + "&passWord=" + password;
        MyLog.i("login", "login data:" + data);
        data = CryptUtils.getInstance().encryptXOR(data);
        MyLog.i("login", "jiami data:" + data);
        url = url + data;
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        HttpService httpService = new HttpService(getActivity());
        httpService.get(url, loginResult, null);
    }

	/**
     * 登录返回值逻辑
     * 	成功则显示用户头像，否则显示登录字样
     * 
     * by 谭智文
     */
    private HttpServiceInterface loginResult = new HttpServiceInterface() {
    	
        @Override
	    public void getResult(String result, Object objParam) {
        	
	        Map<String, String> map = null;
	        try {
	            map = MapUtils.parseData(result);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        if (map != null) {
	            String value = map.get("returnCode");
	            if (value.equals("SUCCESS")) {
	            	//获取当前状态（签到签出状态）
	                if (map.containsKey("status") && map.get("status").equals("1")) {
	                    MySharedPreferencesUtils.put("signFlag", true);
	                    
	                    //add for upload
//	                    MySharedPreferencesUtils.put("upFlag", true);
//	                    SignInAndOut signInAndOut = new SignInAndOut(HomeFragment.this.getActivity());
//	        			signInAndOut.upData();
	                } 
	                if (map.containsKey("headImage") && !map.get("headImage").equals("")) {
	                	String faceImageUrl = map.get("headImage");
	                	MySharedPreferencesUtils.put("headImage", faceImageUrl);
					}
	                MySharedPreferencesUtils.put("loginFlag", true);
	                //推送初始化--谭智文
	                new YoumengPush(getActivity());
	            } else {
	            	MySharedPreferencesUtils.put("loginFlag", false);
	            	MySharedPreferencesUtils.put("signFlag", false);
	            	OnlyToast.showToast(map.get("returnMsg"));
	            }   
	        }else {
	        	MySharedPreferencesUtils.put("loginFlag", false);
            	MySharedPreferencesUtils.put("signFlag", false);
            	OnlyToast.showToast("网络错误,自动登录失败。");
			}
	        changeStatus();
	    }
    };
	
}
