package com.mge.tools.http;
public class HttpWorkThread {
    private int chunk;
    public byte TYPE; //0Ϊδ��ʼ 1Ϊ����ִ�й��� 2Ϊ�������
    public String url;
    public byte getTYPE() {
        return TYPE;
    }

    public int executesize; //ִ�У��ϴ�/���أ����ֽ���
    public int getExecuteSize() {
        return executesize;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    public int getChunk() {
        return chunk;
    }


    public void setURL(String url) {
        this.url = url;
    }

    public void start() {
    }

}
