package com.cloudvision.ui.tabitem;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;


/**
 * 自定义载入的滚动框
 * @author zhangyun
 *
 */
public class LoadingProgressDialog extends Dialog {
	private Context context = null;
	private static LoadingProgressDialog customProgressDialog = null;
	
	public LoadingProgressDialog(Context context){
		super(context);
		this.context = context;
	}
	
	public LoadingProgressDialog(Context context, int theme) {
        super(context, theme);
    }
	
	public static LoadingProgressDialog createDialog(Context context){
		customProgressDialog = new LoadingProgressDialog(context,R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.loading_progress_dialog);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;		
		return customProgressDialog;
	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (customProgressDialog == null){
    		return;
    	}
    	
        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
 
   /**
    * 设置标题
    * @param strTitle
    * @return
    */
    public LoadingProgressDialog setTitile(String strTitle){
    	return customProgressDialog;
    }
    
   /**
    * 设置消息
    * @param strMessage
    * @return
    */
    public LoadingProgressDialog setMessage(String strMessage){
    	TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
    	
    	if (tvMsg != null){
    		tvMsg.setText(strMessage);
    	}
    	
    	return customProgressDialog;
    }
}