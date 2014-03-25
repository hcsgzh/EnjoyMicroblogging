package com.aisidi.kuaiyue.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.flood.mycar.drawable.ImageMode;




import android.os.Environment;
import android.util.Log;

public class FileManager {
	private static final String PICROOT = "/.kuaiyue";
	private static final String SMALLPATH = "small";
	private static final String BIGPATH = "large";
	private static final String ORIGINALPATH = "original";
	private static final int DAYS = 3;
	private static final String picStore = "/kuaiyue";
	
	public static File tempFile;

    public static String getSdCardPath() {
        if (isExternalStorageMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return "";
        }
    }
    
    /**
     * 根据网址去截取文件目录和名称
     * @param url
     * @param mode
     * @return
     */
    public static String getFilePathFromURL(String url, ImageMode mode)
    {
    	if (!isExternalStorageMounted()) {
			return null;
		}
    	int index = url.indexOf("//");
    	String s = url.substring(index+2);
    	String oldRelativePath = s.substring(s.indexOf("/"));
    	
    	String newRelativePath = "";
    	
    	switch (mode) {
		case small:
			newRelativePath = PICROOT+File.separator + SMALLPATH + oldRelativePath;
			break;
		case large:
			newRelativePath = PICROOT+File.separator + BIGPATH + oldRelativePath;
			break;
		case original:
			newRelativePath = PICROOT+File.separator + ORIGINALPATH + oldRelativePath;
			break;
		default:
			newRelativePath = PICROOT+File.separator + SMALLPATH + oldRelativePath;
			break;
		}
    	
    	return getSdCardPath() + newRelativePath;
    }
	
    /**
     * 创建�?��新的文件
     * @param absolutPath
     * @return
     */
	public static File creatNewFileInSDCard(String absolutPath)
	{
		if (!isExternalStorageMounted()) {
			Log.e("creatNewFileInSDCard", "sdcard unavailiable");
			
			return null;
		}
		
		File file = new File(absolutPath);
		if (file.exists()) {
			return file;
		}else {
			File dir = file.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			try {
				if (file.createNewFile()) {
					return file;
				}
			} catch (Exception e) {
				// TODO: handle exception
				return null;
			}
		}
		
		return null;
	}
	
	/**
	 * 判断sdcard是否存在可用
	 * @return
	 */
    public static boolean isExternalStorageMounted() {

        boolean canRead = Environment.getExternalStorageDirectory().canRead();
        boolean onlyRead = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED_READ_ONLY);
        boolean unMounted = Environment.getExternalStorageState().equals(
                Environment.MEDIA_UNMOUNTED);

        return !(!canRead || onlyRead || unMounted);
    }
    
    public static void deleteCachePic()
    {
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handleDir(new File(getSdCardPath() + File.separator + PICROOT));
			}
		}).start();
    	
    }
    
    /**
     * 清除指定目录下的全部文件，下面有修改时间判断
     * @param file
     */
    public static void handleDir(File file) {
        File[] fileArray = file.listFiles();
        if (fileArray != null && fileArray.length != 0) {
            for (File fileSI : fileArray) {
                if (fileSI.isDirectory()) {
                    handleDir(fileSI);
                }

                if (fileSI.isFile()) {
                    handleFile(fileSI);
                }
            }
        }
    }

    /**
     * 删除文件，加入时间限�?
     * @param file
     */
    private static void handleFile(File file) {
        long time = file.lastModified();
        long calcMills = System.currentTimeMillis() - time;
        long day = calcMills/1000/3600/24;
        if (day > DAYS) {
            file.delete();
        }
    }
    public static void setFilePath()
	{
		String path = getSdCardPath();
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		String iconStr = System.currentTimeMillis()+ ".png";
		tempFile = new File(path + "/" +iconStr);
	}
    /**
     * 保存图片文件
     * @param src 原文件地址
     * @return
     */
    public static boolean storeImage(String src)
    {
    	String filename = src.substring(src.lastIndexOf(File.separator)+1,
    			src.length());
    	File file = new File(getSdCardPath()+picStore);
    	if (!file.exists()) {
    		file.mkdirs();
		}
    	file = new File(getSdCardPath()+picStore+File.separator+filename);
    	try {
    		FileOutputStream fileOutputStream = new FileOutputStream(file);
        	FileInputStream fis = new FileInputStream(new File(src));
        	int len = 0;
    		byte[] buffer = new byte[1024*8];
    		while ((len = fis.read(buffer))!=-1) {
    			fileOutputStream.write(buffer, 0, len);
    		}
    		fileOutputStream.flush();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
    	
    	return true;
    }
}
