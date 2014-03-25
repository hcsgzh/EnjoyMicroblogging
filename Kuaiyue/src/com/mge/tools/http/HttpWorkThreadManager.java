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
    private Vector vec = new Vector(); //����
    public int downsize = 0;
    public int lastDownsize = 0;
    public int filelen;
    public Context context;
    private static byte NULL[] = new byte[4096];
    public byte[] TYPES;
    //url�ļ���������ַ chunkSize���ش����С filename���������ļ��������ص������ļ��� filelen�ļ��ܴ�С
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

    //url�ļ���������ַ chunkSize�ϴ����С filename�����ļ������ϴ������������ļ���
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
        	//�����ļ���������ɾ��
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
                if (fc.length() == 0) {//�ļ����������·���ռ�
                  malloc = true;
                  for (int i = 0; i < TYPES.length; i++) {
                      TYPES[i] = 0;
                  }
                }
                if (malloc) {//����ռ�
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
        	//�ϴ��Ȳ�ʵ��
        }
    }

    public void setTYPES(byte[] data) {
        TYPES = new byte[data.length];
        System.arraycopy(data, 0, TYPES, 0, data.length);
    }

    public void setThreadNum(int num) {
        maxThread = num;
    }

    //���ص�������������Ϊ��ʼ ż����Ϊ����
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
        //����߳�״̬Ϊnull������֮��Ϊnull��Ϊ�ⲿ���õ�ʹ��֮
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
                t.TYPE = TYPES[i]; //����״̬
                vec.addElement(t);
            } else {
                HttpUpThread t = new HttpUpThread(data[i * 2], data[i * 2 + 1], this);
                t.setMax(filelen);
                t.setURL(loc);
                t.setChunk(i);
                t.setFileName(filename);
                t.TYPE = TYPES[i]; //����״̬
                vec.addElement(t);
            }
        }
    }

    public void run() {
        CreateFc();
        CreateTask(); //��������
        //ִ������
        while (!DOWNLOADFINAL) {
            //�������������
            DOWNLOADFINAL = true;
            Enumeration enumer = vec.elements();
            while (enumer.hasMoreElements()) {
                HttpWorkThread item = (HttpWorkThread) enumer.nextElement();
                if (item.getTYPE() != 2) { //�����һ���̲߳�Ϊ�������״̬������δ���
                    DOWNLOADFINAL = false;
                }
            }
//            System.out.println("���¼�� DOWNLOADFINAL=" + DOWNLOADFINAL);
            int startThread = 0;
            enumer = vec.elements();
            while (enumer.hasMoreElements()) {
                HttpWorkThread item = (HttpWorkThread) enumer.nextElement();
                if (item.getTYPE() == 1) {
                    startThread++;
                }
            }
            int sThread = maxThread - startThread;
//            System.out.println("��Ҫ��ʼ���߳���=" + sThread);
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
            //�������ص��ֽ������߳�״̬
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
