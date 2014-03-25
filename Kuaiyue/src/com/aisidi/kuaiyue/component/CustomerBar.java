package com.aisidi.kuaiyue.component;

import java.util.concurrent.atomic.AtomicBoolean;


import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.Resources.Theme;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class CustomerBar extends View{

	private int count;
	private Paint paint, alphaPaint;
	private boolean asc = true;
	private boolean isShowing = true;
	public static AtomicBoolean isAppRunning = new AtomicBoolean(true);
	private Thread thread;
	private static final float y = 15;
	
	public CustomerBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}

	public CustomerBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		initView();
	}
	
	private void initView()
	{
		paint = new Paint();
		paint.setColor(0XFFCDCDCD);
		paint.setStrokeWidth(6.0f);
		
		alphaPaint = new Paint();
		alphaPaint.setColor(Color.TRANSPARENT);
		alphaPaint.setStrokeWidth(6.0f);
		
		thread = new Thread(runnable);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		drawPoint(canvas, count);
	}
	
	private void drawPoint(Canvas canvas, int id)
	{
		switch (id) {
		case 0:
			canvas.drawPoint(10, y, alphaPaint);
			canvas.drawPoint(20, y, alphaPaint);
			canvas.drawPoint(30, 10, alphaPaint);
			break;
		case 1:
			canvas.drawPoint(10, y, paint);
			canvas.drawPoint(20, y, alphaPaint);
			canvas.drawPoint(30, y, alphaPaint);
			break;
		case 2:
			canvas.drawPoint(10, y, paint);
			canvas.drawPoint(20, y, paint);
			canvas.drawPoint(30, y, alphaPaint);
			break;
		case 3:
			canvas.drawPoint(10, y, paint);
			canvas.drawPoint(20, y, paint);
			canvas.drawPoint(30, y, paint);
			break;
		default:
			canvas.drawPoint(10, y, paint);
			canvas.drawPoint(20, y, paint);
			canvas.drawPoint(30, y, paint);
			break;
		}
		
	}

	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(isShowing&&isAppRunning.get())
			{
				if (count>=3) {
					asc = false;
				}else if (count<=0) {
					asc = true;
				}
				postInvalidate();
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (asc) {
					count++;
				}else {
					count--;
				}
				
			}
		}
	};
	
	Runnable delayRunnable = new Runnable() {
		
		@Override
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated method stub
			while(isShowing&&isAppRunning.get())
			{
				if (count>=3) {
					asc = false;
				}else if (count<=0) {
					asc = true;
				}
				postInvalidate();
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (asc) {
					count++;
				}else {
					count--;
				}
				
			}
		}
	};
	
	public void start()
	{
		this.setVisibility(View.VISIBLE);
		isShowing = true;
		if (!thread.isAlive()) {
			thread = new Thread(runnable);
			thread.start();
		}
		
	}
	
	public void startDelay()
	{
		this.setVisibility(View.VISIBLE);
		isShowing = true;
		if (!thread.isAlive()) {
			thread = new Thread(delayRunnable);
			thread.start();
		}
	}
	
	public void stop()
	{
		this.setVisibility(View.GONE);
		isShowing = false;
	}
	
}

