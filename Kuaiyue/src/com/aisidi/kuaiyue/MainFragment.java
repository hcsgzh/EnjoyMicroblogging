package com.aisidi.kuaiyue;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aisidi.kuaiyue.adapter.ViewPagerAdapter;
import com.aisidi.kuaiyue.bean.LoginInfoBean;
import com.aisidi.kuaiyue.bean.UserBean;
import com.aisidi.kuaiyue.component.TitleView;
import com.aisidi.kuaiyue.component.TitleView.ChangePageListener;
import com.aisidi.kuaiyue.component.TitleView.OnClickHeadListener;
import com.aisidi.kuaiyue.database.KuaiDataBase;
import com.aisidi.kuaiyue.net.NetTools;
import com.aisidi.kuaiyue.net.NetType;
import com.aisidi.kuaiyue.net.WeiBoHelper;
import com.aisidi.kuaiyue.pager.BasePager;
import com.aisidi.kuaiyue.pager.CommentPagerView;
import com.aisidi.kuaiyue.pager.HomePagerView;
import com.aisidi.kuaiyue.pager.HomePagerView.OnHomePageFinishListener;
import com.aisidi.kuaiyue.pager.UpdateWeibo;
import com.aisidi.kuaiyue.utils.FileManager;
import com.aisidi.kuaiyue.utils.ReplayState;
import com.aisidi.kuaiyue.utils.Utils;
import com.flood.mycar.drawable.BitmapDownloader;
import com.flood.mycar.drawable.ImageMode;
import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Token;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class MainFragment extends Fragment implements ChangePageListener, OnHomePageFinishListener, OnClickHeadListener{

	private ViewPager viewPager;
	private TextView name;
	private ImageView headImage;
	private Weibo weibo = Weibo.getInstance();
	private LinearLayout loginLinear;
	private int[] counts = {0,0,0,0,0};
	private KuaiDataBase base;
	private UpdateWeibo updateWeibo;
	private BasePager homePage, atPage, commPage;
	private OnScreenListener screenListener;
	
	public static BitmapDownloader downloader;
	public static int status, mention_status, cmt;
	public static String screen_name,profile_image_url;
	public static String adminId="";
	public static UserBean bean;
	private ArrayList<HashMap<String, String>> groupsList = new ArrayList<HashMap<String,String>>();
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return initView();
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (weiboLogin()) {
			freshUI();
			Log.i("weiboLogin", "succeed");
		}else {
			Log.i("weiboLogin", "false");
			loginWeiBo();
		}
	}

	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		WeiboApplication.imageMode = NetTools.getImageMode(getActivity());
		if (homePage!=null) {
			((HomePagerView)homePage).notifyAdapter();
		}
		
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		base = WeiboApplication.getInstance().getDBInstance();
		downloader = new BitmapDownloader();
		
	}
	
	private View initView()
	{
//		setContentView(R.layout.activity_main);
		Utils.setFace(getActivity());
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_main, null);
		name = (TextView)view.findViewById(R.id.title_name);
		headImage = (ImageView)view.findViewById(R.id.title_image);
		loginLinear = (LinearLayout)view.findViewById(R.id.loginLinear);
		final TitleView titleView = (TitleView)view.findViewById(R.id.title_view);
		titleView.setPageListener(this);
		titleView.setOnClickHeadListener(this);
		
		viewPager = (ViewPager)view.findViewById(R.id.viewPager);
		viewPager.setAdapter(new ViewPagerAdapter(getActivity(), getPagerView()));
		
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				titleView.dispatchClick(arg0);
				if (screenListener!=null) {
					screenListener.whichScreen(arg0);
				}
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
		
		return view;
	}

	/**
	 * 初始化各个分页面
	 * @return
	 */
	private View[] getPagerView()
	{
		View[] views = new View[4];
		views[0] = homePage = new HomePagerView(getActivity(), WeiBoHelper.homeTimelineUrl);
		views[1] = atPage = new HomePagerView(getActivity(), WeiBoHelper.atTimelineUrl);
		views[2] = commPage = new CommentPagerView(getActivity(), WeiBoHelper.commentLineUrl);
		views[3] = updateWeibo= new UpdateWeibo(getActivity(), ReplayState.PUBLISH);
		
		((HomePagerView)homePage).setHomePageFinishListener(this);
		return views;
	}
	
	private boolean weiboLogin()
	{
		
		LoginInfoBean bean = base.getLoginedUserInfo();
		
		String token = bean.getToken();
		String expires_in = bean.getExpires();
		screen_name = bean.getName();
		adminId = bean.getUid();
		profile_image_url = bean.getImage();
		
		if (adminId==null) {
			adminId = "";
		}
		
		if (TextUtils.isEmpty(token)||TextUtils.isEmpty(expires_in)||
				TextUtils.isEmpty(screen_name)||!WeiBoHelper.isSessionValid(expires_in)) {
			return false;
		}
		
		Utility.setAuthorization(new Oauth2AccessTokenHeader());
		AccessToken accessToken = new AccessToken(token, WeiBoHelper.CONSUMER_SECRET);
		accessToken.setExpiresIn(expires_in);
		Weibo.getInstance().setAccessToken(accessToken);
		
		return true;
	}
	
	/**
	 * 设置发微博图片路径
	 */
	public void setPicPath()
	{
		updateWeibo.setPicPath(FileManager.tempFile.getAbsolutePath());
	}
	
