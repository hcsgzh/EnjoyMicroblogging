package com.aisidi.kuaiyue.net;

import com.aisidi.kuaiyue.activity.SetActivity;
import com.flood.mycar.drawable.ImageMode;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetTools {

	public static boolean isConnected(Context context)
	{
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		
		return networkInfo!=null && networkInfo.isConnected();
	}
	
	public static boolean isWifi(Context context)
	{
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo!=null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 判断是否移动流量，没有区分具�?G�?G
	 * @param context
	 * @return
	 */
	public static boolean isGprs(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }
	
	public static ImageMode getImageMode(Context context)
	{
		if (SetActivity.imageSize(context)==0) {
			switch (getNetType(context)) {
			case NONE:
				
				return ImageMode.none;
			case WIFI:
				
				return ImageMode.large;
			case G2:
				
				return ImageMode.small;
			case G3:
				
				return ImageMode.small;
			default:
				return ImageMode.small;
			}
		}else if (SetActivity.imageSize(context)==1) {
			return ImageMode.large;
		}else if (SetActivity.imageSize(context)==2) {
			return ImageMode.small;
		}else {
			return ImageMode.none;
		}
		
	}
	
	public static NetType getNetType(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);//获取系统的连接服�?
		
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();//获取网络的连接情�?
		
		if (activeNetInfo==null || !activeNetInfo.isConnected()) {
			return NetType.NONE;
		}
		
		if(activeNetInfo.getType()==ConnectivityManager.TYPE_WIFI){
			
			return NetType.WIFI;
			
		}else if (activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) {
			int subType = activeNetInfo.getSubtype();
			if(subType==TelephonyManager.NETWORK_TYPE_1xRTT||subType==TelephonyManager.NETWORK_TYPE_CDMA
					||subType==TelephonyManager.NETWORK_TYPE_EDGE||subType==TelephonyManager.NETWORK_TYPE_GPRS)
			{
				return NetType.G2;
				
			}else if (subType==TelephonyManager.NETWORK_TYPE_EVDO_0||subType==TelephonyManager.NETWORK_TYPE_EVDO_A
					||subType==TelephonyManager.NETWORK_TYPE_HSDPA||subType==TelephonyManager.NETWORK_TYPE_HSPA
					||subType==TelephonyManager.NETWORK_TYPE_HSUPA||subType==TelephonyManager.NETWORK_TYPE_UMTS||subType==15) 
			{
				return NetType.G3;
			}
			
		}
		
		return NetType.G2;
	}
}
