package com.cloudvision.tanzhenv2.activity;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.cloudvision.appconfig.AppConfig;
import com.cloudvision.service.ClientService;
import com.cloudvision.tanzhenv2.FileFragment;
import com.cloudvision.tanzhenv2.HomeFragment;
import com.cloudvision.tanzhenv2.MapFragment;
import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.application.ContextUtil;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.deal.OnlyToast;
import com.cloudvision.tanzhenv2.order.function.MyBDLocation;
import com.cloudvision.ui.tabitem.ChangeColorIconWithTextView;
import com.cloudvision.util.AppSP;
import com.cloudvision.util.MyLog;
import com.cloudvision.util.SPUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnClickListener{
	
	private String TAG = "MainActivity";
	private List<ChangeColorIconWithTextView> mTabIndicator = new ArrayList<ChangeColorIconWithTextView>();
	private HomeFragment homeFragment;
//	private TestFragment testFragment;
	private MapFragment mapFragment;
	private FileFragment fileFragment;
	private int currentPage;
	private long mExitTime;
	private int workId;
	private int fragId;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //增加功能初始化 -- 谭智文
        
        initApp();
        initFunction();
        initData();
        initTabIndicator();
        initView();

    }
    
    private void initApp()
    {
    	if(new AppSP(MainActivity.this).getFirstRun())
		{
    		MyLog.e("AppSP","第一次安装");
			appSetFirst();
		}
    	else {
    		MyLog.e("AppSP","非第一次安装");
		}
    }
    
    private void appSetFirst()
    {
    	new AppSP(MainActivity.this).setFirstRun(false);
		SPUtils.put(MainActivity.this, "serverIp", "192.168.1.1");
		SPUtils.put(MainActivity.this, "serverPort", "8080");

		if (android.os.Environment.getExternalStorageState().
				equals(android.os.Environment.MEDIA_MOUNTED))
		{
			File file = new File(AppConfig.CacheDir);
			if(!file.exists())
			{
				file.mkdir();
			}
			File fileConfigDir = new File(AppConfig.CacheConfigDir);
			if(!fileConfigDir.exists())
			{
				fileConfigDir.mkdir();
			}
		}
		else
		{
			MyLog.e(TAG, "SD");
			
		}
    }
    /**
     * 初始化功能
     * 		百度地图定位（用于信息上报）
     * 		启动时自动登录控制
     * 		读取配置文件
     * 
     * Created by 谭智文
     */
    private void initFunction() {
    	
    	//ImageLoader初始化
    	File cacheDir = new File(Constants.CACHE_PATH);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration  
				    .Builder(MainActivity.this)  
				    .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽  
				    .threadPoolSize(3)//线程池内加载的数量  
				    .threadPriority(Thread.NORM_PRIORITY - 2)  
				    .denyCacheImageMultipleSizesInMemory()  
				    .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现  
				    .memoryCacheSize(2 * 1024 * 1024)
				    .diskCache(new UnlimitedDiskCache(cacheDir))
				    .tasksProcessingOrder(QueueProcessingType.LIFO)  
				    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())  
				    .imageDownloader(new BaseImageDownloader(MainActivity.this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间  
				    .build();//开始构建 
	    ImageLoader.getInstance().init(config);//全局初始化此配置  
    	
    	//工具类初始化
    	MySharedPreferencesUtils.getInstance(MainActivity.this, "share_data");
    	OnlyToast.getInstance(MainActivity.this);
    	//启动百度定位 -- 谭智文
        SDKInitializer.initialize(getApplicationContext());
        MyBDLocation.getInstance(MainActivity.this);
		MyBDLocation.startBaidu();
        //自动登录autoLoginFlag
		MySharedPreferencesUtils.put("loginFlag", false);
	}
    
    private void initData()
    {
//    	homeFragment = new HomeFragment();
//    	testFragment = new TestFragment();
//    	mapFragment = new MapFragment();
//    	fileFragment = new FileFragment();
    	//增加fragment的初始化 -- 谭智文
//    	FragmentManager fm = getFragmentManager();  
//        FragmentTransaction transaction = fm.beginTransaction(); 
//        transaction.add(R.id.framelayout,homeFragment);
//    	transaction.add(R.id.framelayout,testFragment);
//    	transaction.add(R.id.framelayout,mapFragment);
//    	transaction.add(R.id.framelayout,fileFragment);
//    	transaction.commit();
    	
    	currentPage = 0;
    }
    
    private void initTabIndicator()
	{
		ChangeColorIconWithTextView one = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_one);
//		ChangeColorIconWithTextView two = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_two);
//		ChangeColorIconWithTextView three = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_three);
//		ChangeColorIconWithTextView four = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_four);

		mTabIndicator.add(one);
//		mTabIndicator.add(two);
//		mTabIndicator.add(three);
//		mTabIndicator.add(four);

		one.setOnClickListener(this);
//		two.setOnClickListener(this);
//		three.setOnClickListener(this);
//		four.setOnClickListener(this);

		one.setIconAlpha(1.0f);
	}
    
    private void initView()
    {
    	replaceFragment(homeFragment);
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent intent = getIntent();
		int id = intent.getIntExtra("fragid", 0);
		MyLog.e(TAG, String.valueOf(id));
		if(id == 1)
		{
			workId = intent.getIntExtra("orderid", 0);
			MyLog.e(TAG, String.valueOf(workId));
			resetOtherTabs();
			mTabIndicator.get(1).setIconAlpha(1.0f);
			changePage(1);
			intent.putExtra("fragid", 0);
		}
	}
    
    @Override
	public void onClick(View v)
	{

		resetOtherTabs();

		switch (v.getId())
		{
		case R.id.id_indicator_one:
			mTabIndicator.get(0).setIconAlpha(1.0f);
			changePage(0);
			break;
//		case R.id.id_indicator_two:
//			mTabIndicator.get(1).setIconAlpha(1.0f);
//			changePage(1);
//			break;
//		case R.id.id_indicator_three:
//			mTabIndicator.get(2).setIconAlpha(1.0f);
//			changePage(2);
//			break;
//		case R.id.id_indicator_four:
//			mTabIndicator.get(3).setIconAlpha(1.0f);
//			changePage(3);
//			break;

		}

	}
    
    public void changePage(int page)
    {
    	if(page != currentPage)
    	{
    		currentPage = page;
    		if(page == 0)
    		{
    			replaceFragment(homeFragment);
    		}
//    		if(page == 1)
//    		{
//    			replaceFragment(testFragment);
//    		}
//    		if(page == 2)
//    		{
//    			replaceFragment(mapFragment);
//    		}
//    		if(page == 3)
//    		{
//    			replaceFragment(fileFragment);
//    		}
    	}
    }
    
    /**
	 * 重置其他的Tab
	 */
	private void resetOtherTabs()
	{
		for (int i = 0; i < mTabIndicator.size(); i++)
		{
			mTabIndicator.get(i).setIconAlpha(0);
		}
	}
	
	public void replaceFragment(Fragment fragment){
		FragmentManager fm = getFragmentManager();  
        FragmentTransaction transaction = fm.beginTransaction();   
//        transaction.replace(R.id.framelayout, fragment); 
        //更改显示方式为show/hide -- 谭智文
        hideFragments(transaction);
//        if(currentPage == 0)
//        {
//        	transaction.show(homeFragment);
//        }
//        if(currentPage == 1)
//        {
//        	transaction.show(testFragment);
//        }
//        if(currentPage == 2)
//        {
//        	transaction.show(mapFragment);
//        }
//        if(currentPage == 3)
//        {
//        	transaction.show(fileFragment);
//        }
        switch (currentPage){
    	case 0:
    		if(homeFragment == null){
            	homeFragment = new HomeFragment();
            	transaction.add(R.id.framelayout,homeFragment);
            }else{
            	transaction.show(homeFragment);
            }
    		break;
    	case 1:
//    		if(testFragment == null){
//    			testFragment = new TestFragment();
//            	transaction.add(R.id.framelayout,testFragment);
//            }else{
//            	transaction.show(testFragment);
//            }
    		break;
    	case 2:
    		if(mapFragment == null){
    			mapFragment = new MapFragment();
            	transaction.add(R.id.framelayout,mapFragment);
            }else{
            	transaction.show(mapFragment);
            }
    		break;
    	case 3:
    		if(fileFragment == null){
    			fileFragment = new FileFragment();
            	transaction.add(R.id.framelayout,fileFragment);
            }else{
            	transaction.show(fileFragment);
            }
    		break;
    		default:break;
    }
        transaction.commit();
    }
	
	private void hideFragments(FragmentTransaction ft){
        if(homeFragment != null){
            ft.hide(homeFragment);
        }
//        if(testFragment !=null ){
//            ft.hide(testFragment);
//        }
        if(mapFragment != null){
            ft.hide(mapFragment);
        }
        if(fileFragment != null){
            ft.hide(fileFragment);
        }
    }
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
            } 
            else 
            {
            	ContextUtil contextUtil = ContextUtil.getInstance_o();
            	if(contextUtil.ifClientService)//关闭连接设备AP服务
            	{
            		stopClientService();
            	}
            	
            	Object modeStr = SPUtils.get(this, "mode","String");
//        		if(modeStr.equals("ap"))//关闭热点
//        		{
//        			ServiceUtil.stopConnectDevice(MainActivity.this);
//        			if(testFragment != null)
//        			{
//        				testFragment.setWifiApEnabled(false);
//        			}
//        		}

//            	Probe myProbe = new Probe();
//        		myProbe.ssm.bSockRouterUsable = false;
//        		myProbe.srp.bRouterRecvFlag = false;
//        		myProbe.ssm.needRouterHeart = false;
            	finish();
            }
            return true;  
		}
		return false;
	}
	
	private void stopClientService()
	{
		Object modeStr = SPUtils.get(this, "mode","String");
		if(modeStr.equals("ap"))
		{
			MyLog.e(TAG, "apmode");
		}
		else {
			Intent intent = new Intent();
	  	    intent.setClass(this, ClientService.class);
	  	    this.stopService(intent);;
		}

	}

}
