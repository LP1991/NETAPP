package com.cloudvision.ui.tabitem;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;


/**
 * 自定义的Dialog  两个按钮
 * @author 
 *
 */
public class DialogAlert_two_btn {
	private AlertDialog dialog;

	/**
	 * 构造函数
	 * @param context  		Context对象
	 * @param title    		Title
	 * @param contentTxt	内容message
	 * @param btn1Txt       btn1的文本
	 * @param btn2Txt		btn2的文本
	 * @param listen1		btn1的监听器
	 * @param listen2		btn2的监听器
	 */
	public DialogAlert_two_btn(Context context, String title, String contentTxt,String btn1Txt,String btn2Txt,
			OnClickListener listen1,OnClickListener listen2) {
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
		dialog.getWindow().setContentView(R.layout.app_dialog_alert_two_btn); 
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		TextView title_txt = (TextView) dialog.findViewById(R.id.title_txt);
		TextView content_txt = (TextView) dialog.findViewById(R.id.content_txt);
		
		title_txt.setText(title);
		content_txt.setText(contentTxt);
		
		TextView btn1_txt = (TextView) dialog.findViewById(R.id.btn1_txt);
		TextView btn2_txt = (TextView) dialog.findViewById(R.id.btn2_txt);
		
		btn1_txt.setText(btn1Txt);
		btn2_txt.setText(btn2Txt);
		
		LinearLayout ly1 = (LinearLayout) dialog.findViewById(R.id.ly1);
		LinearLayout ly2 = (LinearLayout) dialog.findViewById(R.id.ly2);
		
		ly1.setOnClickListener(listen1);
		ly2.setOnClickListener(listen2);
		
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