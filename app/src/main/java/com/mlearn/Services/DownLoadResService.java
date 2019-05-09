package com.mlearn.Services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.mlearn.Entity.MaterialInfo;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownLoadResService extends Service {
    private static final String TAG = "DownLoadResService";

    public static final String ACTION_START = "DownLoadResService_ACTION_START";
    public static final String ACTION_STOP = "DownLoadResService_CTION_STOP";
    public static final String ACTION_UPDATE = "DownLoadResService_ACTION_UPDATE";
    public static String DOWNLOAD_PATH = "";

    public static List<DownLoadResTask> mTasks = new ArrayList<>();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        MaterialInfo materialInfo = null;
        if (intent.getAction() != null && ACTION_START.equals(intent.getAction())) {
            materialInfo = (MaterialInfo) intent.getSerializableExtra("materialInfo");
            DOWNLOAD_PATH = materialInfo.getSaveDir();

            Log.e(TAG, "服务开启拿到信息:" + materialInfo.toString());
            new MyAsyncTask().execute(materialInfo);
        } else if (intent.getAction() != null && ACTION_STOP.equals(intent.getAction())) {
            materialInfo = (MaterialInfo) intent.getSerializableExtra("materialInfo");
            Log.e(TAG, "服务拿到停止信息:" + materialInfo.getDownloadUrl());
            if (mTasks.size() != 0) {
                for (DownLoadResTask mTask : mTasks) {
                    if (mTask.url.equals(materialInfo.getDownloadUrl())) {
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

    public class MyAsyncTask extends AsyncTask<MaterialInfo, Void, MaterialInfo> {
        private MaterialInfo mFileInfo;

        @Override
        protected MaterialInfo doInBackground(MaterialInfo... materialInfos) {//这边是初始化
            mFileInfo = materialInfos[0];
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                URL url = new URL(mFileInfo.getDownloadUrl());
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
                File dir = new File(mFileInfo.getSaveDir());
                if (!dir.exists()) {
                    dir.mkdirs();
                    Log.e(TAG, mFileInfo.getSaveDir() + "文件夹不存在，现已创建");
                    if (dir.exists()) {
                        Log.e(TAG, mFileInfo.getSaveDir() + "文件夹已经创建");
                    }
                }

                File file = new File(mFileInfo.getSavePath());
                raf = new RandomAccessFile(file, "rwd");
                raf.setLength(length);
                mFileInfo.setLength(length);
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
            return mFileInfo;
        }

        @Override
        protected void onPostExecute(MaterialInfo materialInfo) {
            super.onPostExecute(materialInfo);
            if (materialInfo == null) {
                return;
            }
            DownLoadResTask downLoadTask = null;
            for (DownLoadResTask mTask : mTasks) {
                if (mTask.url.equals(materialInfo.getDownloadUrl())) {//如果有url相同的存在，则不用再一次add
                    mTask.download();
                    downLoadTask = mTask;
                    Log.e(TAG, "这个线程之前就存在了");
                    break;
                }
            }
            if (downLoadTask == null) {//如果还未空，说明之前不存在，则要add
                downLoadTask = new DownLoadResTask(DownLoadResService.this, materialInfo);
                downLoadTask.download();
                mTasks.add(downLoadTask);
                Log.e(TAG, "这个线程之前不存在，现在已加入集合");
            }
        }
    }

}
