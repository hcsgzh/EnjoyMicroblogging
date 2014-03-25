package com.aisidi.iweibo.download;

public class JoyreadBean {
	private int VersionCode;
	private String VersionDescription;
	private String DownloadURL;
	private String VersionName;
	private int apkSize;
	
	public String getVersionName() {
		return VersionName;
	}
	public void setVersionName(String versionName) {
		VersionName = versionName;
	}
	public int getApkSize() {
		return apkSize;
	}
	public void setApkSize(int apkSize) {
		this.apkSize = apkSize;
	}
	public int getVersionCode() {
		return VersionCode;
	}
	public void setVersionCode(int versionCode) {
		VersionCode = versionCode;
	}
	public String getVersionDescription() {
		return VersionDescription;
	}
	public void setVersionDescription(String versionDescription) {
		VersionDescription = versionDescription;
	}
	public String getDownloadURL() {
		return DownloadURL;
	}
	public void setDownloadURL(String downloadURL) {
		DownloadURL = downloadURL;
	}

	
	
}
