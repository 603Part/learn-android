package com.mlearn.Services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mlearn.db.ThreadDAO;
import com.mlearn.db.ThreadDAOImpl;
import com.mlearn.Entity.StuHomework;
import com.mlearn.Entity.ThreadInfo;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 *
 */
public class DownLoadMyHwTask {

    private static final String TAG = "DownLoadMyHwTask";
    public boolean isPause = false;
    public String url = "";
    private Context mContext = null;
    private StuHomework myHomework = null;
    private ThreadDAO mDAO = null;
    private long mFinished = 0;

    public DownLoadMyHwTask(Context mContext, StuHomework myHomework) {
        this.mContext = mContext;
        this.myHomework = myHomework;
        url = myHomework.getDownloadUrl();
        mDAO = new ThreadDAOImpl(mContext);
    }

    public void init() {
        mFinished = 0;
        isPause = false;
    }

    public void download() {
        init();
        List<ThreadInfo> threadInfos = mDAO.getThreads(myHomework.getDownloadUrl());
        ThreadInfo threadInfo = null;
        if (threadInfos.size() == 0)//第一次下载该文件
        {
            threadInfo = new ThreadInfo(myHomework.getLength(), 0, 0, 0, myHomework.getDownloadUrl());
        } else//暂停后继续下载
        {
            threadInfo = threadInfos.get(0);//因为是单线程下载// ，所以一个url只可能拿出一个threadInfo

        }
        Log.e(TAG, "download:ThreadInfo 为" + threadInfo.toString());
        new DownLoadThread(threadInfo).start();

    }

    class DownLoadThread extends Thread {
        private static final String TAG = "DownLoadThread";
        private ThreadInfo mThreadInfo = null;

        public DownLoadThread(ThreadInfo mThreadInfo) {
            this.mThreadInfo = mThreadInfo;
        }

        @Override
        public void run() {
            if (!mDAO.isExists(mThreadInfo.getUrl(), mThreadInfo.getId())) {
                mDAO.insertThread(mThreadInfo);
                Log.e(TAG, "该下载线程不存在，现已创建");
            } else {
                Log.e(TAG, "该下载线程已存在");
            }

            URL url = null;
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream input = null;
            try {
                url = new URL(mThreadInfo.getUrl());
                Log.e(TAG, mThreadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(8000);
                conn.setRequestMethod("GET");
                long start = mThreadInfo.getFinished();//现在开始的量
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnded());
//                File file = new File(DownLoadResService.DOWNLOAD_PATH, myHomework.getResTitle());

                File file = new File(myHomework.getSavePath());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);
                Log.e("lzh", "写入客户端");
                Intent intent = new Intent(DownLoadMyHwService.ACTION_UPDATE);
                mFinished += mThreadInfo.getFinished();//表示已完成
                if (conn.getResponseCode() == HttpStatus.SC_OK) ; //SC_PARTIAL_CONTENT
                {
                    input = conn.getInputStream();
                    Log.e("lzh", "input start");
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = input.read(buffer)) != -1) {
                        String s = "run: ";
                        for (int i = 0; i < len; i++) {
                            s += buffer[i];
                        }
                        Log.e(TAG, s);
                        raf.write(buffer, 0, len);
                        mFinished += len;
                        if (System.currentTimeMillis() - time > 100) {
                            //每隔100毫秒发送广播
                            time = System.currentTimeMillis();
                            long progress = mFinished * 100 / myHomework.getLength();
                            intent.putExtra("progress", progress);
//                            Log.e("lzh","已完成"+mFinished+"总共"+myHomework.getLength()+"百分比："+progress+"%");
                            intent.putExtra("position", myHomework.getPosition());
                            intent.putExtra("isFinish", false);
                            mContext.sendBroadcast(intent);
                        }
                        if (isPause) {
                            mDAO.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mFinished);
                            Log.e(TAG, "run: 暂停的线程信息" + mThreadInfo.toString());
                            return;
                        }
                    }
                    //下载完了还要再广播一次
                    intent.putExtra("position", myHomework.getPosition());
                    intent.putExtra("isFinish", true);
                    mContext.sendBroadcast(intent);
                    Log.e("lzh", "下载完成");
                    mDAO.deleteThread(mThreadInfo.getUrl(), mThreadInfo.getId());
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                try {
                    conn.disconnect();
                    if (raf != null) {
                        raf.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
