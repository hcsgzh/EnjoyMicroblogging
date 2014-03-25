package com.aisidi.kuaiyue.adapter;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView.RecyclerListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.WeiboApplication;
import com.aisidi.kuaiyue.activity.ProfileActivity;
import com.aisidi.kuaiyue.bean.HomeTimeLineBean;
import com.aisidi.kuaiyue.bean.UserBean;
import com.aisidi.kuaiyue.component.ImageLayout;
import com.aisidi.kuaiyue.component.MyListView;
import com.aisidi.kuaiyue.pager.HomePagerView;
import com.aisidi.kuaiyue.utils.Utils;
import com.flood.mycar.drawable.ImageMode;

public class HomeAdapter extends MyBaseAdapter{

	private long first,second,distanceTime;
	private Calendar myCalendar;
	private static final int TAP = 0, DOUBLE_TAP = 1, LONG_TAP = 2;
	private boolean isTwoPointers = false;
	private boolean isSelf;
	private HomePagerView homePagerView;
	
	public HomeAdapter(Context context, ArrayList<Object> list,
			MyListView listView, HomePagerView homePagerView, boolean isSelf) {
		super(context, list, listView);
		this.homePagerView = homePagerView;
		this.isSelf = isSelf;
	}

	@Override
	protected View initConvertView(final int position, View convertView) {
		// TODO Auto-generated method stub
		RecentViewHolder holder = new RecentViewHolder();
		if (convertView==null) {
			convertView = inflater.inflate(R.layout.home_list_item, null);
			holder.name = (TextView)convertView.findViewById(R.id.home_name);
			holder.head = (ImageView)convertView.findViewById(R.id.home_head);
			holder.time = (TextView)convertView.findViewById(R.id.home_time);
			holder.content = (TextView)convertView.findViewById(R.id.content_text);
			holder.reContent = (TextView)convertView.findViewById(R.id.recontent_text);
			holder.reLayout = (LinearLayout)convertView.findViewById(R.id.home_recontent);
			holder.source = (TextView)convertView.findViewById(R.id.source_text);
			holder.forward = (TextView)convertView.findViewById(R.id.forward);
			holder.comment = (TextView)convertView.findViewById(R.id.comment);
			holder.vip = (ImageView)convertView.findViewById(R.id.head_v);
			holder.imageLayout = (ImageLayout)convertView.findViewById(R.id.imageLayout);
			holder.reImageLayout = (ImageLayout)convertView.findViewById(R.id.re_imageLayout);
			holder.user_layout = (RelativeLayout)convertView.findViewById(R.id.user_layout);
			convertView.setTag(holder);
		}else {
			holder = (RecentViewHolder) convertView.getTag();
		}
		
		holder.reLayout.setVisibility(View.GONE);
		
		if (position>=list.size()) {
			return convertView;
		}
		HomeTimeLineBean bean = (HomeTimeLineBean) list.get(position);
		final UserBean userBean = bean.getUser();
		UserBean reUserBean = bean.getRe_user();
		
		if (isSelf) {
			holder.user_layout.setVisibility(View.GONE);
		}else {
			if (userBean.isVerified()) {
				holder.vip.setVisibility(View.VISIBLE);
			}else {
				holder.vip.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(userBean.getScreen_name())&&!TextUtils.isEmpty(userBean.getRemark())) {
				holder.name.setText(userBean.getScreen_name()+"("+userBean.getRemark()+")");
			}else if (!TextUtils.isEmpty(userBean.getScreen_name())) {
				holder.name.setText(userBean.getScreen_name());
			}
			if (!TextUtils.isEmpty(userBean.getProfile_image_url())) {
				downloader.downPic(holder.head, userBean.getProfile_image_url(), ImageMode.small);
			}
			
			holder.name.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openProfile(userBean);
				}
			});
			holder.head.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openProfile(userBean);
				}
			});
			
		}
		
		if (!TextUtils.isEmpty(bean.getText())) {
			holder.content.setText(Utils.setTextColor(bean.getText(),color,mContext));
		}
		if (!TextUtils.isEmpty(bean.getCreated_at())) {//时间
			holder.time.setText(bean.getCreated_at());
		}
		if (!TextUtils.isEmpty(bean.getSource())) {
			holder.source.setText(bean.getSource());
		}
		holder.forward.setText(bean.getReposts_count()+"");
		holder.comment.setText(bean.getComments_count()+"");
		if (!TextUtils.isEmpty(bean.getRe_text())&&!TextUtils.isEmpty(reUserBean.getScreen_name())) {
			holder.reLayout.setVisibility(View.VISIBLE);
			holder.reContent.setText(Utils.setTextColor("@"+reUserBean.getScreen_name()+": "+bean.getRe_text(),
					color,mContext));
		}
		if (bean.getPic_urls()!=null) {
			holder.imageLayout.setVisibility(View.VISIBLE);
			holder.imageLayout.getImages(mContext, bean.getPic_urls(), downloader,listView, WeiboApplication.imageMode);
		}else {
			holder.imageLayout.setVisibility(View.GONE);
		}
		if (bean.getRe_pic_urls()!=null) {
			holder.reImageLayout.setVisibility(View.VISIBLE);
			holder.reImageLayout.getImages(mContext, bean.getRe_pic_urls(), downloader, listView, WeiboApplication.imageMode);
		}else {
			holder.reImageLayout.setVisibility(View.GONE);
		}
		convertView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
