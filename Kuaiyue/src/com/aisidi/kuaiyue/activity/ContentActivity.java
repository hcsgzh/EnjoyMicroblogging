package com.aisidi.kuaiyue.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aisidi.kuaiyue.MainFragment;
import com.aisidi.kuaiyue.R;
import com.flood.mycar.drawable.BitmapDownloader;
import com.flood.mycar.drawable.ImageMode;

public class ContentActivity extends Activity{

	private ImageView title_image;
	private TextView title_name;
//	private MenuPopupwindow menuPopupwindow;
	private String uid, screen_name, profile_image_url;
	private int statuses_count, followers_count, friends_count;
	private boolean following;
	private BitmapDownloader downloader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_detail);
		
		downloader = new BitmapDownloader();
		
		title_name = (TextView)findViewById(R.id.title_name);
		title_image = (ImageView)findViewById(R.id.title_image);
		
		Intent intent = getIntent();
		int menuId = intent.getIntExtra("menuId", -1);
		
		uid = intent.getStringExtra("uid");
		screen_name = intent.getStringExtra("screen_name");
		profile_image_url = intent.getStringExtra("profile_image_url");
		following = intent.getBooleanExtra("following", false);
		statuses_count = intent.getIntExtra("statuses_count", 0);
		followers_count = intent.getIntExtra("followers_count", 0);
		friends_count = intent.getIntExtra("friends_count", 0);
		
		title_name.setText(screen_name);
		downloader.downPic(title_image, profile_image_url, ImageMode.small);
		
		LinearLayout detailLayout = (LinearLayout)findViewById(R.id.details_list_content);
		View view = null;
		switch (menuId) {
		case 0://΢���б�
//			view = new UserTimeLine(this,uid,0);
			break;
		case 1://��˿
//			view = 
			break;
		case 2://��ע
			
			break;
		case 3://�ղ�
//			view = new UserTimeLine(this,uid,1);
			break;
		default:
			break;
		}
		
		detailLayout.addView(view, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add("menu");// ���봴��һ��
	    return super.onCreateOptionsMenu(menu);
    }
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		downloader.totalStopLoadPicture();
		downloader = null;
	}

}
