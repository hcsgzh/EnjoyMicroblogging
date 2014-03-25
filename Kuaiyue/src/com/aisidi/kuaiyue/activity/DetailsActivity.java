package com.aisidi.kuaiyue.activity;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.bean.DetailsListBean;
import com.aisidi.kuaiyue.bean.HomeTimeLineBean;
import com.aisidi.kuaiyue.bean.UserBean;
import com.aisidi.kuaiyue.component.ImageLayout;
import com.aisidi.kuaiyue.component.MyListView;
import com.aisidi.kuaiyue.net.WeiBoHelper;
import com.aisidi.kuaiyue.pager.DetailsPage;
import com.aisidi.kuaiyue.utils.Utils;
import com.flood.mycar.drawable.BitmapDownloader;
import com.flood.mycar.drawable.ImageMode;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class DetailsActivity extends Activity{

	private ImageView title_image;
	private BitmapDownloader downloader;
	private HomeTimeLineBean bean;
	private UserBean userBean, reUserBean;
	private DetailsPage detailsPage;
	
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
		bean = (HomeTimeLineBean) intent.getSerializableExtra("bean");
		userBean = bean.getUser();
		
		setContentView(R.layout.activity_detail);
		
		LinearLayout details_list_content = (LinearLayout)findViewById(R.id.details_list_content);
		
		detailsPage = new DetailsPage(this, WeiBoHelper.detailsLineUrl, bean.getId()+"", downloader, bean);
		details_list_content.addView(detailsPage,
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		TextView title_name = (TextView)findViewById(R.id.title_name);
		title_image = (ImageView)findViewById(R.id.title_image);
		ImageView pic_back = (ImageView)findViewById(R.id.pic_back);
		
		title_name.setText(userBean.getScreen_name());
		downloader.downPic(title_image, userBean.getProfile_image_url(), ImageMode.small);
		
		pic_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		detailsPage.freshPage(false);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		clearReference();
	}
	
	private void clearReference()
	{
		title_image.setImageBitmap(null);
		title_image.getDrawable().setCallback(null);
		
		if (downloader!=null) {
			downloader.totalStopLoadPicture();
			downloader = null;
		}
	}
	
}
