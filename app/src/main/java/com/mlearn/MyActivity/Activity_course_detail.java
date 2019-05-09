package com.mlearn.MyActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mlearn.GlobalParams.GlobalParam;
import com.mlearn.HttpUtil.AskForInternet;
import com.mlearn.HttpUtil.ConstsUrl;
import com.mlearn.HttpUtil.ImageLoader;
import com.mlearn.Entity.Course;
import com.mlearn.Entity.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Activity_course_detail extends AppCompatActivity {
    private static final String TAG = "Activity_course_detail";

    public static final String UPDATE_MY_COURSE_LIST = "UPDATE_MY_COURSE_LIST";

    private Intent intent;
    private Course course;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView course_ImageView;
    private TextView courseDetailInfo_TextView, courseAbstract_TextView;
    private Switch aSwitch;
    private User user;

    private String courseID, studentID;
    private String op = "";//操作分为选课、退订、查课 selectCourse cancelCourse checkSelect

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**************************状态栏透明*********************************************************/
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        /***********************************************************************************/
        setContentView(R.layout.activity_course_detail);
        intent = getIntent();
        //分别获得由上一个fragment传来的课程和用户信息
        course = (Course) intent.getSerializableExtra("course");

        user = GlobalParam.user;
        courseID = String.valueOf(course.getCourseID());
        studentID = String.valueOf(user.getUserId());
        toolbar = findViewById(R.id.course_detail_toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        course_ImageView = findViewById(R.id.course_detail_cover_view);
        courseAbstract_TextView = findViewById(R.id.course_abstract);
        courseDetailInfo_TextView = findViewById(R.id.course_detail_info);
        aSwitch = findViewById(R.id.course_select);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(course.getCourseName());
        courseAbstract_TextView.setText(course.getCourseAbstract());
        courseDetailInfo_TextView.setText(course.getDetailInfo());
        course_ImageView.setTag(ConstsUrl.BASE_URL + course.getCourseUrl());

        ImageLoader imageLoader = new ImageLoader();
        imageLoader.showImageByAsyncTask(course_ImageView, ConstsUrl.BASE_URL + course.getCourseUrl());

        initevent();
    }

    private void initevent() {
        op = "checkSelect";
        doInstruction();
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //如果是用户点击的，则响应事件
                if (aSwitch.isPressed())
                    if (isChecked) {
                        showChooseDialog();
                    } else {
                        showCancelDialog();
                    }
            }
        });
    }

    //退订课程对话框
    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_course_detail.this);
        builder.setMessage("确定退订吗？");
        builder.setCancelable(false);
        builder.setPositiveButton("退订", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                op = "cancelCourse";
                doInstruction();
                aSwitch.setText("选课情况：未选");
                //发送广播消息，广播接受者注册在MainActivity
                Intent intent = new Intent(Activity_course_detail.UPDATE_MY_COURSE_LIST);
                sendBroadcast(intent);
            }
        });
        builder.setNegativeButton("再想想", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                aSwitch.setChecked(true);
            }
        });
        builder.show();
    }


    //确定选课对话框
    private void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_course_detail.this);
        builder.setMessage("确定选课吗？");
        builder.setCancelable(false);
        builder.setPositiveButton("选课", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //选课
                op = "selectCourse";
                doInstruction();
                aSwitch.setText("选课情况：已选");
                //发送广播消息，广播接受者注册在MainActivity
                Intent intent = new Intent(Activity_course_detail.UPDATE_MY_COURSE_LIST);
                sendBroadcast(intent);
            }
        });
        builder.setNegativeButton("再想想", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                aSwitch.setChecked(false);
            }
        });
        builder.show();
    }

    private void doInstruction() {
        List<NameValuePair> paras = new ArrayList<NameValuePair>();
        paras.add(new BasicNameValuePair("operation", op));
        paras.add(new BasicNameValuePair("courseID", courseID));
        paras.add(new BasicNameValuePair("studentID", studentID));
        new MyAsyncTask().execute(paras);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyAsyncTask extends AsyncTask<List<NameValuePair>, Void, String> {

        @Override
        protected String doInBackground(List<NameValuePair>[] lists) {
            return AskForInternet.post(ConstsUrl.COURSE_URL, lists[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    if (op.equals("selectCourse")) {
                        Toast.makeText(Activity_course_detail.this, "选课失败",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Activity_course_detail.this, "退订失败",
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (code == 1) {
                    if (op.equals("selectCourse")) {
                        Toast.makeText(Activity_course_detail.this, "选课成功",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Activity_course_detail.this, "退订成功",
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (code == 2) {
                    String re = jsonObject.getString("message");
                    if (re.equals("0")) {//说明一开始没选
                        aSwitch.setChecked(false);
                        aSwitch.setText("选课情况：未选");
                    } else {//说明一开始选了
                        aSwitch.setChecked(true);
                        aSwitch.setText("选课情况：已选");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
