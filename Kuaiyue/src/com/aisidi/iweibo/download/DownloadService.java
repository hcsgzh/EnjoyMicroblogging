package com.aisidi.iweibo.download;

import java.util.Vector;

import com.mge.tools.http.DownloadManager;
import com.mge.tools.http.Task;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.widget.RemoteViews;

public class DownloadService extends Service {
	public DownloadManager manager;
	public static Context mcontext;
	public static NotificationManager nm = null;

	@Override
	public void onCreate() {
		super.onCreate();
		mcontext = this;
		manager = DownloadManager.getInstance(mcontext);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// System.out.println("IS ShellDownloadService onCreate");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// System.out.println("IS ShellDownloadService onStart");
		notificationTasks.clear();
		for (int i = 0; i < manager.tasks.size(); i++) {
			Task task = (Task) manager.tasks.elementAt(i);
			if (task.thread != null) {
				if (task.thread.filelen != 0) {
					long part = 100L * task.thread.downsize
							/ task.thread.filelen;
					// System.out.println(" FS="+task.thread.filelen+" DS="+task.thread.downsize+" PR="+part);
					task.part = (int) part;
				}
				notificationTasks.add(task);
			}
		}
//		for (int i = 0; i < notificationTasks.size(); i++) {
//			Task task = (Task) notificationTasks.elementAt(i);
//			if (task.notification == null) {
//				task.notification = showNotification(R.drawable.downicon,
//						getResources().getString(R.string.notification),
//						task.appName,
//						getResources().getString(R.string.notification),
//						task.part);
//			}
//			int part = task.part;
//			task.notification.contentView.setTextViewText(R.id.down_tv, part
//					+ "%");
//			task.notification.contentView.setProgressBar(R.id.pb, 100, part,
//					false);
//			// Notification notification =showNotification(R.drawable.downicon,
//			// "֪ͨ", task.appName, "֪ͨ",
//			// task.part);
//			nm.notify(task.nid, task.notification);
//		}
	}

	private Vector<Task> notificationTasks = new Vector<Task>();
	private static int notification_id = 19172448;
	public static void cancel(int nid)
	{
		nm.cancel(nid);
	}
//	public static Notification showNotification(int icon, String tickertext,
//			String title, String content, int part) {
//		Notification notification;
//		notification = new Notification(icon, title, System.currentTimeMillis());
//		notification.contentView = new RemoteViews(mcontext.getPackageName(),
//				R.layout.notification);
//		notification.contentView.setProgressBar(R.id.pb, 100, 0, false);
//		notification.contentView.setTextViewText(R.id.down_tv, part + "%");
//		notification.contentView.setTextViewText(R.id.appname, title
//				+ mcontext.getResources().getString(R.string.downloading));
//
//		Intent notificationIntent = new Intent(mcontext, MainActivity.class);
//		Bundle bundle = new Bundle();
//		bundle.putInt("pageindex", 3);
//		notificationIntent.putExtra("key", bundle);
//
//		PendingIntent contentIntent = PendingIntent.getActivity(mcontext, 0,
//				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		notification.contentIntent = contentIntent;
//		notification.contentView.setProgressBar(R.id.pb, 100, part, false);
//		return notification;
//	}
//
//	private static Notification notification;

	public IBinder onBind(Intent intent) {
		System.out.println("IS ShellDownloadService onBind!");
		// TODO Auto-generated method stub
		return null;
	}
}
