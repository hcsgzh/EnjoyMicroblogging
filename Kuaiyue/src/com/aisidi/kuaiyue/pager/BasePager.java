package com.aisidi.kuaiyue.pager;


import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.bean.UserBean;
import com.aisidi.kuaiyue.component.BottomPopupWindow;
import com.aisidi.kuaiyue.component.CustomerBar;
import com.aisidi.kuaiyue.component.MyListView;
import com.aisidi.kuaiyue.component.MyListView.OnRefreshListener;
import com.aisidi.kuaiyue.component.MyListView.OnToNextPageListener;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;

public abstract class BasePager extends LinearLayout implements OnRefreshListener, OnToNextPageListener{

	private String url;
	private int page = 1;
	private static final int count = 10;
	private boolean canGetData = true;
	
	protected boolean isFreshing;
	protected MyListView listView;
	protected CustomerBar waittingBar;
	protected boolean nextPage = false;
	protected Context mContext;
	protected int nowCount;
	protected BottomPopupWindow bottomPopupWindow;
	
	
	public BasePager(Context context, String url, boolean isDetails) {
		super(context);
		
		this.url = url;
		this.mContext = context;
		initView(context);
		
		initPopwindow(context, isDetails);
	}

	private void initView(Context context)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.home, null);
		
		this.addView(view,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		waittingBar = (CustomerBar)view.findViewById(R.id.waiting_bar);
		waittingBar.start();
		
		listView = (MyListView)findViewById(R.id.home_list);
		listView.setScrollbarFadingEnabled(true);
		listView.setonRefreshListener(this);
		listView.setOnToNextPageListener(this);
		
	}
	
	class getDataFromUrl extends  AsyncTask<String, Integer, String>
	{

		@Override
		protected String doInBackground(String... params) {
			String data = "";
			canGetData = false;
			try {
				data = getTimeLineFromUrl(mContext,url, Weibo.getInstance(), page, count);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WeiboException e) {
				e.printStackTrace();
			}finally{
				canGetData = true;
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
			dealJsonAndFreshView(result);
			waittingBar.stop();
			listView.hideFootView();
			listView.onRefreshComplete();
			}
				
	};


	protected abstract String getTimeLineFromUrl(Context context,String url, Weibo weibo, int page, int count) throws MalformedURLException, IOException, WeiboException;
    protected abstract void dealJsonAndFreshView(String json);
    
    protected void initPopwindow(Context context, boolean isDetails)
    {
    	FrameLayout containt = (FrameLayout)findViewById(R.id.homeView);
    	bottomPopupWindow = new BottomPopupWindow(context, containt, isDetails);
    }
    
    public void freshPage(boolean fresh)
    {
    	if (!canGetData) {
			return;
		}
    	if (fresh) {
    		isFreshing = true;
		}
    	page = 1;
    	new getDataFromUrl().execute();
    }
    
    private void nextPage()
    {
    	if (!canGetData) {
			return;
		}
    	page++;
    	new getDataFromUrl().execute();
    }
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		freshPage(true);
	}

	@Override
	public void toNextPage(int totalItemCount) {
		// TODO Auto-generated method stub
		if (nextPage) {
			nextPage = false;
			nextPage();
			listView.showFootView();
		}
	}
	
}
