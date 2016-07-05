package com.cloudvision.tanzhenv2.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.cloudvision.tanzhenv2.R;
import com.cloudvision.tanzhenv2.activity.MainActivity;
import com.cloudvision.util.MyLog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class GuideActivity extends Activity implements OnClickListener, OnPageChangeListener {
	
	private static final String TAG = "GuideActivity";
	//定义ViewPager对象
    private ViewPager viewPager;
    //定义ViewPager适配器
    private StartPagerAdapter vpAdapter;
    //定义一个ArrayList来存放View
    private ArrayList<View> views;
    //底部小点的图片
    private ImageView[] points;
    //是否显示小点
    private boolean pointVisible = false;

    //记录当前选中位置
    private int currentIndex;
    private String[] imageUrl;		//将要显示的引导页图片
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_main);
        
        firstInitView();
        firstInitData();      
    }
    
    /**
     * 初始化组件
     */
    private void firstInitView() {

        //实例化ArrayList对象
        views = new ArrayList<View>();
        //实例化ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        //实例化ViewPager适配器
        vpAdapter = new StartPagerAdapter(views);   
        
        Button enterMain = (Button) findViewById(R.id.first_enter_main);
        enterMain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MyLog.e(TAG, "进入主界面。。。");
				Intent intent = new Intent(GuideActivity.this, MainActivity.class);
				GuideActivity.this.startActivity(intent);
				finish();
			}
		});
    }
    
    /**
     * 初始化数据
     */
    private void firstInitData() {
        //定义一个布局并设置参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        
        String StartDefault = "drawable://"+R.drawable.default_start;
        String StartDefault2 = "drawable://"+R.drawable.ad1;
        String StartDefault3 = "drawable://"+R.drawable.ad2;
        imageUrl = new String[]{StartDefault,StartDefault2,StartDefault3};
        
//        List<String> list = new ArrayList<String>();
//        imageUrl = (String[])list.toArray(new String[list.size()]);
        
        //初始化引导图片列表
        for (int i = 0; i < imageUrl.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            ImageLoader.getInstance().displayImage(imageUrl[i], iv);
            iv.setScaleType(ScaleType.CENTER_CROP);
            views.add(iv);
        }

        //设置数据
        viewPager.setAdapter(vpAdapter);
        //设置监听
        viewPager.setOnPageChangeListener(this);
        //初始化底部小点
        initPoint();
    }

    /**
     * 初始化底部小点
     */
    private void initPoint() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);
        if (!pointVisible) {
        	linearLayout.setVisibility(View.GONE);
		}
        
        points = new ImageView[imageUrl.length];

        //循环取得小点图片
        for (int i = 0; i < imageUrl.length; i++) {
            //得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            //默认都设为灰色
            points[i].setEnabled(true);
            //给每个小点设置监听
            points[i].setOnClickListener(this);
            //设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
        }

        //设置当面默认的位置
        currentIndex = 0;
        //设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }

    /**
     * 当滑动状态改变时调用
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    /**
     * 当前页面被滑动时调用
     */

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    /**
     * 当新的页面被选中时调用
     */

    @Override
    public void onPageSelected(int position) {
        //设置底部小点选中状态
        setCurDot(position);
        Button enterMain = (Button) findViewById(R.id.first_enter_main);
        if (position == imageUrl.length - 1) {
        	enterMain.setVisibility(View.VISIBLE);
		}else {
			enterMain.setVisibility(View.GONE);
		}
    }

    /**
     * 通过点击事件来切换当前的页面
     */
    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }

    /**
     * 设置当前页面的位置
     */
    private void setCurView(int position) {
        if (position < 0 || position >= imageUrl.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > imageUrl.length - 1 || currentIndex == positon) {
            return;
        }
        
    	points[positon].setEnabled(false);
    	points[currentIndex].setEnabled(true);
    	currentIndex = positon;
    }
    
    /**
     *  功能描述：ViewPager适配器，用来绑定数据和view
     *  
     */
    class StartPagerAdapter extends PagerAdapter {

        //界面列表
        private ArrayList<View> views;

        public StartPagerAdapter(ArrayList<View> views) {
            this.views = views;
        }

        /**
         * 获得当前界面数
         */
        @Override
        public int getCount() {
            if (views != null) {
                return views.size();
            }
            return 0;
        }

        /**
         * 初始化position位置的界面
         */
        @Override
        public Object instantiateItem(View view, int position) {

            ((ViewPager) view).addView(views.get(position), 0);
            return views.get(position);
        }

        /**
         * 判断是否由对象生成界面
         */
        @Override
        public boolean isViewFromObject(View view, Object arg1) {
            return (view == arg1);
        }

        /**
         * 销毁position位置的界面
         */
        @Override
        public void destroyItem(View view, int position, Object arg2) {
            ((ViewPager) view).removeView(views.get(position));
        }
    }

}
