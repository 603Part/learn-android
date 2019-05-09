package com.mlearn.MyActivity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mlearn.GlobalParams.GlobalParam;
import com.mlearn.HttpUtil.AskForInternet;
import com.mlearn.HttpUtil.ConstsUrl;
import com.mlearn.MyAdapter.MyAdapter_my_hw;
import com.mlearn.Entity.Course;
import com.mlearn.Entity.StuHomework;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_my_hw extends Fragment {

    private static final String TAG = "Fragment_my_hw";

    private Course course;

    private int tag = 1;//标志，1表示初始化，2表示刷新

    private RecyclerView recyclerView;
    private List<StuHomework> myHomeworkList = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private MyAdapter_my_hw myAdapter_my_hw;

    private SwipeRefreshLayout swipeRefresh;//下拉刷新

    public Fragment_my_hw() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_hw, container, false);
        Bundle bundle = getArguments();//从activity传过来的Bundle
        course = (Course) bundle.getSerializable("course");
        initView(view);
        tag = 1;
        initData();
        return view;
    }


    private void initData() {
        List<NameValuePair> paras = new ArrayList<NameValuePair>();
        paras.add(new BasicNameValuePair("operation", "findMyHw"));
        paras.add(new BasicNameValuePair("courseID", String.valueOf(course.getCourseID())));
        paras.add(new BasicNameValuePair("userID", String.valueOf(GlobalParam.user.getUserId())));
        myHomeworkList.removeAll(myHomeworkList);
        new Fragment_my_hw.MyAsyncTask().execute(paras);
    }

    private void initView(View view) {
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView = view.findViewById(R.id.my_hw_recycler_view);
        Log.e(TAG, "initView: " + recyclerView);
        recyclerView.setLayoutManager(layoutManager);

        //下拉刷新
        swipeRefresh = view.findViewById(R.id.my_hw_refresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshHw();
            }
        });

    }

    public MyAdapter_my_hw getAdapter() {
        return myAdapter_my_hw;
    }

    /****************************用于删除操作后的刷新****************************************/
    public void refreshPage() {
        tag = 2;
        myAdapter_my_hw.refreshButttonText("下载");
        initData();
    }

    /********************************下拉刷新************************************************/
    private void refreshHw() {
        tag = 2;
        initData();
    }

    /********************************通过网络获取作业************************************************/
    public class MyAsyncTask extends AsyncTask<List<NameValuePair>, Void, String> {

        @Override
        protected String doInBackground(List<NameValuePair>[] lists) {
            return AskForInternet.post(ConstsUrl.HW_URL, lists[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray hwArray = jsonObject.getJSONArray("shList");
                for (int i = 0; i < hwArray.length(); i++) {
                    StuHomework myHomework = new StuHomework();
                    myHomework.setShwID(hwArray.getJSONObject(i).getInt("shwID"));
                    myHomework.setHwID(hwArray.getJSONObject(i).getInt("hwID"));
                    myHomework.setCourseID(hwArray.getJSONObject(i).getInt("courseID"));
                    myHomework.setSubTime(hwArray.getJSONObject(i).getString("subTime"));
                    myHomework.setHwUrl(hwArray.getJSONObject(i).getString("hwUrl"));
                    myHomework.setStuWorkTitle(hwArray.getJSONObject(i).getString("stuWorkTitle"));
                    myHomework.setUserID(hwArray.getJSONObject(i).getInt("userID"));
                    myHomework.setSize(hwArray.getJSONObject(i).getInt("size"));
                    myHomework.setHwTitle(hwArray.getJSONObject(i).getString("hwTitle"));
                    myHomework.setStudentName(hwArray.getJSONObject(i).getString("studentName"));
                    myHomework.setSid(hwArray.getJSONObject(i).getString("sid"));
                    myHomework.setCourseName(hwArray.getJSONObject(i).getString("courseName"));

                    String dir = GlobalParam.APP_LOCAL_DIRECTORY + "course/" + myHomework.getCourseName() + "/homework/";
                    myHomework.setSaveDir(dir);
                    myHomework.setSavePath(dir + myHomework.getStuWorkTitle());
                    myHomework.setDownloadUrl(ConstsUrl.BASE_URL + myHomework.getHwUrl());

                    File file = new File(myHomework.getSavePath());
                    if (file.exists()) {
                        myHomework.setExistLocal(true);
                    }

                    myHomeworkList.add(myHomework);
                }
                if (tag == 1) {//初始化
                    myAdapter_my_hw = new MyAdapter_my_hw(myHomeworkList, getActivity());
                    recyclerView.setAdapter(myAdapter_my_hw);
                } else if (tag == 2) {
                    myAdapter_my_hw.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
