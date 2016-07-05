package com.cloudvision.tanzhenv2.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudvision.listview.adapter.UserListViewAdapter;
import com.cloudvision.listview.adapter.UserListViewAdapter.MyClickListener;
import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.LoginActivity;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.BitUtils;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.function.YoumengPush;
import com.cloudvision.tanzhenv2.order.infoedit.EditNameActivity;
import com.cloudvision.tanzhenv2.order.infoedit.EditPasswordActivity;
import com.cloudvision.util.MyLog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class UserActivity extends FragmentActivity implements OnClickListener,OnItemClickListener{
	
	private static final int PHOTO_CAPTURE_CODE = 0x11; // 拍照
    private static final int PHOTO_ALBUM_CODE = 0x12; // 相册
    public static final int IMAGE_SIZE_HEIGHT = 100;
    public static final int IMAGE_SIZE_WIDTH = 100;
    
	private TextView backTextView;
	private TextView escTextView;
	private String TAG = "UserActivity";
	
//	private List<String>  list = null; 

	private static UserListViewAdapter adapter;
	
	private String[] nameList = {"tag","user","tag","用户名","修改密码"}; 
//	private String[] nameList = {"tag","模式切换","热点配置","tag","检测更新","关于我们","软件评分","意见反馈"};
	
    private ListView listview;
    
    /**
     * 数据上传结果Message
     */
    private Handler myHandler = new Handler() {
    	@Override
		public void handleMessage(Message msg) {
    		super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                	Toast.makeText(UserActivity.this, "头像已更新", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(UserActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        backTextView = (TextView) findViewById(R.id.top_back);
        backTextView.setOnClickListener(this);
        listview = (ListView) findViewById(R.id.user_list);  
        
        //增加退出登录监听--谭智文
        escTextView = (TextView) findViewById(R.id.top_esc);
        escTextView.setOnClickListener(this);
        //初始化sp -- 谭智文
        MySharedPreferencesUtils.getInstance(UserActivity.this, "share_data");
        //初始化头像
//        initFaceImage();
        
        //增加全局变量adaper -- 谭智文
        adapter = new UserListViewAdapter(UserActivity.this, nameList, mListener);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }
	
//	private void initFaceImage(){
//		
//		Object userName = MySharedPreferencesUtils.get("userName", "");
//		String tempImgPath = Constants.CACHE_PATH + userName;
//		File f=new File(tempImgPath);
//        if(!f.exists()){
//             MyLog.i(TAG, "need downLoad faceImage....");
//             
//             String url = Constants.URL_UPGRADEDOWN;
//             if(!url.equals("")){
//            	 String fileName = Constants.CACHE_PATH + userName;
//                 HttpDownFile httpDownFile = new HttpDownFile(UserActivity.this);
//                 httpDownFile.get(url, fileName, httpFaceImage, null);
//             }
//        }
//	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int viewId = arg0.getId();
		switch (viewId) {
		case R.id.top_back:
			this.finish();
			break;
		case R.id.top_esc:	//退出登录--谭智文
			escLogin();
			break;
		default:
			break;
		}
	}
	
	/**
     * 选择退出登录的提示 
     * 
     * by 谭智文
     */
    private void escLogin() {
    	
    	//获取当前签到签出状态
    	boolean signFlag = (Boolean) MySharedPreferencesUtils.get("signFlag", false);
        if (signFlag) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setTitle("退出登录");
            builder.setMessage("你还没有签出，是否退出登录？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	escSuccess();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create();
            builder.show();
            
        } else {
        	
        	escSuccess();
        }
    }
    
    /**
     * 退出登录操作 
     * 
     * by 谭智文
     */
    private void escSuccess() {
    	MySharedPreferencesUtils.put("password", "");
        MySharedPreferencesUtils.put("loginFlag", false);
        MySharedPreferencesUtils.put("signFlag", false);
        MySharedPreferencesUtils.put("headImage", "");
        finish();
        YoumengPush.closePush();
        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
        finish();
        UserActivity.this.startActivity(intent);
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent;
		switch (arg2) {
		case 1:	
			intent = new Intent(UserActivity.this,EditNameActivity.class);
			UserActivity.this.startActivity(intent);
			break;
		case 4:	
			intent = new Intent(UserActivity.this,EditPasswordActivity.class);
			UserActivity.this.startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	/**
	* 实现类，响应头像点击事件
	* 
	* by 谭智文
	*/
	private MyClickListener mListener = new MyClickListener() {
	    @Override
	     public void myOnClick(View v) {
	    	MyLog.i(TAG,"点击用户头像");
	    	faceDialog();
	     }
	};

	/**
     * 显示头像选择对话框
     * 
     * by 谭智文
     */
    public void faceDialog() {
    	
    	String[] items = new String[]{"选择本地图片", "拍照"};

        new AlertDialog.Builder(this)
            .setTitle("设置头像")
            .setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Intent intentFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
                            intentFromGallery.addCategory(Intent.CATEGORY_OPENABLE);
                            intentFromGallery.setType("image/*");
                            startActivityForResult(intentFromGallery, PHOTO_ALBUM_CODE);
                            break;
                        case 1:
                            Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            // 判断存储卡是否可以用，可用进行存储
                            if (CommonUtils.checkSdCard()) {
                                intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constants.CACHE_PATH, "cache")));
                            } else {
                                Toast.makeText(getApplicationContext(), "存储卡不可用", Toast.LENGTH_LONG).show();
                            }
                            startActivityForResult(intentFromCapture, PHOTO_CAPTURE_CODE);
                            break;
                    }
                }
            })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
    }
    
    /**
     * 接收头像选择返回的数据
     * 
     * by 谭智文
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {

            String mFileName;
            Bitmap bitmap = null;
            switch (requestCode) {
                case PHOTO_ALBUM_CODE:
                    mFileName = CommonUtils.getPath(getApplicationContext(), data.getData());
                    bitmap = BitUtils.decodeSampledBitmapFromFile(mFileName, IMAGE_SIZE_WIDTH, IMAGE_SIZE_HEIGHT);
                    savePic(bitmap);
                    break;
                case PHOTO_CAPTURE_CODE:
                    mFileName = Constants.CACHE_PATH + "cache";
                    bitmap = BitUtils.decodeSampledBitmapFromFile(mFileName, IMAGE_SIZE_WIDTH, IMAGE_SIZE_HEIGHT);
                    savePic(bitmap);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * 保存图片
     *
     * @param photo 待保存的图片
     * 
     * by 谭智文
     */
    private void savePic(Bitmap photo) {
    	
    	Object userName = MySharedPreferencesUtils.get("userName", "");
        String tempImgPath = Constants.CACHE_PATH + userName;
        int lastSlastPos = tempImgPath.lastIndexOf('/');
        String dir = tempImgPath.substring(0, lastSlastPos);
        File dirFile = new File(dir);
        dirFile.mkdirs();
        if (!dirFile.exists()) {
            Toast.makeText(UserActivity.this, "无法创建SD卡目录,图片无法保存", Toast.LENGTH_SHORT).show();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempImgPath));
            photo.compress(Bitmap.CompressFormat.JPEG, 75, bos);	// (0 - 100)压缩文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        MySharedPreferencesUtils.put("isLocation", true);
        refreshAdapter();
        //上传头像
        faceHttpPost();
    }
    
    public static void refreshAdapter() {
    	adapter.notifyDataSetChanged();
	}
    
    /**
     * post方法传递用户头像数据
     *
     * @param data 参数
     */
    private void faceHttpPost() {
    	
    	Object userName = MySharedPreferencesUtils.get("userName", "");
    	String dataString = "userName=" + userName;
        MyLog.i(TAG, "faceImage data: " + dataString);
        String data03 = CryptUtils.getInstance().encryptXOR(dataString);
        String url = Constants.URL_UPDATE_FACE;
        url = url + data03;
        MyLog.i(TAG, "faceImage url :" + url);
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
        String CONTENT_TYPE = "multipart/form-data";   //内容类型

		RequestParams params = new RequestParams();
		params.addQueryStringParameter("Charset", "UTF-8");  //设置编码
		params.addQueryStringParameter("connection", "keep-alive");
		params.addQueryStringParameter("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
		params.addBodyParameter("file", new File(Constants.CACHE_PATH + userName));
		
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.POST,
				url,params,
		    new RequestCallBack<String>() {
		
		        @Override
		        public void onStart() {
		        }
		
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	MyLog.e(TAG, current+"/"+total);
		        }
		
		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	
		        	MyLog.e(TAG, responseInfo.result);
		        	Map<String, String> map = null;
			        try {
			            map = MapUtils.parseData(responseInfo.result);
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
			        if (map != null) {
			        	if (map.containsKey("headImage")) {
			        		myHandler.sendEmptyMessage(0);
		                	MySharedPreferencesUtils.put("headImage", map.get("headImage"));
		                	MySharedPreferencesUtils.put("isLocation", false);
						}
			        }
		        }
		        
		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	myHandler.sendEmptyMessage(1);
		        	MyLog.e(TAG, "upload Failure....");
		        }
		});
    }
}
