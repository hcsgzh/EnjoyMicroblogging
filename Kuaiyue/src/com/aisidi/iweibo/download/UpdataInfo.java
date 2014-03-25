package com.aisidi.iweibo.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;

import com.mge.tools.http.DownloadFinalCallback;
import com.mge.tools.http.DownloadManager;

public class UpdataInfo implements DownloadFinalCallback{

//	private List<JoyreadBean> list;
	private Context mContext;
	public DownloadManager dm;
	private JoyreadBean bean;
	private SharedPreferences spf;
	private static final String SPFNAME = "down_info";
	private static final String FILENAME = "filename";
	
	public UpdataInfo(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		
		spf = mContext.getSharedPreferences(SPFNAME, 0); 
	}

	public void getVersionInfo()
	{
		new Thread(versionInfo).start();
	}
	

	private void downLoadApk() throws IOException, ParserConfigurationException, SAXException, NameNotFoundException
	{
		
//		InputStream is = mContext.getAssets().open("joyread.xml");
		
		
		Log.i("url", bean.getDownloadURL());
		Log.i("getVersionCode()", getVersionCode()+"");
		
//		File sdCard = Environment.getExternalStorageDirectory();
//		String sd = sdCard.getPath();
//		���·����д���˵�
//		String baseloc = sd + "/" + ".cache/r";
		String baseloc = spf.getString(FILENAME, "");
		
//		���ȡ�õķ������汾�Ŵ��ڰ�װ����
		if (getVersionCode()<bean.getVersionCode()) {
//			������ش�ŵ�apk�İ汾�źͷ�����һ��
			int apkversionCode = getApkFileInfo(mContext, baseloc);
			Log.i("apkversionCode", apkversionCode+"");
			Log.i("bean.getVersionCode()", bean.getVersionCode()+"");
			if (apkversionCode==bean.getVersionCode()) {
				showUpdataDia(baseloc);
				
				return;
			}
			
			dm = DownloadManager.getInstance(mContext);
			dm.setCallback(this);
			
			AlarmManager alarmManager = (AlarmManager) mContext
					.getSystemService(Service.ALARM_SERVICE);
			Intent stintent = new Intent();
			stintent.setAction("com.aisidi.iweibo.DownloadService");
			PendingIntent pendingIntent = PendingIntent.getService(mContext, 0,
					stintent, 0);
			long time = 1000;
			alarmManager.setInexactRepeating(AlarmManager.RTC, 0,time, pendingIntent);
			
			if (dm!=null) {
				Log.i("url", bean.getDownloadURL());
				dm.addTask("����",bean.getDownloadURL(), 777595, DownloadManager.DOWNLOAD);
			}
		}
	}
	
	Runnable versionInfo = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				URL url = new URL("http://shellapp.cn/joyread/joyread.version");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				InputStream is = connection.getInputStream();
				List<JoyreadBean> list = SaxXmlTest.getXmlList(is);
				bean = list.get(0);
				is.close();
				connection.disconnect();
				
