package com.aisidi.kuaiyue.adapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.RecyclerListener;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.activity.ProfileActivity;
import com.aisidi.kuaiyue.adapter.HomeAdapter.RecentViewHolder;
import com.aisidi.kuaiyue.bean.UserBean;
import com.aisidi.kuaiyue.component.MyListView;
import com.flood.mycar.drawable.BitmapDownloader;
import com.flood.mycar.drawable.ImageMode;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

public class FriendsAdapter extends MyBaseAdapter{

	private int color1, color2;
	private Drawable drawable1, drawable2;
	private BitmapDownloader downloader;
	private ProgressDialog mpDialog;
	private String item_uid;
	private int listPosition;
	private boolean creat;
	
	public FriendsAdapter(Context context, ArrayList<Object> list,
			MyListView listView, BitmapDownloader downloader) {
		super(context, list, listView);
		this.downloader = downloader;
		
		color1 = mContext.getResources().getColor(R.color.title_text);
		color2 = mContext.getResources().getColor(R.color.white);
		
		drawable1 = mContext.getResources().getDrawable(R.drawable.button_selector);
		drawable2 = mContext.getResources().getDrawable(R.drawable.button_selector2);
	}

	@Override
	protected View initConvertView(final int position, View convertView) {
		// TODO Auto-generated method stub
		RecentViewHolder holder = new RecentViewHolder();
		if (convertView==null) {
			convertView = inflater.inflate(R.layout.friend_item, null);
			holder.headView = (ImageView)convertView.findViewById(R.id.friend_head);
			holder.name = (TextView)convertView.findViewById(R.id.friend_name);
			holder.followBtn = (Button)convertView.findViewById(R.id.friend_follow);
			convertView.setTag(holder);
			
		}else {
			holder = (RecentViewHolder)convertView.getTag();
		}
		
		final UserBean userBean = (UserBean) list.get(position);
		
		holder.name.setText(userBean.getScreen_name());
		downloader.downPic(holder.headView, userBean.getProfile_image_url(),listView, ImageMode.small);
		holder.headView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("user", userBean);
				intent.setClass(mContext, ProfileActivity.class);
				mContext.startActivity(intent);
			}
		});
		
		final boolean following;
		
		if (userBean.isFollowing()) {
			holder.followBtn.setText("取消关注");
			holder.followBtn.setTextColor(color1);
			holder.followBtn.setBackgroundDrawable(drawable1);
			following = false;
		}else {
			holder.followBtn.setText("添加关注");
			holder.followBtn.setTextColor(color2);
			holder.followBtn.setBackgroundDrawable(drawable2);
			following = true;
		}
		
		holder.followBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				item_uid = userBean.getUserId();
				
				listPosition = position;
				creat = following;
				new SetFriends().start();
				mpDialog = new ProgressDialog(mContext);  
                mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
                mpDialog.setTitle("提示");//设置标题  
                mpDialog.setMessage("正在提交中");  
                mpDialog.setIndeterminate(false);//设置进度条是否为不明确 
				mpDialog.show();
			}
		});
		return convertView;
	}

	@Override
	protected void clearReference(ListView listView) {
		// TODO Auto-generated method stub
		listView.setRecyclerListener(new RecyclerListener() {
			
			@Override
			public void onMovedToScrapHeap(View view) {
				// TODO Auto-generated method stub
				RecentViewHolder holder = (RecentViewHolder) view.getTag();
				if (holder!=null) {
					holder.headView.setImageBitmap(null);
					holder.headView.getDrawable().setCallback(null);
				}
			}
		});
	}
	
	 private String createFriends(Weibo weibo, String uid) throws MalformedURLException, IOException, WeiboException {
		String url = Weibo.SERVER + "friendships/create.json";
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey());
		bundle.add("uid", uid);
		String rlt = weibo.request(mContext, url, bundle, "POST", weibo.getAccessToken());
		return rlt;
	}
	    
	private String destroyFriends(Weibo weibo, String uid) throws MalformedURLException, IOException, WeiboException {
		String url = Weibo.SERVER + "friendships/destroy.json";
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("source", Weibo.getAppKey());
		bundle.add("uid", uid);
		String rlt = weibo.request(mContext, url, bundle, "POST", weibo.getAccessToken());
		return rlt;
		}
	class SetFriends extends Thread
    {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			String lrt = "";
			try {
				if (creat) {
					lrt = createFriends(Weibo.getInstance(), item_uid);
				}else {
					lrt = destroyFriends(Weibo.getInstance(), item_uid);
				}
				if (!lrt.equals("")) {
					Message message = handler.obtainMessage();
					message.what = 2;
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
				message.what = 5;
				handler.sendMessage(message);
				e.printStackTrace();
			}
		}
    }
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 2://添加或者取消关注
				if (mpDialog!=null) {
					mpDialog.cancel();
				}
				Toast.makeText(mContext, "ok", Toast.LENGTH_SHORT).show();
				break;
			case 4:
				if (mpDialog!=null) {
					mpDialog.cancel();
					Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
				}
				break;
			case 5:
				if (mpDialog!=null) {
					mpDialog.cancel();
				}
				Toast.makeText(mContext, "错误", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
		
	};
	class RecentViewHolder{
    	ImageView headView;
    	TextView name;
    	Button followBtn;
    }
}
