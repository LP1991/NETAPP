package com.cloudvision.tanzhenv2.order.function;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cloudvision.appconfig.AppConfig;
import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.tanzhenv2.order.model.UpdateJson;
import com.cloudvision.tanzhenv2.order.model.UpdateRoot;
import com.cloudvision.util.MyLog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Map;

public class AppUpdate {
	
	private static final String TAG = "AppUpdate";
	
    private static final int DOWNLOAD = 1;		//下载中
    private static final int DOWNLOAD_FINISH = 2;	//下载结束
    private static final int DOWNLOAD_FAILED = 3;	//下载结束

    private String target = AppConfig.CacheDir + "/";
    private UpdateJson updateJson;

    private Context mContext;
    private ProgressDialog pdDialog;
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    private int progress;		//下载进度
    private HttpHandler<?> handler = null;

    private Handler mHandler = new Handler(){
    	
        public void handleMessage(Message msg){
            switch (msg.what){
            // 正在下载
            case DOWNLOAD:
                // 设置进度条位置
                mProgress.setProgress(progress);
                break;
            case DOWNLOAD_FINISH:
                // 安装文件
                installApk();
                break;
            case DOWNLOAD_FAILED:
            	Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
            }
        };
    };

    public AppUpdate(Context context){
        this.mContext = context;
    }

    /**
     * 检查软件是否有更新版本
     * 
     * @return
     */
    public void checkUpdate(){
    	
    	showProgress();
        // 获取当前软件版本
//        int versionCode = getVersionCode(mContext);
    	String versionName = getVersionName(mContext);
        MyLog.e(TAG, versionName);
        StringBuilder data = new StringBuilder();
		data.append("type=1");
		data.append("&version=");
		data.append(versionName);
		MyLog.e(TAG, "url :" + data.toString());
		String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_APP_UPDATE;
        url = url + data03;
        MyLog.i(TAG, "url :" + url);
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        HttpService httpService = new HttpService(mContext);
        httpService.get(url, httpResult, null);
    }
    
    /**
     * Http的请求结果接收
     */
    private HttpServiceInterface httpResult = new HttpServiceInterface() {
        @Override
        public void getResult(String result, Object objParam) {

            MyLog.e(TAG, "result: " + result);
 
            Map<String, Object> map = null;
            try {
                map = MapUtils.parseObjectData(result);
            } catch (Exception e) {
                e.printStackTrace();
            } 
            pdDialog.cancel();
             
            if(map != null)
            {
            	if(map.containsKey("returnCode"))
            	{
            		String error =(String)map.get("returnMsg");
            		CommonUtils.showTips(mContext, "提示", error);
            	}
            	else {
            		if (parseImageData(result)) {
        				showNoticeDialog();		//需要更新，出现提示
        			}else {
        				CommonUtils.showTips(mContext, "提示", "已经是最新版本");
        			}
				}
            }
            else {
            	CommonUtils.showTips(mContext, "提示", "检查新版本失败");
			}
        }
    };
    
    private boolean parseImageData(String result) {
    	
    	boolean needUpdate = false;
    	
    	Gson gson = new Gson();
		UpdateRoot updateRoot = null;
		try {
			updateRoot = gson.fromJson(result, UpdateRoot.class);
        } catch (JsonSyntaxException e) {
        	e.printStackTrace();
        }
		if (null != updateRoot) {
			needUpdate = updateRoot.getNeedUpdate();
			updateJson = updateRoot.getAppVersion();
//			MyLog.e(TAG, updateJson.getMd5());
		}
		
		return needUpdate;
	}
    
    private void showProgress() {
		// 创建ProgressDialog对象    
		pdDialog = new ProgressDialog(mContext);   
		pdDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);    
        pdDialog.setTitle("检查更新");    
        pdDialog.setMessage("正在努力查找中……");     
        pdDialog.setProgress(100);    
        pdDialog.setIndeterminate(false);    
        pdDialog.setCancelable(true);    
        pdDialog.show();    
	}

	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context){
	    int versionCode = 0;
	    try{
	        // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
	        versionCode = context.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
	    } catch (NameNotFoundException e){
	        e.printStackTrace();
	    }
	    return versionCode;
	}
	
	private String getVersionName(Context context){
	    String versionName = "";
	    try{
	        // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
	    	versionName = context.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
	    } catch (NameNotFoundException e){
	        e.printStackTrace();
	    }
	    return versionName;
	}

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog(){
        // 构造对话框
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        // 更新
        builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
                // 显示下载对话框
                showDownloadDialog();
            }
        });
        // 稍后更新
        builder.setNegativeButton(R.string.soft_update_later, new OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog(){
    	
        // 构造软件下载对话框
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        int layoutId = R.layout.softupdate_progress; 
        View v = inflater.inflate(layoutId, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        // 取消更新
        builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
                // 取消下载
                handler.cancel();
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        // 下载文件
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk(){
        // 启动新线程下载软件
//        new downloadApkThread().start();
    	
    	HttpUtils http = new HttpUtils();
		handler = http.download(
			updateJson.getFilePath(),
			target+updateJson.getFileName(),
		    new RequestCallBack<File>() {

		        @Override
		        public void onStart() {
		        }

		        @Override
		        public void onLoading(long total, long current, boolean isUploading) { 
		        	MyLog.e(TAG, current+"/"+total);
                    progress = (int) (((float) current / total) * 100);// 计算进度条位置
                    mHandler.sendEmptyMessage(DOWNLOAD);// 更新进度
		        }

		        @Override
		        public void onSuccess(ResponseInfo<File> responseInfo) {
		        	mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
		        	mDownloadDialog.dismiss();
		        }
		        
		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	MyLog.e(TAG, "download Failure....");
		        	mDownloadDialog.dismiss();
		        }
		});
    	
    }

    /**
     * 安装APK文件
     */
    private void installApk(){
        File apkfile = new File(target, updateJson.getFileName());
        if (!apkfile.exists()){
            return;
        }
        String md5 = getFileMd5(target+updateJson.getFileName());
        MyLog.e(TAG+"md5", md5);
        if(!md5.equals(updateJson.getMd5()))
        {
        	MyLog.e(TAG, "md5校验失败");
        	apkfile.delete();
        	return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
    
    private String getFileMd5(String FilePath) {
		char hexdigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		FileInputStream fis = null;
		String sString;
		char str[] = new char[16 * 2];
		int k = 0;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			fis = new FileInputStream(FilePath);
			byte[] buffer = new byte[2048];
			int length = -1; // long s = System.currentTimeMillis();
			while ((length = fis.read(buffer)) != -1) {
				md.update(buffer, 0, length);
			}
			byte[] b = md.digest();
			for (int i = 0; i < 16; i++) {
				byte byte0 = b[i];
				str[k++] = hexdigits[byte0 >>> 4 & 0xf];
				str[k++] = hexdigits[byte0 & 0xf];
			}
			fis.close();
			sString = new String(str);
			return sString;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
