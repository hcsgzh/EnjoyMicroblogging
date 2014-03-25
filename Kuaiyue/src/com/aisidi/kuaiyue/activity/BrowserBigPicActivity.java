package com.aisidi.kuaiyue.activity;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aisidi.kuaiyue.MainFragment;
import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.component.CustomerBar;
import com.aisidi.kuaiyue.utils.FileManager;
import com.flood.mycar.drawable.BitmapDownloader;
import com.flood.mycar.drawable.ImageMode;


public class BrowserBigPicActivity extends Activity{

	private WebView webView;
	private String url;
	private TextView pic_size;
	private ImageView back, head, save;
	private String imagePath, path;
	private CustomerBar waiting_bar;
	private BitmapDownloader downloader;
	private MyAsyncTask asyncTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.big_pic);
		
		downloader = new BitmapDownloader();
		
		path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"joyread"+File.separator;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		webView = (WebView) findViewById(R.id.iv);
		waiting_bar = (CustomerBar)findViewById(R.id.waiting_bar);
		pic_size = (TextView)findViewById(R.id.pic_size);
		back = (ImageView)findViewById(R.id.pic_back);
		head = (ImageView)findViewById(R.id.pic_head);
		save = (ImageView)findViewById(R.id.pic_save);
		
		downloader.downPic(head, MainFragment.profile_image_url, ImageMode.small);

        webView.setBackgroundColor(getResources().getColor(R.color.transparent));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setInitialScale(25);

        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        
        url = getIntent().getStringExtra("url");
        
        asyncTask = new MyAsyncTask(url);
		asyncTask.execute();
		
		save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (imagePath!=null) {
					new SaveImageThread(imagePath).start();
				}else {
					Toast.makeText(BrowserBigPicActivity.this, "û��ͼƬ ", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		head.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	class SaveImageThread extends Thread
	{
		private String imageUrl;
		
		SaveImageThread(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				saveImage(imageUrl);
				Message message = handler2.obtainMessage();
				message.what = 1;
				handler2.sendMessage(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Message message = handler2.obtainMessage();
				message.what = 2;
				handler2.sendMessage(message);
			}
		}
		
	}
	
	private void saveImage(String filePath) throws IOException
	{
		String filename = filePath.substring(filePath.lastIndexOf(File.separator)+1,
				filePath.length());
		File file = new File(path+filename);
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		
		FileInputStream fis = new FileInputStream(new File(filePath));
		
		int len = 0;
		byte[] buffer = new byte[1024*8];
		while ((len = fis.read(buffer))!=-1) {
			fileOutputStream.write(buffer, 0, len);
		}
		fileOutputStream.flush();
	}

	private class MyAsyncTask extends AsyncTask<Object, Object, Object>
	{
		private String imageUrl;
		public MyAsyncTask(String imageUrl)
		{
			this.imageUrl = imageUrl;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			waiting_bar.start();
		}

		@Override
		protected String doInBackground(Object... objects) {
			imagePath = loadImageFromUrl(imageUrl);
			return imagePath;
		}

		@Override
		protected void onPostExecute(Object o) {
			if (o==null) {
				Toast.makeText(BrowserBigPicActivity.this, "就有图片", Toast.LENGTH_SHORT).show();
				return;
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(o.toString(), options);
			
			pic_size.setText(options.outWidth+"X"+options.outHeight);
			waiting_bar.stop();
			String str1 = null;
			if (o!=null) {
				str1 = "file://" + o.toString().replace("/mnt/sdcard/", "/sdcard/");
			}
			String str2 = "<html>\n<head>\n     <style>\n          html,body{background:transparent;margin:0;padding:0;}          *{-webkit-tap-highlight-color:rgba(0, 0, 0, 0);}\n     </style>\n     <script type=\"text/javascript\">\n     var imgUrl = \"" + str1 + "\";" + "     var objImage = new Image();\n" + "     var realWidth = 0;\n" + "     var realHeight = 0;\n" + "\n" + "     function onLoad() {\n" + "          objImage.onload = function() {\n" + "               realWidth = objImage.width;\n" + "               realHeight = objImage.height;\n" + "\n" + "               document.gagImg.src = imgUrl;\n" + "               onResize();\n" + "          }\n" + "          objImage.src = imgUrl;\n" + "     }\n" + "\n" + "     function onResize() {\n" + "          var scale = 1;\n" + "          var newWidth = document.gagImg.width;\n" + "          if (realWidth > newWidth) {\n" + "               scale = realWidth / newWidth;\n" + "          } else {\n" + "               scale = newWidth / realWidth;\n" + "          }\n" + "\n" + "          hiddenHeight = Math.ceil(30 * scale);\n" + "          document.getElementById('hiddenBar').style.height = hiddenHeight + \"px\";\n" + "          document.getElementById('hiddenBar').style.marginTop = -hiddenHeight + \"px\";\n" + "     }\n" + "     </script>\n" + "</head>\n" + "<body onload=\"onLoad()\" onresize=\"onResize()\" >\n" + "     <table style=\"width: 100%;height:100%;\">\n" + "          <tr style=\"width: 100%;\">\n" + "               <td valign=\"middle\" align=\"center\" style=\"width: 100%;\">\n" + "                    <div style=\"display:block\">\n" + "                         <img name=\"gagImg\" src=\"\" width=\"100%\" style=\"\" />\n" + "                    </div>\n" + "                    <div id=\"hiddenBar\" style=\"position:absolute; width: 100%; background: transparent;\"></div>\n" + "               </td>\n" + "          </tr>\n" + "     </table>\n" + "</body>\n" + "</html>";
            webView.loadDataWithBaseURL("file:///android_asset/", str2, "text/html", "utf-8", null);
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public File getDataFromUrl(String imgUrl, String filename)
	{
		URL url = null;
		InputStream is = null;
		byte[] data = null;
		
		try {
			url = new URL(imgUrl);
			int length = url.openConnection().getContentLength();
			is = url.openConnection().getInputStream();
			data = readStream(is,length);
			
			return WriteFile(this, filename, data);
		} catch (Exception e) {
			// TODO: handle exception
		}catch (OutOfMemoryError e) {
			// TODO: handle exception
			System.gc();
		}finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	protected String loadImageFromUrl(String imageUrl) {
		String filename = FileManager.getFilePathFromURL(imageUrl, ImageMode.large);
		File file = ReadFile(this, filename);
		if (file == null) {
			file = getDataFromUrl(imageUrl, filename);
		}
		
		if (file==null) {
			return null;
		}
		return file.getAbsolutePath();
	}
//	
//	 public String getFileNameFromUrl(String url) {
//			
//			int index = url.indexOf("//");
//		    String s = url.substring(index + 2);
//		    String oldRelativePath = s.substring(s.indexOf("/")+1);
//		    oldRelativePath = oldRelativePath.substring(s.indexOf("/")+1);
//		    
//		    return oldRelativePath;
//		 }
		public File WriteFile(Context context, String filename, byte[] data) {
			if (data==null) {
				return null;
			}
			
			File file = new File(filename.substring(0,filename.lastIndexOf("/")));
			if (!file.exists()) {
				file.mkdirs();
			}
			file = new File(filename);
			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream(file);
				if (fOut != null) {
					fOut.write(data);
					fOut.flush();
				}
			} catch (Exception e) {
//				e.printStackTrace();
			} finally {
				if (fOut != null) {
					try {
						fOut.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			return file;
		}
	
		public File ReadFile(Context context, String filename) {
			
			File file = new File(filename);
			
			if (file.exists()) {
				return file;
			}else {
				return null;
			}
			
		}
		public byte[] readStream(InputStream inStream, int length) throws Exception{
			 //����ݶ�ȡ��ŵ��ڴ���ȥ
			   ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			   byte[] buffer = new byte[1024];
			   int len = -1;
			   int szie = 0;
			   while( (len=inStream.read(buffer)) != -1){
				   
				szie +=len;
			    outSteam.write(buffer, 0, len);
			    
			    
			    Message message = handler.obtainMessage(szie*100/length);
			    message.sendToTarget();
			    
			   }
			   byte[] data = outSteam.toByteArray();
			   outSteam.reset();
			   outSteam.close();
			   inStream.close();
			   outSteam = null;
			   return data;
		}
		
		Handler handler = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				pic_size.setText(msg.what+"%");
			}
			
		};
		
		Handler handler2 = new Handler()
		{

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					break;
				case 1:
					BrowserBigPicActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"  
			                 + Environment.getExternalStorageDirectory())));
					Toast.makeText(BrowserBigPicActivity.this, "已保存到joyread", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					Toast.makeText(BrowserBigPicActivity.this, "读取错误", Toast.LENGTH_SHORT).show();
					break;
				case 3:
					break;
				default:
					break;
				}
			}
			
		};

	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        asyncTask.cancel(true);
	        webView.loadUrl("about:blank");
	        webView.stopLoading();
	        webView.destroyDrawingCache();
	    }
}
