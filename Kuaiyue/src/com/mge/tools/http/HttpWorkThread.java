package com.mge.tools.http;
public class HttpWorkThread {
    private int chunk;
    public byte TYPE; //0为未开始 1为正在执行工作 2为工作完成
    public String url;
    public byte getTYPE() {
        return TYPE;
    }

    public int executesize; //执行（上传/下载）的字节数
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
