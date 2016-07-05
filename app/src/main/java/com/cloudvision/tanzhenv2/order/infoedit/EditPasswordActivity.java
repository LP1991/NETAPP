package com.cloudvision.tanzhenv2.order.infoedit;

import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.BaseActivity;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.util.MyLog;

public class EditPasswordActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "EditPasswordActivity";

	private EditText oldPassword;
	private EditText newPassword;
	private EditText confirmPassword;
	private InputMethodManager imm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_password);
		
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		oldPassword = (EditText) findViewById(R.id.dialog_et1);
		newPassword = (EditText) findViewById(R.id.dialog_et2);
		confirmPassword = (EditText) findViewById(R.id.dialog_et3);
		
		oldPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		newPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		
		TextView tv_back = (TextView) findViewById(R.id.top_back);
		tv_back.setOnClickListener(this);
		TextView tv_done = (TextView) findViewById(R.id.top_done);
		tv_done.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		
		int viewId = v.getId();
		switch (viewId) {
		case R.id.top_back:
			this.finish();
			break;
		case R.id.top_done:	//完成更改--谭智文
			MyLog.e(TAG, "修改完成...");
			changeDone();
			break;
		default:
			break;
		}
	}
	
	private void changeDone() {
		
		imm.hideSoftInputFromWindow(confirmPassword.getWindowToken(), 0);
        
        oldPassword.setError(null);
        newPassword.setError(null);
        confirmPassword.setError(null);

        String oldString = oldPassword.getText().toString();
        String newString = newPassword.getText().toString();
        String confirmString = confirmPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(oldString)) {
        	oldPassword.setError(getString(R.string.error_dialog));
            focusView = oldPassword;
            cancel = true;
        } else if (!oldString.equals(MySharedPreferencesUtils.get("password", ""))) {
        	oldPassword.setError(getString(R.string.error_incorrect_password));
            focusView = oldPassword;
            cancel = true;
        }
        
        if (TextUtils.isEmpty(newString)) {
        	newPassword.setError(getString(R.string.error_dialog));
            focusView = newPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(confirmString)) {
        	confirmPassword.setError(getString(R.string.error_dialog));
            focusView = confirmPassword;
            cancel = true;
        } else if (!confirmString.equals(newString)) {
        	newPassword.setError(getString(R.string.error_different_password));
        	confirmPassword.setError(getString(R.string.error_different_password));
            focusView = newPassword;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
        	editHttp(newString);
        }
	}
	
	/**
     * get方法传递数据到后台
     *
     * @param data 需要传递的数据
     */
    private void editHttp(String newData) {
    	
    	StringBuilder data = new StringBuilder();
		data.append("userName=");
		data.append(MySharedPreferencesUtils.get("userName", ""));
		data.append("&newPwd=");
		data.append(newData);

        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_UPDATE_USER;
        url = url + data03;
        MyLog.i(TAG, "url :" + url);
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        HttpService httpService = new HttpService(this);
        httpService.get(url, httpResult, null);
    }
    
    /**
     * Http的请求结果接收
     */
    private HttpServiceInterface httpResult = new HttpServiceInterface() {
        @Override
        public void getResult(String result, Object objParam) {

            MyLog.e(TAG, "result: " + result);
            System.out.println(result);

            Map<String, String> map = null;
            try {
                map = MapUtils.parseData(result);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (map != null) {
                String value = map.get("returnCode");

                if (value.equals("SUCCESS")) {
					MySharedPreferencesUtils.put("password", newPassword.getText().toString());
					finish();
				}
                CommonUtils.showTips(EditPasswordActivity.this, "提示", map.get("returnMsg")); 
            }
        }
    };
}
