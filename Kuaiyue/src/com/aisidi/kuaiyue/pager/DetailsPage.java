package com.aisidi.kuaiyue.pager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.adapter.DetailsListAdapter;
import com.aisidi.kuaiyue.adapter.HomeAdapter;
import com.aisidi.kuaiyue.bean.DetailsListBean;
import com.aisidi.kuaiyue.bean.HomeTimeLineBean;
import com.aisidi.kuaiyue.bean.UserBean;
import com.aisidi.kuaiyue.component.ImageLayout;
import com.aisidi.kuaiyue.utils.Utils;
import com.flood.mycar.drawable.BitmapDownloader;
import com.flood.mycar.drawable.ImageMode;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class DetailsPage extends BasePager{

	private String id;
	private ArrayList<Object> homeList = new ArrayList<Object>();
	private DetailsListAdapter adapter;
	private BitmapDownloader downloader;
	private HomeTimeLineBean bean;
	private UserBean reUserBean;
	
	public DetailsPage(Context context, String url, String id, BitmapDownloader downloader, HomeTimeLineBean bean) {
		super(context, url, true);
		
		this.downloader = downloader;
		this.id = id;
		this.bean = bean;
		this.reUserBean = bean.getRe_user();
		listView.addHeaderView(new HeadView(context));
	}

	@Override
	protected String getTimeLineFromUrl(Context context, String url,
			Weibo weibo, int page, int count) throws MalformedURLException,
			IOException, WeiboException {
		// TODO Auto-generated method stub
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey());
		bundle.add("id", id);
		bundle.add("count", count+"");
		bundle.add("page", page+"");
		String rlt = weibo.request(context, url, bundle, "GET", weibo.getAccessToken());
		return rlt;
	}

	@Override
	protected void dealJsonAndFreshView(String json) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray comments = jsonObject.getJSONArray("comments");
			int itemCount = comments.length();
			nowCount += itemCount;
			int next_cursor = jsonObject.getInt("next_cursor");
			if (next_cursor!=0) {
				nextPage = true;
			}else {
				nextPage = false;
			}
			for (int i = 0; i < itemCount; i++) {
				DetailsListBean bean = new DetailsListBean();
				JSONObject object = comments.getJSONObject(i);
				
				bean.setId(Long.parseLong(id));
				bean.setCid(object.optLong("id"));
				bean.setText(object.optString("text"));
				bean.setCreated_at(Utils.getTime(object.getString("created_at")));
				
				JSONObject user = object.getJSONObject("user");
				UserBean userBean = new UserBean();
				
				userBean.setUserId(user.getString("id"));
				userBean.setScreen_name(user.getString("screen_name"));
				userBean.setProfile_image_url(user.getString("profile_image_url"));
				userBean.setAvatar_large(user.getString("avatar_large"));
				userBean.setStatuses_count(user.getInt("statuses_count"));
				userBean.setFollowers_count(user.getInt("followers_count"));
				userBean.setFriends_count(user.getInt("friends_count"));
				userBean.setFollowing(user.getBoolean("following"));
				userBean.setLocation(user.getString("location"));
				userBean.setDescription(user.getString("description"));
				userBean.setGender(user.getString("gender"));
				userBean.setVerified(user.getBoolean("verified"));
				bean.setUser(userBean);
				
				homeList.add(bean);
			}
			
			freshView();
		} catch (JSONException e) {
			// TODO: handle exception
		}
		
	}

	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus) {
			bottomPopupWindow.showMenu(bean);
		}else {
			bottomPopupWindow.hideMenu();
		}
	}

	private void freshView()
	{
		if (adapter==null) {
			adapter = new DetailsListAdapter(mContext, homeList,listView,downloader);
			listView.setAdapter(adapter);
		}
		adapter.count = nowCount;
		adapter.notifyDataSetChanged();
	}
	
	class HeadView extends LinearLayout
	{
		private TextView content_text, recontent_text, home_time, source_text, reposts_text, comments_text;
		private ImageLayout imageLayout, re_ImageLayout;
		
		public HeadView(Context context) {
			super(context);
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.comment_head, null);
			
			LinearLayout layout = (LinearLayout)view.findViewById(R.id.comm_recontent);
			content_text = (TextView)view.findViewById(R.id.content_text);
			recontent_text = (TextView)view.findViewById(R.id.recontent_text);
			home_time = (TextView)view.findViewById(R.id.home_time);
			source_text = (TextView)view.findViewById(R.id.source_text);
			reposts_text = (TextView)view.findViewById(R.id.forward);
			comments_text = (TextView)view.findViewById(R.id.comment);
			imageLayout = (ImageLayout)view.findViewById(R.id.imageLayout);
			re_ImageLayout = (ImageLayout)view.findViewById(R.id.re_imageLayout);
			
			content_text.setText(Utils.setTextColor(bean.getText(), getResources().getColor(R.color.blue),context));
			home_time.setText(bean.getCreated_at());
			source_text.setText(bean.getSource());
			
			reposts_text.setText(bean.getReposts_count()+"");
			comments_text.setText(bean.getComments_count()+"");
			if (bean.getPic_urls()!=null) {
				imageLayout.setVisibility(View.VISIBLE);
				
				imageLayout.getImages(context, bean.getPic_urls(), downloader,null, ImageMode.small);
				}
			if (!TextUtils.isEmpty(bean.getRe_text())) {
				layout.setVisibility(View.VISIBLE);
				recontent_text.requestFocus();
				recontent_text.setText(Utils.setTextColor("@"+reUserBean.getScreen_name()+": "+
						bean.getRe_text(),getResources().getColor(R.color.blue),context));
			}
			if (bean.getRe_pic_urls()!=null) {
				re_ImageLayout.setVisibility(View.VISIBLE);
				layout.setVisibility(View.VISIBLE);
				
				re_ImageLayout.getImages(context, bean.getRe_pic_urls(), downloader,null, ImageMode.small);
			}
			this.addView(view, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		}
		
	}
}
