package com.cloudvision.tanzhenv2.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.activity.MainActivity;
import com.cloudvision.tanzhenv2.order.constants.Constants;
import com.cloudvision.tanzhenv2.order.deal.CryptUtils;
import com.cloudvision.tanzhenv2.order.deal.MapUtils;
import com.cloudvision.tanzhenv2.order.deal.MySharedPreferencesUtils;
import com.cloudvision.tanzhenv2.order.deal.NoScrollViewPager;
import com.cloudvision.tanzhenv2.order.deal.UploadUtils;
import com.cloudvision.tanzhenv2.order.fragment.MapTabFragment;
import com.cloudvision.tanzhenv2.order.fragment.TroubleTabFragment;
import com.cloudvision.tanzhenv2.order.fragment.UserTabFragment;
import com.cloudvision.tanzhenv2.order.httpservice.HttpService;
import com.cloudvision.tanzhenv2.order.httpservice.HttpServiceInterface;
import com.cloudvision.tanzhenv2.order.model.WorkListJson;
import com.cloudvision.tanzhenv2.order.tabLayout.SlidingTabLayout;
import com.cloudvision.util.MyLog;
import com.google.gson.Gson;

import java.io.File;
import java.util.Map;

/**
 * 工单详情界面
 * Created by Admin on 2015/7/29.
 */
public class WorkListDetailsActivity extends FragmentActivity {
	
	private static final String TAG = "WorkListDetails";

    private int workId;
    private int tag;
    private String userMsg;
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private TextView backTextView;

    /**
     * 地图反编码
     */
    private Runnable runnable = new Runnable() {
        public void run() {

            Gson gson = new Gson();
            WorkListJson json = Constants.json;
            String city = json.getAddresspath();
            String[] temp = city.split(" ");
            String address = json.getCustomeraddress();
            reverseGeoCode(temp[0], address);

            if (!MySharedPreferencesUtils.contains(String.valueOf(json.getId()))) {
                String sjson = gson.toJson(json);
                MySharedPreferencesUtils.put(String.valueOf(json.getId()), sjson);
            }
        }
    };
    
    /**
     * 数据上传结果Message
     */
    private Handler ButtonHandler = new Handler() {
        public void handleMessage(Message msg) {
        	if(1 == msg.what){
        		switch (tag) {
                case 0:
                    Toast.makeText(getApplicationContext(), "成功接受", Toast.LENGTH_SHORT).show();
                    linearLayout1.setVisibility(View.GONE);
                    linearLayout2.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "拒绝已提交", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "处理结果已提交", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "工单文件已上报", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(), "无法处理原因已提交", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    break;
        		}
        	}else {
        		switch (tag) {
                case 0:
                    Toast.makeText(getApplicationContext(), "接受失败", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "拒绝失败", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "提交失败", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "提交失败", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(), "提交失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
        		}
			}
            super.handleMessage(msg);
        }
    };
    
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

            MyLog.e(TAG,Thread.currentThread().getName());
            if (map != null) {
                String value = map.get("returnCode");
                System.out.println(value);

                Message message = new Message();
                if (value.equals("SUCCESS")) {
                	message.what = 1;
                }else {
                	message.what = 0;
				} 	
                WorkListDetailsActivity.this.ButtonHandler.sendMessage(message);
            }
        }
    };

    /**
     * 数据上传结果Message
     */
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(WorkListDetailsActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(WorkListDetailsActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worklist_details);
        //使用到了百度地图反编码
        SDKInitializer.initialize(getApplicationContext());

        SlidingTabLayout tabLayout = (SlidingTabLayout) findViewById(R.id.tabLayout);
        NoScrollViewPager viewPager = (NoScrollViewPager) findViewById(R.id.pager);
        viewPager.setNoScroll(true);//设置viewPaper不能滑动
        viewPager.setOffscreenPageLimit(2);
        linearLayout1 = (LinearLayout) findViewById(R.id.line_btn_one);
        linearLayout2 = (LinearLayout) findViewById(R.id.line_btn_two);

        Button btnYes = (Button) findViewById(R.id.btn_work_yes);
        Button btnNo = (Button) findViewById(R.id.btn_work_no);
        Button btnDone = (Button) findViewById(R.id.btn_work_done);
        Button btnUp = (Button) findViewById(R.id.btn_work_up);
        Button btnFail = (Button) findViewById(R.id.btn_work_fail);
        backTextView = (TextView)findViewById(R.id.top_back);  

        btnYes.setTag(0);
        btnNo.setTag(1);
        btnDone.setTag(2);
        btnUp.setTag(3);
        btnFail.setTag(4);
        backTextView.setTag(5);
        
        TextView tv_back = (TextView) findViewById(R.id.top_back);
		tv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		}); 

        Intent intent = getIntent();
        workId = Integer.valueOf(intent.getStringExtra("id"));
        String orderstatus = intent.getStringExtra("orderstatus");
        String history = intent.getStringExtra("order");

        if (orderstatus != null && orderstatus.equals("维修中")) {
            linearLayout1.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.VISIBLE);
        }
        if (history != null && history.equals("true")) {
            linearLayout1.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.GONE);
        }

        //从缓存中取工单数据
        //构造MySharedPreferencesUtils
        MySharedPreferencesUtils.getInstance(WorkListDetailsActivity.this, "share_data");
        String sjson = (String) MySharedPreferencesUtils.get(String.valueOf(workId), "");
        Gson gson = new Gson();
        Constants.json = gson.fromJson(sjson, WorkListJson.class);

        //设置tabPager
