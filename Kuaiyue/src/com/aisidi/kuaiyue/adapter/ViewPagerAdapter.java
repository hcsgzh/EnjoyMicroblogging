package com.aisidi.kuaiyue.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerAdapter extends PagerAdapter{

	private Context mContext;
	private View[] views;
	
	
	public ViewPagerAdapter(Context context, View[] views) {
		this.mContext = context;
		this.views = views;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.length;
	}

	
	@Override
	public void destroyItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager)container).removeView(views[position]);
	}

	@Override
	public Object instantiateItem(View container, int position) {
		// TODO Auto-generated method stub'
		((ViewPager)container).addView(views[position]);
		return views[position];
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

}
