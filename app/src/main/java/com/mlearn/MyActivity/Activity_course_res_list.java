package com.mlearn.MyActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.mlearn.GlobalParams.GlobalParam;
import com.mlearn.HttpUtil.AskForInternet;
import com.mlearn.HttpUtil.ConstsUrl;
import com.mlearn.MyAdapter.MyAdapter_res_list;
import com.mlearn.Permission.PermissionApply;
import com.mlearn.Entity.Course;
import com.mlearn.Entity.MaterialInfo;
import com.mlearn.Services.DownLoadResService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程资源，用于下载
 */
public class Activity_course_res_list extends AppCompatActivity {
    private String TAG = "AIRCode";
    private int tag = 1;//标志，1表示初始化，2表示刷新
    private Toolbar toolbar;
    private Course course;
    private Intent intent;

    /*****************************************************************************************/
    private RecyclerView recyclerView;
    private MyAdapter_res_list adapter;
    private SwipeRefreshLayout swipeRefresh;//下拉刷新
    /********************************注册广播更新进度条********************************************/
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (adapter == null) {
                return;
            }
            if (DownLoadResService.ACTION_UPDATE.equals(intent.getAction())) {
                boolean isFinished = intent.getBooleanExtra("isFinish", false);
                if (!isFinished) {//如果没有下载完
                    long progress = intent.getLongExtra("progress", 0);
                    int position = intent.getIntExtra("position", 0);
                    adapter.updateProgress(position, (int) progress);
//                    adapter.refreshButon(position);
                } else {
                    int position = intent.getIntExtra("position", 0);
                    adapter.updateProgress(position, 100);
                }
            }
        }
    };
    private List<MaterialInfo> materialInfos = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    /*****************************************************************************************/
//    private TextView mTvFileName=null;
//    private ProgressBar mPbProgress=null;
//    private Button mBtStart=null;
//    private Button mBtStop=null;
//    private long time = System.currentTimeMillis();

    /*****************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_res_list);
        toolbar = findViewById(R.id.res_list_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        intent = getIntent();
        course = (Course) intent.getSerializableExtra("course");
        toolbar.setTitle(course.getCourseName() + "资源");
        initView();
        tag = 1;
        initData();
        initFunc();
    }

    private void initFunc() {
        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownLoadResService.ACTION_UPDATE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void initData() {
        List<NameValuePair> paras = new ArrayList<>();
        paras.add(new BasicNameValuePair("operation", "findResByCourseID"));
        paras.add(new BasicNameValuePair("courseID", String.valueOf(course.getCourseID())));
        materialInfos.clear();
        new MyAsyncTask().execute(paras);
    }

    private void initView() {
        recyclerView = findViewById(R.id.res_list_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getItemAnimator().setChangeDuration(0);

        //下拉刷新
        swipeRefresh = findViewById(R.id.course_res_refresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPage();
            }
        });
    }

    /****************************用于选课操作后的刷新****************************************/
    public void refreshPage() {
        tag = 2;
        initData();
    }

    /********************************点击箭头返回****************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://返回箭头
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /********************************通过网络获取资源列表********************************************/
    public class MyAsyncTask extends AsyncTask<List<NameValuePair>, Void, String> {

        @Override
        protected String doInBackground(List<NameValuePair>[] lists) {
            return AskForInternet.post(ConstsUrl.RES_URL, lists[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, "传来的信息" + s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray resArray = jsonObject.getJSONArray("resList");
                for (int i = 0; i < resArray.length(); i++) {
                    MaterialInfo materialInfo = new MaterialInfo();
                    materialInfo.setResID(resArray.getJSONObject(i).getInt("resID"));
                    materialInfo.setCourseID(resArray.getJSONObject(i).getInt("courseID"));
                    materialInfo.setTeacherNumber(resArray.getJSONObject(i).getString("teacherNumber"));
                    materialInfo.setPublishTime(resArray.getJSONObject(i).getString("publishTime"));
                    materialInfo.setResTitle(resArray.getJSONObject(i).getString("resTitle"));
                    materialInfo.setResUrl(resArray.getJSONObject(i).getString("resUrl"));
                    materialInfo.setSize(resArray.getJSONObject(i).getLong("size"));
                    materialInfo.setTeacherName(resArray.getJSONObject(i).getString("teacherName"));
                    materialInfo.setCourseName(resArray.getJSONObject(i).getString("courseName"));
                    String dir = GlobalParam.APP_LOCAL_DIRECTORY + "course/" + materialInfo.getCourseName() + "/res/";
                    materialInfo.setSaveDir(dir);
                    materialInfo.setSavePath(dir + materialInfo.getResTitle());
                    materialInfo.setDownloadUrl(ConstsUrl.BASE_URL + materialInfo.getResUrl());

                    File file = new File(materialInfo.getSavePath());
                    if (file.exists()) {
                        materialInfo.setExistLocal(true);
                    }

                    materialInfos.add(materialInfo);
                }

                PermissionApply.ReadWritePermissionApply(Activity_course_res_list.this);
                if (tag == 1) {//初始化
                    adapter = new MyAdapter_res_list(materialInfos, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                } else if (tag == 2) {
                    //刷新
                    adapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
