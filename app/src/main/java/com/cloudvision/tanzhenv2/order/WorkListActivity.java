package com.cloudvision.tanzhenv2.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.deal.refresh.PullToRefreshLayout;
import com.cloudvision.tanzhenv2.order.deal.refresh.PullToRefreshLayout.OnRefreshListener;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.tanzhenv2.order.model.WorkListJson;
import com.cloudvision.tanzhenv2.order.model.WorkListRoot;
import com.cloudvision.util.MyLog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 工单查询界面
 * 
 * Created by 谭智文
 */
public class WorkListActivity extends BaseActivity implements OnClickListener,HttpServiceInterface {

    private static final String TAG = "WorkList";

    private TextView backTextView;
    private List<WorkListJson> jsons;
    private ListView lvWorkList;
    private FrameLayout frameLayout;
    private PullToRefreshLayout pullToRefreshLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worklist);
        
        backTextView = (TextView)findViewById(R.id.top_back);        
        backTextView.setOnClickListener(this);

        lvWorkList = (ListView) findViewById(R.id.lv_workList);
        frameLayout = (FrameLayout) findViewById(R.id.fm_workList);
        pullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        pullToRefreshLayout.setOnRefreshListener(new pullToRefreshListener());

        //构造MySharedPreferencesUtils
        MySharedPreferencesUtils.getInstance(WorkListActivity.this, "share_data");

    }
    
    /**
     * 点击屏幕图片刷新
     */
    public void FvRefresh(View view) {
    	MyLog.e(TAG,"refresh ....");
		workListHttp();
	}

    /**
     * 工单list适配器
     */
	private void simpleAdapter() {

        lvWorkList.setVisibility(View.VISIBLE);
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();/*在数组中存放数据*/
//        HashMap<Integer, String> mapId = new HashMap<Integer, String>();
        SparseArray<String> mapId = new SparseArray<String>();
        Gson gson = new Gson();

        for (int i = 0; i < jsons.size(); i++) {
            WorkListJson json = jsons.get(i);
            mapId.put(i, String.valueOf(json.getId()));

            //如果从没保存过此工单，则保存
            if (!MySharedPreferencesUtils.contains(String.valueOf(json.getId()))) {
                String sjson = gson.toJson(json);
                MySharedPreferencesUtils.put(String.valueOf(json.getId()), sjson);
            }

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.ic_search_find);
            map.put("ItemCity", json.getCustomername());
            map.put("ItemAddress", json.getCustomeraddress());
            map.put("ItemTrouble", json.getTroubledesc());
            listItem.add(map);
        }

        String smapId = gson.toJson(mapId);
        MySharedPreferencesUtils.put("work_id", smapId);

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.worklist_view,
                new String[]{"ItemImage", "ItemCity", "ItemAddress", "ItemTrouble"},
                new int[]{R.id.ItemImage, R.id.ItemCity, R.id.ItemAddress, R.id.ItemTrouble}
        );

        lvWorkList.setAdapter(mSimpleAdapter);//为ListView绑定适配器
        lvWorkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                Intent intent = new Intent(WorkListActivity.this, WorkListDetailsActivity.class);
                Constants.workId = String.valueOf(jsons.get(arg2).getId());
                intent.putExtra("id", String.valueOf(jsons.get(arg2).getId()));
                intent.putExtra("orderstatus", jsons.get(arg2).getOrderstatus());
                WorkListActivity.this.startActivity(intent);
            }
        });

    }

	/**
     * 工单http请求
     */
    private void workListHttp() {

        //构造MySharedPreferencesUtils
        MySharedPreferencesUtils.getInstance(WorkListActivity.this, "share_data");

        StringBuilder data = new StringBuilder(256);
        data.append("userName=");
        data.append(MySharedPreferencesUtils.get("userName", ""));
        data.append("&orderStatus=");
        data.append(2);

        MyLog.i(TAG, "work data: " + data.toString());
        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_WORKLIST;
        url = url + data03;
        MyLog.i(TAG, "work url :" + url);
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        HttpService httpService = new HttpService(this);
        httpService.get(url, this, null);
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

    /**
     * 重新请求加载（由工单详情界面返回时）
     */
    @Override
    protected void onResume() {
        super.onResume();
        workListHttp();
    }

    /**
     * 初始接收到的工单数据
     */
    @Override
    public void getResult(String result, Object objParam) {

        MyLog.e(TAG, "result: " + result);
        Gson gson = new Gson();
        
        if(!result.equals("")){
        	
        	WorkListRoot workListData = null;
            try {
                workListData = gson.fromJson(result, WorkListRoot.class);
            } catch (JsonSyntaxException e) {
            	pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                Toast.makeText(WorkListActivity.this, "网络错误，获取失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            if (workListData != null) {
            	pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                if (workListData.getReturnCode().equals("SUCCESS")) {	//获取成功
                    jsons = workListData.getJson();
                    if (jsons != null) {
                    	lvWorkList.setVisibility(View.INVISIBLE);
                    	frameLayout.setVisibility(View.GONE); 	
                        simpleAdapter();
                    } else {				//获取成功，但无工单内容
                    	lvWorkList.setVisibility(View.GONE);
                        Toast.makeText(WorkListActivity.this, workListData.getReturnMsg(), Toast.LENGTH_SHORT).show();
                    }
                } else {				//获取失败
                	lvWorkList.setVisibility(View.GONE);
                	frameLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(WorkListActivity.this, workListData.getReturnMsg(), Toast.LENGTH_SHORT).show();
                }
            }else {
            	lvWorkList.setVisibility(View.GONE);
            	frameLayout.setVisibility(View.VISIBLE);
    		}
        	
        }
    }
    
    /**
     * 下拉刷新
     */
    private class pullToRefreshListener implements OnRefreshListener
    {

    	@Override
    	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout)
    	{
    		workListHttp();
    	}
    }    
}
