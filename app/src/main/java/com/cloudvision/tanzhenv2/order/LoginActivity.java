package com.cloudvision.tanzhenv2.order;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.activity.UserActivity;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.deal.OnlyToast;
import com.cloudvision.tanzhenv2.order.function.YoumengPush;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.util.MyLog;
import com.cloudvision.util.SPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 登录界面
 * 
 * Created by 谭智文
 */
public class LoginActivity extends FragmentActivity implements OnClickListener,OnItemClickListener,HttpServiceInterface{

	private static final String TAG = "LoginActivity";
	private TextView backTextView;
	
	private String[] nameList = {"tag","userName","password","tag","登录"}; 
    private ListView listview;
	private List<String> list = null;
	
	private static boolean autoLoginFlag;

	private CheckBox checkBox; 
	private EditText mEmailView;
    private EditText mPasswordView;
	private InputMethodManager imm;
	private ProgressDialog pdDialog;
	
	private String userName = "";
	private String password = "";

	private int selectPosition=-1;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        backTextView = (TextView)findViewById(R.id.top_back);        
        backTextView.setOnClickListener(this);      
        
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        LoadUserdata();
        
        listview = (ListView) findViewById(R.id.user_list);   
        LoginListViewAdapter adapter = new LoginListViewAdapter(LoginActivity.this, nameList);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        
        listview.setOnItemClickListener(this);
        
    }
	
	/**
     * 加载用户数据
     * 
     */
    private void LoadUserdata() {
    	
    	//初始化
    	OnlyToast.getInstance(LoginActivity.this);
        MySharedPreferencesUtils.getInstance(LoginActivity.this, "share_data");
        
        boolean firstLogin = (Boolean) MySharedPreferencesUtils.get("firstLogin", true);
        if (firstLogin) {
            MySharedPreferencesUtils.put("userName", "");
            MySharedPreferencesUtils.put("password", "");
            MySharedPreferencesUtils.put("firstLogin", false);
        } else {
            autoLoginFlag = (Boolean) MySharedPreferencesUtils.get("autoLoginFlag", false);
            //载入用户信息
            userName = (String) MySharedPreferencesUtils.get("userName", "");
            password = (String) MySharedPreferencesUtils.get("password", "");
        }
    }
	
    
    
    /**
     * 验证是否是手机号码
     *
     * @param str 待验证的字符串
     * @return 是否正确
     */
