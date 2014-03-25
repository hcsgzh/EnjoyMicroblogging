package com.mge.tools.http;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Enumeration;
import java.util.Vector;

import android.content.Context;
import android.os.Environment;

import com.aisidi.iweibo.download.DownloadService;

public class DownloadManager implements Runnable {
	private static final int chunksize = 50 * 1024;
	public static String baseloc;
	public static final byte DOWNLOAD = 1;
	public static final byte UPLOAD = 2;
	public Context context;
	public Vector tasks;
	private DownloadFinalCallback callback;

	private DownloadManager(Context context) {
		// System.out.println("DownloadManager init");
		this.context = context;
		tasks = new Vector();
		// 只有创建UPDOWNManager实例时才从loadTasks其他时候只是saveTasks
		loadTasks();
		Thread t = new Thread(this);
		t.start();
		File sdCard = Environment.getExternalStorageDirectory();
		String sd = sdCard.getPath();
		baseloc = sd + "/" + ".cache/";
		;
		File file = new File(baseloc);
		if (!file.exists()) {
			file.mkdir();
		}
		
	}

	public void setCallback(DownloadFinalCallback callback) {
		this.callback = callback;
	}

	public void showToast(String desc) {
		// if (callback != null) {
		// callback.showToast(desc);
		// }
	}
	public boolean TemporaryPause;
	private int maxThread = 1;// 同时只能有一个任务进行

