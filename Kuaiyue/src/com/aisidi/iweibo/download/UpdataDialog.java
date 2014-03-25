package com.aisidi.iweibo.download;

import java.io.File;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.aisidi.kuaiyue.R;


public class UpdataDialog extends Dialog{

	private Context mContext;
	private TextView version_name, updata_des,up_size;
	private Button cancel_btn, ok_btn;
	
	private String name, des;
	private String filename;
	private int size;
	
	public UpdataDialog(Context context, String versionName, String des, int size, String filename) {
		super(context,R.style.dialog);
		this.mContext = context;
		
		this.name = versionName;
		this.des = des;
		this.size = size;
		
		this.filename = filename;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.updata_dialog);
		
		version_name = (TextView)findViewById(R.id.version_name);
		updata_des = (TextView)findViewById(R.id.updata_des);
		up_size = (TextView)findViewById(R.id.up_size);
		
		cancel_btn = (Button)findViewById(R.id.updata_cancel_btn);
		ok_btn = (Button)findViewById(R.id.updata_ok_btn);
		
		version_name.setText(" �汾�ţ�"+name);
		
		des = des.replace("\\n", "\n");
		updata_des.setText(des);
		String apkSize = size/1024 +"K";
		up_size.setText(apkSize);
		
		cancel_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		ok_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						int i = 0;
						while (i<100) {
							try {
								Thread.sleep(30);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							i++;
							Message message = handler.obtainMessage();
							message.what = 0;
							message.arg1 = i;
							message.sendToTarget();
						}
					}
				}).start();
				
			}
		});
	}
	
	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				ok_btn.setText("���أ�"+msg.arg1+"%");
				if (msg.arg1==100) {
					installApk(filename);
					dismiss();
				}
				break;

			default:
				break;
			}
		}
		
	};

	private void installApk(String filename)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Log.i("filename", filename);
		Uri uri = Uri.fromFile(new File(filename));
		intent.setDataAndType(uri,
				"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
	
}
