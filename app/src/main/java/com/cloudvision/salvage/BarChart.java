package com.cloudvision.salvage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

import com.cloudvision.tanzhenv2.application.ContextUtil;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

public class BarChart extends AbstractDemoChart {
	  /**
	   * Returns the chart name.
	   * 
	   * @return the chart name
	   */
	  public String getName() {
	    return "Sales stacked bar chart";
	  }

	  /**
	   * Returns the chart description.
	   * 
	   * @return the chart description
	   */
	  public String getDesc() {
	    return "The monthly sales for the last 2 years (stacked bar chart)";
	  }

	  /**
	   * 自定义X轴坐标；单柱状图
	   * Executes the chart demo.
	   * 
	   * @param context the context
	   * @return the built intent
	   */
	  public GraphicalView execute(Context context) {

		String[] titles = new String[] { "values" };

	
//			double[] arr = getResult(srp.carrierResult);
		
		//增加横坐标x
		List<double[]> x = new ArrayList<double[]>();
	
	    double[] xValue = new double[3000];
	    for(int i=0;i<3000;i++)
	    {
	    	xValue[i] = i;
	    }
		for (int i = 0; i < titles.length; i++) {
			x.add(xValue);
		}                     
		
		List<double[]> values = new ArrayList<double[]>();


		int[] colors = new int[] { Color.YELLOW };
		
		PointStyle[] styles = new PointStyle[] { PointStyle.POINT, PointStyle.DIAMOND,
		        PointStyle.TRIANGLE, PointStyle.SQUARE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		
//		XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
		setChartSettings(renderer, "比特分配图",
				"Carrier:MHZ", "单位:dB", 0, 3100, 0, 14, Color.CYAN,
				Color.CYAN);     

		for(int i=0;i<3100;i++){
			
			if(i%200 == 0)
			{
				int j = i/200;
				renderer.addXTextLabel(i+1, String.valueOf(j*0.5));
			}
		}

		//如果想要在X轴显示自定义的标签，那么首先要设置renderer.setXLabels(0);  
		//如果不设置为0，那么所设置的Labels会与原X坐标轴labels重叠
		renderer.setXLabels(0);
		renderer.setYLabels(8);
		renderer.setXLabelsAlign(Align.LEFT);
		renderer.setYLabelsAlign(Align.LEFT);
		renderer.setPanEnabled(true, false);
		renderer.setZoomRate(1.1f);
		renderer.setBarSpacing(0.5f);
//		renderer.setZoomButtonsVisible(true);
		
		XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);
		//返回GraphicalView,可以灵活设置也可以仅作为一部分显示在任何activity上.
//		return ChartFactory.getBarChartView(context, dataset, renderer, Type.STACKED);
		
		new Thread()
		{
			public void run()
			{
				try {
					Thread.sleep(500);
					Intent intent = new Intent();
					intent.setAction("bitmap");
					ContextUtil contextUtil =ContextUtil.getInstance_o();
					contextUtil.localBroadcastManager.sendBroadcast(intent);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		return ChartFactory.getLineChartView(context, dataset, renderer);
	  }

	}

