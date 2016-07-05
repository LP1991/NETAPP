package com.cloudvision.tanzhenv2.order.wifi.activity;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.BaseActivity;
import com.cloudvision.tanzhenv2.order.function.refreshCallbackImpl;
import com.cloudvision.tanzhenv2.order.wifi.WifiScanResult;
import com.cloudvision.tanzhenv2.order.wifi.WifiUtil;
import com.cloudvision.util.MyLog;

/**
 * 周边各个信道的占用情况
 * 
 * Created by 谭智文
 */
public class WifiChannelActivity extends BaseActivity{
	
	private static final int SCAN_DURING = 2000; 	//wifi扫描间隔
	
	private int colorNumber = 0;
	
	private WifiManager wifiManager = null ;
	private WifiScanResult wifiScanResult;
	private List<ScanResult> result;
	private ColumnChartData data;
	private ColumnChartView columnChartView;
	private boolean isSelectDone = true;
	
	private Handler mHandler = new Handler();
	
	private Runnable dataRefreshListener = new Runnable() {
		@Override
		public void run() {
			result = WifiScanResult.getResult();
			wifiScanResult.setCallfuc(new refreshCallbackImpl() {
				@Override
				public void refreshTip() {
					if(result != null ){
						prepareDataAnimation();
						columnChartView.startDataAnimation();
					}
					mHandler.post(dataRefreshListener);
					
					if(isSelectDone){
						wifiScanResult.missProgress();
						isSelectDone = false;	
					}
				}
			});		
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//禁止休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_wifi_channel);
	
		TextView tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText("信道占用情况");
		TextView tv_back = (TextView) findViewById(R.id.top_back);
		tv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		generateChart();
		
		//启动wifi扫描
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiScanResult = new WifiScanResult(WifiChannelActivity.this, wifiManager, SCAN_DURING);
		wifiScanResult.setAutoMiss(false);
		mHandler.post(dataRefreshListener);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		
		MyLog.e("wifiActivity","onDestroy");
		if(wifiScanResult != null){
			wifiScanResult.stopReceiver();
		}
		if(dataRefreshListener != null){
			mHandler.removeCallbacks(dataRefreshListener);
		}
	}
	
	private void prepareDataAnimation() {
		
		int[] channel = new int[14];
		for (int j = 0; j < result.size(); j++) {
			int frequency = result.get(j).frequency;
			int channelValue = WifiUtil.frequency2Channel(frequency);
			if(channelValue > 0)
				channel[channelValue-1]++;
		}

		
		for (int i = 0; i < data.getColumns().size(); i++) {
			Column column = data.getColumns().get(i);
			SubcolumnValue value = column.getValues().get(0);
			value.setTarget(channel[i]);
		}
    }
	
	private void generateChart(){
		
		columnChartView = (ColumnChartView) findViewById(R.id.chart);
		data = generateColumnChartData();
		columnChartView.setColumnChartData(data);
        columnChartView.setZoomEnabled(false);
        columnChartView.setValueSelectionEnabled(true);
        columnChartView.setDrawingCacheQuality(1000);

        /** Note: Chart is within ViewPager so enable container scroll mode. **/
        columnChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
		
	}
	
	private ColumnChartData generateColumnChartData() {
		
        int numSubcolumns = 1;
        int numColumns = 14;

        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        SubcolumnValue mSubcolumnValue;
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
            	mSubcolumnValue = new SubcolumnValue(0, pickColor());
                values.add(mSubcolumnValue);
            }
            Column column = new Column(values);
            column.setHasLabels(true);
            columns.add(column);
        }
        
        ColumnChartData data = new ColumnChartData(columns);

        Axis axisX = new Axis();
        List<AxisValue> vAxisValues = new ArrayList<AxisValue>();
        for (int i = 0; i < numColumns; i++) {
            AxisValue vAxisValue = new AxisValue(i);
            vAxisValue.setLabel(String.valueOf(i+1));
            vAxisValues.add(vAxisValue);
        }
        axisX.setValues(vAxisValues);
        axisX.setName("信道");
        data.setAxisXBottom(axisX);
        
        Axis axisY = new Axis();
//        vAxisValues = new ArrayList<AxisValue>();
//        for (int i = 0; i < numColumns; i++) {
//            AxisValue vAxisValue = new AxisValue(i);
//            vAxisValue.setLabel(String.valueOf(i));
//            vAxisValues.add(vAxisValue);
//        }
//        axisY.setValues(vAxisValues);
        axisY.setHasLines(true);
        axisY.setName("信道占用数量");
        data.setAxisYLeft(axisY);

        return data;
    }
	
	private int pickColor() {

    	int COLOR_RED = getResources().getColor(R.color.chart_red_normal);
        int COLOR_VIOLET = getResources().getColor(R.color.chart_violet_normal);
        int COLOR_BLUE = getResources().getColor(R.color.chart_blue_normal);
        int COLOR_GREEN = getResources().getColor(R.color.chart_green_normal);
        int COLOR_YELLOW = getResources().getColor(R.color.chart_yellow_normal);
        int COLOR_BROWN = getResources().getColor(R.color.chart_brown_normal);
        int COLOR_TEAL = getResources().getColor(R.color.chart_teal_normal);
        
        int[] COLORS = new int[]{COLOR_BLUE, COLOR_VIOLET, COLOR_GREEN, 
        		COLOR_BROWN, COLOR_RED,COLOR_YELLOW,COLOR_TEAL};
        
    	colorNumber++;
        if (colorNumber >= COLORS.length) {
        	colorNumber = 0;
		}
        
        return COLORS[colorNumber];
    }

}
