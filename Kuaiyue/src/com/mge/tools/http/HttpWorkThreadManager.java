package com.mge.tools.http;

import java.util.*;
import java.io.*;
import android.content.Context;

public class HttpWorkThreadManager extends Thread {

    public static final boolean useProxy = false;
    public boolean DOWNLOADFINAL = false;
    private static final byte DOWNLOAD = 1;
    private static final byte UPLOAD = 2;
    public byte TaskType;
    private String loc;
    public String filename;
    private int chunkSize = 200 * 1024;
    private int maxThread = 3;
    private Vector vec = new Vector(); //容器
    public int downsize = 0;
    public int lastDownsize = 0;
    public int filelen;
    public Context context;
    private static byte NULL[] = new byte[4096];
    public byte[] TYPES;
    //url文件服务器地址 chunkSize下载传块大小 filename服务器的文件名和下载到本地文件名 filelen文件总大小
    public HttpWorkThreadManager(String url, int chunkSize,
                                 String filename, int filelen) {
        this.loc = url;
        if (chunkSize != 0) {
            this.chunkSize = chunkSize;
        }
        this.filename = filename;
        this.filelen = filelen;
        TaskType = DOWNLOAD;
    }

    //url文件服务器地址 chunkSize上传块大小 filename本地文件名和上传到服务器的文件名
    public HttpWorkThreadManager(String url, int chunkSize, String filename,
                                 int filelen, int a) {
        this.loc = url;
        this.filename = filename;
        if (chunkSize != 0) {
            this.chunkSize = chunkSize;
        }
        this.filelen = filelen;
        TaskType = UPLOAD;
//        if (filelen == 0) {
//            try {
//                String fileloc = CGame.baseloc + filename;
//                fc = (FileConnection) Connector.open(
//                        fileloc,
//                        Connector.READ_WRITE);
//                filelen = (int) fc.fileSize();
//                System.out.println("filelen=" + filelen);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
    }

    public void CreateFc() {
        if (TaskType == DOWNLOAD) {
        	//假如文件存在则先删除
            String tloc = DownloadManager.baseloc + filename;
        	File file=new File(tloc);
        	if(file.exists())
        	{
        		file.delete();
        	}
            boolean malloc = true;
            for (int i = 0; i < TYPES.length; i++) {
                if (TYPES[i] == 2) {
                    malloc = false;
                }
            }
            try {
                String fileloc = DownloadManager.baseloc + filename;
                RandomAccessFile fc = new RandomAccessFile(fileloc, "rw");
                if (fc.length() == 0) {//文件不存在重新分配空间
                  malloc = true;
                  for (int i = 0; i < TYPES.length; i++) {
                      TYPES[i] = 0;
                  }
                }
                if (malloc) {//分配空间
                    for (int i = 0; i < filelen / 4096; i++) {
                        fc.write(NULL);
                    }
                    fc.write(NULL, 0, filelen % 4096);
                    fc.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
        	//上传先不实现
        }
    }

    public void setTYPES(byte[] data) {
        TYPES = new byte[data.length];
        System.arraycopy(data, 0, TYPES, 0, data.length);
    }

    public void setThreadNum(int num) {
        maxThread = num;
    }

    //返回的数组中奇数项为开始 偶数项为结束
    public int[] CreateTaskData() {
        int[] data = null;
        int ThreadNum = 0;
        if (filelen <= chunkSize) {
            ThreadNum = 1;
            data = new int[ThreadNum * 2];
            data[0] = 0;
            data[1] = filelen - 1;
        } else {
            ThreadNum = filelen / chunkSize;
            int dsize = filelen % chunkSize;
            boolean append = false;
            if (dsize >= (chunkSize / 2)) {
                ThreadNum++;
                append = false;
            } else {
                append = true;
            }
            data = new int[ThreadNum * 2];
            for (int i = 0; i < ThreadNum; i++) {
                int start = chunkSize * i;
                int end = chunkSize * (i + 1);
                if (i == ThreadNum - 1) {
                    if (append) {
                        end += dsize - 1;
                    } else {
                        end = start + dsize - 1;
                    }
                } else {
                    end--;
                }
//                System.out.println("st=" + start);
//                System.out.println("ed=" + end);
                data[i * 2] = start;
                data[i * 2 + 1] = end;
            }
        }
        return data;
    }

    public void CreateTask() {
        int[] data = CreateTaskData();
        //如果线程状态为null则生成之不为null则为外部设置的使用之
        if (TYPES == null) {
            TYPES = new byte[data.length / 2];
        }
//        System.out.println("TYPES len=" + TYPES.length);
//        System.out.println("data len=" + data.length);
        for (int i = 0; i < data.length / 2; i++) {
            System.out.println("i=" + i);
            if (TaskType == DOWNLOAD) {
                HttpDownThread t = new HttpDownThread(data[i * 2],
                        data[i * 2 + 1], this);
                t.filename = this.filename;
                t.setChunk(i);
                t.setURL(loc);
                t.TYPE = TYPES[i]; //设置状态
                vec.addElement(t);
            } else {
                HttpUpThread t = new HttpUpThread(data[i * 2], data[i * 2 + 1], this);
                t.setMax(filelen);
                t.setURL(loc);
                t.setChunk(i);
                t.setFileName(filename);
                t.TYPE = TYPES[i]; //设置状态
                vec.addElement(t);
            }
        }
    }

    public void run() {
        CreateFc();
        CreateTask(); //建立任务
        //执行任务
        while (!DOWNLOADFINAL) {
            //假设下载以完成
            DOWNLOADFINAL = true;
            Enumeration enumer = vec.elements();
            while (enumer.hasMoreElements()) {
                HttpWorkThread item = (HttpWorkThread) enumer.nextElement();
                if (item.getTYPE() != 2) { //如果有一个线程不为下载完成状态则下载未完成
                    DOWNLOADFINAL = false;
                }
            }
//            System.out.println("重新检测 DOWNLOADFINAL=" + DOWNLOADFINAL);
            int startThread = 0;
            enumer = vec.elements();
            while (enumer.hasMoreElements()) {
                HttpWorkThread item = (HttpWorkThread) enumer.nextElement();
                if (item.getTYPE() == 1) {
                    startThread++;
                }
            }
            int sThread = maxThread - startThread;
//            System.out.println("需要开始的线程数=" + sThread);
            enumer = vec.elements();
            while (enumer.hasMoreElements()) {
                HttpWorkThread item = (HttpWorkThread) enumer.nextElement();
                if (sThread != 0) {
                    if (item.getTYPE() == 0) {
                        item.start();
                        sThread--;
                    }
                }
            }
            //计算下载的字节数和线程状态
            int downsize = 0;
            for (int i = 0; i < vec.size(); i++) {
                HttpWorkThread item = (HttpWorkThread) vec.elementAt(i);
                if (item.getTYPE() == 2 || item.getTYPE() == 1) {
                    downsize += item.getExecuteSize();
                }
                if(downsize>lastDownsize){
                	this.downsize=downsize;
                	lastDownsize=downsize;
                }
                TYPES[item.getChunk()] = item.getTYPE();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }
    }
}
