package com.cloudvision.tanzhenv2.order.wifi.activity;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 *  wifi信号强度波形图
 * 
 * by 谭智文
 */
public class WifiRssiActivity extends BaseActivity{
	
	private static final int SCAN_DURING = 3000; 	//wifi扫描间隔
//	private static final int numberOfPoints = 14;
	
	private int colorNumber = 0;
	private int indexNamber = 0;
	private int fontColor;
	
	private WifiManager wifiManager = null ;
	private WifiScanResult wifiScanResult;
	private List<ScanResult> result;
	private LineChartView lineChartView;
	private LineChartData data = new LineChartData();
	private List<Line> lines = new ArrayList<Line>();
	
	private SparseArray<ScanResult> sparseArray = new SparseArray<ScanResult>();
	private SparseArray<Line> indexArray = new SparseArray<Line>();
	private HashMap<String, Integer> lineIndex = new HashMap<String, Integer>();
	private HashMap<String, String> ssidIndex = new HashMap<String, String>();
	
	private Handler mHandler = new Handler();
	
	private Runnable dataRefreshListener = new Runnable() {
		@Override
		public void run() {
			result = WifiScanResult.getResult();
			wifiScanResult.setCallfuc(new refreshCallbackImpl() {
				@Override
				public void refreshTip() {
					if(result != null ){
						updateApInfo(result);
						prepareDataAnimation();
						lineChartView.startDataAnimation();	
						wifiScanResult.missProgress();
					}
					mHandler.post(dataRefreshListener);
				}
			});		
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//禁止休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_wifi_level);
	
		TextView tv_title = (TextView) findViewById(R.id.top_title);
		tv_title.setText("实时信号强度");
		TextView tv_back = (TextView) findViewById(R.id.top_back);
		tv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
 		
		lineChartView = (LineChartView) findViewById(R.id.chart);
		
		initChart();
		
		//启动wifi扫描
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiScanResult = new WifiScanResult(WifiRssiActivity.this, wifiManager, SCAN_DURING);
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
	
	private void initChart() {
		fontColor = getResources().getColor(R.color.font_color_black);
		Axis axisX = new Axis();
		String[] labels = new String[]{"","","","","1","","2","","3","","4","","5","","6",
				"","7","","8","","9","","10","","11","","12","","13","","14","","","",""};
        List<AxisValue> vAxisValues = new ArrayList<AxisValue>();
        for (int i = 0; i < 35; i++) {
            AxisValue vAxisValue = new AxisValue(i);
            vAxisValue.setLabel(labels[i]);
            vAxisValues.add(vAxisValue);
        }
        axisX.setValues(vAxisValues);
        axisX.setName("信道");
        axisX.setTextColor(fontColor);
        
        data.setAxisXBottom(axisX);
        Axis axisY = new Axis();
        axisY.setName("信号强度(dbm)");
        axisY.setHasLines(true);
        axisY.setTextColor(fontColor);
        data.setAxisYLeft(axisY);
		
		lineChartView.setLineChartData(data);
		lineChartView.setZoomEnabled(false);
		lineChartView.setValueSelectionEnabled(true);
		lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);	
		
		resetViewport();
	}	
	
	private void resetViewport() {
		
        final Viewport v = new Viewport(lineChartView.getMaximumViewport());
        v.bottom = -100;
        v.top = -20;
        v.left = 0;
        v.right = 35;
        lineChartView.setViewportCalculationEnabled(false);
        lineChartView.setMaximumViewport(v);
        lineChartView.setCurrentViewport(v);
    }
	
	/**
     * 使用扫描得到的结果更新图表数据
     * 
     * by 谭智文
     */
    private void updateApInfo(List<ScanResult> result) {

    	sparseArray.clear();
    	
    	for (ScanResult scanResult:result) {
			int keyIndex = -1;
			
			if (lineIndex.containsKey(scanResult.BSSID)) {
				sparseArray.put(lineIndex.get(scanResult.BSSID), scanResult);
			}else {
				keyIndex = addDataSet(scanResult);		//增加一条折线
				sparseArray.put(keyIndex, scanResult);
			}
		}
	}
	
	/**
     * 增加一条线
     * 
     * by 谭智文
     */
    private int addDataSet(ScanResult wData) {

    	int color = pickColor();
    	
    	List<PointValue> values = new ArrayList<PointValue>();
        for (int j = 0; j < 7; ++j) {
        	if (3 == j) {
        		values.add(new PointValue(20, -110).setLabel(wData.SSID));
			}else {
				values.add(new PointValue(20, -110).setLabel(""));
			}           
        }
        
    	Line line = new Line(values);
        line.setColor(color);
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(true);
        line.setFilled(false);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(false);
        line.setPointRadius(0);
        line.setHasLines(true);
        line.setHasPoints(true);
        line.setPointColor(color);
        lines.add(line);

        data.setLines(lines);
        data.setBaseValue(Float.NEGATIVE_INFINITY);
        lineChartView.setLineChartData(data);
        
        lineIndex.put(wData.BSSID, indexNamber);
    	indexArray.put(indexNamber, line);
    	ssidIndex.put(wData.BSSID, wData.SSID);
    	
    	indexNamber++;
    	
        return indexNamber-1; 
    }
	
    /**
     * 更新将要变化的数据
     * 
     * by 谭智文
     */
	private void prepareDataAnimation() {

		for (int i = 0; i < lines.size(); i++) {
			
			ScanResult wData = null;
			Line line = lines.get(i);
			
			int pointXvalue = (int) line.getValues().get(3).getX();
			int level = -120;
			
			if (i < sparseArray.size()-1) {
				wData = sparseArray.get(i);
			}
			if (null != wData) {
				int channel = WifiUtil.frequency2Channel(wData.frequency);
				if (channel > 0) {
					pointXvalue = 2*channel+2;
					level = wData.level;
				}
			}
			
			//共生成7个点构成一条曲线
	    	List<PointValue> newValues = generate7Point(pointXvalue, level);
	    	
	    	for (int j = 0; j < 7; j++) {
	    		PointValue value = line.getValues().get(j);
	    		value.setTarget(newValues.get(j).getX(), newValues.get(j).getY());
	    	}
		}
    }
	
	private List<PointValue> generate7Point(int pointXvalue, int level) {
		
		List<PointValue> values = new ArrayList<PointValue>();
    	//共生成7个点构成一条曲线
		if (level < -110) {			//信号消失时
			values.add(new PointValue(pointXvalue-3, level));
	    	values.add(new PointValue(pointXvalue-2, level));
	    	values.add(new PointValue(pointXvalue-1, level));
	    	values.add(new PointValue(pointXvalue, level));
	    	values.add(new PointValue(pointXvalue+1, level));
	    	values.add(new PointValue(pointXvalue+2, level));
	    	values.add(new PointValue(pointXvalue+3, level));
		}else if (level > -75) {	//较强的信号
			values.add(new PointValue(pointXvalue-3, -110));
	    	values.add(new PointValue(pointXvalue-2, (float) ((level+100)*0.55)-100));
	    	values.add(new PointValue(pointXvalue-1, (float) ((level+100)*0.86)-100));
	    	values.add(new PointValue(pointXvalue, level));
	    	values.add(new PointValue(pointXvalue+1, (float) ((level+100)*0.86)-100));
	    	values.add(new PointValue(pointXvalue+2, (float) ((level+100)*0.55)-100));
	    	values.add(new PointValue(pointXvalue+3, -110));
		}else {						//较弱的信号
			values.add(new PointValue(pointXvalue-3, -110));
	    	values.add(new PointValue(pointXvalue-2, (float) ((level+100)*0.48)-100));
	    	values.add(new PointValue(pointXvalue-1, (float) ((level+100)*0.91)-100));
	    	values.add(new PointValue(pointXvalue, level));
	    	values.add(new PointValue(pointXvalue+1, (float) ((level+100)*0.91)-100));
	    	values.add(new PointValue(pointXvalue+2, (float) ((level+100)*0.48)-100));
	    	values.add(new PointValue(pointXvalue+3, -110));
		}

    	return values;
	}
	
	private int pickColor() {

		int COLOR_RED = getResources().getColor(R.color.chart_red_light);
        int COLOR_VIOLET = getResources().getColor(R.color.chart_violet_light);
        int COLOR_BLUE = getResources().getColor(R.color.chart_blue_light);
        int COLOR_GREEN = getResources().getColor(R.color.chart_green_light);
        int COLOR_YELLOW = getResources().getColor(R.color.chart_yellow_light);
        int COLOR_BROWN = getResources().getColor(R.color.chart_brown_light);
        int COLOR_TEAL = getResources().getColor(R.color.chart_teal_light);
        
        int[] COLORS = new int[]{COLOR_BLUE, COLOR_VIOLET, COLOR_GREEN, 
        		COLOR_BROWN, COLOR_RED,COLOR_YELLOW,COLOR_TEAL};
        
    	colorNumber++;
        if (colorNumber >= COLORS.length) {
        	colorNumber = 0;
		}
        
        return COLORS[colorNumber];
    }
	
}
