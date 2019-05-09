package com.mlearn.MyActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.mlearn.Entity.ActivityCollector;
import com.mlearn.Entity.BaseActivity;
import com.mlearn.GlobalParams.GlobalParam;
import com.mlearn.HttpUtil.AskForInternet;
import com.mlearn.HttpUtil.ConstsUrl;
import com.mlearn.HttpUtil.ImageLoader;
import com.mlearn.Entity.User;
import com.makeramen.roundedimageview.RoundedImageView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private Intent intent;
    private User user;
    private TextView name_TextView;
    private TextView signaturel_TextView;
    private View nav_header_View;
    private RoundedImageView userPhoto_RoundeImageView;
    private Fragment course_fragment;//浏览课程fragment

    /***********************************注册广播*********************************************/
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "onReceive: " + action);
            if (action.equals(Activity_personal_info.UPDATE_INFO)) {
                user = GlobalParam.user;
                name_TextView.setText(user.getName());
                signaturel_TextView.setText(user.getSignature());
                String url = ConstsUrl.BASE_URL + user.getPhoto();
                ImageLoader imageLoader = new ImageLoader();
                imageLoader.showImageByAsyncTask(userPhoto_RoundeImageView, url);
            } else if (action.equals(Activity_test_question.UPDATE_TEST_COURSE_LIST)) {
                ((Fragment_my_course) course_fragment).refreshPage();
            } else if (action.equals(Activity_course_detail.UPDATE_MY_COURSE_LIST)) {
                ((Fragment_main_course) course_fragment).refreshPage();
            } else if (action.equals(Activity_bbs_post_list.REFRESH_POST_LIST_NUM)) {
                GlobalParam.state = "forum";
                ((Fragment_my_course) course_fragment).refreshPage();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**************************状态栏透明*********************************************************/
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
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        /***********************************************************************************/
        intent = getIntent();
        user = GlobalParam.user;
        navigationView = findViewById(R.id.nav_view);
        nav_header_View = navigationView.getHeaderView(0);
        name_TextView = nav_header_View.findViewById(R.id.name_view_id);
        signaturel_TextView = nav_header_View.findViewById(R.id.studentNumber_view_id);
        userPhoto_RoundeImageView = nav_header_View.findViewById(R.id.user_photo);
        name_TextView.setText(user.getName());
        String str = user.getSignature();
        if (str.equals("")) {
            signaturel_TextView.setText("在个人信息界面可修改签名哦");
        } else {
            signaturel_TextView.setText(str);
        }

        String url = ConstsUrl.BASE_URL + user.getPhoto();
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.showImageByAsyncTask(userPhoto_RoundeImageView, url);
        initFirstFragment();

        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Activity_personal_info.UPDATE_INFO);
        filter.addAction(Activity_test_question.UPDATE_TEST_COURSE_LIST);
        filter.addAction(Activity_course_detail.UPDATE_MY_COURSE_LIST);
        registerReceiver(mReceiver, filter);
    }

    private void initFirstFragment() {
        toolbar.setTitle("课程浏览");
        GlobalParam.state = "course";
        course_fragment = new Fragment_main_course();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.course_main, course_fragment);
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_quit) {
            Toast.makeText(this, "退出登录，我在com.example.mlearn.MainActivity.165 line", Toast.LENGTH_LONG).show();
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定退出登录吗？");
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<NameValuePair> paras = new ArrayList<NameValuePair>();
                NameValuePair nameValuePair = new BasicNameValuePair("studentNumber", GlobalParam.user.getsId());
                paras.add(nameValuePair);
                nameValuePair = new BasicNameValuePair("operation", "logout");
                paras.add(nameValuePair);
                new MyAsyncTask().execute(paras);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            //点击第一个选项
            initFirstFragment();
        } else if (id == R.id.nav_gallery) {//课堂资源
            toolbar.setTitle("课程资源");
            GlobalParam.state = "resource";
            course_fragment = new Fragment_my_course();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.course_main, course_fragment);
            transaction.commit();
        } else if (id == R.id.nav_slideshow) {//作业
            toolbar.setTitle("课程作业");
            GlobalParam.state = "homework";
            course_fragment = new Fragment_my_course();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.course_main, course_fragment);
            transaction.commit();
        } else if (id == R.id.nav_test) {//在线测试
            toolbar.setTitle("在线测试");
            GlobalParam.state = "test";
            course_fragment = new Fragment_my_course();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.course_main, course_fragment);
            transaction.commit();
        } else if (id == R.id.t_s_communication) {//师生交流
            toolbar.setTitle("师生交流");
            GlobalParam.state = "forum";
            course_fragment = new Fragment_my_course();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.course_main, course_fragment);
            transaction.commit();
        } else if (id == R.id.notice_message) {//通知
            intent = new Intent(MainActivity.this, Activity_notice.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {//个人信息
            intent = new Intent(MainActivity.this, Activity_personal_info.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {   //分享应用

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public class MyAsyncTask extends AsyncTask<List<NameValuePair>, Void, String> {

        @Override
        protected String doInBackground(List<NameValuePair>[] lists) {
            return AskForInternet.post(ConstsUrl.LOGIN_URL, lists[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    Toast.makeText(getApplicationContext(), "退出登录错误", Toast.LENGTH_SHORT).show();
                } else if (code == 1) {
                    GlobalParam.USER_LOGIN_STATE = 0;//表示成功退出登录
                    Toast.makeText(getApplicationContext(), "退出登录成功",
                            Toast.LENGTH_LONG).show();
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    GlobalParam.user = null;
                    startActivity(intent);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
