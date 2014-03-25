package com.aisidi.kuaiyue.database;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.aisidi.kuaiyue.bean.LoginInfoBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class KuaiDataBase {

	private static MyDBhelper dBhelper;
	
	public KuaiDataBase(Context context) {
		if (dBhelper==null) {
			dBhelper = new MyDBhelper(context);
		}
	}
	
	/**
	 * 存储登录用户信息
	 * @param uid
	 * @param name
	 * @param image
	 * @param token
	 * @param expires
	 * @param time_stamp
	 * @param logined
	 * @return
	 */
	public synchronized boolean storeLoginedUserInfo(String uid, String name, String image, 
			String token, String expires, long time_stamp, int logined)
	{
		
		SQLiteDatabase db = dBhelper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(UserTable.USER_ID, uid);
		cv.put(UserTable.USER_NAME, name);
		cv.put(UserTable.USER_IMAGE, image);
		cv.put(UserTable.EXPIRES, expires);
		cv.put(UserTable.TOKEN, token);
		cv.put(UserTable.TIME_STAMP, time_stamp);
		cv.put(UserTable.LOGINED, logined);
		
		long l = db.insert(UserTable.NAME, null, cv);
		
		db.close();
		if (l==-1) {
			return false;
		}
		return true;
	}
	public synchronized boolean isLoginExist(String uid)
	{
		SQLiteDatabase db = dBhelper.getWritableDatabase();
		Cursor cursor = db.query(UserTable.NAME, null, UserTable.USER_ID + "=?", new String[]{uid}, null, null, null);
		
		if (cursor.moveToFirst()&&cursor.getCount()>0) {
			cursor.close();
			db.close();
			return true;
		}else {
			cursor.close();
			db.close();
			return false;
		}
	}
	/**
	 * 查询全部用户登录信息
	 * @return
	 */
	public synchronized ArrayList<LoginInfoBean> selectAllLoginUser()
	{
		ArrayList<LoginInfoBean> arrayList = new ArrayList<LoginInfoBean>();
		
		SQLiteDatabase db = dBhelper.getReadableDatabase();
		Cursor cursor = db.query(UserTable.NAME, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				LoginInfoBean bean = new LoginInfoBean();
				bean.setUid(cursor.getString(cursor.getColumnIndex(UserTable.USER_ID)));
				bean.setName(cursor.getString(cursor.getColumnIndex(UserTable.USER_NAME)));
				bean.setImage(cursor.getString(cursor.getColumnIndex(UserTable.USER_IMAGE)));
				bean.setToken(cursor.getString(cursor.getColumnIndex(UserTable.TOKEN)));
				bean.setExpires(cursor.getString(cursor.getColumnIndex(UserTable.EXPIRES)));
				bean.setTime_stamp(cursor.getLong(cursor.getColumnIndex(UserTable.TIME_STAMP)));
				bean.setLogined(cursor.getInt(cursor.getColumnIndex(UserTable.LOGINED))==1);
				
				arrayList.add(bean);
			} while (cursor.moveToNext());
			
		}
		cursor.close();
		db.close();
		return arrayList;
	}
	
	public synchronized int deleteAdmin(String uid)
	{
		SQLiteDatabase db = dBhelper.getReadableDatabase();
		int result = db.delete(UserTable.NAME, UserTable.USER_ID+"=?", new String[]{uid});
		db.close();
		return result;
	}
	/**
	 * 取当前登录用户的验证信息
	 * @return
	 */
	public synchronized LoginInfoBean getLoginedUserInfo()
	{
		LoginInfoBean bean = new LoginInfoBean();
		
		SQLiteDatabase db = dBhelper.getReadableDatabase();
		Cursor cursor = db.query(UserTable.NAME, null, UserTable.LOGINED +"=?", new String[]{"1"}, null, null, null);
		
		if (cursor.moveToFirst()) {
			bean.setUid(cursor.getString(cursor.getColumnIndex(UserTable.USER_ID)));
			bean.setName(cursor.getString(cursor.getColumnIndex(UserTable.USER_NAME)));
			bean.setImage(cursor.getString(cursor.getColumnIndex(UserTable.USER_IMAGE)));
			bean.setToken(cursor.getString(cursor.getColumnIndex(UserTable.TOKEN)));
			bean.setExpires(cursor.getString(cursor.getColumnIndex(UserTable.EXPIRES)));
			bean.setTime_stamp(cursor.getLong(cursor.getColumnIndex(UserTable.TIME_STAMP)));
			bean.setLogined(cursor.getInt(cursor.getColumnIndex(UserTable.LOGINED))==1);
		}
		cursor.close();
		db.close();
		return bean;
	}
	/**
	 * 设置用户登录状态
	 * @param uid
	 * @param login
	 */
	public synchronized boolean makeAdminLogin(String uid, boolean login)
	{
		SQLiteDatabase db = dBhelper.getReadableDatabase();
		ContentValues cv = new ContentValues();
		if (login) {
			cv.put(UserTable.LOGINED, 1);
		}else {
			cv.put(UserTable.LOGINED, 0);
		}
		long result = db.update(UserTable.NAME, cv, UserTable.USER_ID + "=?", new String[]{uid});
		db.close();
		
		if (result!=-1) {
			return true;
		}
		return false;
	}
	public synchronized boolean updateLoginInfo(String uid, String name, String image, 
			String token, String expires, long time_stamp, int logined)
	{
		SQLiteDatabase db = dBhelper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(UserTable.USER_ID, uid);
		cv.put(UserTable.USER_NAME, name);
		cv.put(UserTable.USER_IMAGE, image);
		cv.put(UserTable.EXPIRES, expires);
		cv.put(UserTable.TOKEN, token);
		cv.put(UserTable.TIME_STAMP, time_stamp);
		cv.put(UserTable.LOGINED, logined);
		
		long result = db.update(UserTable.NAME, cv, UserTable.USER_ID + "=?", new String[]{uid});
		db.close();
		
		if (result<=0) {
			return false;
		}
		
		return true;
	}
	public synchronized boolean updateLoginInfo(String uid, String name, String image)
	{
		SQLiteDatabase db = dBhelper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(UserTable.USER_NAME, name);
		cv.put(UserTable.USER_IMAGE, image);
		
		long result = db.update(UserTable.NAME, cv, UserTable.USER_ID + "=?", new String[]{uid});
		db.close();
		
		if (result<=0) {
			return false;
		}
		
		return true;
	}
	
	public synchronized String[] queryTrends(String adminId)
	{
		String[] names;
		SQLiteDatabase sql = dBhelper.getWritableDatabase();
		Cursor cursor = sql.query(TrendsTable.NAME, new String[]{TrendsTable.TRENDS_NAME},
				TrendsTable.ADMIN_ID+"=?", new String[]{adminId}, null, null, null);
			
		names = new String[cursor.getCount()];
		int i = 0;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			names[i++] = cursor.getString(0);
			cursor.moveToNext();
		}
		cursor.close();
		sql.close();
		return names;
	}
	
	public synchronized void deleteAllTrends(String adminId)
	{
		SQLiteDatabase sql = dBhelper.getWritableDatabase();
			
		sql.delete(TrendsTable.NAME, TrendsTable.ADMIN_ID+"=?", new String[]{adminId});
		sql.close();
	}
	public synchronized void deleteAllFriendsName(String adminId)
	{
		SQLiteDatabase sql = dBhelper.getWritableDatabase();
			
		sql.delete(FriendsTable.NAME, FriendsTable.ADMIN_ID+"=?", new String[]{adminId});
		sql.close();
	}
	
	public synchronized void insertFriendsName(ArrayList<String> names, String adminId) throws UnsupportedEncodingException
	{
			SQLiteDatabase sql = dBhelper.getWritableDatabase();
			
			sql.beginTransaction();
			for (int i = 0; i < names.size(); i++) {
				ContentValues cv = new ContentValues();
				cv.put(FriendsTable.FRIENDS_NAME, new String(names.get(i).toString().getBytes(),"UTF-8"));
				cv.put(FriendsTable.ADMIN_ID, adminId);
				sql.insert(FriendsTable.NAME, null, cv);
			}
			sql.setTransactionSuccessful();
			sql.endTransaction();
			sql.close();
	}
	
	public synchronized void insertTrends(String[] names, String adminId)
	{
		SQLiteDatabase sql = dBhelper.getWritableDatabase();
			
		sql.beginTransaction();
		for (int i = 0; i < names.length; i++) {
			ContentValues cv = new ContentValues();
			cv.put(TrendsTable.ADMIN_ID, adminId);
			cv.put(TrendsTable.TRENDS_NAME, names[i]);
			sql.insert(TrendsTable.NAME, null, cv);
		}
		sql.setTransactionSuccessful();
		sql.endTransaction();
		
		sql.close();
	}
	
	public synchronized String[] queryUserName(String name,String adminId)
	{
		String[] names;
		SQLiteDatabase sql = dBhelper.getWritableDatabase();
		String sqlStr = "select "+FriendsTable.FRIENDS_NAME+" from " + FriendsTable.NAME + 
				" where "+FriendsTable.ADMIN_ID+"="+adminId +" AND "+FriendsTable.FRIENDS_NAME+" like '%"+name+"%';";
		Cursor cursor = sql.rawQuery(sqlStr, null);
		
		names = new String[cursor.getCount()];
		int i = 0;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			names[i++] = cursor.getString(0);
			cursor.moveToNext();
		}
		cursor.close();
		sql.close();
		return names;
	}
	public synchronized String[] queryUserName(String adminId)
	{
		String[] names;
		SQLiteDatabase sql = dBhelper.getWritableDatabase();
		Cursor cursor = sql.query(FriendsTable.FRIENDS_NAME,
				new String[]{FriendsTable.FRIENDS_NAME}, FriendsTable.ADMIN_ID+"=?", new String[]{adminId}, null, null, null);
		
		names = new String[cursor.getCount()];
//		Log.i("cursor.getCount()", cursor.getCount()+"");
		int i = 0;
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			names[i++] = cursor.getString(0);
			cursor.moveToNext();
		}
		cursor.close();
		sql.close();
		return names;
	}
	
	public synchronized long getLonginTimeStamp(String adminId)
	{
		long timeStamp = 0;
		SQLiteDatabase sql = dBhelper.getWritableDatabase();
		
		Cursor cursor = sql.query(UserTable.NAME, null, 
				UserTable.USER_ID+"=?", new String[]{adminId}, null, null, null);
		
		if (cursor.getCount()==0) {
			return 0;
		}
		cursor.moveToFirst();
		timeStamp = cursor.getLong(cursor.getColumnIndex(UserTable.TIME_STAMP));
		cursor.close();
		sql.close();
		return timeStamp;
	}
	
	public synchronized void updateAdminTimeStamp(String adminId, long timeStamp)
	{
		SQLiteDatabase sql = dBhelper.getWritableDatabase();
			
		ContentValues cv = new ContentValues();
		cv.put(UserTable.USER_ID, adminId);
		cv.put(UserTable.TIME_STAMP, timeStamp);
			
		sql.update(UserTable.NAME, cv, UserTable.USER_ID+"=?", new String[]{adminId});
		sql.close();
	}
	
	private class MyDBhelper extends SQLiteOpenHelper
	{
		private static final String name = "ikan.db";
		private static final int version = 2;
		
		public MyDBhelper(Context context) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			String createUserTable = "create table "+UserTable.NAME + " (" +UserTable.ID +" integer primary key autoincrement,"+
					UserTable.USER_ID + " text," + UserTable.USER_IMAGE + " text," + UserTable.USER_NAME + " text," + 
					UserTable.TOKEN + " text,"+
					UserTable.EXPIRES + " text," + UserTable.TIME_STAMP + " INT8," + UserTable.LOGINED + " integer)";
			
			String createTrendsTable = "create table " + TrendsTable.NAME + "("+TrendsTable.ID + " integer primary key autoincrement,"+
					TrendsTable.ADMIN_ID + " text," + TrendsTable.TRENDS_NAME + " text)";
			String createFriendsTable = "create table " + FriendsTable.NAME + "(" + FriendsTable.ID + " integer primary key autoincrement,"+
					FriendsTable.ADMIN_ID + " text," + FriendsTable.FRIENDS_NAME + " text)";
			
			db.execSQL(createUserTable);
			db.execSQL(createTrendsTable);
			db.execSQL(createFriendsTable);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table if exists " + UserTable.NAME);
			db.execSQL("drop table if exists " + TrendsTable.NAME);
			db.execSQL("drop table if exists " + FriendsTable.NAME);
			
			onCreate(db);
		}
		
	}
}
