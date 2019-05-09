package com.mlearn.db;


import com.mlearn.Entity.ThreadInfo;

import java.util.List;


public interface ThreadDAO {

    public void insertThread(ThreadInfo threadInfo);

    public void deleteThread(String url, int thread_id);

    public void updateThread(String url, int thread_id, long finished);

    public List<ThreadInfo> getThreads(String url);

    public boolean isExists(String url, int thread_id);//这在多线程下载中才会用到，故此方法没用

    public boolean isExists(String url);

}
