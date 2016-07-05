package com.cloudvision.tanzhenv2.order.deal;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 *		确保只会出现一个Toast，不会累积
 * 
 * by 谭智文
 */
public class OnlyToast {
	
	private static Toast mToast;
	
	private static Context context;
	
	private OnlyToast(Context context) {
		OnlyToast.context = context;
    }

    public static void getInstance(Context context) {
        new OnlyToast(context);
    }

	public static void showToast(String text) {  
        if(mToast == null) {  
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);  
        } else {  
            mToast.setText(text);    
            mToast.setDuration(Toast.LENGTH_SHORT);  
        }  
        mToast.show();  
    } 
}
