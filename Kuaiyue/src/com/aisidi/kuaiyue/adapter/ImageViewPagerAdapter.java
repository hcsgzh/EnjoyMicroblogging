package com.aisidi.kuaiyue.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.aisidi.kuaiyue.component.CustomerBar;
import com.flood.mycar.drawable.BitmapDownloader;
import com.flood.mycar.drawable.ImageMode;

public class ImageViewPagerAdapter extends PagerAdapter{

	private Context mContext;
	private ImageView[] views;
	private String[] url;
	private BitmapDownloader downloader;
	private CustomerBar waiting_bar;
	
	
	public ImageViewPagerAdapter(Context context, String[] url, BitmapDownloader downloader, CustomerBar waiting_bar) {
		this.mContext = context;
		this.views = new ImageView[url.length];
		this.url = url;
		this.downloader = downloader;
		this.waiting_bar = waiting_bar;
		
		for (int i = 0; i < views.length; i++) {
			views[i] = new ImageView(context);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.length;
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
		waiting_bar.stop();
		((ViewPager)container).removeView(views[position]);
		views[position].setImageBitmap(null);
		views[position].getDrawable().setCallback(null);
	}

	@Override
	public Object instantiateItem(View container, int position) {
		// TODO Auto-generated method stub'
		downloader.downPic(views[position], url[position],waiting_bar, ImageMode.large);
		((ViewPager)container).addView(views[position]);
		return views[position];
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	public void clearReference()
	{
		if (views==null) {
			return;
		}
		for (int i = 0; i < views.length; i++) {
			views[i].setImageBitmap(null);
			views[i].getDrawable().setCallback(null);
		}
	}
}
