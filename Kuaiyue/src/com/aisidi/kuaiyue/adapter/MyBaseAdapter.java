package com.aisidi.kuaiyue.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.WeiboApplication;
import com.aisidi.kuaiyue.component.MyListView;
import com.aisidi.kuaiyue.utils.Utils;
import com.flood.mycar.drawable.BitmapDownloader;

public abstract class MyBaseAdapter extends BaseAdapter{

	protected ArrayList<Object> list;
	protected LayoutInflater inflater;
	protected Context mContext;
	protected float font1, font2, font3;
	protected int defaultColor, pressColor;
	public static int homeContentWidth;
	protected BitmapDownloader downloader;
	protected int color;
	protected MyListView listView;
	
	public int count;
	
	public MyBaseAdapter(Context context, ArrayList<Object> list, MyListView listView) {
		// TODO Auto-generated constructor stub
		this.list = list;
		this.listView = listView;
		mContext = context;
		inflater = LayoutInflater.from(context);
		downloader = new BitmapDownloader();
		
		homeContentWidth = WeiboApplication.getInstance().getDisplayMetrics().widthPixels-Utils.getHomePadding(context);
		font1 = mContext.getResources().getDimension(R.dimen.text_normal);
		font2 = mContext.getResources().getDimension(R.dimen.text_normal2);
		font3 = mContext.getResources().getDimension(R.dimen.text_normal3);
		
		defaultColor = mContext.getResources().getColor(R.color.alpha);
		pressColor = mContext.getResources().getColor(R.color.menu_list_bg);
		color = mContext.getResources().getColor(R.color.blue);
		clearReference(listView);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return initConvertView(position, convertView);
	}
	
	protected abstract View initConvertView(int position, View convertView);
	protected abstract void clearReference(ListView listView);

}
