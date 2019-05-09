package com.mlearn.MyActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mlearn.Encrypt.Encrypt;
import com.mlearn.Entity.ActivityCollector;
import com.mlearn.Entity.BaseActivity;
import com.mlearn.GlobalParams.GlobalParam;
import com.mlearn.HttpUtil.AskForInternet;
import com.mlearn.HttpUtil.ConstsUrl;
import com.mlearn.Entity.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class   LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private EditText studentNumber_EditText, password_EditText;
    private AppCompatButton login_Button;
    private User user;
    private Intent intent;
    private CheckBox remember_CheckBox;
    private TextView register_TextView;
    private SharedPreferences sp;
    private SharedPreferences.Editor sp_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.materil_login_layout);

        init();
    }

    private void init() {
        studentNumber_EditText = findViewById(R.id.input_studentNumber);
        password_EditText = findViewById(R.id.input_password);
        login_Button = findViewById(R.id.btn_login);
        remember_CheckBox = findViewById(R.id.login_checkBox);
        register_TextView = findViewById(R.id.link_register);
        user = new User();
        sp = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        sp_editor = sp.edit();
        String str1 = sp.getString("studentNumber", null);
        String str2 = sp.getString("password", null);
        if (str1 == null || str2 == null) {
            remember_CheckBox.setChecked(false);
        } else {
            studentNumber_EditText.setText(str1);
            password_EditText.setText(str2);
            remember_CheckBox.setChecked(true);
        }
        login_Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        register_TextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void startRegister() {
        intent = new Intent(LoginActivity.this, RegisteredActivity.class);
        startActivity(intent);
    }

    private void attemptLogin() {
        Log.e(TAG, "点击登录按钮");
        String studentNumber = studentNumber_EditText.getText().toString();
        String password = password_EditText.getText().toString();
        if (remember_CheckBox.isChecked()) {
            sp_editor.putString("studentNumber", studentNumber);
            sp_editor.putString("password", password);
            sp_editor.commit();
        } else {
            sp_editor.remove("studentNumber");
            sp_editor.remove("password");
            sp_editor.commit();
        }
        List<NameValuePair> paras = new ArrayList<NameValuePair>();
        NameValuePair nameValuePair = new BasicNameValuePair("studentNumber", studentNumber);
        paras.add(nameValuePair);
        nameValuePair = new BasicNameValuePair("password", Encrypt.getSecretCode(studentNumber, password));
        paras.add(nameValuePair);
        nameValuePair = new BasicNameValuePair("operation", "login");
        paras.add(nameValuePair);

        new MyAsyncTask().execute(paras);
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
                Log.e(TAG, "onPostExecute: " + s );
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } else if (code == 1) {
                    GlobalParam.USER_LOGIN_STATE = 1;//表示成功登录
                    Toast.makeText(getApplicationContext(), "登录成功",
                            Toast.LENGTH_LONG).show();
                    //从服务器中获取user的属性 放入 user
                    jsonObject = jsonObject.getJSONObject("user");
                    user.setUserId(jsonObject.optInt("userId"));
                    user.setsId(jsonObject.optString("sId"));
                    user.setPassword(jsonObject.optString("password"));
                    user.setName(jsonObject.optString("name"));
                    user.setSex(jsonObject.optString("sex"));
                    user.setEmail(jsonObject.optString("email"));
                    user.setPhone(jsonObject.optString("phone"));
                    user.setPhoto(jsonObject.optString("photo"));
                    user.setSignature(jsonObject.optString("signature"));
                    user.setType(jsonObject.optInt("type"));
                    user.setIslogin(User.LOGIN);
                    user.setCollege(jsonObject.optString("college"));
                    user.setSpecialty(jsonObject.optString("specialty"));
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    GlobalParam.user = user;
                    startActivity(intent);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