	public void run() {
		while (true) {
			if (TemporaryPause) {// 当TemporaryPause打开时不做任何事情
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ex) {
				}
			} else {
				// add2Manager code
				Enumeration enumer = tasks.elements();
				int startThread = 0;
				enumer = tasks.elements();
				while (enumer.hasMoreElements()) {
					Task item = (Task) enumer.nextElement();
					if (item.thread != null) {
						startThread++;
					}
				}
				// 计算需要开始的线程数
				int sThread = maxThread - startThread;
				// ////System.out.println("sThread=" + sThread);
				// 开始任务
				enumer = tasks.elements();
				while (enumer.hasMoreElements()) {
					Task item = (Task) enumer.nextElement();
					if (!item.TaskState) {
						if (item.thread == null && item.DOWNLOADFINAL == false) { // 此任务未完成
							// System.out.println("有任务未完成");
							if (sThread != 0) {
								if (item.TaskType == UPLOAD) {
									item.thread = new HttpWorkThreadManager(
											item.downloading_url, chunksize,
											item.filename, item.filelen, 0);
									item.thread.context = this.context;
								} else {
									item.thread = new HttpWorkThreadManager(
											item.downloading_url, chunksize,
											item.filename, item.filelen);
									item.thread.context = this.context;
								}
								if (item.TYPES == null) {
									int[] data = item.thread.CreateTaskData();
									item.TYPES = new byte[data.length / 2];
									item.thread.setTYPES(item.TYPES);
								} else {
									item.thread.setTYPES(item.TYPES);
								}
								item.thread.start();
								sThread--;
							}
						}
					}
				}
				// 完成任务清理
				enumer = tasks.elements();
				int index = 0;
				while (enumer.hasMoreElements()) {
					Task item = (Task) enumer.nextElement();
					if (item.thread != null && item.DOWNLOADFINAL == false) {
						// item.DOWNLOADFINAL = item.thread.DOWNLOADFINAL;
						for (int i = 0; i < item.thread.TYPES.length; i++) {
							if (item.thread.TYPES[i] != 1) {
								item.TYPES[i] = item.thread.TYPES[i];
							}
						}
					}
					if (item.thread != null) {
						if (item.thread.downsize == item.filelen) {
							item.DOWNLOADFINAL = true;
							item.thread = null;
							if (callback != null) {
								if (item != null) {
									callback.DownloadFinal(
											item.downloading_url,
											item.downsize, baseloc
													+ item.filename);
									DownloadService.cancel(item.nid);
								}
							}
						}
					}
					index++;
				}
				// 保存状态
				saveTasks();
				try {
					Thread.sleep(500);
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	private int getDataLenFromUrl(String url) {
		int len = 0;
		HttpURLConnection hc = null;
		InputStream is = null;
		try {
			hc = TOOLS.httpConnect(url, context);
			if (hc == null) {
				len = 0;
			}
			int ResponseCode = hc.getResponseCode();
			// System.out.println("hc.getResponseCode() -- " + ResponseCode);
			if (ResponseCode == 200) {
				int getlen = hc.getContentLength();
				// System.out.println("getlen=" + getlen);
				len = getlen;
			}
		} catch (SecurityException se) {
			se.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex1) {
				}
			}
			if (hc != null) {
				try {
					hc.disconnect();
				} catch (Exception ex) {
				}
			}
		}
		return len;
	}

	private byte[] getDataFromUrl(String url) {
		byte[] data = null;
		HttpURLConnection hc = null;
		InputStream is = null;
		try {
			hc = TOOLS.httpConnect(url, context);
			int ResponseCode = hc.getResponseCode();
			// System.out.println("hc.getResponseCode() -- " + ResponseCode);
			if (ResponseCode == 200) {
				int getlen = hc.getContentLength();
				if (ResponseCode == HttpURLConnection.HTTP_OK) {
					data = new byte[getlen];
					is = hc.getInputStream();
					int readlen = 0;
					int len;
					byte[] buffer = new byte[1024];
					while ((len = is.read(buffer, 0, buffer.length)) != -1) {
						System.arraycopy(buffer, 0, data, readlen, len);
						readlen += len;
					}
				}
			}
		} catch (SecurityException se) {
			se.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex1) {
				}
			}
			if (hc != null) {
				try {
					hc.disconnect();
				} catch (Exception ex) {
				}
			}
		}
		return data;
	}

	public void addTask(final String name,final String downloading_url,int filelen,final byte TaskType) {
//		new Thread() {
//			public void run() {
//				int filelen = getDataLenFromUrl(downloading_url);
				String realurl = downloading_url;
//				if (filelen == 0) {
//					showToast("网络异常无法完成下载。");
//				} else {
//					showToast(downloading_url + "开始下载");
					Task task = new Task(name,realurl, filelen);
					task.TaskType = TaskType;
					task.nid = tasks.size() + 1 + notification_id;
					tasks.addElement(task);
//				}
//			}
//		}.start();
	}

	private static int notification_id = 19172448;

	public int getTaskPosition(String url) {
		int index = 0;
		Enumeration enumer = tasks.elements();
		enumer = tasks.elements();
		while (enumer.hasMoreElements()) {
			Task item = (Task) enumer.nextElement();
			if (item.downloading_url.equals(url)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public final int readInt(InputStream in) throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		int ch3 = in.read();
		int ch4 = in.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0) {
			throw new EOFException();
		}
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));

	}

	public final void writeInt(OutputStream out, int v) throws IOException {
		out.write((v >>> 24) & 0xFF);
		out.write((v >>> 16) & 0xFF);
		out.write((v >>> 8) & 0xFF);
		out.write((v >>> 0) & 0xFF);
	}

	private static String rsname = "tasks.bin";

	private void saveTasks() {
		CMessage msg = new CMessage();
		msg.putInt("len", tasks.size());
		for (int i = 0; i < tasks.size(); i++) {
			Task task = (Task) tasks.elementAt(i);
			if (task.thread != null) {
				task.downsize = task.thread.downsize;
			}
			byte[] data = task.getBytes();
			msg.putByteArray("" + i, data);
		}
		CMessage.setSettings(context, rsname, msg);
	}

	private void loadTasks() {
		CMessage msg = CMessage.getSettings(context, rsname);
		msg.print();
		int len = msg.getInt("len");
		for (int i = 0; i < len; i++) {
			byte[] taskdata = msg.getByteArray("" + i);
			Task task = new Task(taskdata);
			tasks.addElement(task);
			// System.out.println(task);
		}
	}

	public void pauseTask(int index) {
		Task item = (Task) tasks.elementAt(index);
		item.TaskState = true;
		if (item.thread != null) {
			item.thread.DOWNLOADFINAL = true;
			item.thread = null;
		}

	}

	// public void pauseDeleteTask(int index) {
	// Task item = (Task) tasks.elementAt(index);
	// item.TaskState = true;
	// if (item.thread != null) {
	// item.thread.DOWNLOADFINAL = true;
	// }
	// item.thread = null;
	// tasks.remove(index);
	// File file = new File(baseloc + item.filename);
	// if (file.exists()) {
	// //System.out.println("执行文件删除。");
	// file.delete();
	// } else {
	// //System.out.println("执行文件删除出错。");
	// }
	//
	// }

	public void startTask(int index) {
		Task item = (Task) tasks.elementAt(index);
		item.TaskState = false;
	}

	private static DownloadManager instance;

	public static DownloadManager getInstance(Context context) {
		if (instance == null) {
			instance = new DownloadManager(context);
		}
		return instance;
	}
}
