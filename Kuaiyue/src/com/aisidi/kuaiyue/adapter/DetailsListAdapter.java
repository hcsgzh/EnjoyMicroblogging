package com.aisidi.kuaiyue.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.activity.ReplayActivity;
import com.aisidi.kuaiyue.bean.DetailsListBean;
import com.aisidi.kuaiyue.bean.UserBean;
import com.aisidi.kuaiyue.component.MyListView;
import com.aisidi.kuaiyue.utils.ReplayState;
import com.aisidi.kuaiyue.utils.Utils;
import com.flood.mycar.drawable.BitmapDownloader;
import com.flood.mycar.drawable.ImageMode;

public class DetailsListAdapter extends MyBaseAdapter{

	private BitmapDownloader downloader;
	
	public DetailsListAdapter(Context context,
			ArrayList<Object> list, MyListView listView, BitmapDownloader downloader) {
		super(context, list, listView);
		// TODO Auto-generated constructor stub
		
		this.downloader = downloader;
	}

	@Override
	protected View initConvertView(int position, View convertView) {
		// TODO Auto-generated method stub
		RecentViewHolder holder = new RecentViewHolder();
		if (convertView==null) {
			convertView = inflater.inflate(R.layout.detail_list_item, null);
			holder.comm_head = (ImageView)convertView.findViewById(R.id.comm_head);
			holder.comm_name = (TextView)convertView.findViewById(R.id.comm_name);
			holder.comm_time = (TextView)convertView.findViewById(R.id.comm_time);
			holder.comm_text = (TextView)convertView.findViewById(R.id.comm_text);
			holder.re_comm = (TextView)convertView.findViewById(R.id.re_comm);
			
			convertView.setTag(holder);
		}else {
			holder = (RecentViewHolder)convertView.getTag();
		}
		if (position>=list.size()) {
			return convertView;
		}
		final DetailsListBean bean = (DetailsListBean) list.get(position);
		UserBean userBean = bean.getUser();
		
		holder.comm_name.setText(userBean.getScreen_name());
		downloader.downPic(holder.comm_head, userBean.getProfile_image_url(), ImageMode.small);
		holder.comm_time.setText(bean.getCreated_at());
		holder.comm_text.setText(Utils.setTextColor(bean.getText(), color, mContext));

		holder.re_comm.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("bean.getCid()", bean.getCid()+"");
				if (bean.getCid()==0) {
					return;
				}
				Intent intent = new Intent();
				intent.putExtra("state", ReplayState.REPLAY);
				intent.putExtra("bean", bean);
				
				intent.setClass(mContext, ReplayActivity.class);
				
				mContext.startActivity(intent);
			}
		});
		
		return convertView;
	}

	@Override
	protected void clearReference(ListView listView) {
		// TODO Auto-generated method stub
		listView.setRecyclerListener(new AbsListView.RecyclerListener() {
			
			@Override
			public void onMovedToScrapHeap(View view) {
				// TODO Auto-generated method stub
				RecentViewHolder holder = (RecentViewHolder) view.getTag();
				if (holder!=null) {
					holder.comm_head.setImageBitmap(null);
					holder.comm_head.getDrawable().setCallback(null);
				}
			}
		});
	}

	class RecentViewHolder
	{
		TextView comm_name;
		ImageView comm_head;
		TextView comm_time;
		TextView comm_text;
		TextView re_comm;
	}
}
