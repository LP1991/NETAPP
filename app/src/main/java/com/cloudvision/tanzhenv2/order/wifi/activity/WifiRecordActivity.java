package com.cloudvision.tanzhenv2.order.wifi.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.order.BaseActivity;
import com.cloudvision.tanzhenv2.order.function.refreshCallbackImpl;
import com.cloudvision.tanzhenv2.order.wifi.WifiScanResult;
import com.cloudvision.util.MyLog;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 随时间变化的ap强度折线图
 * 
 * by 谭智文
 */
public class WifiRecordActivity extends BaseActivity implements OnChartValueSelectedListener {

	private static final int RANGE_MAX_X = 20;		//折线同屏最大显示的点数
	private static final int SCAN_DURING = 5000; 	//wifi扫描间隔
//	private static final int REFRESH_SPANNER = 0;
	private static final int REFRESH_CHART_DATA = 1;
	private static final int REFRESH_CHART_VIEW = 2;
	
	private int colorNumber = 0;
	private int time = 1;
	private boolean isVisible = false;
	private boolean isSelectDone = true;
	
	private LineChart mChart;
	private WifiManager wifiManager = null ;
	private WifiScanResult wifiScanResult;
	private List<ScanResult> result;
	
	private List<String> wifiList ; 
	private List<String> multiChoiceItemsKey;
	private String[] multiChoiceItems;
	private boolean[] defaultSelectedStatus;
	private boolean[] currentSelectedStatus = null;
//	private ArrayAdapter<String> adapter;
	
	private List<SparseArray<ScanResult>> allData = new ArrayList<SparseArray<ScanResult>>();
	private HashMap<String, Integer> lineIndex = new HashMap<String, Integer>();
	private HashMap<String, String> ssidIndex = new HashMap<String, String>();
//	private int ssidIndexSize = 0;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
//			case REFRESH_SPANNER:
//				adapter.notifyDataSetChanged();
//				break;
				
			case REFRESH_CHART_DATA:
				mChart.notifyDataSetChanged();
				break;
				
			case REFRESH_CHART_VIEW:
				// 同屏内可以显示的点个数
				mChart.setVisibleXRangeMaximum(RANGE_MAX_X);
				LineData data = mChart.getData();
				if (data != null) {
					mChart.moveViewToX(data.getXValCount() - RANGE_MAX_X -1);
				}
				break;
				
			default:
				break;
			}
		};
	};
	
	private Runnable dataRefreshListener = new Runnable() {

		@Override
		public void run() {

			wifiScanResult.setCallfuc(new refreshCallbackImpl() {
				@Override
				public void refreshTip() {
					
					result = WifiScanResult.getResult();
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							
							if(result != null){
								updateApInfo(result);
//								updateSpanner();
//								wifiScanResult.missProgress();
							}
							
							mHandler.post(dataRefreshListener);	
						}
					}).start();	
					
					if(isSelectDone){
						if(!ssidIndex.isEmpty()){
							wifiScanResult.missProgress();
							showMenuDialog();
							isSelectDone = false;
						}	
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
        setContentView(R.layout.activity_wifi_realtime);
        
        initView(); 
        initChart();
        
        //启动wifi扫描
  		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
  		wifiScanResult = new WifiScanResult(WifiRecordActivity.this, wifiManager, SCAN_DURING);
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
    
    private void initView() {
		
    	TextView tv_title = (TextView) findViewById(R.id.top_title);
 		tv_title.setText("信号强度趋势");
 		TextView tv_back = (TextView) findViewById(R.id.top_back);
 		tv_back.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View v) {
 				finish();
 			}
 		});
 		ImageView iButton = (ImageView) findViewById(R.id.top_menu);
 		iButton.setVisibility(View.VISIBLE);
 		iButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showMenuDialog();
				
			}
		});
 		
