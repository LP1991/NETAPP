package com.cloudvision.util;

import android.content.Context;

import com.cloudvision.ui.tabitem.LoadingProgressDialog;

public class MyProgressDialog {
	
	public static LoadingProgressDialog myDialog;
	public static void show(Context context,LoadingProgressDialog dialog,String msg,Boolean flag)  
    {
		dismiss();
		dialog = LoadingProgressDialog.createDialog(context);
		dialog.setMessage(msg);
		dialog.setCancelable(flag);
		myDialog = dialog;
		myDialog.show(); 
    }

	public static void dismiss()
    {  
		if(myDialog != null)
		{
			myDialog.dismiss();
		}
    }
	public static void  setMessage(String msg)  
    {  
		if(myDialog != null)
		{
			myDialog.setMessage(msg);
		}
    }
}
