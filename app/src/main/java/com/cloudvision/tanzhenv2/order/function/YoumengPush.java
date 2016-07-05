package com.cloudvision.tanzhenv2.order.function;

import android.content.Context;
import android.os.AsyncTask;

import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.util.MyLog;
import com.umeng.message.PushAgent;

/**
 * 友盟推送的的初始化
 * 
 * Created by 谭智文
 */
public class YoumengPush {
	
	private static final String TAG = "Push";
	
	private Context context;
	private static PushAgent mPushAgent;
	
	public YoumengPush(Context context) {
		this.context = context;
		initPush();
	}
	
	/**
     * 接收推送初始化
     * 
     * by 谭智文
     */
    private void initPush() {
        mPushAgent = PushAgent.getInstance(context);
        mPushAgent.onAppStart();
        mPushAgent.enable();
        mPushAgent.setMergeNotificaiton(true);

        String userName = (String) MySharedPreferencesUtils.get("userName", "");
        if(!userName.equals("")){
        	new AddAliasTask(userName, Constants.ALIAS_TYPE).execute();
        }
    }
    
    /**
	 * 异步任务,发出alias（推送）
	 * 
	 * by 谭智文
	 */
	class AddAliasTask extends AsyncTask<Void, Void, Boolean> {

        String alias;
        String aliasType;

        public AddAliasTask(String aliasString, String aliasTypeString) {
            // TODO Auto-generated constructor stub
            this.alias = aliasString;
            this.aliasType = aliasTypeString;
        }

        protected Boolean doInBackground(Void... params) {
            try {
                return mPushAgent.addAlias(alias, aliasType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (Boolean.TRUE.equals(result))
                MyLog.e(TAG, "alias was set successfully.");
        }
    }
	
	public static void closePush() {
		MyLog.e(TAG,"关闭接收推送");
		mPushAgent.disable();
	}

}
