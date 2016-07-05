package com.cloudvision.file;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cloudvision.appconfig.AppConfig;
import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.TestBaseActivity;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.tanzhenv2.order.model.UpdateJson;
import com.cloudvision.tanzhenv2.order.model.UpdateRoot;
import com.cloudvision.ui.tabitem.DialogAlert_one_btn;
import com.cloudvision.ui.tabitem.DialogAlert_two_btn;
import com.cloudvision.util.MyLog;
import com.cloudvision.util.SPUtils;
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
import java.util.ArrayList;
import java.util.Map;

public class UpdateFileActivity extends TestBaseActivity implements OnClickListener,OnItemLongClickListener,HttpServiceInterface{
	
	private String TAG = "UpdateFileActivity";
	private ListView fileListView;
	
	private ArrayList<String> mListItems;
	private ArrayList<String> pathItems;
	private SampleListAdapter mAdapter;
	private File[] files;
	private DialogAlert_two_btn deleteDialog;
	private DialogAlert_two_btn checkUpdateDialog;
	private ProgressBar mProgress;
	private String target = AppConfig.CacheUpdateDir + "/";
	
	private TextView getFile;
	private Dialog updateDialog;
	private String[] typeName = {"Router","Eoc_Nvm","Eoc_Pib","Catv","Mcu"};
	
	private Dialog mDownloadDialog;
    private int progress;		//下载进度
    private HttpHandler<?> handler = null;
    private UpdateJson updateJson;
    private DialogAlert_one_btn tipDialog;
    
