/*
 * Copyright 2014 trinea.cn All right reserved. This software is the confidential and proprietary information of
 * trinea.cn ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with trinea.cn.
 */
package com.cloudvision.viewpager.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cloudvision.salvage.RecyclingPagerAdapter;
import com.cloudvision.tanzhenv2.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * AdvertImagePagerAdapter
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-23
 */
public class AdvertImagePagerAdapter extends RecyclingPagerAdapter {

	private Context context;
	private List<String> imageUrlList;  //更改为传入url -- 谭智文
	private List<Integer> imageIdList;

	private int size;
	private boolean isInfiniteLoop;

	public AdvertImagePagerAdapter(Context context, List<String> imageUrlList) {
		this.context = context;
		this.imageUrlList = imageUrlList;
		this.size = imageUrlList.size();
		isInfiniteLoop = false;
	}

	@Override
	public int getCount() {
		// Infinite loop
		return isInfiniteLoop ? Integer.MAX_VALUE : imageUrlList.size();
	}

	/**
	 * get really position
	 * 
	 * @param position
	 * @return
	 */
	private int getPosition(int position) {
		return position % size;
	}

	@Override
	public View getView(int position, View view, ViewGroup container) {
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = holder.imageView = new ImageView(context);
			holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		//更改图片从网络获取显示 -- 谭智文
//		holder.imageView.setImageResource(imageIdList.get(getPosition(position)));
		final int finalposition = position;
		final ImageView finalImageView = holder.imageView;
		ImageLoader.getInstance().displayImage(imageUrlList.get(getPosition(position)), holder.imageView
				,new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build()
		        ,new ImageLoadingListener() {

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						imageIdList = new ArrayList<Integer>();
						imageIdList.add(R.drawable.ad1);
						imageIdList.add(R.drawable.ad2);
						imageIdList.add(R.drawable.ad3);
						finalImageView.setImageResource(imageIdList.get(getPosition(finalposition)));
					}

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						
					}  
					 
				 });
		return view;
	}

	private static class ViewHolder {

		ImageView imageView;
	}

	/**
	 * @return the isInfiniteLoop
	 */
	public boolean isInfiniteLoop() {
		return isInfiniteLoop;
	}

	/**
	 * @param isInfiniteLoop the isInfiniteLoop to set
	 */
	public AdvertImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
		this.isInfiniteLoop = isInfiniteLoop;
		return this;
	}
}
