package com.mlearn.GlobalParams;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.util.LruCache;

import com.mlearn.Entity.User;

import java.util.HashMap;

public class GlobalParam {

    public static final int PROCESSING = 1;
    public static final int FAILURE = -1;
    public static final int PHOTO_IS_OK = 1;
    public static String state = "";    //当前模块
    public static int USER_LOGIN_STATE = 0;
    public static LruCache<String, Bitmap> mCaches = null;//设置缓存
    public static User user = new User();
    public static HashMap<Integer, String> cNameMap = new HashMap<>();//这个键值对是用于存储课程id和对应课程名的
    public static String APP_LOCAL_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/TestLogin/";

    public static int courseID = 0;//传递课程id使用
}
