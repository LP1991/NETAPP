package com.cloudvision.tanzhenv2.order;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudvision.appconfig.AppConfig;
import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.activity.NetManagerActivity;
import com.cloudvision.tanzhenv2.activity.ServerIpPortActivity;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.function.ProgressWebView;
import com.cloudvision.util.MyProgressDialog;
import com.cloudvision.util.SPUtils;

public class WebViewActivity extends Activity{
	private static boolean login = false;
	private   String emsServerUrl="";
	
	private Context context;
	private TextView back;
	private ImageView setView;
	private ProgressWebView  webView;
	private ImageView tab_web_backward, tab_web_forward, tab_web_home, tab_web_refresh;
	private boolean checkUrlLoaded=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		
//		Intent i=this.getIntent();
//		String url = i.getStringExtra("webAddress");
//		if (url != null && !"".equals(url)){
//			weiBoUrl= url;
//		}
		SPUtils.put(WebViewActivity.this,"loginFlag",false);
		context=this;
		initView();
		listener();
		String ip = (String) SPUtils.get(this.context,"serverIp","");
		String port = (String)SPUtils.get(this.context, "serverPort","");
		if ("".equals(ip)|| "".equals(port)){
			startActivityForResult(new Intent(WebViewActivity.this,ServerIpPortActivity.class),0);
		}else {
			emsServerUrl = "http://"+ip+":"+port+"/"+AppConfig.EMSWAP_DEPLOY_NAME+"/";
			loadWebView();
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		back = (TextView) findViewById(R.id.top_scan);
		webView=(ProgressWebView) findViewById(R.id.webView);
		setView = (ImageView)findViewById(R.id.top_setting);
		tab_web_backward=(ImageView) findViewById(R.id.tab_web_backward);
		tab_web_forward=(ImageView) findViewById(R.id.tab_web_forward);
		tab_web_home=(ImageView) findViewById(R.id.tab_web_home);
		tab_web_refresh=(ImageView) findViewById(R.id.tab_web_refresh);
	}

/*	@Override
	public boolean shouldUpRecreateTask(Intent targetIntent) {
		// TODO Auto-generated method stub
		return super.shouldUpRecreateTask(targetIntent);
	}
*/
		
	private void listener() {
		// TODO Auto-generated method stub
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				System.out.println("WebViewActivity.onClick");
//				System.out.println("--------------------------->"+login);
				if (!login){
					CommonUtils.showTips(WebViewActivity.this, "未登陆", "请登录后使用");
					//loadWebView();
				}else {
					Intent intent = new Intent(WebViewActivity.this, NetManagerActivity.class);
					startActivity(intent);
				}
			}
		});
		setView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(WebViewActivity.this,ServerIpPortActivity.class),0);
			}
		});
		tab_web_backward.setOnClickListener(listener);
		tab_web_forward.setOnClickListener(listener);
		tab_web_home.setOnClickListener(listener);
		tab_web_refresh.setOnClickListener(listener);
	}
	
	private View.OnClickListener listener=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
//			case R.id.top_back:
//				CookieSyncManager.createInstance(context);
//				CookieSyncManager.getInstance().startSync();
//				CookieManager.getInstance().removeSessionCookie();
//				webView.clearCache(true);
//				webView.clearHistory();
//				finish();
//				break;
			case R.id.tab_web_backward:
				if(webView.canGoBack()){
					webView.goBack();	
				}else{
					WebViewActivity.this.finish();
				}
				
				break;
			case R.id.tab_web_forward:
				webView.goForward();
				break;
			case R.id.tab_web_home:
				if (webView == null){
					loadWebView();
				}else {
					webView.loadUrl(emsServerUrl+"pages/main");
				}
				break;
			case R.id.tab_web_refresh:
				if(checkUrlLoaded) webView.stopLoading();
				else webView.reload();
				break;
			default:
				break;
			}
		}
	};
	
	@SuppressLint("SetJavaScriptEnabled")
	private void loadWebView(){
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.contains("username") && url.contains("password")){
//					System.out.println("WebViewActivity.shouldOverrideUrlLoading");

				}
				if (url.contains("/pages/main")){
					login = true;
					SPUtils.put(WebViewActivity.this,"loginFlag",true);
				}
				if (url.contains("logout")){
					login = false;
					SPUtils.put(WebViewActivity.this,"loginFlag",false);
				}
