package com.aisidi.kuaiyue.activity;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.WeiboApplication;
import com.aisidi.kuaiyue.adapter.ImageViewPagerAdapter;
import com.aisidi.kuaiyue.component.CustomerBar;
import com.flood.mycar.drawable.BitmapDownloader;
import com.flood.mycar.drawable.ImageMode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MultiplePicActivity extends Activity{

	private BitmapDownloader downloader;
	private ImageViewPagerAdapter adapter;
	private ViewPager pager;
	private ImageView[] imageViews;
	private CustomerBar waiting_bar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		downloader = new BitmapDownloader();
		initView();
	}
	
	private void initView()
	{
		Intent intent = getIntent();
		
		setContentView(R.layout.multiple_pic);
		String[] smallPics = intent.getStringArrayExtra("smallPics");
		String[] largePics = intent.getStringArrayExtra("largePics");
		int pic_id = intent.getIntExtra("pic_id", 0);
		
		waiting_bar = (CustomerBar)findViewById(R.id.waiting_bar);
		pager = (ViewPager)findViewById(R.id.pic_viewPager);
		LinearLayout multiple_linear = (LinearLayout)findViewById(R.id.multiple_linear);
		
		adapter = new ImageViewPagerAdapter(this, largePics, downloader, waiting_bar);
		pager.setAdapter(adapter);
		pager.setCurrentItem(pic_id);
		
		imageViews = setBottomLayout(multiple_linear, smallPics);
		setImageBackground(pic_id);
		
		
		pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				setImageBackground(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private ImageView[] setBottomLayout(LinearLayout multiple_linear, String[] smallPics)
	{
		int count = smallPics.length;
		ImageView[] imageViews = new ImageView[count];
		
		int smallImageHeight = getResources().getDimensionPixelSize(R.dimen.bottom_height);
		int smallImageWidth = WeiboApplication.getInstance().getDisplayMetrics().widthPixels/count;
		if (smallImageWidth<smallImageHeight) {
			smallImageHeight = smallImageWidth;
		}
		
		LayoutParams lp = new LayoutParams(smallImageWidth,smallImageHeight);
		
		for (int i = 0; i < count; i++) {
			imageViews[i] = new ImageView(this);
			imageViews[i].setScaleType(ScaleType.FIT_XY);
			imageViews[i].setPadding(2, 2, 2, 2);
			multiple_linear.addView(imageViews[i],lp);
			
			downloader.downPic(imageViews[i], smallPics[i], ImageMode.small);
			final int j = i;
			imageViews[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pager.setCurrentItem(j);
					setImageBackground(j);
				}
			});
		}
		
		return imageViews;
	}
	
	private void setImageBackground(int position)
	{
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i].setBackgroundColor(Color.TRANSPARENT);
		}
		
		imageViews[position].setBackgroundColor(Color.WHITE);
	}
	
	private void clearAllPicReference()
	{
		if (downloader!=null) {
			downloader.totalStopLoadPicture();
			downloader = null;
		}
		
		if (adapter!=null) {
			adapter.clearReference();
		}
		
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i].setImageBitmap(null);
			imageViews[i].getDrawable().setCallback(null);
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		clearAllPicReference();
	}
	
	
}
