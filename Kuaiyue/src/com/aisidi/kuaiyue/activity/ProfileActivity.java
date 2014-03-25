package com.aisidi.kuaiyue.activity;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.aisidi.kuaiyue.MainFragment;
import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.bean.HomeTimeLineBean;
import com.aisidi.kuaiyue.bean.UserBean;
import com.aisidi.kuaiyue.net.WeiBoHelper;
import com.aisidi.kuaiyue.pager.FriendsView;
import com.aisidi.kuaiyue.pager.HomePagerView;
import com.aisidi.kuaiyue.utils.Utils;
import com.flood.mycar.drawable.BitmapDownloader;
import com.flood.mycar.drawable.ImageMode;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class ProfileActivity extends Activity implements OnClickListener{

	private boolean isSelf;
	private Button followerBtn;
	private int statuses_count, followers_count, friends_count,favourites_count;
	private BitmapDownloader downloader;
	private boolean creat;
	private ProgressDialog mpDialog;
	private String uid;
	private UserBean userBean;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		downloader = new BitmapDownloader();
		
		initView();
	}

	private void initView()
	{
		Intent intent = getIntent();
		userBean = (UserBean) intent.getSerializableExtra("user");
		setContentView(R.layout.profile);
		
		uid = userBean.getUserId();
		isSelf = uid.equals(MainFragment.adminId);
		
		statuses_count = userBean.getStatuses_count();
		followers_count = userBean.getFollowers_count();
		friends_count = userBean.getFriends_count();
		favourites_count = userBean.getFavourites_count();
		
		FrameLayout head_frame = (FrameLayout)findViewById(R.id.head_frame);
		
		TextView title_name = (TextView)findViewById(R.id.title_name);
		TextView nameTextView = (TextView)findViewById(R.id.profile_userName);
		TextView sexTextView = (TextView)findViewById(R.id.profile_sex);
		TextView fromTextView = (TextView)findViewById(R.id.profile_from);
		TextView introTextView = (TextView)findViewById(R.id.profile_intro);
		
		followerBtn = (Button)findViewById(R.id.profile_follower_btn);
		
		ImageView back = (ImageView)findViewById(R.id.pic_back);
		ImageView headView = (ImageView)findViewById(R.id.profile_head);
		ImageView vip = (ImageView)findViewById(R.id.head_v);
		
		head_frame.setVisibility(View.GONE);
		title_name.setOnClickListener(this);
		back.setOnClickListener(this);
		followerBtn.setOnClickListener(this);
		
		title_name.setText(getResources().getString(R.string.profile_title));
		nameTextView.setText(userBean.getScreen_name());
		sexTextView.setText(getSex(userBean.getGender()));
		fromTextView.setText("来自："+userBean.getLocation());
		introTextView.setText(getIntro(userBean.getDescription()));
		
		if (isSelf) {
			followerBtn.setText(getResources().getString(R.string.menu3)+"("+favourites_count+")");
		}else {
			setFollowingState(userBean.isFollowing());
		}
		showHeadImage(userBean, headView);
		showVip(userBean.isVerified(), vip);
		
		initTabHost();
	}
	
	private void initTabHost()
	{
		final TabHost mHost = (TabHost)findViewById(R.id.tabhost);
        mHost.setup();
        
        mHost.addTab(mHost.newTabSpec("radio_weibo").setIndicator("radio_weibo").setContent(R.id.tab1));
        mHost.addTab(mHost.newTabSpec("radio_guan").setIndicator("radio_guan").setContent(R.id.tab2));
        mHost.addTab(mHost.newTabSpec("radio_fllowing").setIndicator("radio_fllowing").setContent(R.id.tab3));
        
        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//		第一个分页
        HomePagerView view = new HomePagerView(this, WeiBoHelper.userTimeLineUrl, uid);
        view.freshPage(false);
		LinearLayout layout1 = (LinearLayout)findViewById(R.id.tab1);
		layout1.addView(view,lp);
		
//		第二个分页
		FriendsView friendsView = new FriendsView(this, WeiBoHelper.getfriendsUrl, uid, downloader);
		LinearLayout layout2 = (LinearLayout)findViewById(R.id.tab2);
		layout2.addView(friendsView,lp);
		
//		第三个分页 
		FriendsView fllower = new FriendsView(this, WeiBoHelper.getfollowersUrl, uid, downloader);
		LinearLayout layout3 = (LinearLayout)findViewById(R.id.tab3);
		layout3.addView(fllower,lp);
        
		RadioButton button1 = (RadioButton)findViewById(R.id.radio_details);
		RadioButton button2 = (RadioButton)findViewById(R.id.radio_comment);
		RadioButton button3 = (RadioButton)findViewById(R.id.radio_interest);
        
        button1.setText(Utils.setTextSize("微博("+statuses_count+")", 10, this));
        button2.setText(Utils.setTextSize("关注("+friends_count+")", 10, this));
        button3.setText(Utils.setTextSize("粉丝("+followers_count+")", 10, this));
        
        button1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					mHost.setCurrentTabByTag("radio_weibo");
				}
			}
		});
        
        button2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					mHost.setCurrentTabByTag("radio_guan");
				}
			}
		});

        button3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	
        	@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					mHost.setCurrentTabByTag("radio_fllowing");
				}
			}
		});
	}
	
	private void setFollowingState(boolean isFollowing)
	{
		if (isFollowing) {
			creat = false;
			followingState();
		}else {
			creat = true;
			unFollowingState();
		}
	}
	
	private void followingState()
	{
		followerBtn.setBackgroundResource(R.drawable.buying);
		followerBtn.setTextColor(this.getResources().getColor(R.color.title_text));
		followerBtn.setText(this.getResources().getString(R.string.attention_not));
	}
	
	private void unFollowingState()
	{
		followerBtn.setBackgroundResource(R.drawable.bugreen);
		followerBtn.setTextColor(this.getResources().getColor(R.color.white));
		followerBtn.setText(this.getResources().getString(R.string.attention));
	}

	
	private void showVip(boolean isVip, ImageView vip)
	{
		if (isVip) {
			vip.setVisibility(View.VISIBLE);
		}else {
			vip.setVisibility(View.GONE);
		}
	}
	private void showHeadImage(UserBean userBean, ImageView headView)
	{
		String headPic;
		if (TextUtils.isEmpty(userBean.getAvatar_large())) {
			headPic = userBean.getProfile_image_url();
		}else {
			headPic = userBean.getAvatar_large();
		}
		Log.i("headPic", headPic);
		downloader.downPic(headView, headPic, ImageMode.small);
	}

	private String getSex(String gender)
	{
		if (gender.equals("m")) {
			gender = "男";
		}else if(gender.equals("f")) {
			gender = "女";
		}else {
			gender = "未知";
		}
		
		return gender;
	}
	
	private String getIntro(String description)
	{
		if (description.length()>10) {
			description = description.substring(0, 10);
		}
		
		return "简介: "+description;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_name:
		case R.id.pic_back:
			finish();
			break;
		case R.id.profile_follower_btn:
			if (isSelf) {
				openContentActivity();
			}else {
				followSomeOne();
			}
			break;
		default:
			break;
		}
	}
	
	private void openContentActivity()
	{
		Intent intent = new Intent();
		intent.putExtra("menuId", 3);
		intent.putExtra("user", userBean);
		intent.setClass(ProfileActivity.this, ContentActivity.class);
		startActivity(intent);
	}
	private void followSomeOne()
	{
		mpDialog = new ProgressDialog(ProfileActivity.this);  
        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
        mpDialog.setTitle("提示");//设置标题  
        mpDialog.setMessage("正在提交中");  
        mpDialog.setIndeterminate(false);//设置进度条是否为不明确 
		mpDialog.show();
		new SetFriends().start();
	}
	
	class SetFriends extends Thread
    {
    	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String lrt = "";
			try {
				Log.i("createFriends", creat+"");
				if (creat) {
					lrt = createFriends(Weibo.getInstance(), uid);
				}else {
					lrt = destroyFriends(Weibo.getInstance(), uid);
				}
				if (!lrt.equals("")) {
					Message message = handler.obtainMessage(0);
					handler.sendMessage(message);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WeiboException e) {
				// TODO Auto-generated catch block
				Message message = handler.obtainMessage();
				message.what = 1;
				handler.sendMessage(message);
				e.printStackTrace();
			}
		}
    }
    private String createFriends(Weibo weibo, String uid) throws MalformedURLException, IOException, WeiboException {
		String url = Weibo.SERVER + "friendships/create.json";
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey());
		bundle.add("uid", uid);
		String rlt = weibo.request(this, url, bundle, "POST", weibo.getAccessToken());
		return rlt;
	}
    
    private String destroyFriends(Weibo weibo, String uid) throws MalformedURLException, IOException, WeiboException {
		String url = Weibo.SERVER + "friendships/destroy.json";
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey());
		bundle.add("uid", uid);
		String rlt = weibo.request(this, url, bundle, "POST", weibo.getAccessToken());
		return rlt;
	}
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mpDialog!=null) {
					mpDialog.cancel();
					if (creat) {
						followingState();
					}else {
						unFollowingState();
					}
					creat = !creat;
				}
				Toast.makeText(ProfileActivity.this, "ok", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				if (mpDialog!=null) {
					mpDialog.cancel();
					Toast.makeText(ProfileActivity.this, "微博异常", Toast.LENGTH_SHORT).show();
				}
				break;
			case 4:
				if (mpDialog!=null) {
					mpDialog.cancel();
					Toast.makeText(ProfileActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
				}
				break;
			case 5:
				if (mpDialog!=null) {
					mpDialog.cancel();
					Toast.makeText(ProfileActivity.this, "有错误", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
    	
    };
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (downloader!=null) {
			downloader.totalStopLoadPicture();
			downloader = null;
		}
		
	}
}
