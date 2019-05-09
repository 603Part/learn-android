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
import com.mlearn.MyAdapter.MyAdaper_my_course;
import com.mlearn.MyAdapter.MyAdapter_bbs_course_list;
import com.mlearn.MyAdapter.MyAdapter_test_course_list;
import com.mlearn.Entity.Course;
import com.mlearn.Entity.StudentCourse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_my_course extends Fragment {
    private static final String TAG = "Fragment_all_course";

    private int tag = 1;//标志，1表示初始化，2表示刷新

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter myAdaper;
    private SwipeRefreshLayout swipeRefresh;//下拉刷新

    List<Course> courseList = new ArrayList<>();
    List<StudentCourse> relationList = new ArrayList<>();
    HashMap<Integer, Integer> courseStuMap = new HashMap<>();
    HashMap<Integer, Integer> courseThemeNumMap = new HashMap<>();

    public Fragment_my_course() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_course, container, false);
        initView(view);
        tag = 1;
        initData();
        return view;
    }

    private void initData() {
        List<NameValuePair> paras = new ArrayList<NameValuePair>();
        if (GlobalParam.state.equals("course") || GlobalParam.state.equals("resource") || GlobalParam.state.equals("homework")) {
            NameValuePair nameValuePair = new BasicNameValuePair("operation", "findMyCourse");
            paras.add(nameValuePair);
            nameValuePair = new BasicNameValuePair("studentID", String.valueOf(GlobalParam.user.getUserId()));
            paras.add(nameValuePair);

            courseList.clear();
        } else if (GlobalParam.state.equals("test")) {
            NameValuePair nameValuePair = new BasicNameValuePair("operation", "findCourseTestScore");
            paras.add(nameValuePair);
            nameValuePair = new BasicNameValuePair("studentID", String.valueOf(GlobalParam.user.getUserId()));
            paras.add(nameValuePair);

            relationList.clear();
        } else if (GlobalParam.state.equals("forum")) {
            NameValuePair nameValuePair = new BasicNameValuePair("operation", "findCourseList");
            paras.add(nameValuePair);
            nameValuePair = new BasicNameValuePair("studentID", String.valueOf(GlobalParam.user.getUserId()));
            paras.add(nameValuePair);

            relationList.clear();
            courseStuMap.clear();
            courseThemeNumMap.clear();
        }
        new MyAsyncTask().execute(paras);
    }

    private void initView(View view) {
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.list_view_my_course);
        recyclerView.setLayoutManager(layoutManager);

        //下拉刷新
        swipeRefresh = view.findViewById(R.id.my_course_refresh);
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

    /********************************通过网络获取所有课程************************************************/
    public class MyAsyncTask extends AsyncTask<List<NameValuePair>, Void, String> {

        @Override
        protected String doInBackground(List<NameValuePair>[] lists) {
            if (GlobalParam.state.equals("course") || GlobalParam.state.equals("resource") || GlobalParam.state.equals("homework")) {
                return AskForInternet.post(ConstsUrl.COURSE_URL, lists[0]);
            } else if (GlobalParam.state.equals("test")) {
                return AskForInternet.post(ConstsUrl.TEST_URL, lists[0]);
            } else if (GlobalParam.state.equals("forum")) {
                return AskForInternet.post(ConstsUrl.BBS_URL, lists[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (GlobalParam.state.equals("course") || GlobalParam.state.equals("resource") || GlobalParam.state.equals("homework")) {
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
                        myAdaper = new MyAdaper_my_course(courseList);
                        recyclerView.setAdapter(myAdaper);
                    } else if (tag == 2) {
                        myAdaper.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                } else if (GlobalParam.state.equals("test")) {
                    JSONArray relationArray = jsonObject.getJSONArray("relationList");
                    for (int i = 0; i < relationArray.length(); i++) {
                        StudentCourse studentCourse = new StudentCourse();
                        studentCourse.setCourseID(relationArray.getJSONObject(i).getInt("courseID"));
                        studentCourse.setRelationID(relationArray.getJSONObject(i).getInt("relationID"));
                        studentCourse.setStudentID(relationArray.getJSONObject(i).getInt("studentID"));
                        studentCourse.setStudentGrade(relationArray.getJSONObject(i).getInt("studentGrade"));
                        studentCourse.setStudentAnswer(relationArray.getJSONObject(i).getString("studentAnswer"));
                        studentCourse.setTestNumber(relationArray.getJSONObject(i).getInt("testNumber"));

                        relationList.add(studentCourse);
                    }
                    if (tag == 1) {//初始化
                        myAdaper = new MyAdapter_test_course_list(relationList);
                        recyclerView.setAdapter(myAdaper);
                    } else if (tag == 2) {
                        myAdaper.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                } else if (GlobalParam.state.equals("forum")) {
                    JSONArray relationArray = jsonObject.getJSONArray("relationList");
                    JSONObject stuObj = jsonObject.getJSONObject("stuNum");
                    JSONObject noteObj = jsonObject.getJSONObject("themeNum");
                    for (int i = 0; i < relationArray.length(); i++) {
                        StudentCourse studentCourse = new StudentCourse();
                        studentCourse.setCourseID(relationArray.getJSONObject(i).getInt("courseID"));
                        studentCourse.setRelationID(relationArray.getJSONObject(i).getInt("relationID"));
                        studentCourse.setStudentID(relationArray.getJSONObject(i).getInt("studentID"));
                        relationList.add(studentCourse);
                        Log.e(TAG, "onPostExecute: studentCourse" + studentCourse);
                    }
                    Iterator iterator = stuObj.keys();
                    while (iterator.hasNext()) {
                        String id = (String) iterator.next();
                        int courseID = Integer.parseInt(id);
                        int num = stuObj.getInt(id);
                        courseStuMap.put(courseID, num);
                    }

                    Iterator iterator2 = noteObj.keys();
                    while (iterator2.hasNext()) {
                        String id = (String) iterator2.next();
                        int courseID = Integer.parseInt(id);
                        int num = noteObj.getInt(id);
                        courseThemeNumMap.put(courseID, num);
                    }
                    if (tag == 1) {//初始化
                        myAdaper = new MyAdapter_bbs_course_list(relationList, courseStuMap, courseThemeNumMap);
                        recyclerView.setAdapter(myAdaper);
                    } else if (tag == 2) {
                        //刷新
                        myAdaper.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
