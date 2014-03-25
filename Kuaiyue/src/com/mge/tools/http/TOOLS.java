package com.mge.tools.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

public class TOOLS {
	//此版本根据4.0 wap代理方式修改
//	public static String NET_TYPE_WIFI="WIFI";
//	public static HttpURLConnection httpConnect(String mReqUrl, Context mContext) throws IOException{
//		HttpURLConnection httpURLConn=null;
//		   // 获取当前可用网络信息  
//	    ConnectivityManager connMng = (ConnectivityManager) mContext  
//	                    .getSystemService(Context.CONNECTIVITY_SERVICE);  
//	    NetworkInfo netInf = connMng.getActiveNetworkInfo();  
//	    if (netInf != null)
//	    System.out.println("netInf="+netInf.getTypeName());
//	    System.out.println("mReqUrl="+mReqUrl);
////	    // 如果当前是WIFI连接  
////	    if (netInf != null && NET_TYPE_WIFI.equals(netInf.getTypeName())) {  
////	        httpURLConn = (HttpURLConnection) new URL(mReqUrl)  
////	                        .openConnection();  
////	    }  
////	    // 非WIFI联网  
////	    else {  
////	        String proxyHost = android.net.Proxy.getDefaultHost();  
//////	        MyLog.i(TAG, "proxyHost : " + proxyHost);  
////	        // 代理模式  
////	        if (proxyHost != null) {  
////	            java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(android.net.Proxy.getDefaultHost(),android.net.Proxy.getDefaultPort()));  
////	  
////	            httpURLConn = (HttpURLConnection) new URL(mReqUrl).openConnection(p);  
////	  
////	        }  
////	         直连模式  
////	        else {  
//	            httpURLConn = (HttpURLConnection) new URL(mReqUrl).openConnection();  
////	        }  
////	    }
////	    httpURLConn.setDoInput(true);
////	    httpURLConn.setDoOutput(true);
//	    return httpURLConn;
//	}
	
	public static String NET_TYPE_WIFI="WIFI";
	public static HttpURLConnection httpConnect(String mReqUrl, Context mContext) throws IOException{
		HttpURLConnection httpURLConn=null;
		   // 获取当前可用网络信息  
	    ConnectivityManager connMng = (ConnectivityManager) mContext  
	                    .getSystemService(Context.CONNECTIVITY_SERVICE);  
	    NetworkInfo netInf = connMng.getActiveNetworkInfo();  
//	    if (netInf != null){
//	    System.out.println("netInf="+netInf.getTypeName());
//	    }
//	    System.out.println("mReqUrl="+mReqUrl);
	    // 如果当前是WIFI连接  
	    if (netInf != null && NET_TYPE_WIFI.equals(netInf.getTypeName())) {  
	        httpURLConn = (HttpURLConnection) new URL(mReqUrl)  
	                        .openConnection();  
	    }  
	    // 非WIFI联网  
	    else {  
			int OsVersionInt = Build.VERSION.SDK_INT;
			// 4.0版本以上系统自动代理
			if (OsVersionInt >= 14) {
				httpURLConn = (HttpURLConnection) new URL(mReqUrl)
						.openConnection();
			} else {
				String proxyHost = android.net.Proxy.getDefaultHost();
				// 代理模式
				if (proxyHost != null) {
					java.net.Proxy p = new java.net.Proxy(
							java.net.Proxy.Type.HTTP, new InetSocketAddress(
									android.net.Proxy.getDefaultHost(),
									android.net.Proxy.getDefaultPort()));

					httpURLConn = (HttpURLConnection) new URL(mReqUrl)
							.openConnection(p);

				}

				// 直连模式
				else {
					httpURLConn = (HttpURLConnection) new URL(mReqUrl)
							.openConnection();
				}
			}
	    }
//	    httpURLConn.setDoInput(true);
//	    httpURLConn.setDoOutput(true);
	    return httpURLConn;
	}
}
