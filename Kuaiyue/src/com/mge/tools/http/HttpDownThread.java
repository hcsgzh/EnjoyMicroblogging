package com.mge.tools.http;

import java.io.*;
import java.net.HttpURLConnection;

import android.content.Context;

public class HttpDownThread extends HttpWorkThread implements Runnable {
    private int start, end, max;
    public String filename;
    private HttpWorkThreadManager manager;
    public HttpDownThread(int start, int end, HttpWorkThreadManager manager) {
        this.start = start;
        this.end = end;
        this.manager = manager;
        TYPE = 0;
    }

    public int getExecuteSize() {
        if (TYPE == 2) {
            return (end - start) + 1;
        } else {
            return executesize;
        }
    }

    public void setMax(int max) {
        this.max = max;
    }


    //将数据写入文件
    public void setData(byte[] data) {
        String fileloc = DownloadManager.baseloc + filename;
        File f=new File(fileloc);
        if(f.exists()){
        try {
        	RandomAccessFile fc = new RandomAccessFile(fileloc, "rw");
        	fc.seek(start);
        	fc.write(data);
        	fc.close();
        } catch (IOException ex2) {
        }}
        return;
    }

    //重载start
    public void start() {
        TYPE = 1;
        Thread t = new Thread(this);
        t.start();
    }
    //使用标准http1.1协议
    public void run() {
    	HttpURLConnection hc = null;
        InputStream is = null;
        try {
            hc = TOOLS.httpConnect(url,manager.context);
            if(hc==null)
            {
            	TYPE = 0;
            	return;
            }
//            hc.setRequestProperty("User-Agent",
//                                  System.getProperty("microedition.platform") +
//                                  " Profile/MIDP-2.0 Configuration/CLDC-1.0");
//            hc.setRequestProperty("Connection", "Keep-Alive");
            hc.setRequestProperty("RANGE", "bytes=" + start + "-" + end);
            int ResponseCode = hc.getResponseCode();
            System.out.println("hc.getResponseCode() -- " + ResponseCode);
            String cr = hc.getHeaderField("content-range");
            String cl = hc.getHeaderField("content-length");
            String ct = hc.getHeaderField("content-type");
            String ar = hc.getHeaderField("accept-ranges");
            System.out.println("cr=" + cr);
            System.out.println("cl=" + cl);
            System.out.println("ct=" + ct);
            System.out.println("ar=" + ar);
//            int getlen=end-start;
            int getlen = hc.getContentLength();
            System.out.println("getlen=" + getlen);
            if (ResponseCode == HttpURLConnection.HTTP_PARTIAL) {
            	ByteArrayOutputStream dos=new ByteArrayOutputStream();
//            	byte[] data = new byte[getlen];
                is = hc.getInputStream();
                //读取文件
                int readlen = 0;
                int len;
                byte[] buffer = new byte[2048];
                while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                    }
                    dos.write(buffer,0,len);
//                    System.arraycopy(buffer, 0, data, readlen, len);
                    readlen += len;
                    executesize = readlen;
                }
                //写入文件
                byte[] data =dos.toByteArray();
                setData(data);
                try {
                    is.close();
                    is=null;
                } catch (IOException ex1) {
                }
                try {
                    hc.disconnect();
                    hc=null;
                } catch (Exception ex) {
                }
                TYPE = 2;
                executesize = data.length;
            } else {
                TYPE = 0;
            }
        } catch (SecurityException se) {
            TYPE = 0;
            se.printStackTrace();
        } catch (IOException e) {
            TYPE = 0;
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
    }
}