    private static final int DOWNLOAD = 1;		//下载中
    private static final int DOWNLOAD_FINISH = 2;	//下载结束
    private static final int MD5_CHECK_FAILED = 3;

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
            case MD5_CHECK_FAILED:
            	tipDialog = new DialogAlert_one_btn(UpdateFileActivity.this, "提示", "文件校验失败", "确定", new View.OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					tipDialog.dismiss();
    				}
    			});
                break;
            default:
                break;
            }
        };
    };
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_file);
		
		initData();
		initView();
	}
	
	public void getUpdate(int index)
	{
		//构造MySharedPreferencesUtils
		Boolean loginFlag =(Boolean)MySharedPreferencesUtils.get("loginFlag", false);
		if(!loginFlag)
		{
			MyLog.e(TAG, "未登录");
			CommonUtils.showTips(UpdateFileActivity.this, "提示", "未登入");
			return;
		}
		Boolean version = (Boolean)SPUtils.get(UpdateFileActivity.this, "versionFlag",false);
		if(!version)
		{
			MyLog.e(TAG, "未获取设备版本号");
			CommonUtils.showTips(UpdateFileActivity.this, "提示", "未获取设备信息");
			return;
		}
		
        MySharedPreferencesUtils.getInstance(UpdateFileActivity.this, "share_data");
  
        //设备升级文件
        StringBuilder data = new StringBuilder(256);
        data.append("userName=");
        data.append(MySharedPreferencesUtils.get("userName", ""));
        data.append("&probeMac=");
        data.append(SPUtils.get(UpdateFileActivity.this, "probeMac",""));
        MyLog.e(TAG, (String)SPUtils.get(UpdateFileActivity.this, "probeMac",""));
        data.append("&deviceType=");
        if(index==0)
        {
        	data.append("ROUTER");
        }
        if(index==1)
        {
        	data.append("NVM");
        }
        if(index==2)
        {
        	data.append("PIB");
        }
        if(index==3)
        {
        	data.append("CATV");
        }
        if(index==4)
        {
        	data.append("MCU");
        }
        data.append("&versionNum=");
        if(index==0)
        {
        	data.append(SPUtils.get(UpdateFileActivity.this, "routerSw",""));
        }
        if(index==1)
        {
        	data.append(SPUtils.get(UpdateFileActivity.this, "eocSw",""));
        }
        if(index==2)
        {
        	data.append("0.0");
        }
        if(index==3)
        {
        	data.append(SPUtils.get(UpdateFileActivity.this, "catvSw",""));
        }
        if(index==4)
        {
        	data.append(SPUtils.get(UpdateFileActivity.this, "mcuSw",""));
        }

        MyLog.e(TAG, "work data: " + data.toString());
        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_DEVICE_UPDATE;
        url = url + data03;
        MyLog.e(TAG, "work url :" + url);
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        HttpService httpService = new HttpService(this);
        httpService.get(url, this, null);
		
	}
		
	public void initData()
	{
		mListItems = new ArrayList<String>();
		pathItems = new ArrayList<String>();
		
		File fileUpdate = new File(AppConfig.CacheUpdateDir);
        files = fileUpdate.listFiles();
        
        if(files != null)
        {
        	int count = files.length;
        	for(int i =0;i<count;i++)
        	{
        		File file = files[i]; 
        		mListItems.add(file.getName()); 
        		pathItems.add(file.getPath());
        	}
        }
	}
	
	public void initView()
	{
		titltView = (TextView)findViewById(R.id.top_title);
        titltView.setText("升级文件");
        backTextView = (TextView)findViewById(R.id.top_back);
        backTextView.setOnClickListener(this);
//        device_state_btn = (ImageButton)findViewById(R.id.device_state);
//        device_state_btn.setVisibility(View.GONE);
		
		fileListView = (ListView)findViewById(R.id.fileList);
        
        mAdapter = new SampleListAdapter();  
        fileListView.setAdapter(mAdapter);
        fileListView.setOnItemLongClickListener(this);
        
        getFile = (TextView)findViewById(R.id.top_update);
        getFile.setOnClickListener(this);
        
        updateDialog = new AlertDialog.Builder(this).
			    setTitle("获取升级文件").
			    setIcon(null).
			    setItems(typeName, new DialogInterface.OnClickListener() {
			 
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	getUpdate(which);
	     		}
			    }).
			    setNegativeButton("取消", new DialogInterface.OnClickListener() {
			    	//取消升级
			     @Override
			     public void onClick(DialogInterface dialog, int which) {
			      // TODO Auto-generated method stub
			     }
			     }).create();
        
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int viewId = arg0.getId();  
		switch (viewId) {
		case R.id.top_back:
			this.finish();
			break;
		case R.id.top_update:
			updateDialog.show();
			break;
		default:
			break;
		}
	}
	
	private class SampleListAdapter extends BaseAdapter {  
        
        @Override  
        public int getCount() {  
            return mListItems.size();  
        }  
   
        @Override  
        public Object getItem(int index) {  
            return mListItems.get(index);  
        }  
   
        @Override  
        public long getItemId(int index) {  
            return index;  
        }  
   
        @Override  
        public View getView(int index, View view, ViewGroup arg2) {  
            if(view == null){  
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
                view = inflater.inflate(R.layout.wifilist_ltem, null);  
            }  
            TextView textView = (TextView)view.findViewById(R.id.wifilist_item_text);  
            textView.setText(mListItems.get(index));  
            return view;  
        }  
    }
	
	private void showDownloadDialog(){
    	
        // 构造软件下载对话框
		Builder builder = new Builder(UpdateFileActivity.this);
        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(UpdateFileActivity.this);
        int layoutId = R.layout.softupdate_progress; 
        View v = inflater.inflate(layoutId, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
	     public void onClick(DialogInterface dialog, int which) {
	      // TODO Auto-generated method stub
	     	}
	     });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        downloadFile();
    }

	private void downloadFile()
	{
		MyLog.e(TAG, "开始下载");
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

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		deleteDialog = new DialogAlert_two_btn(this,
				"提示",
				"是否删除文件",
				"确定",
				"取消",
				new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						deleteDialog.dismiss();
						String fileName = pathItems.get(arg2);
						MyLog.e(TAG, fileName);
						File file = new File(fileName);
						file.delete();											
						mListItems.remove(arg2);
						pathItems.remove(arg2);
						mAdapter.notifyDataSetChanged();
					}
				},
				new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						deleteDialog.dismiss();
					}
				});
		
		return true;
	}
	
	@Override
	public void getResult(String result, Object objParam) {
		// TODO Auto-generated method stub
		MyLog.e(TAG, "result: " + result);
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
//        		CommonUtils.showTips(UpdateFileActivity.this, "提示", error);
        		tipDialog = new DialogAlert_one_btn(UpdateFileActivity.this, "提示", error, "确定", new View.OnClickListener() {
    				@Override
    				public void onClick(View arg0) {
    					tipDialog.dismiss();
    				}
    			});
        	}
        	else {
        		if (parseImageData(result)) {
        			checkUpdateDialog = new DialogAlert_two_btn(this,
        					"提示",
        					"是否下载升级文件",
        					"确定",
        					"取消",
        					new View.OnClickListener() {
        						@Override
        						public void onClick(View arg0) {
        							checkUpdateDialog.dismiss();
        							showDownloadDialog();
        						}
        					},
        					new View.OnClickListener() {
        						@Override
        						public void onClick(View arg0) {
        							checkUpdateDialog.dismiss();
        						}
        					});
    			}else {
    				tipDialog = new DialogAlert_one_btn(UpdateFileActivity.this, "提示", "已经是最新版本", "确定", new View.OnClickListener() {
        				@Override
        				public void onClick(View arg0) {
        					tipDialog.dismiss();
        				}
        			});
    			}
			}
        }
        else {
        	tipDialog = new DialogAlert_one_btn(UpdateFileActivity.this, "提示", "获取异常", "确定", new View.OnClickListener() {
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
			updateJson = updateRoot.getSoftVersion();
		}
		
		return needUpdate;
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
	
	private void checkFile()
	{
		File downloadFile = new File(target, updateJson.getFileName());
        if (!downloadFile.exists()){
            return;
        }
        String md5 = getFileMd5(target+updateJson.getFileName());
        MyLog.e(TAG+"md5", md5);
        if(!md5.equals(updateJson.getMd5()))
        {
        	MyLog.e(TAG, "md5校验失败");
        	downloadFile.delete();
        	mHandler.sendEmptyMessage(MD5_CHECK_FAILED);
        }
        else
        {
        	mListItems.clear();
			pathItems.clear();
			File fileUpdate = new File(AppConfig.CacheUpdateDir);
	        files = fileUpdate.listFiles();
	        
	        if(files != null)
	        {
	        	int count = files.length;
	        	for(int i =0;i<count;i++)
	        	{
	        		File file = files[i]; 
	        		mListItems.add(file.getName()); 
	        		pathItems.add(file.getPath());
	        	}
	        }
			mAdapter.notifyDataSetChanged();
        }
	}

}
