package com.aisidi.kuaiyue;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;
import android.widget.Toast;

import com.aisidi.kuaiyue.MainFragment.OnScreenListener;
import com.aisidi.kuaiyue.component.CustomerBar;
import com.aisidi.kuaiyue.utils.FileManager;
import com.aisidi.kuaiyue.utils.ProcessType;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;

public class HomeActivity extends SlidingActivity implements OnScreenListener{

	private MainFragment mainFragment;
	private int backCount = 0;
	private boolean isClosed;
	private int currentScreen;
	private SlidingMenu sm;
	private MenuFragment menuFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		WeiboApplication.getInstance().setActivity(this);
		
		setContentView(R.layout.frame_content);
		setBehindContentView(R.layout.frame_menu);
		
		mainFragment = new MainFragment();
		menuFragment = new MenuFragment();
		
		mainFragment.setOnScreenListener(this);
		
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.menu, menuFragment);
        fragmentTransaction.replace(R.id.content, mainFragment);
        fragmentTransaction.commit();
        
        
        int screenW = WeiboApplication.getInstance().getDisplayMetrics().widthPixels;
     // customize the SlidingMenu
        sm = getSlidingMenu();
        sm.setShadowWidth(50);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffset(screenW-260);
        sm.setFadeDegree(0.35f);
        //设置slding menu的几种手势模式
        //TOUCHMODE_FULLSCREEN 全屏模式，在content页面中，滑动，可以打开sliding menu
        //TOUCHMODE_MARGIN 边缘模式，在content页面中，如果想打开slding ,你需要在屏幕边缘滑动才可以打开slding menu
        //TOUCHMODE_NONE 自然是不能通过手势打开啦
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	public void UpdateListChange()
	{
		menuFragment.updateList();
	}
	public void OpenLonginPage()
	{
		mainFragment.loginWeiBo();
	}
	
	public void changeUserUI(String access_token, String expires_in)
	{
		mainFragment.changeUser(access_token, expires_in);
	}
	
	public void showGroups(ArrayList<HashMap<String, String>> groupsList)
	{
		menuFragment.showGroups(groupsList);
	}
	
	public void changeGroupsTimeLine(String list_id)
	{
		mainFragment.changeGroups(list_id);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//图库修剪+摄像机修剪通过
		if (resultCode == RESULT_OK) {
			if (ProcessType.processType) {
				mainFragment.setPicPath();
			} else {
				FileOutputStream out = null;
				try {
					Intent intent = new Intent(
								"com.android.camera.action.CROP");
					intent.setDataAndType(Uri.fromFile(FileManager.tempFile), "image/*");// 设置要裁剪的图片
					intent.putExtra("crop", "true");// crop=true
														// 有这句才能出来最后的裁剪页面.
					intent.putExtra("output", Uri.fromFile(FileManager.tempFile));// 保存到原文件
					intent.putExtra("outputFormat", "JPEG");// 返回格式
					ProcessType.processType = true;
					startActivityForResult(intent, 1);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		backCount++;
		if (backCount==1) {
			Toast.makeText(this, "再按一次推出", Toast.LENGTH_SHORT).show();
			Message message = handler.obtainMessage(0);
			handler.sendMessageDelayed(message, 2000);
		}else if (backCount>1&&!isClosed) {
			isClosed = true;
			destroyApp();
		}
	}
	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			backCount = 0;
		}
		
	};
	
	private void destroyApp()
	{
		mainFragment.stopTimeLinePicDownLoad();
		CustomerBar.isAppRunning.set(false);
		FileManager.deleteCachePic();
		finish();
	}

	@Override
	public void whichScreen(int index) {
		// TODO Auto-generated method stub
		switch (index) {
		case 0:
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			break;

		default:
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			break;
		}
	}
	
}
