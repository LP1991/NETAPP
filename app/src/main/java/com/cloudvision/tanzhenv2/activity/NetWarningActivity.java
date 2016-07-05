package com.cloudvision.tanzhenv2.activity;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cloudvision.appconfig.AppConfig;
import com.cloudvision.listview.adapter.WarnigListAdapter;
import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.TestBaseActivity;
import com.cloudvision.tanzhenv2.model.NetAlarmsInfo;
import com.cloudvision.tanzhenv2.model.NetDeviceInfo;
import com.cloudvision.tanzhenv2.order.deal.CommonUtils;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.util.MyLog;
import com.cloudvision.util.MyProgressDialog;
import com.cloudvision.util.SPUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class NetWarningActivity extends TestBaseActivity implements
		OnClickListener, HttpServiceInterface {

	private String TAG = "NetWarningActivity";
	private TextView exitTextView;
	private Button startDate, startTime, endDate, endTime;
	private String start_date, start_time;
	private ListView warning_list;
	private List<NetAlarmsInfo> warningInfoList;
	// begin add defind for master warning search by caiming
	private List<NetAlarmsInfo> master_warningInfoList;
	private List<NetAlarmsInfo> slave_warningInfoList;
	// end add defind for master warning search by caiming

	private WarnigListAdapter warnigListAdapter;

	private int curYear;
	private int curMonth;
	private int curDay;
	private int mHour;
	private int mMinute;
	private String mac = "";
	private NetDeviceInfo netDeviceInfo;

	private Spinner sp_dev_type;
	private ArrayAdapter<String> add_dev_type;
	private List<String> is_dev_type;

	private final int GET_DEVICEINFO_SUCCEED = 0;
	private final int GET_DEVICEINFO_FAILED = 1;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_DEVICEINFO_SUCCEED:
				// modfiy begin for serach master warning by caiming
				// if (warningInfoList.size() > 0) {
				// warningInfoList.clear();
				// }
				// for (int i = 0; i < netDeviceInfo.alarms.length; i++) {
				// warningInfoList.add(netDeviceInfo.alarms[i]);
				// }
				if (warningInfoList.size() > 0) {
					warningInfoList.clear();
					slave_warningInfoList.clear();
					master_warningInfoList.clear();
				}
				for (int i = 0; i < netDeviceInfo.alarms.length; i++) {
					warningInfoList.add(netDeviceInfo.alarms[i]);
					if(netDeviceInfo.alarms[i].mac!=null){
						if(netDeviceInfo.alarms[i].mac.equals(mac)){
							slave_warningInfoList.add(netDeviceInfo.alarms[i]);
						} else {
							master_warningInfoList.add(netDeviceInfo.alarms[i]);
						}						
					}
				}
				// modfiy end for serach master warning by caiming
				warnigListAdapter.notifyDataSetChanged();
				break;
			case GET_DEVICEINFO_FAILED:
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_net_warning);

		initData();
		initView();
	}

	public void initData() {
		Intent intent = getIntent();
		mac = intent.getStringExtra("mac");

		start_date = getTimeStamp("yyyy-MM-dd");
		start_time = getTimeStamp("HH:mm");

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());

		curYear = c.get(Calendar.YEAR);
		curMonth = c.get(Calendar.MONTH);
		curDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);

		is_dev_type = new ArrayList<String>();
		is_dev_type.add("终端");
		is_dev_type.add("局端");
		is_dev_type.add("全部");

		warningInfoList = new ArrayList<NetAlarmsInfo>();
		// begin add init for master warning search by caiming
		master_warningInfoList = new ArrayList<NetAlarmsInfo>();
		slave_warningInfoList = new ArrayList<NetAlarmsInfo>();
		// end add defind for master warning search by caiming
		

		warning_list = (ListView) findViewById(R.id.warning_list);
		warnigListAdapter = new WarnigListAdapter(this, warningInfoList);
		warning_list.setAdapter(warnigListAdapter);
	}

	public void initView() {
		titltView = (TextView) findViewById(R.id.top_title);
		titltView.setText("告警信息");
		backTextView = (TextView) findViewById(R.id.top_back);
		backTextView.setOnClickListener(this);
		exitTextView = (TextView) findViewById(R.id.top_esc);
		exitTextView.setText("查找");
		exitTextView.setOnClickListener(this);

		startDate = (Button) findViewById(R.id.start_date);
		startDate.setText(start_date);
		startDate.setOnClickListener(this);
		startTime = (Button) findViewById(R.id.start_time);
		startTime.setText(start_time + ":00");
		startTime.setOnClickListener(this);

		endDate = (Button) findViewById(R.id.end_date);
		endDate.setText(start_date);
		endDate.setOnClickListener(this);
		endTime = (Button) findViewById(R.id.end_time);
		endTime.setText(start_time + ":00");
		endTime.setOnClickListener(this);
		// begin add init for master warning search by caiming
		sp_dev_type = (Spinner) findViewById(R.id.spinner_devtype);
		add_dev_type = new ArrayAdapter(this,
				android.R.layout.simple_spinner_dropdown_item, is_dev_type);
		sp_dev_type.setAdapter(add_dev_type);
		sp_dev_type
				.setOnItemSelectedListener(new SpinnerSelectedListenerDevType());
		getWarningInfo();
		// end add defind for master warning search by caiming

	}

	/*
	 * create by caiming for list warning by device type
	 */
	class SpinnerSelectedListenerDevType implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			update_devType(arg2);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}

	}

	/*
	 * create by caiming for list warning by device type
	 */
	private void update_devType(int pos) {
		/* 更新告警列表 */
		MyLog.e(TAG, "当前选择" + pos);
		switch (pos) {
		case 0:
			MyLog.e(TAG, "显示终端告警");
			warnigListAdapter = new WarnigListAdapter(this,
					slave_warningInfoList);
			warning_list.setAdapter(warnigListAdapter);
			break;
		case 1:
			MyLog.e(TAG, "显示局端告警");
			warnigListAdapter = new WarnigListAdapter(this,
					master_warningInfoList);
			warning_list.setAdapter(warnigListAdapter);
			break;
		case 2:
			MyLog.e(TAG, "显示全部告警");
			warnigListAdapter = new WarnigListAdapter(this, warningInfoList);
			warning_list.setAdapter(warnigListAdapter);
			break;
		default:
			break;
		}

	}

	private String getTimeStamp(String timeFormat/* yyyyMMddHHmmss */) {
		String timeStamp;
		SimpleDateFormat dateFormatter = new SimpleDateFormat(timeFormat);
		timeStamp = dateFormatter.format(Calendar.getInstance().getTime());
		return timeStamp;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int viewId = arg0.getId();
		switch (viewId) {
		case R.id.top_esc:
			MyLog.e(TAG, "查找");
			MyProgressDialog.show(this, progressDialog, "正在查找", false);
			getWarningInfo();
			break;
		case R.id.top_back:
			this.finish();
			break;
		case R.id.start_date:
			new DatePickerDialog(NetWarningActivity.this,
					new OnDateSetListener() {
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {

							String mouth = "";
							String day = "";

							if (monthOfYear + 1 < 10) {
								mouth = "0" + String.valueOf(monthOfYear + 1);
							} else {
								mouth = String.valueOf(monthOfYear + 1);
							}

							if (dayOfMonth < 10) {
								day = "0" + String.valueOf(dayOfMonth);
							} else {
								day = String.valueOf(dayOfMonth);
							}
							startDate.setText(year + "-" + mouth + "-" + day);
						}
					}, curYear, curMonth, curDay).show();
			break;
		case R.id.start_time:
			new TimePickerDialog(NetWarningActivity.this,
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							String hourStr = "";
							String minuteStr = "";
							if (hourOfDay < 10) {
								hourStr = "0" + String.valueOf(hourOfDay);
							} else {
								hourStr = String.valueOf(hourOfDay);
							}
							if (minute < 10) {
								minuteStr = "0" + String.valueOf(minute);
							} else {
								minuteStr = String.valueOf(minute);
							}
							startTime
									.setText(hourStr + ":" + minuteStr + ":00");
						}
					}, mHour, mMinute, true).show();
			break;
		case R.id.end_date:
			new DatePickerDialog(NetWarningActivity.this,
					new OnDateSetListener() {
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							String mouth = "";
							String day = "";

							if (monthOfYear + 1 < 10) {
								mouth = "0" + String.valueOf(monthOfYear + 1);
							} else {
								mouth = String.valueOf(monthOfYear + 1);
							}

							if (dayOfMonth < 10) {
								day = "0" + String.valueOf(dayOfMonth);
							} else {
								day = String.valueOf(dayOfMonth);
							}
							endDate.setText(year + "-" + mouth + "-" + day);

						}
					}, curYear, curMonth, curDay).show();
			break;
		case R.id.end_time:
			new TimePickerDialog(NetWarningActivity.this,
					new TimePickerDialog.OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							String hourStr = "";
							String minuteStr = "";
							if (hourOfDay < 10) {
								hourStr = "0" + String.valueOf(hourOfDay);
							} else {
								hourStr = String.valueOf(hourOfDay);
							}
							if (minute < 10) {
								minuteStr = "0" + String.valueOf(minute);
							} else {
								minuteStr = String.valueOf(minute);
							}
							endTime.setText(hourStr + ":" + minuteStr + ":00");
						}
					}, mHour, mMinute, true).show();
			break;

		default:
			break;
		}
	}

	public void getWarningInfo() {
		MySharedPreferencesUtils.getInstance(NetWarningActivity.this,
				"share_data");
		String start = startDate.getText().toString() + " "
				+ startTime.getText().toString();
		String end = endDate.getText().toString() + " "
				+ endTime.getText().toString();
		// 设备升级文件
		StringBuilder data = new StringBuilder(256);
		data.append("userName=root");
//		data.append(MySharedPreferencesUtils.get("userName", ""));
		data.append("&mac=");
		data.append(mac);
		data.append("&startTime=");
		data.append(start);
		data.append("&endTime=");
		data.append(end);
		data.append("&num=");
		data.append("");
		MyLog.e(TAG, "work data: " + data.toString());
		String data03 = CryptUtils.getInstance().encryptXOR(data.toString());
		// String url = Constants.URL_WARNING_INFO;

		String ip = (String) SPUtils.get(NetWarningActivity.this, "serverIp",
				"");
		String port = (String) SPUtils.get(NetWarningActivity.this,
				"serverPort", "");
		String url = "http://" + ip + ":" + port + "/"+ AppConfig.EMSWAP_DEPLOY_NAME+"/"
				+ "app?queryAlarmInfo&ct=";

		url = url + data03;
		MyLog.e(TAG, "work url :" + url);
		url = url.replaceAll("\n", ""); // base64会分段加入\n，导致请求失败

		HttpService httpService = new HttpService(this);
		// httpService.get(url, this, null);
		httpService.getForWarning(url, this, null);
	}

	@Override
	public void getResult(String result, Object objParam) {
		// TODO Auto-generated method stub
		// test code
		// String testjsonstring =
		// "{\"alarms\":[{\"advice\":\"不用处理\",\"description\":\"这是一个终端告警\",\"ip\":\"192.168.13.65\",\"level\":1,\"mac\":\"11-22-33-44-55-66\",\"time\":\"2015-12-11 00:00:11\"},{\"advice\":\"不用处理\",\"description\":\"这是终端告警\",\"ip\":\"192.168.13.65\",\"level\":1,\"mac\":\"11-22-33-44-55-66\",\"time\":\"2015-12-11 00:00:11\"},{\"advice\":\"不用处理\",\"description\":\"这是一个局端告警\",\"ip\":\"192.168.13.65\",\"level\":1,\"mac\":\"12-FC-DF-3F-FA-CF\",\"time\":\"2015-12-11 00:00:11\"}],\"returnCode\":\"SUCCESS\",\"returnMsg\":\"成功\"}";
		// result = testjsonstring;
		// test code

		MyLog.e(TAG, result);
		MyProgressDialog.dismiss();
		Map<String, Object> map = null;
		try {
			map = MapUtils.parseObjectData(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (map != null) {
			if (map.containsKey("returnCode")) {
				String returnCode = (String) map.get("returnCode");
				if (returnCode.equals("SUCCESS")) {
					parseImageData(result);
				} else {
					CommonUtils.showTips(NetWarningActivity.this, "提示",
							(String) map.get("returnMsg"));
				}

			} else {
				CommonUtils.showTips(NetWarningActivity.this, "提示", "查找失败");
			}
		} else {
			CommonUtils.showTips(NetWarningActivity.this, "提示", "查找失败");
		}
	}

	private void parseImageData(String result) {

		Gson gson = new Gson();
		try {
			netDeviceInfo = gson.fromJson(result, NetDeviceInfo.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		if (null != netDeviceInfo) {
			MyLog.e("parseImageData", "一层JSON解析成功");
			handler.sendEmptyMessage(GET_DEVICEINFO_SUCCEED);
		} else {
			MyLog.e("parseImageData", "一层JSON解析失败");
			handler.sendEmptyMessage(GET_DEVICEINFO_FAILED);
		}
	}

}
