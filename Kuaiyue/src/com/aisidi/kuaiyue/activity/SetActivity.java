package com.aisidi.kuaiyue.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aisidi.kuaiyue.MainFragment;
import com.aisidi.kuaiyue.R;
import com.flood.mycar.drawable.ImageMode;


public class SetActivity extends Activity{
	
	private ImageView headView;
	private String imageUrl;
	private RadioButton[] fontButtons = new RadioButton[3];
	private RadioButton[] IamgeButtons = new RadioButton[4];
	private int font, image;
	public static final String SET_FILE = "set";
	public static final String SET_FONT = "set_font";
	public static final String SET_IMAGE = "set_image";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.set_layout);
		
		Intent intent = getIntent();
		imageUrl = intent.getStringExtra("profile_image_url");
		TextView set_version = (TextView)findViewById(R.id.set_version);
		
		PackageInfo pkg;
		try {
			pkg = getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);
			set_version.setText(pkg.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		SharedPreferences preferences = getSharedPreferences(SET_FILE, MODE_PRIVATE);
		font = preferences.getInt(SET_FONT, 1);
		image = preferences.getInt(SET_IMAGE, 0);
		
		headView = (ImageView)findViewById(R.id.set_head);
//		headView.setImageBitmap(MainActivity.headBuffer.getBitmap(imageUrl, this));
		MainFragment.downloader.downPic(headView, imageUrl, ImageMode.small);
		
		headView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		fontButtons[0] = (RadioButton)findViewById(R.id.radio_font1);
		fontButtons[1] = (RadioButton)findViewById(R.id.radio_font2);
		fontButtons[2] = (RadioButton)findViewById(R.id.radio_font3);
		
		IamgeButtons[0] = (RadioButton)findViewById(R.id.radio_image1);
		IamgeButtons[1] = (RadioButton)findViewById(R.id.radio_image2);
		IamgeButtons[2] = (RadioButton)findViewById(R.id.radio_image3);
		IamgeButtons[3] = (RadioButton)findViewById(R.id.radio_image4);
		
		for (int i = 0; i<fontButtons.length;i++) {
			final int j = i;
			fontButtons[i].setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setFontClickedState(SetActivity.this, j);
					font = j;
				}
			});
		}
		
		for (int i = 0; i<IamgeButtons.length;i++) {
			final int j = i;
			IamgeButtons[i].setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					setIamgeClickedState(SetActivity.this, j);
					image = j;
				}
			});
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			setFontClickedState(this, font);
			setIamgeClickedState(this, image);
		}
	}



	public static int fontSize(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(SET_FILE, MODE_PRIVATE);
		return preferences.getInt(SET_FONT, 1);
	}
	
	public static int imageSize(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(SET_FILE, MODE_PRIVATE);
		return preferences.getInt(SET_IMAGE, 0);
	}
	
	//��������ݵı���
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}


	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SharedPreferences preferences = getSharedPreferences(SET_FILE, MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt(SET_FONT, font);
		editor.putInt(SET_IMAGE, image);
		editor.commit();
//		Log.i("SET_FONT", font+"");
	}

	private void setFontClickedState(Context context, int which)
	{
		if (which>=fontButtons.length) {
			Log.e("setActivity", "fontButtons����Խ��");
			return;
		}
		Drawable falseDrawable = context.getResources().getDrawable(R.drawable.set_false);
		Drawable trueDrawable = context.getResources().getDrawable(R.drawable.set_true);
		for (RadioButton button : fontButtons) {
			button.setCompoundDrawablesWithIntrinsicBounds(null, null, falseDrawable, null);
		}
//		Log.i("fontButtons", which+"");
		fontButtons[which].setCompoundDrawablesWithIntrinsicBounds(null, null, trueDrawable, null);
	}
	
	private void setIamgeClickedState(Context context, int which)
	{
		if (which>=IamgeButtons.length) {
			Log.e("setActivity", "IamgeButtons����Խ��");
			return;
		}
		Drawable falseDrawable = context.getResources().getDrawable(R.drawable.set_false);
		Drawable trueDrawable = context.getResources().getDrawable(R.drawable.set_true);
		for (RadioButton button : IamgeButtons) {
			button.setCompoundDrawablesWithIntrinsicBounds(null, null, falseDrawable, null);
		}
		
		IamgeButtons[which].setCompoundDrawablesWithIntrinsicBounds(null, null, trueDrawable, null);
	}
	
}
