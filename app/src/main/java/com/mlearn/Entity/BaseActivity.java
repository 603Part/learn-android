package com.mlearn.Entity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.mlearn.GlobalParams.GlobalParam;
import com.mlearn.HttpUtil.AskForInternet;
import com.mlearn.HttpUtil.ConstsUrl;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private long firstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
            if (System.currentTimeMillis()-firstTime > 2000){
                Toast.makeText(this, "再按一下退出", Toast.LENGTH_SHORT).show();
                firstTime=System.currentTimeMillis();
            } else {
                ActivityCollector.finishAll();
                List<NameValuePair> paras = new ArrayList<NameValuePair>();
                NameValuePair nameValuePair = new BasicNameValuePair("studentNumber", "unLogin");
                paras.add(nameValuePair);
                nameValuePair = new BasicNameValuePair("operation", "logout");
                paras.add(nameValuePair);
                new MyAsyncTask().execute(paras);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
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
                Log.e(TAG, "onPostExecute: "+s );
                JSONObject jsonObject = new JSONObject(s);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    ActivityCollector.finishAll();
                    ActivityCollector.removeAllActivity();
                    System.exit(0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
