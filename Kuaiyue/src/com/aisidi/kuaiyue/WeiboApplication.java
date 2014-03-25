package com.aisidi.kuaiyue;


import com.aisidi.kuaiyue.database.KuaiDataBase;
import com.aisidi.kuaiyue.net.ConnectionChangeReceiver;
import com.aisidi.kuaiyue.net.NetTools;
import com.flood.mycar.drawable.ImageMode;

import android.app.Activity;
import android.app.Application;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Toast;

public class WeiboApplication extends Application{

	private static WeiboApplication mInstance = null;
  //image memory cache
    private LruCache<String, Bitmap> avatarCache = null;
    private ConnectionChangeReceiver changeReceiver;
    private Activity activity;
    private DisplayMetrics displayMetrics = null;
    private KuaiDataBase dataBase;
    
    public static ImageMode imageMode;
    
    @Override
    public void onCreate() {
	    super.onCreate();
		mInstance = this;
		checkNet();
		imageMode = NetTools.getImageMode(this);
		registerNetWorkReceiver();
	}
    
    public KuaiDataBase getDBInstance()
    {
    	if (dataBase==null) {
    		dataBase = new KuaiDataBase(this);
		}
    	return dataBase;
    }
    
    public DisplayMetrics getDisplayMetrics() {
        if (displayMetrics != null) {
            return displayMetrics;
        } else {
            Activity a = getActivity();
            if (a != null) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);
                this.displayMetrics = metrics;
                return metrics;
            } else {
                //default screen is 800x480
                DisplayMetrics metrics = new DisplayMetrics();
                metrics.widthPixels = 480;
                metrics.heightPixels = 800;
                return metrics;
            }
        }
    }
    
    public Activity getActivity() {
		return activity;
	}


	public void setActivity(Activity activity) {
		this.activity = activity;
	}


	public static WeiboApplication getInstance() {
		return mInstance;
	}
    
    private void checkNet()
	{
		if (!NetTools.isConnected(this)) {
			Toast.makeText(this, getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
		}
	}
    
    private void registerNetWorkReceiver()
	{
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		changeReceiver = new ConnectionChangeReceiver();
		this.registerReceiver(changeReceiver, filter);
	}
	private void unRegisterNetWorkReceiver()
	{
		this.unregisterReceiver(changeReceiver);
	}
	
	public synchronized LruCache<String, Bitmap> getCache() {
        if (avatarCache == null) {
            buildCache();
        }
        return avatarCache;
    }
	
	private void buildCache() {

        int cacheSize = 1024 * 1024 * 8;

        avatarCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {

                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }
	
    @Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		unRegisterNetWorkReceiver();
		super.onTerminate();
		
	}
}
