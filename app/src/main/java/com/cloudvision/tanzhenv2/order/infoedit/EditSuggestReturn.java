package com.cloudvision.tanzhenv2.order.infoedit;

import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class EditSuggestReturn extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "EditSuggestReturn";

	private EditText etSuggest;
	private String userMsg;
	private InputMethodManager imm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_suggest_return);
		
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		etSuggest = (EditText) findViewById(R.id.dialog_et);
		TextView tv_tip = (TextView) findViewById(R.id.tip_tv);
		TextView tv_back = (TextView) findViewById(R.id.top_back);
		tv_back.setOnClickListener(this);
		TextView tv_done = (TextView) findViewById(R.id.top_done);
		tv_done.setOnClickListener(this);
		
		tv_tip.setText("感谢您给我们提出的宝贵意见。(限200字)");
		
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
		
		imm.hideSoftInputFromWindow(etSuggest.getWindowToken(), 0);
		
		userMsg = etSuggest.getText().toString();
		etSuggest.setError(null);

        if (TextUtils.isEmpty(userMsg)) {
            Toast.makeText(getApplicationContext(), "请输入内容", Toast.LENGTH_SHORT).show();
            etSuggest.setError(getString(R.string.error_dialog));
            etSuggest.requestFocus();
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
		data.append("&appType=1");
		data.append("&ftypeId=1");
		data.append("&content=");
		data.append(newData);

        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_SUGGEST_RETURN;
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
                	showTips(EditSuggestReturn.this, "提交成功", "感谢您的反馈，我们会不断改进。"); 
				}else {
					CommonUtils.showTips(EditSuggestReturn.this, "提示", map.get("returnMsg"));
				}
            }
        }
    };
    
    private void showTips(Context context,String title,String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	dialog.cancel();
            	finish();
            }
        });
        builder.create();
        builder.show();
    }
}
