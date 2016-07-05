package com.cloudvision.tanzhenv2.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.activity.MainActivity;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.tanzhenv2.order.model.ImageRoot;
import com.cloudvision.util.MyLog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;

/**
*  功能描述：引导页
*  
*  by  谭智文
*/
public class StartActivity extends Activity {
	
	private static final String TAG = "StartActivity";
	private static final String CLASS_NAME = "com.cloudvision.tanzhenv2.order.StartActivity";

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    
    private boolean isAdBack = false;
    
    private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (CommonUtils.isForeground(StartActivity.this, CLASS_NAME)) {
					Button enterMain = (Button) findViewById(R.id.enter_main);
					enterMain.performClick();
				}
				break;

			default:
				break;
			}
		}
	};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_main);
        
        initImageConfig();
        MySharedPreferencesUtils.getInstance(StartActivity.this, "share_data");
        
        initView();
        
        //获取网络图片
        initImageSource(1,httpMainImage);
        initImageSource(2,httpStartImage);
        
        
    }
    
    @Override
   	protected void onResume() {
    	super.onResume();
    	if (isAdBack) {
    		inittime(300);
    		isAdBack = true;
		}else {
			inittime(3000);
		}
    }
    
    /**
    *  image-loader初始化配置
    *  
    *  by  谭智文
    */
    private void initImageConfig(){
    	
    	File cacheDir = new File(Constants.CACHE_PATH);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration  
				    .Builder(StartActivity.this)  
				    .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽  
				    .threadPoolSize(3)//线程池内加载的数量  
				    .threadPriority(Thread.NORM_PRIORITY - 2)  
				    .denyCacheImageMultipleSizesInMemory()  
				    .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现  
				    .memoryCacheSize(2 * 1024 * 1024)
				    .diskCache(new UnlimitedDiskCache(cacheDir))
				    .tasksProcessingOrder(QueueProcessingType.LIFO)  
				    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())  
				    .imageDownloader(new BaseImageDownloader(StartActivity.this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间  
				    .build();//开始构建 
		
	    options = new DisplayImageOptions.Builder()  
        .cacheInMemory(true)        //启用内存缓存  
        .cacheOnDisk(true)
        .build();  

	    ImageLoader.getInstance().init(config);//全局初始化此配置  
    }
    
    /**
     * 初始化组件
     */
    private void initView() {
    	
    	ImageView adImageView = (ImageView) findViewById(R.id.ad_image);
    	String imageUrl = parseImageData();
    	MyLog.e(TAG, "ad Image url : "+ imageUrl);
    	ImageLoader.getInstance().displayImage(imageUrl, adImageView, options);
    	adImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyLog.i(TAG, "点击了启动页的广告。。。");
				Intent intent = new Intent();
				intent.putExtra("webAddress", "http://www.baidu.com/");
			    intent.setClass(StartActivity.this, WebViewActivity.class);	
			    StartActivity.this.startActivity(intent);
			}
		});
    	
    	Button enterMain = (Button) findViewById(R.id.enter_main);
        enterMain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				boolean FirstEntryApp = (Boolean) MySharedPreferencesUtils.get("FirstEntryApp", false);
		        if (FirstEntryApp) {
		        	intent = new Intent(StartActivity.this, GuideActivity.class);
		        	StartActivity.this.startActivity(intent);
		        	MySharedPreferencesUtils.put("FirstEntryApp", false);
				}else {	
					intent = new Intent(StartActivity.this, MainActivity.class);
		        	StartActivity.this.startActivity(intent);
				}
				finish();
			}
		});
    }
    
    private String parseImageData() {
    	
    	String urlString = "";
    	
    	String result = (String) MySharedPreferencesUtils.get("StartimageData", "");
    	Gson gson = new Gson();
    	if (!"".equals(result)) {
			
    		ImageRoot imageData = null;
            try {
            	imageData = gson.fromJson(result, ImageRoot.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
            
            if (null != imageData) {
            	urlString = imageData.getImgs().get(0).getUrl();
            }
		}
		return urlString;
	}
    
    /* 初始化时间跳转器 */
	private void inittime(final int count) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				
				Message msg = new Message();
				msg.what = 0;
				handler.sendMessageDelayed(msg, count);
			}
		}).start();
	}

    /**
     * 获取图片地址
     * 
     */
    private void initImageSource(int parm, HttpServiceInterface getItr){
    	
    	StringBuilder data = new StringBuilder();
		data.append("type=");
		data.append(parm);
     			 
        MyLog.i(TAG, "data: " + data.toString());
        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_START_IMAGE;
        url = url + data03;
        url = url.replaceAll("\n", ""); 	//base64会分段加入\n，导致请求失败
        MyLog.i(TAG, "url :" + url);

        HttpService httpService = new HttpService(StartActivity.this);
        httpService.get(url, getItr, null);
    }
    
    private HttpServiceInterface httpStartImage = new HttpServiceInterface() {
        @Override
        public void getResult(String result, Object objParam) {
        	       	
        	MyLog.e(TAG, "result: " + result);
        	if(!result.equals("")){
            	
            	Gson gson = new Gson();
        		ImageRoot imageData = null;
                try {
                	imageData = gson.fromJson(result, ImageRoot.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                
                if (null != imageData && null != imageData.getImgs()) {
                	MySharedPreferencesUtils.put("StartimageData", result);
                }
            }
        }
    };
    
    private HttpServiceInterface httpMainImage = new HttpServiceInterface() {
        @Override
        public void getResult(String result, Object objParam) {
        	       	
        	MyLog.e(TAG, "result: " + result);
            if(!result.equals("")){
            	Gson gson = new Gson();
        		ImageRoot imageData = null;
                try {
                	imageData = gson.fromJson(result, ImageRoot.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                
                if (null != imageData && null != imageData.getImgs()) {
                	MySharedPreferencesUtils.put("MainimageData", result);
                }
            }
        }
    };

    
}