//  登录微博的方法  
    public void loginWeiBo()
    {
    	Log.i("AuthDialogListener", "AuthDialogListener");
    	
		weibo.setupConsumerConfig(WeiBoHelper.CONSUMER_KEY, WeiBoHelper.CONSUMER_SECRET);
		weibo.setRedirectUrl(WeiBoHelper.RedirectUrl);
		weibo.authorize(getActivity(), new AuthDialogListener(),loginLinear);
    }
    
    public void changeUser(String access_token, String expires_in)
    {
		AccessToken accessToken = new AccessToken(access_token, WeiBoHelper.CONSUMER_SECRET);
		accessToken.setExpiresIn(expires_in);
		weibo.setAccessToken(accessToken);
		
		freshUI();
    }
    
    class AuthDialogListener implements WeiboDialogListener {

		@Override
		public void onComplete(Bundle values) {
			if (adminId.equals(values.getString("uid"))) {
				return;
			}
			base.makeAdminLogin(adminId, false);
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			adminId = values.getString("uid");
			
			AccessToken accessToken = new AccessToken(token, WeiBoHelper.CONSUMER_SECRET);
			accessToken.setExpiresIn(expires_in);
			weibo.setAccessToken(accessToken);
			
			if (base.isLoginExist(values.getString("uid"))) {
				base.updateLoginInfo(adminId, "", "", token, expires_in, 0, 1);
				freshUI();
			}else {
				base.storeLoginedUserInfo(adminId, "", "", token, expires_in, 0, 1);
				freshUI();
			}
			loginLinear.setVisibility(View.GONE);
		}

		@Override
		public void onError(DialogError e) {
			Toast.makeText(getActivity(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getActivity(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getActivity(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}
    
    private void freshUI()
    {
    	getUserTask();
    	
    	homePage.freshPage(true);
    	atPage.freshPage(true);
    	commPage.freshPage(true);
    	
    	new Thread(getGroups).start();
    	
    	updateWeibo.getTrends(weibo, "", 0);
    	updateWeibo.getMyFriends();
    }
    
    
    Runnable getGroups = new Runnable() {
    	
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		try {
    			String str = getGroups(getActivity(), weibo);
    			handleGroupsJson(str);
    		} catch (MalformedURLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (WeiboException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    };
    private String getGroups(Context context, Weibo weibo) throws MalformedURLException, IOException, WeiboException {
		String url = Weibo.SERVER + "friendships/groups.json";
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey()); 
		String rlt = weibo.request(context, url, bundle, "GET", weibo.getAccessToken());
		return rlt;
	}
    private void handleGroupsJson(String str) throws JSONException
	{
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", "");
		map.put("idstr", "");
		map.put("name", screen_name);
		list.add(map);
		
		JSONObject jsonObject = new JSONObject(str);
		JSONArray array = jsonObject.getJSONArray("lists");
		
		for (int i = 0; i < array.length(); i++) {
			map = new HashMap<String, String>();
			JSONObject item = array.getJSONObject(i);
			map.put("id", item.getString("id"));
			map.put("idstr", item.getString("idstr"));
			map.put("name", item.getString("name"));
			
			list.add(map);
		}
		groupsList = list;
		Message message = handler.obtainMessage();
		message.sendToTarget();
	}
    
    Handler handler = new Handler()
    {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			((HomeActivity)getActivity()).showGroups(groupsList);
		}
    	
    };
    
    public void changeGroups(String list_id)
    {
    	((HomePagerView)homePage).goTopAndFresh(list_id);
    }
    
    private void getUserTask()
    {
    	new AsyncTask<Object, Object, String>()
		{

			@Override
			protected String doInBackground(Object... params) {
				// TODO Auto-generated method stub
				WeiboParameters bundle = new WeiboParameters();
		        bundle.add("uid", adminId);
		        
		        String rlt = "";
				String url = Weibo.SERVER + "users/show.json";
				Token token = Weibo.getInstance().getAccessToken();
				try {
					rlt = Weibo.getInstance().request(getActivity(), url, bundle, "GET",	token);
				} catch (WeiboException e) {
					if (e.getStatusCode()==-1) {
						return "0";
					}
					if (e.getStatusCode()==21327||e.getStatusCode()==21332||e.getStatusCode()==21315) {//token过期
						base.updateLoginInfo(adminId, "", "");
						
						return "1";
					}
					e.printStackTrace();
				}
				return rlt;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (result.equals("0")) {
					if (getActivity()!=null) {
						Toast.makeText(getActivity(), getResources().getString(R.string.time_out), Toast.LENGTH_SHORT).show();
					}
				}else if (result.equals("1")) {
					loginWeiBo();
				}else {
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(result);
						screen_name = jsonObject.getString("screen_name");
						profile_image_url = jsonObject.getString("profile_image_url");
						counts[0] = jsonObject.getInt("statuses_count");
						counts[1] = jsonObject.getInt("followers_count");
						counts[2] = jsonObject.getInt("friends_count");
						counts[4] = jsonObject.getInt("favourites_count");
						
						bean = new UserBean();
						bean.setUserId(adminId);
						bean.setScreen_name(screen_name);
						bean.setProfile_image_url(profile_image_url);
						bean.setAvatar_large(jsonObject.getString("avatar_large"));
						bean.setStatuses_count(counts[0]);
						bean.setFollowers_count(counts[1]);
						bean.setFriends_count(counts[2]);
						bean.setFavourites_count(counts[4]);
						bean.setFollowing(false);
						bean.setLocation(jsonObject.getString("location"));
						bean.setDescription(jsonObject.getString("description"));
						bean.setGender(jsonObject.getString("gender"));
						bean.setVerified(jsonObject.getBoolean("verified"));
						
						base.updateLoginInfo(adminId, screen_name, profile_image_url);
						name.setText(screen_name);
						
						((HomeActivity)getActivity()).UpdateListChange();
						
						downloader.downPic(headImage, profile_image_url, ImageMode.small);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}.execute();
    }

	public interface Notifaction
	{
		void notifactionCount(int one, int two, int three);
		void clearHome();
		void clearMotion();
		void clearCmt();
	}

	@Override
	public void setPage(int page) {
		// TODO Auto-generated method stub
		viewPager.setCurrentItem(page);
	}

	public void stopTimeLinePicDownLoad()
	{
		downloader.totalStopLoadPicture();
	}

	interface OnScreenListener
	{
		void whichScreen(int index);
	}
	
	public void setOnScreenListener(OnScreenListener l)
	{
		this.screenListener = l;
	}


	@Override
	public void HomePageComplete() {
		// TODO Auto-generated method stub
		checkShowFirstPage();
	}
	private final static String SPNAME = "spname";
	private static final String FIRSTTIME = "firsttime";
	private void checkShowFirstPage()
	    {
	    	SharedPreferences sp = getActivity().getSharedPreferences(SPNAME, 0);
	        boolean isfirst = sp.getBoolean(FIRSTTIME, true);
	        if (isfirst) {
	        	Editor editor = sp.edit();
	        	editor.putBoolean(FIRSTTIME, false);
	        	editor.commit();
	        	 
	        	 final ImageView showPage = (ImageView)getActivity().findViewById(R.id.showPic);
	        	 showPage.setVisibility(View.VISIBLE);
	        	 showPage.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						 showPage.setVisibility(View.GONE);
					}
				});
			}
	    }


	@Override
	public void clickHead() {
		// TODO Auto-generated method stub
		HomeActivity activity = ((HomeActivity)getActivity());
		activity.showMenu();
	}
}