// 		Spinner spinner = (Spinner)findViewById(R.id.wifi_select);  
// 		wifiList = new ArrayList<String>();  
// 		wifiList.add("all");
//        adapter = new ArrayAdapter<String>(this,R.layout.wifi_select_item, R.id.text, wifiList);  
//        spinner.setAdapter(adapter);  
//        spinner.setPrompt("wifi");  
//        spinner.setOnItemSelectedListener(new WifiOnItemSelectedListener());
	}
    
    private void initChart() {
    	
    	mChart = (LineChart) findViewById(R.id.lineChart);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setHardwareAccelerationEnabled(false);
        mChart.setDescription("");
        mChart.setHighlightEnabled(true);
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(true);
        mChart.setScaleXEnabled(true);
        mChart.setScaleYEnabled(true);
        mChart.setDrawGridBackground(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);
        
        //数据指示配置
        Legend l = mChart.getLegend();
        l.setForm(LegendForm.SQUARE);
        l.setTextColor(Color.BLACK);
        int[] colors = new int[]{};
    	String[] labels = new String[]{};
        l.setCustom(colors, labels);
        
        //x轴配置
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setPosition(XAxisPosition.BOTTOM);
        xl.setAvoidFirstLastClipping(true);
        xl.setSpaceBetweenLabels(2);
        xl.setEnabled(true);

        //y轴配置
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(-20);
        leftAxis.setAxisMinValue(-100);
        leftAxis.setStartAtZero(false);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTextColor(Color.BLACK);
        rightAxis.setAxisMaxValue(-20);
        rightAxis.setAxisMinValue(-100);
        rightAxis.setStartAtZero(false);
        rightAxis.setDrawGridLines(true);
        
	}
    
    /**
     * 使用扫描得到的结果更新图表数据
     * 
     * by 谭智文
     */
    private void updateApInfo(List<ScanResult> result) {
    	
    	SparseArray<ScanResult> indexArray = new SparseArray<ScanResult>();

    	for (ScanResult scanResult:result) {
			
			int keyIndex = -1;

			if (lineIndex.containsKey(scanResult.BSSID)) {
				indexArray.put(lineIndex.get(scanResult.BSSID), scanResult);
			}else {
				keyIndex = addDataSet(scanResult);	//增加一条折线
				indexArray.put(keyIndex, scanResult);	
			}
		}
    	
		allData.add(indexArray);
		addEntry();		//向前推进，增加点
	}
    
    /**
     * 在有新wifi时增加Spanner的数据
     * 
     * by 谭智文
     */
