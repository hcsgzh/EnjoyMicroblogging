package com.aisidi.kuaiyue.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.bean.CommTimeLineBean;
import com.aisidi.kuaiyue.bean.UserBean;
import com.aisidi.kuaiyue.component.MyListView;
import com.aisidi.kuaiyue.utils.Utils;
import com.flood.mycar.drawable.ImageMode;

public class CommAdapter extends MyBaseAdapter{

	public CommAdapter(Context context, ArrayList<Object> list,
			MyListView listView) {
		super(context, list, listView);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View initConvertView(int position, View convertView) {
		// TODO Auto-generated method stub
		RecentViewHolder holder = new RecentViewHolder();
		if (convertView==null) {
			convertView = inflater.inflate(R.layout.comment_list_item, null);
			holder.head = (ImageView)convertView.findViewById(R.id.commt_head);
			holder.name = (TextView)convertView.findViewById(R.id.commt_name);
			holder.time = (TextView)convertView.findViewById(R.id.commt_time);
			holder.content = (TextView)convertView.findViewById(R.id.commt_text);
			holder.reContent = (TextView)convertView.findViewById(R.id.commt_retext);
			
			convertView.setTag(holder);
		}else {
			holder = (RecentViewHolder)convertView.getTag();
		}
		
		if (position>=list.size()) {
			return convertView;
		}
		CommTimeLineBean bean = (CommTimeLineBean) list.get(position);
		UserBean userBean = bean.getUser();
		
		downloader.downPic(holder.head, userBean.getProfile_image_url(), ImageMode.small);
		
		if (!TextUtils.isEmpty(userBean.getScreen_name())&&!TextUtils.isEmpty(userBean.getRemark())) {
			holder.name.setText(userBean.getScreen_name()+"("+userBean.getRemark()+")");
		}else if (!TextUtils.isEmpty(userBean.getScreen_name())) {
			holder.name.setText(userBean.getScreen_name());
		}
		
		holder.time.setText(bean.getCreated_at());
		holder.content.setText(Utils.setTextColor(bean.getCmt_text(),mContext.getResources().getColor(R.color.blue),mContext));
		holder.reContent.setText(Utils.setTextColor(bean.getReply_comment(), mContext.getResources().getColor(R.color.blue),mContext));
		
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
					holder.head.setImageBitmap(null);
					holder.head.getDrawable().setCallback(null);
				}
			}
		});
	}

	private class RecentViewHolder{
		TextView name;
		ImageView head;
		TextView time;
		TextView content;
		TextView reContent;
	}
}
