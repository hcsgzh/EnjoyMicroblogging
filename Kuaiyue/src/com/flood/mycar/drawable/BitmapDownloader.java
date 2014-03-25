package com.flood.mycar.drawable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.aisidi.kuaiyue.WeiboApplication;
import com.aisidi.kuaiyue.component.CustomerBar;
import com.aisidi.kuaiyue.component.MyListView;

public class BitmapDownloader {

	private Drawable transPic = new ColorDrawable(Color.TRANSPARENT);
	private Map<String, PictureBitmapWorkerTask> picTasks = new ConcurrentHashMap<String, PictureBitmapWorkerTask>();
	
	protected Bitmap getBitmapFromMemCache(String key)
	{
		if (TextUtils.isEmpty(key)) {
			return null;
		}else {
			return WeiboApplication.getInstance().getCache().get(key);
		}
	}
	/**
	 * 这个是去单图，单地址的情况下的方法，不需要看见等待条，不需要获得listview滚动状态
	 * @param imageView
	 * @param imagePath
	 * @param mode
	 */
	public void downPic(ImageView imageView, String imagePath,ImageMode mode)
	{
		display(imageView, imagePath,null,null,mode);
	}
	public void downPic(ImageView imageView, String imagePath, MyListView listView, ImageMode mode)
	{
		display(imageView, imagePath,listView,null,mode);
	}
	/**
	 * 这个情况是单图单地址，需要看见等待条，不需要获得listview滚动状态，
	 * @param imageView
	 * @param pics
	 * @param customerBar
	 * @param mode
	 */
	
	public void downPic(ImageView imageView, String imagePath, CustomerBar customerBar, ImageMode mode)
	{
		display(imageView, imagePath,null,customerBar,mode);
	}
	/**
	 * 这个情况是单图多地址，不需要看见等待条，不需要获得listview滚动状态，这里默认listview和customerbar为null
	 * @param imageView
	 * @param pics 容量为3的数组，依次为小，中，大图地址
	 * @param mode
	 */
	public void downPic(ImageView imageView, String[] pics, ImageMode mode)
	{
		downPic(imageView, pics, null, null, mode);
	}
	/**
	 * 这个情况是单图多地址，需要看见等待条，不需要获得listview滚动状态，
	 * @param imageView
	 * @param pics
	 * @param customerBar
	 * @param mode
	 */
	
	public void downPic(ImageView imageView, String[] pics, CustomerBar customerBar, ImageMode mode)
	{
		downPic(imageView, pics, null, customerBar, mode);
	}
	/**
	 * 这个是单图多地址，带listview来获得滚动状态
	 * @param imageView
	 * @param pics
	 * @param listView
	 * @param customerBar
	 * @param mode
	 */
	public void downPic(ImageView imageView, String[] pics, MyListView listView, CustomerBar customerBar, ImageMode mode)
	{
		switch (mode) {
		case small:
			display(imageView, pics[0],listView,customerBar, mode);
			break;
		case large:
			display(imageView, pics[1],listView,customerBar, mode);
			break;
		case original:
			display(imageView, pics[2],listView,customerBar, mode);
			break;
		default:
			break;
		}
	}
	
	private void display(ImageView view, String url, MyListView listView,CustomerBar customerBar, ImageMode mode)
	{
		view.clearAnimation();
		final Bitmap bitmap = getBitmapFromMemCache(url);
		
		if (bitmap!=null) {
			view.setImageBitmap(bitmap);
			cancelPotentialDownload(url, view);
			picTasks.remove(url);
		}else {
			if (listView!=null&&listView.isListViewFling()) {
				view.setImageDrawable(transPic);
				return;
			}
			
			PictureBitmapWorkerTask task = picTasks.get(url);
			if (task!=null) {
				task.addView(view);
				view.setImageDrawable(new PictureBitmapDrawable(task));
				return;
			}
			
			if (cancelPotentialDownload(url, view)) {
				task = new PictureBitmapWorkerTask(picTasks, view, url, customerBar, mode);
				PictureBitmapDrawable downloadedDrawable = new PictureBitmapDrawable(task);
				view.setImageDrawable(downloadedDrawable);
				task.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
				
				picTasks.put(url, task);
				return;
			}
		}
		
	}
	
	private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        PictureBitmapWorkerTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.getUrl();
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static PictureBitmapWorkerTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof PictureBitmapDrawable) {
                PictureBitmapDrawable downloadedDrawable = (PictureBitmapDrawable) drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    public void totalStopLoadPicture() {
        if (picTasks != null) {
            for (String task : picTasks.keySet()) {
//            	try {
//					picTasks.get(task).;
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (ExecutionException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
                picTasks.get(task).cancel(true);
            }
        }
    }
}