				Message message = handler.obtainMessage();
				message.what = 0;
				message.sendToTarget();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				try {
					downLoadApk();
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 1:
				showUpdataDia(msg.obj.toString());
				break;
			default:
				break;
			}
		}
		
	};
	private int getVersionCode() throws NameNotFoundException
	{
	    // ��ȡpackagemanager��ʵ��
	    PackageManager packageManager = mContext.getPackageManager();
	    // getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
	    PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(),0);
	    int version = packInfo.versionCode;
	    return version;
	}


	@Override
	public void DownloadFinal(String url, int size, String filename) {
		// TODO Auto-generated method stub
		Editor editor = spf.edit();
		editor.putString(FILENAME, filename);
		editor.commit();
		
		Message message = handler.obtainMessage();
		message.obj = filename;
		message.what = 1;
		message.sendToTarget();
	}
	
	private void showUpdataDia(String filename)
	{
		UpdataDialog dialog = new UpdataDialog(mContext, bean.getVersionName(), bean.getVersionDescription(), bean.getApkSize(), filename);
		dialog.show();
	}
	
	/** 
	 * ��ȡδ��װ��apk��Ϣ 
	 *  
	 * @param ctx Context
	 * @param apkPath apk·�������Է���SD��
	 * @return 
	 */  
	 public static int getApkFileInfo(Context ctx, String apkPath) 
	 {  
	     System.out.println(apkPath);  
	     File apkFile = new File(apkPath);  
	     if (!apkFile.exists())
	     {  
	         System.out.println("file path is not correct");  
	         return 0;  
	     }  
	     
	     String PATH_PackageParser = "android.content.pm.PackageParser";  
	     String PATH_AssetManager = "android.content.res.AssetManager";  
	     try
	     {  
	         //����õ�pkgParserCls����ʵ����,�в���  
	         Class<?> pkgParserCls = Class.forName(PATH_PackageParser);  
	         Class<?>[] typeArgs = {String.class};  
	         Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);  
	         Object[] valueArgs = {apkPath};  
	         Object pkgParser = pkgParserCt.newInstance(valueArgs);  
	         
	         //��pkgParserCls��õ�parsePackage����  
	         DisplayMetrics metrics = new DisplayMetrics();  
	         metrics.setToDefaults();//���������ʾ�йص�, ���ʹ��Ĭ��  
	         typeArgs = new Class<?>[]{File.class,String.class,  
	         DisplayMetrics.class,int.class};  
	         Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage", typeArgs);  
	         
	         valueArgs=new Object[]{new File(apkPath),apkPath,metrics,0};  
	         
	         //ִ��pkgParser_parsePackageMtd����������  
	         Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);  
	         
	         //�ӷ��صĶ���õ���Ϊ"applicationInfo"���ֶζ���   
	         if (pkgParserPkg==null)
	         {  
	             return 0;  
	         }  
	         Field appInfoFld = pkgParserPkg.getClass().getDeclaredField("applicationInfo");  
	         
	         //�Ӷ���"pkgParserPkg"�õ��ֶ�"appInfoFld"��ֵ  
	         if (appInfoFld.get(pkgParserPkg)==null)
	         {  
	             return 0;  
	         }  
	         ApplicationInfo info = (ApplicationInfo) appInfoFld.get(pkgParserPkg);     
	         
	         //����õ�assetMagCls����ʵ����,�޲�  
	         Class<?> assetMagCls = Class.forName(PATH_AssetManager);     
	         Object assetMag = assetMagCls.newInstance();  
	         //��assetMagCls��õ�addAssetPath����  
	         typeArgs = new Class[1];  
	         typeArgs[0] = String.class;  
	         Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod("addAssetPath", typeArgs);  
	         valueArgs = new Object[1];  
	         valueArgs[0] = apkPath;  
	         //ִ��assetMag_addAssetPathMtd����  
	         assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);  
	         
	         //�õ�Resources����ʵ����,�в���  
	         Resources res = ctx.getResources();  
	         typeArgs = new Class[3];  
	         typeArgs[0] = assetMag.getClass();  
	         typeArgs[1] = res.getDisplayMetrics().getClass();  
	         typeArgs[2] = res.getConfiguration().getClass();  
	         Constructor<Resources> resCt = Resources.class.getConstructor(typeArgs);  
	         valueArgs = new Object[3];  
	         valueArgs[0] = assetMag;  
	         valueArgs[1] = res.getDisplayMetrics();  
	         valueArgs[2] = res.getConfiguration();  
	         //������ص�
	         //�õ�Resource���������кܶ��ô�
	         res = (Resources) resCt.newInstance(valueArgs);  
	         
	         // ��ȡapk�ļ�����Ϣ  
	         PackageManager pm = ctx.getPackageManager();  
	         PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);  
	         if (packageInfo != null)
	         {  
	        	 return packageInfo.versionCode;
	         }  
	     } catch (Exception e)
	     {   
	         e.printStackTrace();  
	     }  
	     return 0;  
	 }
}
