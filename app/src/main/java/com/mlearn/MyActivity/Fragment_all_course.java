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
import com.mlearn.MyAdapter.MyAdaper_all_course;
import com.mlearn.Entity.Course;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_all_course extends Fragment {
    private static final String TAG = "Fragment_all_course";

    private int tag = 1;//标志，1表示初始化，2表示刷新

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private MyAdaper_all_course myAdaper_courses;
    private SwipeRefreshLayout swipeRefresh;//下拉刷新

    List<Course> courseList = new ArrayList<Course>();

    public Fragment_all_course() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_course, container, false);
        initView(view);
        tag = 1;
        initData();
        return view;
    }

    private void initData() {
        List<NameValuePair> paras = new ArrayList<NameValuePair>();
        NameValuePair nameValuePair = new BasicNameValuePair("operation", "findAllCourse");
        paras.add(nameValuePair);
        courseList.clear();
        new MyAsyncTask().execute(paras);
    }

    private void initView(View view) {
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.list_view_all_course);
        recyclerView.setLayoutManager(layoutManager);

        //下拉刷新
        swipeRefresh = view.findViewById(R.id.all_course_refresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAllCourse();
            }
        });
    }

    /****************************用于选课操作后的刷新****************************************/
    public void refreshPage() {
        tag = 2;
        initData();
    }

    /********************************下拉刷新************************************************/
    private void refreshAllCourse() {
        tag = 2;
        initData();
    }

    /********************************通过网络获取所有课程************************************************/
    public class MyAsyncTask extends AsyncTask<List<NameValuePair>, Void, String> {

        @Override
        protected String doInBackground(List<NameValuePair>[] lists) {
            return AskForInternet.post(ConstsUrl.COURSE_URL, lists[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray courseArray = jsonObject.getJSONArray("courses");
                for (int i = 0; i < courseArray.length(); i++) {
                    Course course = new Course();
                    course.setCourseID(courseArray.getJSONObject(i).optInt("courseID"));
                    course.setCourseName(courseArray.getJSONObject(i).optString("courseName"));
                    course.setCourseUrl(courseArray.getJSONObject(i).optString("courseUrl"));
                    course.setCourseAbstract(courseArray.getJSONObject(i).optString("courseAbstract"));
                    course.setDetailInfo(courseArray.getJSONObject(i).optString("detailInfo"));
                    course.setTeacherName(courseArray.getJSONObject(i).optString("teacherName"));
                    Log.i(TAG, course.toString());
                    courseList.add(course);
                    GlobalParam.cNameMap.put(course.getCourseID(), course.getCourseName());
                }
                if (tag == 1) {//初始化
                    myAdaper_courses = new MyAdaper_all_course(courseList);
                    recyclerView.setAdapter(myAdaper_courses);
                } else if (tag == 2) {
                    myAdaper_courses.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
