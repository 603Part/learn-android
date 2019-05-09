package com.mlearn.Entity;

/**
 * Thread Information
 */
public class ThreadInfo {
    private int id;
    private String url;
    private long start;//开始的位置，在一个线程下载一个文件中，都为0，故这个成员是没用的
    private long ended;//需要下载长度
    private long finished;//已完成的量

    public ThreadInfo() {
        super();
    }

    public ThreadInfo(long ended, long finished, int id, long start, String url) {
        super();
        this.ended = ended;
        this.finished = finished;
        this.id = id;
//        this.start = start;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnded() {
        return ended;
    }

    public void setEnded(long ended) {
        this.ended = ended;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "ended=" + ended +
                ", id=" + id +
                ", url='" + url + '\'' +
                ", start=" + start +
                ", finished=" + finished +
                '}';
    }
}
