package com.cloudvision.tanzhenv2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.cloudvision.appconfig.AppConfig;
import com.cloudvision.file.UpdateFileActivity;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.tanzhenv2.order.model.UpdateJson;
import com.cloudvision.tanzhenv2.order.model.UpdateRoot;
import com.cloudvision.ui.tabitem.DialogAlert_one_btn;
import com.cloudvision.util.FileUtil;
import com.cloudvision.util.MyLog;
import com.cloudvision.util.SPUtils;
import com.cloudvision.util.Xmlparse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileFragment extends Fragment implements OnItemClickListener,HttpServiceInterface{
	

	private String TAG = "FileFragment";
	private GridView gView;
	private List<Map<String, Object>> data_list;
	private SimpleAdapter sim_adapter;
	private int[] icon;
	private String[] iconName;
	private Context context = FileFragment.this.getActivity();
	private String [] from;
	private int [] to;
	private Dialog updateDialog;
	private String[] typeName = {"eoc配置","catv频道配置","catv快速配置"};

	private String eocVer,catvVer,catvQuickVer;
	private UpdateJson updateJson;
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	private int progress;
	private String target = AppConfig.CacheConfigDir + "/";
	private HttpHandler<?> handler = null;
	private int currentConfig = 0;//0eoc 1catv 2quick
	private DialogAlert_one_btn tipDialog;
	
	private static final int DOWNLOAD = 1;		//下载中
    private static final int DOWNLOAD_FINISH = 2;	//下载结束
	
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
            	checkFile();
                break;
            default:
                break;
            }
        };
    };
	
	
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_file, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		initData();
		initView();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	public void initData()
	{
		iconName = new String[]{"测试报告","升级文件","更新配置"};
        icon = new int[]{R.drawable.paper,R.drawable.pppoe,R.drawable.workorder};
        
        gView = (GridView) this.getActivity().findViewById(R.id.gridview_file);
        data_list = new ArrayList<Map<String, Object>>();
		//获取数据
		getData();
		//新建适配器
		from = new String[]{"image","text"};
		to = new int[]{R.id.image,R.id.text};
		sim_adapter = new SimpleAdapter(this.getActivity(), data_list, R.layout.test_list_funtion_item, from, to);
		//配置适配器
		gView.setAdapter(sim_adapter);
		gView.setOnItemClickListener(this);
		
		updateDialog = new AlertDialog.Builder(FileFragment.this.getActivity()).
			    setTitle("获取升级文件").
			    setIcon(null).
			    setItems(typeName, new DialogInterface.OnClickListener() {
			 
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	getUpdate(which);
			    	currentConfig = which;
	     		}
			    }).
			    setNegativeButton("取消", new DialogInterface.OnClickListener() {
			    	//取消升级
			     @Override
			     public void onClick(DialogInterface dialog, int which) {
			      // TODO Auto-generated method stub
			     }
			     }).create();
		
