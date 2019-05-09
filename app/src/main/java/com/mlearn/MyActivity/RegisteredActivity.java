package com.mlearn.MyActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mlearn.Encrypt.Encrypt;
import com.mlearn.HttpUtil.AskForInternet;
import com.mlearn.HttpUtil.ConstsUrl;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegisteredActivity extends AppCompatActivity {
    private EditText register_studentNumber_EditText, register_name_EditText, password_one_EditText, password_two_EditText;
    private AppCompatButton register_Button;
    private ImageView rPhoto;
    private TextView login_TextView;
    private String rStudentNumber, rName, rPassOne, rPassTwo;
    private TextInputLayout email_TextInputLayout, password_TextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_registered);
        init();
    }

    private void init() {
        register_studentNumber_EditText = findViewById(R.id.input_register_studentNumber);
        register_name_EditText = findViewById(R.id.input_register_name);
        password_one_EditText = findViewById(R.id.input_register_password);
        password_two_EditText = findViewById(R.id.input_register_password_confirm);
        register_Button = findViewById(R.id.btn_register);
        login_TextView = findViewById(R.id.link_login);
        email_TextInputLayout = findViewById(R.id.input_layout_register_studentNumber);
        password_TextInputLayout = findViewById(R.id.input_layout_register_password);
        rPhoto = findViewById(R.id.input_layout_register_photo);
        getInputInfo();
        rPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisteredActivity.this, "注册后可更改头像", Toast.LENGTH_LONG);
            }
        });
        register_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
        login_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        register_studentNumber_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    email_TextInputLayout.setError("");
                } else {
                    getInputInfo();
                    if (rStudentNumber.length() != 6) {
                        email_TextInputLayout.setError("学号格式有误");
                    }
                }
            }
        });
        password_two_EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    password_TextInputLayout.setError("");
                } else {
                    getInputInfo();
                    if (!rPassOne.equals(rPassTwo)) {
                        password_TextInputLayout.setError("两次密码不一样");
                    }
                }
            }
        });
    }

    private void getInputInfo() {
        rStudentNumber = register_studentNumber_EditText.getText().toString();
        rName = register_name_EditText.getText().toString();
        rPassOne = password_one_EditText.getText().toString();
        rPassTwo = password_two_EditText.getText().toString();
    }

    private void attemptRegister() {
        getInputInfo();
        email_TextInputLayout.setError("");
        password_TextInputLayout.setError("");
        if (rStudentNumber.length() != 6) {
            email_TextInputLayout.setError("学号格式不正确");
            return;
        }
        if (!rPassOne.equals(rPassTwo)) {
            password_TextInputLayout.setError("两次密码不一样");
            return;
        }
        List<NameValuePair> paras = new ArrayList<NameValuePair>();
        NameValuePair nameValuePair = new BasicNameValuePair("studentNumber", rStudentNumber);
        paras.add(nameValuePair);
        nameValuePair = new BasicNameValuePair("name", rName);
        paras.add(nameValuePair);
        nameValuePair = new BasicNameValuePair("password", Encrypt.getSecretCode(rStudentNumber, rPassOne));
        paras.add(nameValuePair);
        nameValuePair = new BasicNameValuePair("operation", "register");
        paras.add(nameValuePair);
        new AsyncTask<List<NameValuePair>, Void, String>() {
            @Override
            protected String doInBackground(List<NameValuePair>[] lists) {
                return AskForInternet.post(ConstsUrl.LOGIN_URL, lists[0]);
            }

            @Override
            protected void onPostExecute(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        Toast.makeText(getApplicationContext(), "该学号已被注册", Toast.LENGTH_SHORT).show();
                    } else if (code == 1) {
                        Toast.makeText(getApplicationContext(), "注册成功",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(paras);
    }

}
