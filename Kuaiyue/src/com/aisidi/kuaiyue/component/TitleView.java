package com.aisidi.kuaiyue.component;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aisidi.kuaiyue.MainFragment;
import com.aisidi.kuaiyue.MainFragment.Notifaction;
import com.aisidi.kuaiyue.R;


public class TitleView extends RelativeLayout implements Notifaction{
	private FrameLayout[] titles = new FrameLayout[4];
	public PopupWindow menuPopupWindow;
	public LinearLayout[] menuLayouts = new LinearLayout[5];
	public LinearLayout headLayout;
	private ChangePageListener listener;
	private ImageView[] images = new ImageView[4];
	private Drawable[] drawables = new Drawable[4];
	private Drawable[] drawables_sel = new Drawable[4];
	private TextView[] textViews = new TextView[3];
	private int one, two, three;
	private OnClickHeadListener clickHeadListener;

	public TitleView(Context context) {
		super(context);
		
		initView(context);
	}
	
	
	
	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initView(context);
	}



	private void initView(Context context)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.title_view, null);
		
		headLayout = (LinearLayout)view.findViewById(R.id.title_left);
		
		titles[0] = (FrameLayout)view.findViewById(R.id.title_1);
		titles[1] = (FrameLayout)view.findViewById(R.id.title_2);
		titles[2] = (FrameLayout)view.findViewById(R.id.title_3);
		titles[3] = (FrameLayout)view.findViewById(R.id.title_4);
		
		images[0] = (ImageView)view.findViewById(R.id.Image_head1);
		images[1] = (ImageView)view.findViewById(R.id.Image_head2);
		images[2] = (ImageView)view.findViewById(R.id.Image_head3);
		images[3] = (ImageView)view.findViewById(R.id.Image_head4);
		
		drawables[0] = getResources().getDrawable(R.drawable.home);
		drawables[1] = getResources().getDrawable(R.drawable.at);
		drawables[2] = getResources().getDrawable(R.drawable.comments);
		drawables[3] = getResources().getDrawable(R.drawable.creat);
		
		drawables_sel[0] = getResources().getDrawable(R.drawable.home_sel);
		drawables_sel[1] = getResources().getDrawable(R.drawable.at_sel);
		drawables_sel[2] = getResources().getDrawable(R.drawable.comments_sel);
		drawables_sel[3] = getResources().getDrawable(R.drawable.creat_sel);
		
		textViews[0] = (TextView)view.findViewById(R.id.head1);
		textViews[1] = (TextView)view.findViewById(R.id.head2);
		textViews[2] = (TextView)view.findViewById(R.id.head3);
		
		for (int i = 0; i < titles.length; i++) {
			final int j = i;
			
			titles[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					// TODO Auto-generated method stub
					if (listener!=null) {
						listener.setPage(j);
						setTitle(j);
					}
				}
			});
		}
		
		headLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (clickHeadListener!=null) {
					clickHeadListener.clickHead();
				}
			}
		});
		
		this.addView(view,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}
	
	public void dispatchClick(int j)
	{
		if (listener!=null) {
			listener.setPage(j);
			setTitle(j);
		}
	}

	private void setUnReadCount(int one, int two, int three)
	{
		this.one = one;
		this.two = two;
		this.three = three;
		if (one!=0) {
			one = Math.min(one, 99);
			textViews[0].setText(one+"");
			textViews[0].setVisibility(View.VISIBLE);
		}else {
			textViews[0].setVisibility(View.GONE);
		}
		
		if (two!=0) {
			two = Math.min(two, 99);
			textViews[1].setText(two+"");
			textViews[1].setVisibility(View.VISIBLE);
		}else {
			textViews[1].setVisibility(View.GONE);
		}
		
		if (three!=0) {
			three = Math.min(three, 99);
			textViews[2].setText(three+"");
			textViews[2].setVisibility(View.VISIBLE);
		}else {
			textViews[2].setVisibility(View.GONE);
		}
		
	}
	
	public void setPageListener(final ChangePageListener listener)
	{
		this.listener = listener;
		
	}
	public void setTitle(int index)
	{
		for (int i = 0; i < titles.length; i++) {
			titles[i].setBackgroundResource(R.color.alpha);
			images[i].setImageDrawable(drawables[i]);
		}
		images[index].setImageDrawable(drawables_sel[index]);
		titles[index].setBackgroundResource(R.drawable.detail_bar_sel);
		switch (index) {
		case 0:
			one = 0;
			break;
		case 1:
			two = 0;
			break;
		case 2:
			three = 0;
			break;
		default:
			break;
		}
	}
	
	public interface ChangePageListener
	{
		void setPage(int page);
	}

	@Override
	public void notifactionCount(int one, int two, int three) {
		// TODO Auto-generated method stub
		setUnReadCount(one, two, three);
	}

	@Override
	public void clearHome() {
		// TODO Auto-generated method stub
		MainFragment.status = 0;
		setUnReadCount(0, two, three);
	}

	@Override
	public void clearMotion() {
		// TODO Auto-generated method stub
		MainFragment.mention_status = 0;
		setUnReadCount(one, 0, three);
	}

	@Override
	public void clearCmt() {
		// TODO Auto-generated method stub
		MainFragment.cmt = 0;
		setUnReadCount(one, two, 0);
	}
	
	public void setOnClickHeadListener(OnClickHeadListener l)
	{
		this.clickHeadListener = l;
	}
	public interface OnClickHeadListener
	{
		void clickHead();
	}
}