//		eocVer = getEocFileVersion();
//		MyLog.e("eoc配置文件版本", eocVer);
//		
//		catvVer = getCatvFileVersion();
//		MyLog.e("catv配置文件版本", catvVer);
//
//		catvQuickVer = getCatvQuickFileVersion();
//		MyLog.e("catv快速测试配置文件版本", catvQuickVer);
		
		
	}
	
	public String getEocFileVersion()
	{
		String version = "";
//		File userEocfile = new File(AppConfig.CacheConfigDir+"/test_userEoc.xml");
//		if(userEocfile.exists())
//		{
//			check = new EocTestSax().sax(AppConfig.CacheConfigDir+"/test_userEoc.xml");
//			if(check!=null)
//			{
//				version = check.getVer();
//			}
//		}
		return version;
	}
	
	public String getCatvFileVersion()
	{
		String version = "";
		File catvFile = new File(AppConfig.CacheConfigDir+"/channel_list.xml");
		if(catvFile.exists())
		{
			Xmlparse xps = new Xmlparse(context);
			version = xps.getFileVersion(AppConfig.CacheConfigDir+"/channel_list.xml");
		}
		return version;
	}
	
	public String getCatvQuickFileVersion()
	{
		String version = "";
		File catvFile = new File(AppConfig.CacheConfigDir+"/quickscan_list.xml");
		if(catvFile.exists())
		{
			Xmlparse xps = new Xmlparse(context);
			version = xps.getFileVersion(AppConfig.CacheConfigDir+"/quickscan_list.xml");
		}
		return version;
	}
	
	public List<Map<String, Object>> getData(){		
		//cion和iconName的长度是相同的，这里任选其一都可以
		data_list.clear();
		for(int i=0;i<icon.length;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", icon[i]);
			map.put("text", iconName[i]);
			data_list.add(map);
		}
			
		return data_list;
	}
	
	public void initView()
	{

	}
	
	public void getUpdate(int index)
	{
		//构造MySharedPreferencesUtils
		Boolean loginFlag =(Boolean)MySharedPreferencesUtils.get("loginFlag", false);
		if(!loginFlag)
		{
			MyLog.e(TAG, "未登录");
			tipDialog = new DialogAlert_one_btn(FileFragment.this.getActivity(), "提示", "未登入", "确定", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					tipDialog.dismiss();
				}
			});
			return;
		}
		String mac =(String)SPUtils.get(FileFragment.this.getActivity(), "probeMac","");
		if(mac.length() == 0)
		{
			MyLog.e(TAG, "未获取设备信息");
			tipDialog = new DialogAlert_one_btn(FileFragment.this.getActivity(), "提示", "未获取设备信息", "确定", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					tipDialog.dismiss();
				}
			});
			return;
		}
        //设备升级文件
        StringBuilder data = new StringBuilder(256);
        data.append("userName=");
        data.append(MySharedPreferencesUtils.get("userName", ""));
        data.append("&probeMac=");
        data.append(SPUtils.get(FileFragment.this.getActivity(), "probeMac",""));
        data.append("&deviceType=");
        if(index == 0)
        {
        	data.append(Constants.TEST_CONFIG_1);
        }
        if(index == 1)
        {
        	data.append(Constants.TEST_CONFIG_2);
        }
        if(index == 2)
        {
        	data.append(Constants.TEST_CONFIG_3);
        }
        data.append("&versionNum=");
        if(index == 0)
        {
        	data.append(eocVer);
        }
        if(index == 1)
        {
        	data.append(catvVer);
        }
        if(index == 2)
        {
        	data.append(catvQuickVer);
        }
        MyLog.e(TAG, "work data: " + data.toString());
        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_TEST_CONFIG;
        url = url + data03;
        MyLog.e(TAG, "work url :" + url);
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        HttpService httpService = new HttpService(context);
        httpService.get(url, this, null);
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(arg2 ==0)
		{
//			Intent intent = new Intent();
//			intent.setClass(this.getActivity(), TestReportActivity.class);
//	  	    this.getActivity().startActivity(intent);
		}
		if(arg2 ==1)
		{
			Intent intent = new Intent();
			intent.setClass(this.getActivity(), UpdateFileActivity.class);
	  	    this.getActivity().startActivity(intent);
		}
		if(arg2 ==2)
		{
			eocVer = getEocFileVersion();
			MyLog.e("eoc配置文件版本", eocVer);
			
			catvVer = getCatvFileVersion();
			MyLog.e("catv配置文件版本", catvVer);

			catvQuickVer = getCatvQuickFileVersion();
			MyLog.e("catv快速测试配置文件版本", catvQuickVer);
			updateDialog.show();
		}
	}
	
	@Override
	public void getResult(String result, Object objParam) {
		// TODO Auto-generated method stub
		MyLog.e(TAG,result);
		
		Map<String, Object> map = null;
        try {
            map = MapUtils.parseObjectData(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(map != null)
        {
        	if(map.containsKey("returnCode"))
        	{
        		String error =(String)map.get("returnMsg");
        		tipDialog = new DialogAlert_one_btn(FileFragment.this.getActivity(), "提示", error, "确定", new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						tipDialog.dismiss();
					}
				});
        	}
        	else {
        		if (parseImageData(result)) {
        			downloadFile();
    			}else {
    				tipDialog = new DialogAlert_one_btn(FileFragment.this.getActivity(), "提示", "已是最新版本", "确定", new View.OnClickListener() {
    					@Override
    					public void onClick(View arg0) {
    						tipDialog.dismiss();
    					}
    				});
    			}
			}
        }
        else {
        	tipDialog = new DialogAlert_one_btn(FileFragment.this.getActivity(), "提示", "获取异常", "确定", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					tipDialog.dismiss();
				}
			});
		}
		
	}
	
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
			updateJson = updateRoot.getProbeConfig();
		}
		
		return needUpdate;
	}
	
	private void downloadFile()
	{
		MyLog.e(TAG, "开始下载");
		HttpUtils http = new HttpUtils();
		String fileName ="";
		if(currentConfig ==0)
		{
			fileName = "test_userEoc.xml";
		}
		if(currentConfig ==1)
		{
			fileName = "channel_list.xml";
		}
		if(currentConfig ==2)
		{
			fileName = "quickscan_list.xml";
		}
		handler = http.download(
				updateJson.getFilePath(),
				target+fileName,
		    new RequestCallBack<File>() {

		        @Override
		        public void onStart() {
		        }

		        @Override
		        public void onLoading(long total, long current, boolean isUploading) { 
		        	MyLog.e(TAG, current+"/"+total);
//                    progress = (int) (((float) current / total) * 100);// 计算进度条位置
//                    mHandler.sendEmptyMessage(DOWNLOAD);// 更新进度
		        }

		        @Override
		        public void onSuccess(ResponseInfo<File> responseInfo) {
//		        	mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
//		        	mDownloadDialog.dismiss();
		        	checkFile();
		        }
		        
		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	MyLog.e(TAG, "download Failure....");
		        	if(FileFragment.this.getActivity()!=null)
		        	{
		        		tipDialog = new DialogAlert_one_btn(FileFragment.this.getActivity(), "提示", "下载失败", "确定", new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								tipDialog.dismiss();
							}
						});
		        	}
		        }
		});
	}
	
	private void checkFile()
	{
		String fileName ="";
		if(currentConfig ==0)
		{
			fileName = "test_userEoc.xml";
		}
		if(currentConfig ==1)
		{
			fileName = "channel_list.xml";
		}
		if(currentConfig ==2)
		{
			fileName = "quickscan_list.xml";
		}
		File downloadFile = new File(target, fileName);
        if (!downloadFile.exists()){
            return;
        }
        String md5 = getFileMd5(target+fileName);
        MyLog.e(TAG+"md5", md5);
        if(!md5.equals(updateJson.getMd5()))
        {
        	MyLog.e(TAG, "md5校验失败");
        	downloadFile.delete();
        	if(currentConfig ==0)
    		{
        		try {
    				FileUtil.copyBigDataToSD(FileFragment.this.getActivity(), "test_eoc.xml", AppConfig.CacheConfigDir+"/test_userEoc.xml");
    			} catch (IOException e) {
    				MyLog.e("拷贝test_userEoc.xml失败", e.getMessage());
    			}
    		}
    		if(currentConfig ==1)
    		{
    			try {
    				FileUtil.copyBigDataToSD(FileFragment.this.getActivity(), "channel_list.xml", AppConfig.CacheConfigDir+"/channel_list.xml");
    			} catch (IOException e) {
    				MyLog.e("拷贝channel_list失败", e.getMessage());
    			}
    		}
    		if(currentConfig ==2)
    		{
    			try {
    				FileUtil.copyBigDataToSD(FileFragment.this.getActivity(), "quickscan_list.xml", AppConfig.CacheConfigDir+"/quickscan_list.xml");
    			} catch (IOException e) {
    				MyLog.e("拷贝quickscan_list.xml失败", e.getMessage());
    			}
    		}
        }
        else {
			MyLog.e(TAG, "md5校验成功");
			tipDialog = new DialogAlert_one_btn(FileFragment.this.getActivity(), "提示", "配置文件获取成功", "确定", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					tipDialog.dismiss();
				}
			});
		}

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
