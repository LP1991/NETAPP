package com.cloudvision.tanzhenv2.order;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.tanzhenv2.order.model.WorkListJson;
import com.cloudvision.tanzhenv2.order.model.WorkListRoot;
import com.cloudvision.util.MyLog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * 历史工单界面
 * 
 * Created by 谭智文
 */
public class HistoryActivity extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "History";

    private Button btnStartDate;
    private Button btnEndDate;
    private List<WorkListJson> jsons;
    private ListView lvHistory;

    private int anio;
    private int mes;
    private int dia;
    private String startDate;
    private String endDate;
    private Long startTime;
    private Long endTime;

    /**
     * 获取历史工单返回值
     */
    private HttpServiceInterface historyWorkResult = new HttpServiceInterface() {
        @Override
        public void getResult(String result, Object objParam) {
            MyLog.e("history", "result: " + result);

            Gson gson = new Gson();

            WorkListRoot workListData = null;
            try {
                workListData = gson.fromJson(result, WorkListRoot.class);
            } catch (JsonSyntaxException e) {
                CommonUtils.CheckNetworkState(HistoryActivity.this);
                Toast.makeText(HistoryActivity.this, "网络问题，获取失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            if ((workListData != null) && workListData.getReturnCode().equals("SUCCESS")) {
                jsons = workListData.getJson();
                String returnMsg = workListData.getReturnMsg();
                Toast.makeText(HistoryActivity.this, returnMsg, Toast.LENGTH_SHORT).show();
                if (jsons != null)
                    simpleAdapter();
                else
                    lvHistory.setVisibility(View.GONE);
            } else {
                Toast.makeText(HistoryActivity.this, "工单获取失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        btnStartDate = (Button) findViewById(R.id.btn_startDate);
        btnEndDate = (Button) findViewById(R.id.btn_endDate);
        lvHistory = (ListView) findViewById(R.id.lv_history);
        TextView backTextView = (TextView)findViewById(R.id.top_back);        
        backTextView.setOnClickListener(this);

        initDate();

        //构造MySharedPreferencesUtils
        MySharedPreferencesUtils.getInstance(HistoryActivity.this, "share_data");

    }

    /**
     * 初始化时间数据
     */
    private void initDate(){

        endTime=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        Date d1=new Date(endTime);
        endDate=format.format(d1);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        startTime = calendar.getTimeInMillis();
        startDate = format.format(calendar.getTime());

        btnStartDate.setText(startDate);
        btnEndDate.setText(endDate);

        dateHttp();

    }
   
    /**
     * 查询的开始时间
     */
    public void onStart(View view) {

        final Calendar c = Calendar.getInstance();
        anio = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH);
        dia = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog mDialog = new DatePickerDialog(this, null, anio, mes, dia);
        //手动设置按钮
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //通过mDialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息
                DatePicker datePicker = mDialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

                startDate = formDate(year, month+1, day);
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                Long startTemp = c.getTimeInMillis();
                if(startTemp < endTime){
                    startTime = startTemp;
                    btnStartDate.setText(startDate);
                }
                else {
                    Toast.makeText(HistoryActivity.this,"开始时间要小于结束时间。",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //取消按钮，如果不需要直接不设置即可
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        mDialog.show();

    }

    /**
     * 查询的结束时间
     */
    public void onEnd(View view) {

        final Calendar c = Calendar.getInstance();
        anio = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH);
        dia = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog mDialog = new DatePickerDialog(this, null, anio, mes, dia);
        //手动设置按钮
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //通过mDialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息
                DatePicker datePicker = mDialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

                endDate = formDate(year, month+1, day);
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                Long endTemp = c.getTimeInMillis();
                if(endTemp > startTime){
                    endTime = endTemp;
                    btnEndDate.setText(endDate);
                }
                else {
                    Toast.makeText(HistoryActivity.this,"结束时间要大于开始时间。",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //取消按钮，如果不需要直接不设置即可
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        mDialog.show();
    }

    /**
     * 点击事件，发起查询请求
     */
    public void onQuery(View view) {

//        if (Constants.offline_flag){
//
//            Gson gson = new Gson();
//            jsons = new ArrayList<Json>();
//            int num=0 ,numtemp = 0 ;
//            Map<String, ?> allContent = MySharedPreferencesUtils.getAll();
//
//            for(Map.Entry<String, ?>  entry : allContent.entrySet()){
//                try {
//                    numtemp = Integer.valueOf(entry.getKey());
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                }
//                if(numtemp != 0 && num != numtemp){
//                    String sjson = (String) MySharedPreferencesUtils.get(String.valueOf(numtemp), "");
//                    Json json = gson.fromJson(sjson,Json.class);
//                    if(json.getCreatedate()<endTime && json.getCreatedate()>startTime){
//                        jsons.add(json);
//                    }
//                    num = numtemp;
//                }
//            }
//            if(jsons != null)simpleAdapter();
//        }else {
//            dateHttp();
//        }
        dateHttp();
    }

    /**
     * 时间格式变换
     */
    private String formDate(int year, int monthOfYear, int dayOfMonth){

        String month;
        String day;
        String date;

        if (monthOfYear < 10) {
            month = "0" + monthOfYear;
            if (dayOfMonth < 10) {
                day = "0" + dayOfMonth;
                date = year + "-" + month + "-" + day;
            }else {
                date = year + "-" + month + "-" + dayOfMonth;
            }
        }else {
            if (dayOfMonth < 10) {
                day = "0" + dayOfMonth;
                date = year + "-" + monthOfYear + "-" + day;
            }else {
                date = year + "-" + monthOfYear + "-" + dayOfMonth;
            }
        }
        return date;
    }

    /**
     * http请求
     */
    private void dateHttp() {

        String userName = (String) MySharedPreferencesUtils.get("userName", "");

        //为结束时间加一天（为了显示当天完成的工单）
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        endDate = format.format(calendar.getTime());

        StringBuilder data = new StringBuilder(256);
        data.append("userName=");
        data.append(userName);
        data.append("&startTime=");
        data.append(startDate);
        data.append("&endTime=");
        data.append(endDate);

        MyLog.i(TAG, "history data: " + data.toString());
        String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
        String url = Constants.URL_WORKHISTORY;
        url = url + data03;
        MyLog.i(TAG, "history works url :" + url);
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        HttpService httpService = new HttpService(HistoryActivity.this);
        httpService.get(url, historyWorkResult, null);
    }

    /**
     * 工单结果List适配器
     */
    private void simpleAdapter() {

        lvHistory.setVisibility(View.VISIBLE);
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();/*在数组中存放数据*/
//        HashMap<Integer, String> mapId = new HashMap<Integer, String>();
        SparseArray<String> mapId = new SparseArray<String>();
        Gson gson = new Gson();

        for (int i = 0; i < jsons.size(); i++) {
            WorkListJson json = jsons.get(i);
            mapId.put(i, String.valueOf(json.getId()));

            //若数据已存在则不保存
//            if (!MySharedPreferencesUtils.contains(String.valueOf(json.getId()))) {
//                String sjson = gson.toJson(json);
//                MySharedPreferencesUtils.put(String.valueOf(json.getId()), sjson);
//            }
            //历史工单信息中多出解决方法的参数，所以直接覆盖。
            String sjson = gson.toJson(json);
            MySharedPreferencesUtils.put(String.valueOf(json.getId()), sjson);

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.ic_search_find);
            map.put("ItemCity", json.getCustomername());
            map.put("ItemAddress", json.getCustomeraddress());
            map.put("ItemTrouble", json.getTroubledesc());
            listItem.add(map);
        }

        MySharedPreferencesUtils.put("history_id", mapId);

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.worklist_view,
                new String[]{"ItemImage", "ItemCity", "ItemAddress", "ItemTrouble"},
                new int[]{R.id.ItemImage, R.id.ItemCity, R.id.ItemAddress, R.id.ItemTrouble}
        );

        lvHistory.setAdapter(mSimpleAdapter);//为ListView绑定适配器
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                Intent intent = new Intent(HistoryActivity.this, HistoryDetailsActivity.class);
                Constants.workId = String.valueOf(jsons.get(arg2).getId());
                intent.putExtra("id", String.valueOf(jsons.get(arg2).getId()));
                HistoryActivity.this.startActivity(intent);
            }
        });
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