//				System.out.println(url);
				webView.loadUrl(url);
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
            	tab_web_refresh.setImageResource(R.drawable.stop_load);
            	checkUrlLoaded=true;
				MyProgressDialog.show(WebViewActivity.this, null, "拼命加载中……", false);
				super.onPageStarted(view, url, favicon);
			}


			@Override
            public void onPageFinished(WebView view, String url) {
            	if(webView.canGoBack()){					
					tab_web_backward.setImageResource(R.drawable.back_white);
				}/*else{
					tab_web_backward.setImageResource(R.drawable.back_gray);
				}*/
                if(webView.canGoForward()){					
    				tab_web_forward.setImageResource(R.drawable.foward_white);
    			}else{					
    				tab_web_forward.setImageResource(R.drawable.foward_gray);;
    			}
                tab_web_refresh.setImageResource(R.drawable.tab_web_refresh);
                checkUrlLoaded=false;
				MyProgressDialog.dismiss();
                super.onPageFinished(view, url);
            }

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				MyProgressDialog.dismiss();
				String msg = "网络错误";
				String title = "ERROR"+errorCode;
				if (errorCode > 400){
//					CommonUtils.showTips(WebViewActivity.this, "ERROR "+errorCode, "访问失败："+description+"\n"+failingUrl);
					msg = "访问失败："+description+"\n"+failingUrl;
				}else {
//					CommonUtils.showTips(WebViewActivity.this, "连接服务器失败", "网络错误，请检查网络连接和服务器设置");
					title = "连接服务器失败";
					msg = "网络错误，请检查网络连接和服务器设置";
				}
				Intent intent = new Intent(WebViewActivity.this,ServerIpPortActivity.class);
				intent.putExtra("title",title);
				intent.putExtra("msg",msg);
				startActivityForResult(intent,0);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});
		webView.setWebChromeClient(new WebChromeClient(){
			/*请求获取地理位置权限*/
			@Override
			public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				MyProgressDialog.setMessage("拼命加载中……"+newProgress+"%");
				super.onProgressChanged(view, newProgress);
			}


		});
		/*下载功能*/
		webView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
		WebSettings settings = webView.getSettings();
		settings.setDatabaseEnabled(true);
		String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
		settings.setGeolocationEnabled(true);
		settings.setGeolocationDatabasePath(dir);
		settings.setDomStorageEnabled(true);
		settings.setJavaScriptEnabled(true);
		settings.setAppCacheEnabled(true);
        webView.setInitialScale(100);
        settings.setSupportZoom(true);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
       	settings.setBuiltInZoomControls(true);
		webView.loadUrl(emsServerUrl);
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	if(webView.canGoBack()){        		
        		webView.goBack();
        		return true;  
        	}else{
				finish();
				System.exit(0);
        		return false;  
        	}
        } else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() > 0){
			finish();
			System.exit(0);
			return true;
		}else {
			return super.onKeyDown(keyCode, event);
		}
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK){
			String url = null;
			if (data != null){
				url = data.getStringExtra("webAddress");
			}

			if (url == null){
				String ip = (String) SPUtils.get(this.context,"serverIp","");
				String port = (String)SPUtils.get(this.context, "serverPort","");
				emsServerUrl = "http://"+ip+":"+port+"/"+ AppConfig.EMSWAP_DEPLOY_NAME+"/";
			}else {
				emsServerUrl = url;
			}
			loadWebView();
		}
	}
	
}

