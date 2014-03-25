package com.aisidi.kuaiyue.net;

import org.json.JSONObject;

import com.aisidi.kuaiyue.bean.UserBean;
import com.weibo.net.Weibo;

import android.util.Log;

public class WeiBoHelper {

	public static final String CONSUMER_KEY = "3495531238";
	public static final String CONSUMER_SECRET = "66d2cea18a58f2bfc8c2ee7db277672b";
	public static final String RedirectUrl = "http://www.sina.com";
	
	public static final String homeTimelineUrl = Weibo.SERVER + "statuses/home_timeline.json";
	public static final String atTimelineUrl = Weibo.SERVER + "statuses/mentions.json";
	public static final String commentLineUrl = Weibo.SERVER + "comments/to_me.json";
	public static final String detailsLineUrl = Weibo.SERVER + "comments/show.json";
	public static final String userTimeLineUrl = Weibo.SERVER + "statuses/user_timeline.json";
	public static final String getfriendsUrl = Weibo.SERVER + "friendships/friends.json";
	public static final String getfollowersUrl = Weibo.SERVER + "friendships/followers.json";
	
	public static boolean isSessionValid(String expires_in) {
        if (expires_in!=null&&!expires_in.equals("")) {
        	Log.i("left_time", (Long.parseLong(expires_in))-System.currentTimeMillis()+"");
            return (System.currentTimeMillis() < Long.parseLong(expires_in));
        }
        return false;
    }
	
	public static UserBean getUserBean(JSONObject user)
	{
		UserBean userBean = new UserBean();
		
		userBean.setUserId(user.optString("id"));
		userBean.setScreen_name(user.optString("screen_name"));
		userBean.setRemark(user.optString("remark"));
		userBean.setProfile_image_url(user.optString("profile_image_url"));
		userBean.setAvatar_large(user.optString("avatar_large"));
		userBean.setStatuses_count(user.optInt("statuses_count"));
		userBean.setFollowers_count(user.optInt("followers_count"));
		userBean.setFriends_count(user.optInt("friends_count"));
		userBean.setFavourites_count(user.optInt("favourites_count"));
		userBean.setFollowing(user.optBoolean("following"));
		userBean.setLocation(user.optString("location"));
		userBean.setDescription(user.optString("description"));
		userBean.setGender(user.optString("gender"));
		userBean.setVerified(user.optBoolean("verified"));
		
		return userBean;
	}
}
