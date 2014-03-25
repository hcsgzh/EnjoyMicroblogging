package com.mge.tools.http;

import java.io.*;
//import javax.microedition.io.Connector;
//import javax.microedition.io.HttpConnection;


public class HttpUpThread extends HttpWorkThread implements Runnable {
    private int start, end;
    private int max; //文件最大长度
    private String filename; //文件名
    private HttpWorkThreadManager manager;
    private byte[] data;
    public HttpUpThread(int start, int end, HttpWorkThreadManager manager) {
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

    public void setFileName(String name) {
        this.filename = name;
    }

    private static byte NULL[] = new byte[4096];
    private void fastSkip(InputStream is, int len) {
        try {
            for (int i = 0; i < len / 4096; i++) {
                is.read(NULL);
            }
            is.read(NULL, 0, len % 4096);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //从文件中获取数据
    private byte[] getData() {
//        long ttime = System.currentTimeMillis();
//        byte[] data = null;
//        if (manager.fc != null) {
//            data = new byte[(end - start) + 1];
//            try {
//                InputStream is = manager.fc.openInputStream();
//                fastSkip(is, start);
//                is.read(data, 0, data.length);
//                is.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//        System.out.println("getData usetime=" +
//                           (System.currentTimeMillis() - ttime));
//        return data;
    	return new byte[1];
    }

    //重载start
    public void start() {
        TYPE = 1;
        data = getData();
        Thread t = new Thread(this);
        t.start();
    }
    //不实现
    public void run() {
        TYPE = 2;
    }
}
