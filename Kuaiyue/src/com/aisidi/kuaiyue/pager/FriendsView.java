package com.aisidi.kuaiyue.pager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.adapter.FriendsAdapter;
import com.aisidi.kuaiyue.adapter.HomeAdapter;
import com.aisidi.kuaiyue.bean.UserBean;
import com.aisidi.kuaiyue.component.CustomerBar;
import com.aisidi.kuaiyue.component.MyListView;
import com.aisidi.kuaiyue.component.MyListView.OnRefreshListener;
import com.aisidi.kuaiyue.component.MyListView.OnToNextPageListener;
import com.aisidi.kuaiyue.net.WeiBoHelper;
import com.aisidi.kuaiyue.pager.BasePager.getDataFromUrl;
import com.flood.mycar.drawable.BitmapDownloader;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;


public class FriendsView extends LinearLayout implements OnRefreshListener, OnToNextPageListener{

	private String url, uid;
	private int next_cursor, previous_cursor, total_number;
	private static final int count = 20;
	private boolean canGetData = true;
	private boolean isFreshing;
	private MyListView listView;
	private CustomerBar waittingBar;
	private boolean nextPage = false;
	private Context mContext;
	private FriendsAdapter adapter;
	private int nowCount;
	private BitmapDownloader downloader;
	
	private ArrayList<Object> list = new ArrayList<Object>();
	
	public FriendsView(Context context, String url, String uid, BitmapDownloader downloader) {
		super(context);
		this.url = url;
		this.uid = uid;
		this.mContext = context;
		this.downloader = downloader;
		initView(context);
		
		freshPage(false);
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
				data = getFollowers(mContext, Weibo.getInstance(),url,uid, count, next_cursor);
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
	
	private String getFollowers(Context context, Weibo weibo,String url, String uid, int count, int cursor) throws MalformedURLException, IOException, WeiboException {
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey());
		bundle.add("uid", uid);
		bundle.add("count", count+"");
		bundle.add("cursor", cursor+"");
		String rlt = weibo.request(context, url, bundle, "GET", weibo.getAccessToken());
		return rlt;
	}
	
	private void dealJsonAndFreshView(String result)
	{
		try {
			if (isFreshing) {
				list.removeAll(list);
			}
			JSONObject jsonObject = new JSONObject(result);
			JSONArray userArray = jsonObject.getJSONArray("users");
			next_cursor = jsonObject.getInt("next_cursor");
			previous_cursor = jsonObject.getInt("previous_cursor");
			total_number = jsonObject.getInt("total_number");
			if (next_cursor!=0) {
				nextPage = true;
			}else {
				nextPage = false;
			}
			
			nowCount += userArray.length();
			for (int i = 0; i < userArray.length(); i++) {
				JSONObject object = userArray.getJSONObject(i);
				list.add(WeiBoHelper.getUserBean(object));
			}
			
			freshView();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void freshView()
	{
		if (adapter==null) {
			adapter = new FriendsAdapter(mContext, list, listView, downloader);
			listView.setAdapter(adapter);
		}
		adapter.count = nowCount;
		adapter.notifyDataSetChanged();
	}
	
	private void freshPage(boolean fresh)
    {
    	if (!canGetData) {
			return;
		}
    	if (fresh) {
    		isFreshing = true;
    		next_cursor = 0;
		}
    	new getDataFromUrl().execute();
    }
    
    private void nextPage()
    {
    	if (!canGetData) {
			return;
		}
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
