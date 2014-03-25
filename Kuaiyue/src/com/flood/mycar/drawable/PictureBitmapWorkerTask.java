package com.flood.mycar.drawable;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.WeiboApplication;
import com.aisidi.kuaiyue.component.CustomerBar;
import com.aisidi.kuaiyue.net.HttpUtility;
import com.aisidi.kuaiyue.utils.BitmapTools;
import com.aisidi.kuaiyue.utils.FileManager;

public class PictureBitmapWorkerTask extends MyAsyncTask<String, Void, Bitmap>{

	private LruCache<String, Bitmap> lruCache;
	private String data;
	private final List<WeakReference<ImageView>> viewList = new ArrayList<WeakReference<ImageView>>();
	private Map<String, PictureBitmapWorkerTask> taskMap;
	private WeiboApplication globalContext;
	private ImageMode mode;
	private int maxWidth;
	private CustomerBar customerBar;
	private Bitmap err_bitmap;
	
	public PictureBitmapWorkerTask(Map<String, PictureBitmapWorkerTask> taskMap,ImageView imageView,
			String url,CustomerBar customerBar, ImageMode mode) {
		this.globalContext = WeiboApplication.getInstance();
		this.taskMap = taskMap;
		this.viewList.add(new WeakReference<ImageView>(imageView));
		this.data = url;
		this.lruCache = globalContext.getCache();
		this.mode = mode;
		this.customerBar = customerBar;
		err_bitmap = BitmapFactory.decodeResource(WeiboApplication.getInstance().getResources(),R.drawable.pic_error);
		maxWidth = WeiboApplication.getInstance().getDisplayMetrics().widthPixels-20;
	}
	
	public String getUrl()
	{
		return data;
	}

	public void addView(ImageView view)
	{
		viewList.add(new WeakReference<ImageView>(view));
	}
	@Override
	protected Bitmap doInBackground(String... params) {
		// TODO Auto-generated method stub
		if (!isCancelled()) {
			return getBitmap();
		}
		return null;
	}

	
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (customerBar!=null) {
			customerBar.start();
		}
		
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		if (customerBar!=null) {
			customerBar.stop();
		}
		displayBitmap(result);
		clean();
	}

	@Override
	protected void onCancelled(Bitmap result) {
		// TODO Auto-generated method stub
		displayBitmap(result);
		clean();
		
		super.onCancelled(result);
	}

	private void displayBitmap(Bitmap bitmap)
	{
		for (WeakReference<ImageView> view : viewList) {
			ImageView imageView = view.get();
			if (imageView!=null) {
				if (canDisplay(imageView)) {
					if (bitmap!=null) {
						playImageViewAnimation(imageView, bitmap);
						lruCache.put(data, bitmap);
					}else {
						imageView.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
					}
				}
			}
		}
	}
	
    private boolean canDisplay(ImageView view) {
        if (view != null) {
            PictureBitmapWorkerTask bitmapDownloaderTask = getBitmapDownloaderTask(view);
            if (this == bitmapDownloaderTask) {
                return true;
            }
        }
        return false;
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
    
    private void playImageViewAnimation(final ImageView view, final Bitmap bitmap) {
        final Animation anim_out = AnimationUtils.loadAnimation(view.getContext(), R.anim.timeline_pic_fade_out);
        final Animation anim_in = AnimationUtils.loadAnimation(view.getContext(), R.anim.timeline_pic_fade_in);

        anim_out.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    //clear animation avoid memory leak
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (view.getAnimation() != null && view.getAnimation().hasEnded()) {
                            view.clearAnimation();
                        }
                    }
                });

                if (canDisplay(view)) {
                    view.setImageBitmap(bitmap);
                    view.startAnimation(anim_in);
                }
            }
        });
        if (view.getAnimation() == null || view.getAnimation().hasEnded())
            view.startAnimation(anim_out);
    }
	private Bitmap getBitmap()
	{
		String filePath = FileManager.getFilePathFromURL(data, mode);
		File file = new File(filePath);
		if (!file.exists()) {
			
			HttpUtility.doGetSaveFile(data, filePath);
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int height=0;
        switch (mode) {
		case small:
			height = 100 * options.outHeight/options.outWidth;
			options.inSampleSize = calculateInSampleSize(options, 100, height);
			break;
		case large:
			if (options.outHeight>options.outWidth*3) {
				height = 100 * options.outHeight/options.outWidth;
				options.inSampleSize = calculateInSampleSize(options, 100, height);
			}else {
				height = maxWidth * options.outHeight/options.outWidth;
				options.inSampleSize = calculateInSampleSize(options, maxWidth, height);
			}
			
			break;
		default:
			break;
		}
       
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;

        Bitmap bitmap = null;
        try {
        	bitmap = BitmapFactory.decodeFile(filePath, options);
		} catch (OutOfMemoryError e) {
			System.gc();
			// TODO: handle exception
		}
        if (mode==ImageMode.small) {
        	return bitmap;
		}
        if (bitmap==null) {
        	return err_bitmap;
		}
        
        if (options.outHeight>options.outWidth*3) {
			return bitmap;
		}
		return BitmapTools.scaleBitmap(bitmap, maxWidth, height);
	}
	
	private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (height > reqHeight && reqHeight != 0) {
                inSampleSize = (int) Math.floor((double) height / (double) reqHeight);
            }

            int tmp = 0;

            if (width > reqWidth && reqWidth != 0) {
                tmp = (int) Math.floor((double) width / (double) reqWidth);
            }

            inSampleSize = Math.max(inSampleSize, tmp);

        }
        int roundedSize;
        if (inSampleSize <= 8) {
            roundedSize = 1;
            while (roundedSize < inSampleSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (inSampleSize + 7) / 8 * 8;
        }

        return roundedSize;
    }
	
	 private void clean() {
	        if (taskMap != null && taskMap.get(data) != null) {
	            taskMap.remove(data);
	        }
	        viewList.clear();
	        taskMap = null;
	        lruCache = null;
	        globalContext = null;
	    }
}
