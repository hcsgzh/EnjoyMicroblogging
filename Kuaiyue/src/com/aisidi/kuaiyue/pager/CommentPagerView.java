package com.aisidi.kuaiyue.pager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.aisidi.kuaiyue.adapter.CommAdapter;
import com.aisidi.kuaiyue.adapter.HomeAdapter;
import com.aisidi.kuaiyue.adapter.MyBaseAdapter;
import com.aisidi.kuaiyue.bean.CommTimeLineBean;
import com.aisidi.kuaiyue.bean.HomeTimeLineBean;
import com.aisidi.kuaiyue.bean.UserBean;
import com.aisidi.kuaiyue.net.WeiBoHelper;
import com.aisidi.kuaiyue.utils.Utils;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class CommentPagerView extends BasePager{

	private ArrayList<Object> commList = new ArrayList<Object>();
	private MyBaseAdapter adapter;
	
	public CommentPagerView(Context context, String url) {
		super(context, url, false);
		
	}

	@Override
	protected String getTimeLineFromUrl(Context context, String url,
			Weibo weibo, int page, int count) throws MalformedURLException,
			IOException, WeiboException {
		// TODO Auto-generated method stub
		WeiboParameters bundle = new WeiboParameters();
	  	bundle.add("source", Weibo.getAppKey());
	  	bundle.add("count", count+"");
	  	bundle.add("page", page+"");
	  	String rlt = weibo.request(context, url, bundle, "GET", weibo.getAccessToken());
	  	return rlt;
	}

	@Override
	protected synchronized void dealJsonAndFreshView(String json) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("comments");
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
				commList.removeAll(commList);
			}
			for (int i = 0; i < jsonArray.length(); i++) {
				CommTimeLineBean bean = new CommTimeLineBean();
				JSONObject object = jsonArray.getJSONObject(i);
				
				bean.setCid(object.optLong("id"));
				bean.setCmt_text(object.optString("text"));
				if (object.has("source")) {
					bean.setSource("来自:"+Utils.setSource(object.getString("source")));
				}
				bean.setCreated_at(Utils.getTime(object.getString("created_at")));
				bean.setReposts_count(object.optInt("reposts_count"));
				bean.setComments_count(object.optInt("comments_count"));
				bean.setAttitudes_count(object.optInt("attitudes_count"));
				
				if (object.has("retweeted_status")) {
					JSONObject retweeted = object.getJSONObject("retweeted_status");
					bean.setRe_text(retweeted.getString("text"));
					
					if (retweeted.has("user")) {
						JSONObject re_user = retweeted.getJSONObject("user");
						UserBean reUserBean = new UserBean();
						reUserBean.setScreen_name("@"+re_user.getString("screen_name"));
						bean.setRe_user(reUserBean);
					}
					
				}
				
				if (object.has("status")) {
					JSONObject status = object.getJSONObject("status");
					bean.setId(status.optLong("id"));
					bean.setText(status.getString("text"));
					if (status.has("source")) {
						bean.setSource("来自:"+Utils.setSource(status.getString("source")));
					}
					bean.setCreated_at(Utils.getTime(status.getString("created_at")));
					if (status.has("reposts_count")) {
						bean.setReposts_count(status.optInt("reposts_count"));
						bean.setComments_count(status.optInt("comments_count"));
						bean.setAttitudes_count(status.optInt("attitudes_count"));
					}
					if (status.has("retweeted_status")) {
						JSONObject retweeted = status.getJSONObject("retweeted_status");
						bean.setRe_text(retweeted.getString("text"));
						if (retweeted.has("user")) {
							JSONObject re_user = retweeted.getJSONObject("user");
							bean.setRe_user(WeiBoHelper.getUserBean(re_user));
						}
					}
					
				}
				JSONObject reply = null;
				if (object.has("reply_comment")) {
					reply = object.getJSONObject("reply_comment");
				}else if(object.has("status")){
					reply = object.getJSONObject("status");
				}
				if (reply!=null) {
					bean.setReply_comment(reply.optString("text"));
				}
				JSONObject user = object.getJSONObject("user");
				bean.setUser(WeiBoHelper.getUserBean(user));
				
				commList.add(bean);
			}
			
			freshView();
		} catch (JSONException e) {
			// TODO: handle exception
		}
	}

	private void freshView()
	{
		if (adapter==null) {
			adapter = new CommAdapter(mContext, commList,listView);
			listView.setAdapter(adapter);
		}
		adapter.count = nowCount;
		adapter.notifyDataSetChanged();
	}
}
