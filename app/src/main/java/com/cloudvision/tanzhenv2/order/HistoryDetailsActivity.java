package com.cloudvision.tanzhenv2.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.model.TroubleSuggestionJson;
import com.cloudvision.tanzhenv2.order.model.WorkListJson;
import com.google.gson.Gson;

import java.util.List;

/**
 * 显示历史工单详情
 *
 * Created by 谭智文
 */
public class HistoryDetailsActivity  extends BaseActivity implements OnClickListener {

    private int workId;
    private TextView textView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);

        textView = (TextView) findViewById(R.id.tv_historyDetails);
        TextView backTextView = (TextView)findViewById(R.id.top_back);        
        backTextView.setOnClickListener(this);

        Intent intent = getIntent();
        workId = Integer.valueOf(intent.getStringExtra("id"));

        //从缓存中取工单数据
        //构造MySharedPreferencesUtils
        MySharedPreferencesUtils.getInstance(HistoryDetailsActivity.this, "share_data");
        String sjson = (String) MySharedPreferencesUtils.get(String.valueOf(workId), "");
        Gson gson = new Gson();
        WorkListJson json = gson.fromJson(sjson, WorkListJson.class);
        historyData(json);
    }

    /**
     * 初始化工单数据
     */
    private void historyData(WorkListJson json){

        StringBuilder data = new StringBuilder(1024);
        List<TroubleSuggestionJson> troubleSuggestionLists = json.getTroubleSuggestionList();

        data.append("用户姓名：\n        ");
        data.append(json.getCustomername());
        data.append("\n用户电话：\n        ");
        data.append(json.getCustomermobile());
        data.append("\n用户地址：\n        ");
        data.append(json.getCustomeraddress());
        data.append("\n设备名称：\n        ");
        data.append(json.getDevicename());
        data.append("\n设备类型：\n        ");
        data.append(json.getDevicetype());
        data.append("\n故障描述：\n        ");
        data.append(json.getTroubledesc());
        data.append("\n故障类型：\n        ");
        data.append(json.getTroubletype());
        data.append("\n维修建议:\n        ");
        for (TroubleSuggestionJson troubleSuggestionList : troubleSuggestionLists) {
            data.append(troubleSuggestionList.getSuggestion());
            data.append("\n        ");
        }
        data.append("\n处理方式：\n        ");
        data.append(json.getResolveresult());
        data.append("\n\n\n");
        textView.setText(data.toString());
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
}
