package com.cloudvision.tanzhenv2.order.infoedit;

import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.activity.UserActivity;
import com.cloudvision.tanzhenv2.order.BaseActivity;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.util.MyLog;

public class EditNameActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "EditNameActivity";

	private EditText et_userName;
	private String userMsg;
	private InputMethodManager imm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_username);
		
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		et_userName = (EditText) findViewById(R.id.dialog_et);
		TextView tv_tip = (TextView) findViewById(R.id.tip_tv);
		TextView tv_back = (TextView) findViewById(R.id.top_back);
		tv_back.setOnClickListener(this);
		TextView tv_done = (TextView) findViewById(R.id.top_done);
		tv_done.setOnClickListener(this);
		
		tv_tip.setText("请输入你的新名字。");
		
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
		
		imm.hideSoftInputFromWindow(et_userName.getWindowToken(), 0);
		userMsg = et_userName.getText().toString();
		et_userName.setError(null);

        if (TextUtils.isEmpty(userMsg)) {
            Toast.makeText(getApplicationContext(), "请输入内容", Toast.LENGTH_SHORT).show();
            et_userName.setError(getString(R.string.error_dialog));
            et_userName.requestFocus();
        } else {
            editHttp(userMsg);
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
		data.append("&trueName=");
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
					MySharedPreferencesUtils.put("trueName", userMsg);
					UserActivity.refreshAdapter();
					finish();
				}
                CommonUtils.showTips(EditNameActivity.this, "提示", map.get("returnMsg")); 
            }
        }
    };
}
