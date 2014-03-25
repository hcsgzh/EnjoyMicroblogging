package com.aisidi.kuaiyue.component;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.WeiboApplication;
import com.aisidi.kuaiyue.activity.BrowserBigPicActivity;
import com.aisidi.kuaiyue.activity.MultiplePicActivity;
import com.aisidi.kuaiyue.adapter.HomeAdapter;
import com.aisidi.kuaiyue.utils.Utils;
import com.flood.mycar.drawable.BitmapDownloader;
import com.flood.mycar.drawable.ImageMode;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class ImageLayout extends LinearLayout{

	private CustomerBar customerBar;
	private LinearLayout imageLayout;
	private LayoutParams lp, image_lp,image_lp2, image_lp3;
	private ImageView image_gif;
	private ImageView[] imageViews;
	private int imageWidth;
	private String[] largePics, smallPics;
	private LayoutInflater inflater;
	
	public ImageLayout(Context context) {
		super(context);
		initView(context);
	}

	public ImageLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context)
	{
		inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.image_layout, null);
		addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		customerBar = (CustomerBar)findViewById(R.id.waiting_bar);
		imageLayout = (LinearLayout)findViewById(R.id.image_lay);
		image_gif = (ImageView)findViewById(R.id.image_gif);
		
		imageWidth = HomeAdapter.homeContentWidth/3-20;
		
		lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		image_lp = new LayoutParams(imageWidth, imageWidth);
		image_lp.setMargins(3, 3, 3, 3);
		
		image_lp2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		image_lp3 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
	}
	
	/***
	 * 
	 * @param context
	 * @param pics 这里是包含多个缩略图地址的数组
	 * @param downloader
	 * @param mode
	 */
	public void getImages(final Context context, final String[] pics, BitmapDownloader downloader, 
			MyListView listView, ImageMode mode)
	{
		imageLayout.removeAllViews();
		int count = pics.length;
		imageViews = new ImageView[count];
		largePics = new String[count];
		smallPics = new String[count];
		image_gif.setVisibility(View.GONE);
		LinearLayout layout = null;
		
		for (int i = 0; i < count; i++) {
			if (i%3==0) {
				layout = new LinearLayout(context);
				imageLayout.addView(layout,lp);
			}
			imageViews[i] = (ImageView) inflater.inflate(R.layout.image_item, null);
			
			if (count==1) {
				if (mode==ImageMode.large||mode==ImageMode.original) {
					layout.addView(imageViews[i],image_lp3);
					imageViews[i].setAdjustViewBounds(true);
					imageViews[i].setMinimumHeight(70);
				}else {
					layout.addView(imageViews[i],image_lp2);
				}
			}else {
				layout.addView(imageViews[i],image_lp);
			}
			
			imageViews[i].setPadding(10, 10, 10, 10);
			
			largePics[i] = Utils.getPicUrl(pics[i])[1];
			smallPics[i] = Utils.getPicUrl(pics[i])[0];
			if (count==1) {//单图模式
				if (isGif(pics[0])) {
					image_gif.setVisibility(View.VISIBLE);
					downloader.downPic(imageViews[i], Utils.getPicUrl(pics[i]),listView, customerBar, ImageMode.small);
				}else {
					image_gif.setVisibility(View.GONE);
					downloader.downPic(imageViews[i], Utils.getPicUrl(pics[i]),listView, customerBar, mode);
				}
				
				imageViews[0].setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.putExtra("url", largePics[0]);
						intent.setClass(context, BrowserBigPicActivity.class);
						context.startActivity(intent);
					}
				});
			}else {//多图模式
				downloader.downPic(imageViews[i], Utils.getPicUrl(pics[i]),listView, customerBar,ImageMode.small);
				final int j = i;
				imageViews[i].setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.putExtra("largePics", largePics);
						intent.putExtra("smallPics", smallPics);
						intent.putExtra("pic_id", j);
						intent.setClass(context, MultiplePicActivity.class);
						context.startActivity(intent);
					}
				});
			}
		}
		
	}
	
	private ImageView createImageView(Context context)
	{
		ImageView imageView = new ImageView(context);
		imageView.setPadding(5, 5, 5, 5);
		imageView.setScaleType(ScaleType.FIT_XY);
		
		return imageView;
	}
	
	private boolean isGif(String picPath)
	{
		if (picPath.lastIndexOf(".gif")!=-1) {
			return true;
		}
		return false;
	}
	
	public void showGifLogo(boolean show)
	{
		if (show) {
			image_gif.setVisibility(View.VISIBLE);
		}else {
			image_gif.setVisibility(View.GONE);
		}
		
	}
	
	public void clearReference()
	{
		if (customerBar!=null) {
			customerBar.stop();
		}
		if (imageViews==null) {
			return;
		}
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[i].setImageBitmap(null);
			imageViews[i].getDrawable().setCallback(null);
		}
	}
	
	public void startWaitting()
	{
		customerBar.startDelay();
	}
	
	public void stopWaitting()
	{
		customerBar.stop();
	}
}