//    private boolean isMobileValid(String str) {
//        Pattern pattern = Pattern.compile("1[0-9]{10}");
//        Matcher matcher = pattern.matcher(str);
//        return matcher.matches();
//    }
	
    /**
     * 登录（内容验证）
     * 
     */
	private void attemptLogin() {

        // Reset errors.
		mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
//        String userName = mEmailView.getText().toString();
//        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        String errorText = "";
//        View focusView = null;

        if (TextUtils.isEmpty(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = mPasswordView;
        	errorText = getString(R.string.error_invalid_password);
            cancel = true;
        }

        if (TextUtils.isEmpty(userName)) {
//            mEmailView.setError(getString(R.string.error_field_required));
//            focusView = mEmailView;
            errorText = getString(R.string.error_field_required);
            cancel = true;
//        } else if (!isMobileValid(userName)) {
////            mEmailView.setError(getString(R.string.error_invalid_email));
////            focusView = mEmailView;
//            errorText = getString(R.string.error_invalid_email);
//            cancel = true;
        }

        if (cancel) {
        	CommonUtils.showTips(LoginActivity.this, "登录提示", errorText); 
//            focusView.requestFocus();
        } else {
        	MyLog.i(TAG,"enter Http ...");
        	showProgress();
            loginHttp();
        }
    }
	
	private void showProgress() {
		// 创建ProgressDialog对象    
		pdDialog = new ProgressDialog(LoginActivity.this);   
		pdDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);    
        pdDialog.setTitle("登录");    
        pdDialog.setMessage("正在登录中……");     
        pdDialog.setProgress(100);    
        pdDialog.setIndeterminate(false);    
        pdDialog.setCancelable(true);    
        pdDialog.show();    
	}
	
	/**
     * 登录请求
     * 
     */
	private void loginHttp() {

//        String url = Constants.URL_LOGIN;
		String ip = (String)SPUtils.get(LoginActivity.this, "serverIp","");
		String port = (String)SPUtils.get(LoginActivity.this, "serverPort","");
		String url = "http://"+ip+":"+port+"/wap/"+"app?login&ct=";
		
        String data = "loginType=Android&userName=" + userName + "&passWord=" + password;
        MyLog.e("login", "login data:" + data);
        data = CryptUtils.getInstance().encryptXOR(data);
        MyLog.i("login", "jiami data:" + data);
        url = url + data;
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败
        MyLog.e("登入URL", url);
        HttpService httpService = new HttpService(this);
        httpService.get(url, this, null);
    } 

	/**
     * 登录请求结果
     * 
     */
    @Override
    public void getResult(String result, Object objParam) {

        MyLog.e(TAG, "result: " + result);
        
        Map<String, String> map = null;
        try {
            map = MapUtils.parseData(result);
        } catch (Exception e) {
            CommonUtils.showTips(LoginActivity.this, "登录失败", "网络错误");  
            e.printStackTrace();
        }

        pdDialog.cancel();
        if (map != null) {
            String value = map.get("returnCode");
            
            if (value.equals("SUCCESS")) {
            	pdDialog.cancel();
                if (map.containsKey("status") && map.get("status").equals("1")) {
                	// "1"为在线,"2"为离线
                    MySharedPreferencesUtils.put("signFlag", true);
                } else {
                    MySharedPreferencesUtils.put("signFlag", false);
                }
                if (map.containsKey("headImage") && !map.get("headImage").equals("")) {
                	String faceImageUrl = map.get("headImage");
                	MySharedPreferencesUtils.put("headImage", faceImageUrl);
				}
                MySharedPreferencesUtils.put("trueName", map.get("trueName"));
                MySharedPreferencesUtils.put("userName", userName);
                MySharedPreferencesUtils.put("password", password);
                MySharedPreferencesUtils.put("autoLoginFlag", autoLoginFlag);
                MySharedPreferencesUtils.put("loginFlag", true);
                new YoumengPush(this);
                startActivity(new Intent(this,UserActivity.class));
                finish();

            } else {
                String returnMsg = map.get("returnMsg");
                MySharedPreferencesUtils.put("loginFlag", false);
                CommonUtils.showTips(LoginActivity.this, "登录失败", returnMsg);        
            }
        }
    }
    
	
	@Override
	public void onClick(View arg0) {
		int viewId = arg0.getId();
		switch (viewId) {
		case R.id.top_back:
			this.finish();
			break;
		default:
			break;
		}
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				//do something what you want
				return true;//返回true，把事件消费掉，不会继续调用onBackPressed
				}
				return super.dispatchKeyEvent(event); 
