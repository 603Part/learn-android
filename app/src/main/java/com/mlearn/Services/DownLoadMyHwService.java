package com.mlearn.Services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.mlearn.Entity.StuHomework;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownLoadMyHwService extends Service {
    private static final String TAG = "DownLoadMyHwService";

    public static final String ACTION_START = "DownLoadMyHwService_ACTION_START";
    public static final String ACTION_STOP = "DownLoadMyHwService_ACTION_STOP";
    public static final String ACTION_UPDATE = "DownLoadMyHwService_ACTION_UPDATE";
    public static String DOWNLOAD_PATH = "";

    public static List<DownLoadMyHwTask> mTasks = new ArrayList<>();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        StuHomework myHomework = null;
        if (intent.getAction() != null && ACTION_START.equals(intent.getAction())) {
            myHomework = (StuHomework) intent.getSerializableExtra("myHomework");
            DOWNLOAD_PATH = myHomework.getSaveDir();

            Log.e(TAG, "服务开启拿到信息:" + myHomework.toString());
            new MyAsyncTask().execute(myHomework);
        } else if (intent.getAction() != null && ACTION_STOP.equals(intent.getAction())) {
            myHomework = (StuHomework) intent.getSerializableExtra("myHomework");
            Log.e(TAG, "服务拿到停止信息:" + myHomework.getDownloadUrl());
            if (mTasks.size() != 0) {
                for (DownLoadMyHwTask mTask : mTasks) {
                    if (mTask.url.equals(myHomework.getDownloadUrl())) {
                        Log.e(TAG, "成功停止");
                        mTask.isPause = true;
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyAsyncTask extends AsyncTask<StuHomework, Void, StuHomework> {
        private StuHomework myHomework;

        @Override
        protected StuHomework doInBackground(StuHomework... myHomeworks) {//这边是初始化
            myHomework = myHomeworks[0];
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                URL url = new URL(myHomework.getDownloadUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int length = -1;
                if (conn.getResponseCode() == HttpStatus.SC_OK) {
                    length = conn.getContentLength();//拿到长度
                }
                if (length <= 0) {
                    return null;
                }
                Log.e(TAG, "length: " + length);
                File dir = new File(myHomework.getSaveDir());
                if (!dir.exists()) {
                    dir.mkdirs();
                    Log.e(TAG, myHomework.getSaveDir() + "文件夹不存在，现已创建");
                    if (dir.exists()) {
                        Log.e(TAG, myHomework.getSaveDir() + "文件夹已经创建");
                    }
                }

                File file = new File(myHomework.getSavePath());
                raf = new RandomAccessFile(file, "rwd");
                raf.setLength(length);
                myHomework.setLength(length);
                Log.e(TAG, "文件长度" + length);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.disconnect();
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return myHomework;
        }

        @Override
        protected void onPostExecute(StuHomework myHomework) {
            super.onPostExecute(myHomework);
            if (myHomework == null) {
                return;
            }
            DownLoadMyHwTask downLoadTask = null;
            for (DownLoadMyHwTask mTask : mTasks) {
                if (mTask.url.equals(myHomework.getDownloadUrl())) {//如果有url相同的存在，则不用再一次add
                    mTask.download();
                    downLoadTask = mTask;
                    Log.e(TAG, "这个线程之前就存在了");
                    break;
                }
            }
            if (downLoadTask == null) {//如果还未空，说明之前不存在，则要add
                downLoadTask = new DownLoadMyHwTask(DownLoadMyHwService.this, myHomework);
                downLoadTask.download();
                mTasks.add(downLoadTask);
                Log.e(TAG, "这个线程之前不存在，现在已加入集合");
            }
        }
    }

}