//    private void updateSpanner() {
//		
//    	int newSize = ssidIndex.size();
//    	if (newSize > ssidIndexSize) {
//    		wifiList.clear();
//        	wifiList.add("all");
//    		for (java.util.Map.Entry<String, String> entry: ssidIndex.entrySet()) {  
//    		    String wifiNameString = entry.getValue() + "(" + entry.getKey() + ")";
//    			wifiList.add(wifiNameString);
//    		}
//		}
//    	ssidIndexSize = newSize;
//    	mHandler.sendEmptyMessage(REFRESH_SPANNER);
//	}
    
    /**
     * 增加一条线
     * 
     * by 谭智文
     */
    private int addDataSet(ScanResult wData) {

        LineData data = mChart.getData();
        
        if(data != null) {
           
            LineDataSet set = createSet(null, wData.SSID);
            data.addDataSet(set);
            
            int index = data.getIndexOfDataSet(set);
            lineIndex.put(wData.BSSID,index);
            ssidIndex.put(wData.BSSID, wData.SSID);
            
            return index;
        }
        return -1;
    }

    /**
     * 为每一折线增加点，时间推进一格
     * 
     * by 谭智文
     */
    private void addEntry() {

        LineData data = mChart.getData();
        if (data != null) {
        
        	SparseArray<ScanResult> lastIndex = allData.get(allData.size()-1);
        	
        	//遍历所有的线
        	for (int i = 0; i < data.getDataSetCount(); i++) {
        		
        		int yValue = -100;
        		
        		ScanResult wifiData = lastIndex.get(i, null);
        		if (null != wifiData) {
        			 yValue = wifiData.level;
				}
        		data.addEntry(new Entry(yValue, time-1), i);
			}
        	
        	// 增加一个X轴的时间点
            data.addXValue(String.valueOf(time++));
            mHandler.sendEmptyMessage(REFRESH_CHART_DATA);
            mHandler.sendEmptyMessage(REFRESH_CHART_VIEW);
                
        }
    }

    private LineDataSet createSet(ArrayList<Entry> yEntries, String lineName) {

        LineDataSet set = new LineDataSet(yEntries, lineName);
        int color = pickColor();
        set.setAxisDependency(AxisDependency.LEFT);
        set.setColor(color);
        set.setCircleColor(color);
        set.setLineWidth(2f);
        set.setCircleSize(3f);
        set.setFillAlpha(65);
        set.setFillColor(color);
        set.setHighLightColor(color);
        set.setValueTextSize(9f);
        set.setDrawValues(true);
        set.setVisible(isVisible);
        if (isVisible) {
        	set.setValueTextColor(color);
		}else {
			set.setValueTextColor(00000000);		
		}
        
        return set;
    }
    
    /**
     * 显示dialog，选择想要显示的wifi强度记录
     * 
     * by 谭智文
     */
    public void showMenuDialog(){  
    	
    	wifiList = new ArrayList<String>();
    	multiChoiceItemsKey = new ArrayList<String>();
//    	SparseArray<ScanResult> lastIndex = allData.get(allData.size()-1);
    	for (java.util.Map.Entry<String, String> entry: ssidIndex.entrySet()) {  
//    		ScanResult scanResult = lastIndex.get(lineIndex.get(entry.getKey()));
//			if (null != scanResult) {
//				wifiList.add(entry.getValue()+"("+scanResult.level+"mdb)");
//			}else {
//				wifiList.add(entry.getValue()+"(-99mdb)");
//			}	
    		wifiList.add(entry.getValue());
			multiChoiceItemsKey.add(entry.getKey());
		}
    	
    	//list转为数组
    	multiChoiceItems = (String[])wifiList.toArray(new String[wifiList.size()]);
        defaultSelectedStatus = new boolean[multiChoiceItems.length];  
        for (int i = 0; i < defaultSelectedStatus.length; i++) {
        	defaultSelectedStatus[i] = false;
		}

        //创建对话框    
        new AlertDialog.Builder(WifiRecordActivity.this)    
        .setTitle("选择想要显示的wifi")//设置对话框标题    
        .setMultiChoiceItems(multiChoiceItems, defaultSelectedStatus, new OnMultiChoiceClickListener(){    
            @Override    
            public void onClick(DialogInterface dialog, int which,    
                    boolean isChecked) {    
                //来回重复选择取消，得相应去改变item对应的bool值，点击确定时，根据这个bool[],得到选择的内容   
                defaultSelectedStatus[which] = isChecked;  
            }    
        })  //设置对话框[肯定]按钮    
        .setPositiveButton("确定",new DialogInterface.OnClickListener() {  
              
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
            	currentSelectedStatus = defaultSelectedStatus;
            	lineVisible();
            }  
        })
        .setNegativeButton("取消", null)//设置对话框[否定]按钮    
        .show();    
    }  
    
    /**
     * 只显示指定的折线
     * 
     * by 谭智文
     */
    private void lineVisible() {
		
    	LineData data = mChart.getData();
    	Legend legend = mChart.getLegend();
    	int[] colors = new int[currentSelectedStatus.length];
    	String[] labels = new String[currentSelectedStatus.length];
  
        if (data != null) {
//        	if (-1 == index) {
//        		isVisible = true;
//        		for (int i = 0; i < data.getDataSetCount(); i++) {
//	        		
//					LineDataSet set = data.getDataSetByIndex(i);
//					set.setVisible(true);
//					set.setValueTextColor(set.getColor());
//					colors[i] = set.getColor();
//					labels[i] = set.getLabel();
//	        	}
//			}else {
//				isVisible = false;
//				for (int i = 0; i < data.getDataSetCount(); i++) {
//					LineDataSet set = data.getDataSetByIndex(i);
//					if (i == index){
//						set.setVisible(true);
//						set.setValueTextColor(set.getColor());
//						colors[0] = set.getColor();
//						labels[0] = set.getLabel();
//					}else {
//						set.setVisible(false);
//						set.setValueTextColor(00000000);
//					}
//	        	}
//			}
        	
        	for (int i = 0; i < data.getDataSetCount(); i++) {
				LineDataSet set = data.getDataSetByIndex(i);
					set.setVisible(false);
					set.setValueTextColor(00000000);
        	}
        	int setNember = 0;
        	for (int i = 0; i < currentSelectedStatus.length; i++) {
        		if (currentSelectedStatus[i] == true) {
            		String tempString = multiChoiceItemsKey.get(i);
            		int index = lineIndex.get(tempString);
            		LineDataSet set = data.getDataSetByIndex(index);
            		set.setVisible(true);
            		int color = pickColor();
            		set.setColor(color);
            		set.setCircleColor(color);
            		colors[setNember] = color;
    				labels[setNember] = set.getLabel();
    				setNember++;
				}
			}
        	
        	legend.setCustom(colors, labels);
        	mHandler.sendEmptyMessage(REFRESH_CHART_DATA);
        }
	}
    
    private int pickColor() {

    	int COLOR_RED = Color.parseColor("#AAe51c23");
        int COLOR_VIOLET = Color.parseColor("#AA651fff");
        int COLOR_BLUE = Color.parseColor("#AA00b0ff");
        int COLOR_GREEN = Color.parseColor("#AA558b2f");
        int COLOR_YELLOW = Color.parseColor("#AAef6c00");
        int COLOR_BROWN = Color.parseColor("#AA4e342e");
        int COLOR_TEAL = Color.parseColor("#AA1de9b6");
        
        int[] COLORS = new int[]{COLOR_BLUE, COLOR_VIOLET, COLOR_GREEN, 
        		COLOR_BROWN, COLOR_RED,COLOR_YELLOW,COLOR_TEAL};
        
    	colorNumber++;
        if (colorNumber >= COLORS.length) {
        	colorNumber = 0;
		}
        
        return COLORS[colorNumber];
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//        MyLog.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
//        MyLog.i("Nothing selected", "Nothing selected.");
    }
    
//    private class  WifiOnItemSelectedListener implements OnItemSelectedListener{         
//    	
//        @Override  
//        public void onItemSelected(AdapterView<?> adapter,View view,int position,long id) {  
//            //获取选择的项的值  
//            String sInfo = adapter.getItemAtPosition(position).toString();  
//            if (sInfo.equals("all")) {
//            	lineVisible(-1);
//			}else {
//				String tempString = sInfo.substring(sInfo.indexOf("(")+1, sInfo.indexOf(")"));
//	            int index = lineIndex.get(tempString);
//                lineVisible(index);
//			}
//        }  
//
//        @Override  
//        public void onNothingSelected(AdapterView<?> arg0) {  
//        }  
//    }
    
}