//        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
//        tabLayout.setViewPager(viewPager);  
        SectionPagerAdapter adapter= new SectionPagerAdapter(getSupportFragmentManager());  
        viewPager.setAdapter(adapter);  
        tabLayout.setViewPager(viewPager);  

        Handler myHandler = new Handler();
        myHandler.post(runnable);
    }

    /**
     *  点击事件处理
     */
    public void onClick(View view) {

        tag = (Integer) view.getTag();
        final StringBuilder data = new StringBuilder(256);
        String userName = (String) MySharedPreferencesUtils.get("userName", "");
        data.append("id=");
        data.append(workId);
        data.append("&userName=");
        data.append(userName);

        switch (tag) {
            case 0:
                data.append("&orderStatus=5");//接受
                workHttp(data.toString());
                break;
            case 1:
                data.append("&orderStatus=3");//拒绝
                data.append("&remark=");
                showDialog("拒绝工单", "请填写拒绝理由", data);
                break;
            case 2:
                data.append("&orderStatus=7");//处理完成
                data.append("&resolveResult=");
//                data.append("故障已解决");
//                data.append("&resolveScore=");
//                showDialog("处理完成", "请填写处理方式", data);
                Intent intent = new Intent();
		  	    intent.setClass(WorkListDetailsActivity.this, MainActivity.class);
		  	    intent.putExtra("fragid", 1);
		  	    intent.putExtra("orderid", workId);
		  	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		  	    startActivity(intent);
                break;
            case 3:					//文件上传
                String datas = "fileType=1&id=" + workId;
                workHttpPost(datas);
                break;
            case 4:
                data.append("&orderStatus=6");//无法解决
                data.append("&remark=");
                showDialog("无法解决", "请填写无法解决的原因", data);
                break;
            case 5:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 反地理编码得到地址信息
     */
    private void reverseGeoCode(String city, String address) {
        // 创建地理编码检索实例
        final GeoCoder geoCoder = GeoCoder.newInstance();

        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            // 反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            }

            // 地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    MyLog.i("order", "抱歉，未能找到结果");
                    myHandler.postDelayed(runnable,2000);
                } else {
                    Constants.pointLatitude = result.getLocation().latitude;
                    Constants.pointLongtitude = result.getLocation().longitude;
                    // 释放地理编码检索实例
                    geoCoder.destroy();
                }
            }
        };
        // 设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(listener);
        geoCoder.geocode(new GeoCodeOption()
                .city(city)
                .address(address));
    }

    /**
     * 显示Dialog
     *
     * @param titleId   标题
     * @param massageId 内容
     * @param data      数据
     */
    private void showDialog(String titleId, String massageId, final StringBuilder data) {

        LayoutInflater inflater = (LayoutInflater) WorkListDetailsActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        int layoutId = R.layout.workdetails_dialog;
        final View view = inflater.inflate(layoutId, null);
//        final View view = inflater.inflate(R.layout.workdetails_dialog, null);
        userMsg = null;
        new AlertDialog.Builder(WorkListDetailsActivity.this)
                .setTitle(titleId)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(massageId)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        EditText et = (EditText) view.findViewById(R.id.dialog_et);
                        if (tag == 2) et.setInputType(InputType.TYPE_CLASS_NUMBER);
                        else et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        userMsg = et.getText().toString();
                        et.setError(null);

                        if (TextUtils.isEmpty(userMsg)) {
                            Toast.makeText(getApplicationContext(), "请输入内容", Toast.LENGTH_SHORT).show();
                            et.setError(getString(R.string.error_dialog));
                            et.requestFocus();
                        } else {
                            data.append(userMsg);
                            workHttp(data.toString());
                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }


    /**
     * get方法传递数据到后台
     *
     * @param data 需要传递的数据
     */
    private void workHttp(String data) {

        MyLog.i(TAG, "work data: " + data);
        String data03 = CryptUtils.getInstance().encryptXOR(data);
        String url = Constants.URL_WORKDEAL;
        url = url + data03;
        MyLog.i(TAG, "work url :" + url);
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        HttpService httpService = new HttpService(this);
        httpService.get(url, httpResult, null);
    }

    /**
     * post方法传递数据
     *
     * @param data 参数
     */
    private void workHttpPost(String data) {

        MyLog.i(TAG, "work data: " + data);
        String data03 = CryptUtils.getInstance().encryptXOR(data);
        String url = Constants.URL_UPLOAD;
        url = url + data03;
        MyLog.i(TAG, "work url :" + url);
        url = url.replaceAll("\n", ""); //base64会分段加入\n，导致请求失败

        final String finalUrl = url;
        Thread updateCurrentTime = new Thread() {
            @Override
            public void run() {

                File upfiles = new File(Constants.upFileURL);
                String result = UploadUtils.uploadFile(upfiles, finalUrl);

                String value;
                Map<String, String> map = null;
                try {
                    map = MapUtils.parseData(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (map != null) {
                    value = map.get("returnCode");
                    Message message = new Message();
                    if (value.equals("SUCCESS")) {             
                        message.what = 0;
                    } else {
                        message.what = 1;
                    }
                    WorkListDetailsActivity.this.myHandler.sendMessage(message);
                }
            }
        };
        updateCurrentTime.start();
    }

    /**
     * 页面适配器
     */
    private class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MapTabFragment();
                case 1:
                    return new TroubleTabFragment();
                case 2:
                default:
                    return new UserTabFragment();
            }
        }

        @Override
        public int getCount() {
            return Constants.TOTAL_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.fragment_third_title);
                case 1:
                    return getString(R.string.fragment_second_title);
                case 2:
                default:
                    return getString(R.string.fragment_first_title);
            }
        }
    }
    
    public boolean dispatchKeyEvent(KeyEvent event) {
		return false;
	}

}
