package com.aisidi.kuaiyue.pager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.aisidi.kuaiyue.activity.DetailsActivity;
import com.aisidi.kuaiyue.activity.ReplayActivity;
import com.aisidi.kuaiyue.adapter.HomeAdapter;
import com.aisidi.kuaiyue.bean.HomeTimeLineBean;
import com.aisidi.kuaiyue.bean.UserBean;
import com.aisidi.kuaiyue.component.BottomPopupWindow;
import com.aisidi.kuaiyue.net.WeiBoHelper;
import com.aisidi.kuaiyue.utils.ReplayState;
import com.aisidi.kuaiyue.utils.Utils;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class HomePagerView extends BasePager{

	private OnHomePageFinishListener finishListener;
	private ArrayList<Object> homeList = new ArrayList<Object>();
	private HomeAdapter adapter;
	private String uid;
	private boolean isGroups;
	
	/**
	 * 这里是显示本人关注微博的构造函数
	 * @param context
	 * @param url
	 */
	public HomePagerView(Context context, String url) {
		super(context, url, false);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 这里是显示某人微博的构造函数
	 * @param context
	 * @param url
	 * @param uid
	 */
	public HomePagerView(Context context, String url, String uid) {
		super(context, url, false);
		this.uid = uid;
	}

	@Override
	protected String getTimeLineFromUrl(Context context,String url, Weibo weibo, int page,
			int count) throws MalformedURLException, IOException,
			WeiboException {
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey());
		bundle.add("count", count+"");
		bundle.add("page", page+"");
		if (uid!=null) {
			url = Weibo.SERVER + "statuses/user_timeline.json";
			bundle.add("uid", uid);
			
		}
		return weibo.request(mContext, url, bundle, "GET", weibo.getAccessToken());
	}
	
	@Override
	protected void dealJsonAndFreshView(String json) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("statuses");
			int itemCount = jsonArray.length();
			nowCount += itemCount;
			int total_number = jsonObject.getInt("total_number");
			
			if (nowCount<total_number) {
				nextPage = true;
			}else {
				nextPage = false;
			}
			
			if (isFreshing&&adapter!=null) {
				isFreshing = false;
				adapter.count = 0;
				homeList.removeAll(homeList);
			}
			
			for (int i = 0; i < itemCount; i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				
				HomeTimeLineBean bean = new HomeTimeLineBean();
				bean.setId(object.getLong("id"));
				bean.setText(Utils.checkURL(object.getString("text")));
				bean.setCreated_at(Utils.getTime(object.getString("created_at")));
				
				if (object.has("source")) {
					bean.setSource("来自:"+Utils.setSource(object.getString("source")));
				}
				if (object.has("reposts_count")) {
					bean.setReposts_count(object.getInt("reposts_count"));
					bean.setComments_count(object.getInt("comments_count"));
					bean.setAttitudes_count(object.getInt("attitudes_count"));
				}
				if (object.has("pic_urls")) {
					JSONArray array = object.getJSONArray("pic_urls");
					String[] pic_urls = new String[array.length()];
					for (int j = 0; j < pic_urls.length; j++) {
						pic_urls[j] = array.getJSONObject(j).getString("thumbnail_pic");
					}
					bean.setPic_urls(pic_urls);
				}
				if (object.has("retweeted_status")){
					JSONObject retweeted = object.getJSONObject("retweeted_status");
					if (retweeted.has("deleted")&&retweeted.optString("deleted").equals("1")) {
						bean.setRe_id(0);
					}else {
						bean.setRe_id(retweeted.getLong("id"));
						bean.setRe_text(Utils.checkURL(retweeted.getString("text")));
						bean.setRe_created_at(Utils.getTime(retweeted.getString("created_at")));
						if (retweeted.has("source")) {
							bean.setRe_source("来自:"+Utils.setSource(retweeted.getString("source")));
						}
						if (retweeted.has("pic_urls")) {
							JSONArray array = retweeted.getJSONArray("pic_urls");
							String[] pic_urls = new String[array.length()];
							for (int j = 0; j < pic_urls.length; j++) {
								pic_urls[j] = array.getJSONObject(j).getString("thumbnail_pic");
							}
							bean.setRe_pic_urls(pic_urls);
						}
						if (retweeted.has("user")) {
							JSONObject re_user = retweeted.getJSONObject("user");
							bean.setRe_user(WeiBoHelper.getUserBean(re_user));
						}
					}
				}
				
				if (object.has("user")) {
					JSONObject user = object.getJSONObject("user");
					bean.setUser(WeiBoHelper.getUserBean(user));
				}
				
				homeList.add(bean);
			}
			
			freshView();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 单点击事件，打开详细内容页面
	 * @param position
	 */
	public void onclickItem(int position)
	{
		HomeTimeLineBean bean = (HomeTimeLineBean) homeList.get(position);
		
		Intent intent = new Intent();
		intent.putExtra("bean", bean);
		intent.setClass(mContext, DetailsActivity.class);
		mContext.startActivity(intent);
	}
	
	/**
	 * 打开发表评论的页面
	 * @param position
	 */
	public void commentT(int position)
	{
		Intent intent = new Intent();
		intent.putExtra("state", ReplayState.COMMENTS);
		intent.putExtra("bean", (HomeTimeLineBean) homeList.get(position));
		intent.setClass(mContext, ReplayActivity.class);
		mContext.startActivity(intent);
	}
	/**
	 * 转发微博
	 * @param position
	 */
	public void repost(int position)
	{
		Intent intent = new Intent();
		intent.putExtra("state", ReplayState.REPOST);
		intent.putExtra("bean", (HomeTimeLineBean) homeList.get(position));
		intent.setClass(mContext, ReplayActivity.class);
		mContext.startActivity(intent);
	}
	
	/**
	 * 长点击弹出popupwindow
	 */
	public void onLongClickItem(int position)
	{
		bottomPopupWindow.showMenu((HomeTimeLineBean) homeList.get(position));
	}
	private void freshView()
	{
		if (finishListener!=null) {
			finishListener.HomePageComplete();
		}
		listView.onRefreshComplete();
		if (adapter==null) {
			
			adapter = new HomeAdapter(mContext, homeList, listView, this, false);
			listView.setAdapter(adapter);
		}
		adapter.count = nowCount;
		adapter.notifyDataSetChanged();
	}
	
	public void goTopAndFresh(final String list_id)
	{
		if (TextUtils.isEmpty(list_id)) {
			isGroups = false;
		}else {
			isGroups = true;
		}
		if (adapter!=null&&homeList!=null) {
			listView.setSelection(0);
			listView.makeFreshState();
			if (isGroups) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							String json = getGroupsTimeLine(mContext, Weibo.getInstance(), list_id+"", 1, 10);
							Message message = handler.obtainMessage();
							message.obj = json;
							message.sendToTarget();
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (WeiboException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}).start();
			}else {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							String json = getTimeLineFromUrl(mContext, WeiBoHelper.homeTimelineUrl, Weibo.getInstance(), 1, 10);
							Message message = handler.obtainMessage();
							message.obj = json;
							message.sendToTarget();
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (WeiboException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
	}
	
	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			isFreshing = true;
			adapter = null;
			homeList.removeAll(homeList);
			dealJsonAndFreshView(msg.obj+"");
		}
		
	};
	
    public String getGroupsTimeLine(Context context, Weibo weibo, String list_id, int page,int count) throws MalformedURLException, IOException, WeiboException {
    	isGroups = true;
    	
    	String url = Weibo.SERVER + "friendships/groups/timeline.json";
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey()); 
		bundle.add("list_id", list_id); 
		bundle.add("count", count+"");
		bundle.add("page", 1+"");
		String rlt = weibo.request(context, url, bundle, "GET", weibo.getAccessToken());
		return rlt;
	}
    
    public void notifyAdapter()
    {
    	if (adapter!=null) {
			adapter.notifyDataSetChanged();
		}
    }

	public void setHomePageFinishListener(OnHomePageFinishListener listener)
	{
		finishListener = listener;
	}
	
	public interface OnHomePageFinishListener
	{
		void HomePageComplete();
	}
}