//				final int pointerCount = event.getPointerCount();
				switch (event.getAction()&MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(pressColor);
					myCalendar=Calendar.getInstance();
					if (first==0) {
						first = myCalendar.getTimeInMillis();
						Message message = handler.obtainMessage();
						message.arg1 = position;
						message.what = LONG_TAP;
						handler.sendMessageDelayed(message, 800);
						
					}else if(second==0){
						handler.removeMessages(TAP);
						second = myCalendar.getTimeInMillis();
						distanceTime = second - first;
						first = 0;
						second = 0;
					}
					isTwoPointers = false;
					break;
				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(defaultColor);
					
					if (!isTwoPointers) {
						if (distanceTime>0&&distanceTime<500l) {
							Message message = handler.obtainMessage();
							message.arg1 = position;
							message.what = DOUBLE_TAP;
							handler.sendMessage(message);
							first = 0;
							second = 0;
							distanceTime = 0;
							break;
						}
						if ((Calendar.getInstance().getTimeInMillis()-first)>800l) {
							first = 0;
							second = 0;
						}else {
							handler.removeMessages(LONG_TAP);
							Message message = handler.obtainMessage();
							message.arg1 = position;
							message.what = TAP;
							handler.sendMessageDelayed(message, 500);
						}
						
					}
					break;
				case MotionEvent.ACTION_POINTER_UP:
					homePagerView.repost(position);
					
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					isTwoPointers = true;
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_CANCEL:
					first = 0;
					handler.removeMessages(LONG_TAP);
					v.setBackgroundColor(defaultColor);
					break;
				default:
					break;
				}
				return true;
			}
		});
		convertView.setBackgroundColor(defaultColor);
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
					holder.head.setImageBitmap(null);
					holder.head.getDrawable().setCallback(null);
					holder.imageLayout.clearReference();
					holder.reImageLayout.clearReference();
				}
			}
		});
	}

	class RecentViewHolder{
		TextView name;
		ImageView head;
		TextView time;
		TextView content;
		ImageView image;
		TextView reContent;
		ImageView reImage;
		LinearLayout reLayout;
		TextView source;
		TextView forward, comment;
		ImageView vip;
		ImageLayout imageLayout, reImageLayout;
		RelativeLayout user_layout;
	}

	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int position = msg.arg1;
			switch (msg.what) {
			case TAP:
				first = 0;
				homePagerView.onclickItem(position);
				break;
			case LONG_TAP:
				homePagerView.onLongClickItem(position);
				break;
			case DOUBLE_TAP:
				homePagerView.commentT(position);
				break;
			default:
				break;
			}
		}
		
	};

	private void openProfile(UserBean userBean)
	{
		Intent intent = new Intent();
		intent.putExtra("user", userBean);
		intent.setClass(mContext, ProfileActivity.class);
		mContext.startActivity(intent);
	}
}
