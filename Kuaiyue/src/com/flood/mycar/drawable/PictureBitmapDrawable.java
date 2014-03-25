package com.flood.mycar.drawable;

import java.lang.ref.WeakReference;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class PictureBitmapDrawable extends ColorDrawable{

	private final WeakReference<PictureBitmapWorkerTask> bitmapDownloaderTaskReference ;

	public PictureBitmapDrawable(PictureBitmapWorkerTask bitmapDownloaderTask) {
		// TODO Auto-generated constructor stub
		super(Color.TRANSPARENT);
		
		bitmapDownloaderTaskReference = new WeakReference<PictureBitmapWorkerTask>(bitmapDownloaderTask);
	}
	
	public PictureBitmapWorkerTask getBitmapDownloaderTask()
	{
		return bitmapDownloaderTaskReference.get();
	}
	
}
