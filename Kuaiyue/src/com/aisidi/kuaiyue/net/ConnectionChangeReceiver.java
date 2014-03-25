package com.aisidi.kuaiyue.net;




import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.aisidi.kuaiyue.R;
import com.aisidi.kuaiyue.WeiboApplication;


public class ConnectionChangeReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
			if (!NetTools.isConnected(context)) {
				Toast.makeText(context, context.getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
			}
			
			WeiboApplication.imageMode = NetTools.getImageMode(context);
		}
	}

	
}
