package com.cloudvision.ui.tabitem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;


/**
 * 自定义的Dialog  一个按钮
 * @author zhangyun
 *
 */
public class DialogAlert_one_btn {
	private AlertDialog dialog;

	/**
	 * 构造函数
	 * @param context  		Context对象
	 * @param title    		Title
	 * @param contentTxt	内容message
	 * @param btn1Txt       btn1的文本
	 * @param listen1		btn1的监听器
	 */
	public DialogAlert_one_btn(Context context, String title, String contentTxt,String btn1Txt,
			OnClickListener listen1) {
		try
		{
			dialog = new AlertDialog.Builder(context).create();
			dialog.show();
		}
		catch(Exception ex)
		{
			dialog = new AlertDialog.Builder(((Activity)context).getParent()).create();
			dialog.show();
		}
		
		dialog.getWindow().setContentView(R.layout.app_dialog_alert_one_btn); 
		
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		TextView title_txt = (TextView) dialog.findViewById(R.id.title_txt);
		TextView content_txt = (TextView) dialog.findViewById(R.id.content_txt);
		
		title_txt.setText(title);
		content_txt.setText(contentTxt);
		
		TextView btn1_txt = (TextView) dialog.findViewById(R.id.btn1_txt);
		
		btn1_txt.setText(btn1Txt);
		
		LinearLayout ly1 = (LinearLayout) dialog.findViewById(R.id.ly1);
		
		ly1.setOnClickListener(listen1);
		
	}
	
	/**
	 * 关闭窗口
	 */
	public void dismiss()
	{
		if(dialog!=null)
		{
			dialog.dismiss();
		}
	}
	
}