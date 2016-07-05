package com.cloudvision.tanzhenv2.application;

import android.app.Application;
import android.support.v4.content.LocalBroadcastManager;

public class ContextUtil extends Application {
	private static ContextUtil instance;

//  static private Context context0;
  
  public LocalBroadcastManager localBroadcastManager;
  
  public Boolean isFromWifi;
  public Boolean connecrService;
  public Boolean ifClientService;//client服务

  
  public static ContextUtil getInstance_o() {
      return instance;
  }

  @Override
  public void onCreate() {
      // TODO Auto-generated method stub
      super.onCreate();
//      CrashHandler crashHandler = CrashHandler.getInstance();  
//      crashHandler.init(getApplicationContext());
      instance = this;
      this.localBroadcastManager = LocalBroadcastManager.getInstance(this);
      this.isFromWifi = false;
      this.connecrService = false;
      this.ifClientService = false;
  }
}
