package com.aisidi.kuaiyue.component;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.WeiboApplication;
import com.aisidi.kuaiyue.activity.DetailsActivity;
import com.aisidi.kuaiyue.activity.ReplayActivity;
import com.aisidi.kuaiyue.bean.HomeTimeLineBean;
import com.aisidi.kuaiyue.utils.ReplayState;
import com.aisidi.kuaiyue.utils.Utils;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class BottomPopupWindow {

	private Context mContext;
	private View containt;
	private HomeTimeLineBean bean;
	private PopupWindow popupWindow;
	private ProgressDialog mpDialog;
	private String id;

	public BottomPopupWindow(Context context, View containt, boolean isDetails) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.containt = containt;
		
		initPopupWindow(isDetails);
	}
	
	public void showMenu(HomeTimeLineBean bean)
	{
		this.bean = bean;
		this.id = bean.getId()+"";
		if (!popupWindow.isShowing()) {
			popupWindow.showAtLocation(containt, Gravity.BOTTOM,0, 0);
		}else{
			hideMenu();
		}
	}
	
	public void hideMenu()
	{
		popupWindow.dismiss();
	}
	
	private void initPopupWindow(boolean isDetails)
	{
		View view;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		if (isDetails) {
			view = inflater.inflate(R.layout.bottom_pop, null);
			popupWindow = new PopupWindow(view, WeiboApplication.getInstance().getDisplayMetrics().widthPixels,
					Utils.dip2px(mContext, 40));
			
		}else {
			view = inflater.inflate(R.layout.bottom_pop, null);
			
			popupWindow = new PopupWindow(view, WeiboApplication.getInstance().getDisplayMetrics().widthPixels,
					Utils.dip2px(mContext, 50));
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setFocusable(true);
		}
		
		ImageView bottom0 = (ImageView)view.findViewById(R.id.bottom_text1);
		ImageView bottom1 = (ImageView)view.findViewById(R.id.bottom_text2);
		ImageView bottom2 = (ImageView)view.findViewById(R.id.bottom_text3);
		ImageView bottom3 = (ImageView)view.findViewById(R.id.bottom_text4);
		
		if (isDetails) {
			LinearLayout layout = (LinearLayout)view.findViewById(R.id.bottom_linear);
			layout.setBackgroundResource(R.drawable.softkey02);
			bottom0.setImageResource(R.drawable.b_yw02);
			bottom1.setImageResource(R.drawable.b_zf02);
			bottom2.setImageResource(R.drawable.b_pl02);
			bottom3.setImageResource(R.drawable.b_sc02);
		}
		
		//原文
		bottom0.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openOriginalDetails();
			}
		});
		//转发
		bottom1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openRepostActivity();
			}
		});
		//评论
		bottom2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openCommActivity();
			}
		});
		//收藏
		bottom3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				collectWeibo();
			}
		});
	}
	
	private void openOriginalDetails()
	{
		Intent intent = new Intent();
		intent.putExtra("bean", bean);
		intent.putExtra("Original", true);
		intent.setClass(mContext, DetailsActivity.class);
		mContext.startActivity(intent);
		
		popupWindow.dismiss();
	}
	
	private void openRepostActivity()
	{
		Intent intent = new Intent();
		intent.putExtra("bean", bean);
		intent.putExtra("state", ReplayState.REPOST);
		intent.setClass(mContext, ReplayActivity.class);
		mContext.startActivity(intent);
		
		popupWindow.dismiss();
	}
	
	private void openCommActivity()
	{
		Intent intent = new Intent();
		intent.putExtra("bean", bean);
		intent.putExtra("state", ReplayState.COMMENTS);
		intent.setClass(mContext, ReplayActivity.class);
		mContext.startActivity(intent);
		
		popupWindow.dismiss();
	}
	
	private void collectWeibo()
	{
		mpDialog = new ProgressDialog(mContext);  
        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
        mpDialog.setTitle("提示");//设置标题  
        mpDialog.setMessage("正在提交中");  
        mpDialog.setIndeterminate(false);//设置进度条是否为不明确 
		mpDialog.show();
		
		new Favorites().start();
		
		popupWindow.dismiss();
	}
	
	class Favorites extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				String lrt = createFavorites(Weibo.getInstance(), id);
				if (lrt!="") {
					Message message = handler.obtainMessage();
					message.what = 0;
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
				int error = e.getStatusCode();
				Message message = handler.obtainMessage();
				message.arg1 = error;
				message.what = 1;
				handler.sendMessage(message);
				
				e.printStackTrace();
			}
		}
		
	}
	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mpDialog.cancel();
				Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				String error = "";
				switch (msg.arg1) {
				case 20019:
					error = "不要太贪心哦，发一次就够啦";
					break;
				case 10003:
					error = "远程服务出错";
					break;
				case 10009:
					error = "任务过多，系统繁忙";
					break;
				case 10001 :
					error = "系统错误";
					break;
				case 21331:
					error = "服务暂时无法访问";
					break;
				default:
					error = "提交失败，错误代码:"+msg.arg1;
					break;
				}
				mpDialog.dismiss();
				Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
		
	};
	
//  收藏微博
  private String createFavorites(Weibo weibo, String id) throws MalformedURLException, IOException, WeiboException {
		String url = Weibo.SERVER + "favorites/create.json";
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey());
		bundle.add("id", id);
		String rlt = weibo.request(mContext, url, bundle, "POST", weibo.getAccessToken());
		return rlt;
	}
}