//		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}
	
	/**
     * list适配器
     * 
     */
	public class LoginListViewAdapter extends BaseAdapter{
		
		private Context context;
		
		public LoginListViewAdapter(Context context,String[] strList){  

			this.context = context;
			
			list = new ArrayList<String>(); 
	        for(int i =0;i<nameList.length;i++)
	        {
	        	list.add(nameList[i]);
	        }
	              
	    } 
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		
		@Override  
	    public boolean isEnabled(int position) {  
	    	String nameString = list.get(position);
	        if(nameString.equals("tag"))
	        {
	        	return false;
	        }
	        return super.isEnabled(position);  
	    } 

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = convertView; 
	        String nameString = list.get(position);
	        if(nameString.equals("tag"))
	        {
	        	LayoutInflater inflate = LayoutInflater.from(context);
	            int layoutId = R.layout.set_list_item_tag;
	            view = inflate.inflate(layoutId, null);
//	        	view = LayoutInflater.from(context).inflate(R.layout.set_list_item_tag, null);
	        }
	        else if(nameString.equals("userName"))
	        {
	        	LayoutInflater inflate = LayoutInflater.from(context);
	            int layoutId = R.layout.login_list_edit;
	            view = inflate.inflate(layoutId, null);         
//	        	view = LayoutInflater.from(context).inflate(R.layout.user_list_edit, null);
	        	ImageView imageView = (ImageView) view.findViewById(R.id.user_list_item_icon);
	        	imageView.setImageResource(R.drawable.ic_user_black);
	        	mEmailView = (EditText) view.findViewById(R.id.user_list_item_ed);
	        	mEmailView.setHint("用户名");
	        	mEmailView.setFocusable(true);
	        	mEmailView.setFocusableInTouchMode(true);
	        	mEmailView.setInputType(InputType.TYPE_CLASS_TEXT);
	        	String ss = mEmailView.getText().toString();
	        	MyLog.i(TAG,ss);
	        	
	        	
	        	final int fposition = position;      	
	        	if(userName.equalsIgnoreCase("")){
	        		mEmailView.setHint("请输入");
				}else{
					mEmailView.setText(userName);
				}
					
				if(selectPosition == position){
					mEmailView.requestFocus();
				}
	
	        	mEmailView.setOnFocusChangeListener(new OnFocusChangeListener() {	 				
	 				@Override
	 				public void onFocusChange(View v, boolean hasFocus) {
	 					selectPosition=fposition;
	 				}
	 			});
	 			
	        	mEmailView.addTextChangedListener(new TextWatcher() {	 				
	 				@Override
	 				public void onTextChanged(CharSequence s, int start, int before, int count) {		
	 				}	 				
	 				@Override
	 				public void beforeTextChanged(CharSequence s, int start, int count,
	 						int after) {	 					
	 				}
	 				@Override
	 				public void afterTextChanged(Editable s) {
	 					//在这里做事
	 					userName = s.toString();	 					
	 				}
	 			});
	       	 	     	
			}
	        else if(nameString.equals("password"))
	        {
	        	LayoutInflater inflate = LayoutInflater.from(context);
	            int layoutId = R.layout.login_list_edit;
	            view = inflate.inflate(layoutId, null);
//	        	view = LayoutInflater.from(context).inflate(R.layout.user_list_edit, null);
	        	ImageView imageView = (ImageView) view.findViewById(R.id.user_list_item_icon);
	        	imageView.setImageResource(R.drawable.ic_password_black);
	        	mPasswordView = (EditText) view.findViewById(R.id.user_list_item_ed);
	        	mPasswordView.setHint("密码");
	        	mPasswordView.setFocusable(true);
	        	mPasswordView.setFocusableInTouchMode(true);
	        	mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
	        	
	        	final int fposition = position;
	        	if(password.equalsIgnoreCase("")){
	        		mPasswordView.setHint("请输入");
				}else{
					mPasswordView.setText(userName);
				}					
				if(selectPosition == position){
					mPasswordView.requestFocus();
				}
				mPasswordView.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
	 				public void onFocusChange(View v, boolean hasFocus) {
	 					selectPosition=fposition;
	 				}
	 			});
				mPasswordView.addTextChangedListener(new TextWatcher() {
	 				@Override
	 				public void onTextChanged(CharSequence s, int start, int before, int count) {		
	 				}	 				
	 				@Override
	 				public void beforeTextChanged(CharSequence s, int start, int count,
	 						int after) {	 					
	 				}	 				
	 				@Override
	 				public void afterTextChanged(Editable s) {
	 					//在这里做事
	 					password = s.toString();	 					
	 				}
	 			});	        	
			}
	        else {
	        	
	        	LayoutInflater inflate = LayoutInflater.from(context);
	            int layoutId = R.layout.login_list_button;
	            view = inflate.inflate(layoutId, null);
	        	Button button = (Button) view.findViewById(R.id.user_list_item_btn);
	        	checkBox = (CheckBox) view.findViewById(R.id.box_savePwd);
	        	
	        	checkBox.setOnClickListener(new View.OnClickListener() {       
                    @Override  
                    public void onClick(View v) {           
                    	checkBox = (CheckBox) findViewById(R.id.box_savePwd);
                        autoLoginFlag = checkBox.isChecked();
                    }  
                }); 

	        	button.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//点击登录时立刻隐藏软键盘
						imm.hideSoftInputFromWindow(listview.getWindowToken(), 0);
	                    //进行登录验证
	                    if (CommonUtils.CheckNetworkState(LoginActivity.this)){
	                    	autoLoginFlag = checkBox.isChecked();
	                    	attemptLogin();
	                    }
					}
				});
			}
	        return view;
		}
	}
}
