package com.mlearn.MyActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mlearn.GlobalParams.GlobalParam;
import com.mlearn.HttpUtil.AskForInternet;
import com.mlearn.HttpUtil.ConstsUrl;
import com.mlearn.HttpUtil.ImageLoader;
import com.mlearn.HttpUtil.StreamTool;
import com.mlearn.Entity.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Activity_personal_info extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Activity_personal_info";

    public static final String UPDATE_INFO = "UPDATE_INFO";

    private static int RESULT_LOAD_IMAGE = 1;//读取照片的标志，将其设为1
    private String pathString;//这是上传图片的url

    private Toolbar toolbar;
    private User user;
    private String studentNumber, name, sex, email, phone, signature, photo_url, userID, college, specialty;
    private ImageView photo_ImageView;
    private TextView studentNumber_TextView, name_TextView, sex_TextView;
    private TextView email_TextView, phone_TextView, signature_TextView;
    private TextView college_TextView, specialty_TextView;
    private ImageLoader imageLoader;
    private Intent intent;

    private Bitmap bitmap;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        toolbar = findViewById(R.id.personal_info_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initview();
        initEvent();
    }

    private void initEvent() {
        photo_ImageView.setOnClickListener(this);
        studentNumber_TextView.setOnClickListener(this);
        name_TextView.setOnClickListener(this);
        sex_TextView.setOnClickListener(this);
        email_TextView.setOnClickListener(this);
        phone_TextView.setOnClickListener(this);
        signature_TextView.setOnClickListener(this);
        college_TextView.setOnClickListener(this);
        specialty_TextView.setOnClickListener(this);
    }

    private void initview() {
        user = GlobalParam.user;
        studentNumber = user.getsId();
        name = user.getName();
        sex = user.getSex();
        email = user.getEmail();
        phone = user.getPhone();
        signature = user.getSignature();
        userID = String.valueOf(user.getUserId());
        photo_url = ConstsUrl.BASE_URL + user.getPhoto();
        college = user.getCollege();
        specialty = user.getSpecialty();

        photo_ImageView = findViewById(R.id.personal_image);
        studentNumber_TextView = findViewById(R.id.personal_studentNumber);
        name_TextView = findViewById(R.id.personal_name);
        sex_TextView = findViewById(R.id.personal_sex);
        email_TextView = findViewById(R.id.personal_email);
        phone_TextView = findViewById(R.id.personal_phone);
        signature_TextView = findViewById(R.id.personal_signature);
        college_TextView = findViewById(R.id.personal_college);
        specialty_TextView = findViewById(R.id.personal_specialty);

        photo_ImageView.setTag(photo_url);
        imageLoader = new ImageLoader();
        imageLoader.showImageByAsyncTask(photo_ImageView, photo_url);

        studentNumber_TextView.setText(studentNumber);
        name_TextView.setText(name);
        sex_TextView.setText(sex);
        email_TextView.setText(email);
        phone_TextView.setText(phone);
        signature_TextView.setText(signature);
        college_TextView.setText(college);
        specialty_TextView.setText(specialty);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://返回箭头
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_image:
                choosePhoto();
                break;
            case R.id.personal_studentNumber:
                showEditDialog("studentNumber");
                break;
            case R.id.personal_name:
                showEditDialog("name");
                break;
            case R.id.personal_sex:
                showSexDialog();
                break;
            case R.id.personal_email:
                showEditDialog("email");
                break;
            case R.id.personal_phone:
                showEditDialog("phone");
                break;
            case R.id.personal_signature:
                showEditDialog("signature");
                break;
            case R.id.personal_college:
                showEditDialog("college");
                break;
            case R.id.personal_specialty:
                showEditDialog("specialty");
                break;
        }
    }

    private void showEditDialog(final String str) {
        final EditText editText = new EditText(Activity_personal_info.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (str.equals("studentNumber")) {
            editText.setText(studentNumber);
            builder.setTitle("修改你的学号");
        } else if (str.equals("name")) {
            editText.setText(name);
            builder.setTitle("修改你的姓名");
        } else if (str.equals("email")) {
            editText.setText(email);
            builder.setTitle("修改你的邮箱");
        } else if (str.equals("phone")) {
            editText.setText(phone);
            builder.setTitle("修改你的电话");
        } else if (str.equals("signature")) {
            editText.setText(signature);
            builder.setTitle("修改你的签名");
        } else if (str.equals("college")) {
            editText.setText(college);
            builder.setTitle("修改你的签名");
        } else if (str.equals("specialty")) {
            editText.setText(specialty);
            builder.setTitle("修改你的签名");
        }
        builder.setView(editText);
        builder.setCancelable(false);
        builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String me = editText.getText().toString();
                List<NameValuePair> paras = new ArrayList<NameValuePair>();
                paras.add(new BasicNameValuePair("operation", "updateUser"));
                paras.add(new BasicNameValuePair("attribute", str));
                paras.add(new BasicNameValuePair("value", me));
                paras.add(new BasicNameValuePair("userID", userID));
                new MyAsyncTask().execute(paras);
                if (str.equals("studentNumber")) {
                    studentNumber_TextView.setText(me);
                    GlobalParam.user.setsId(me);
                } else if (str.equals("name")) {
                    name_TextView.setText(me);
                    GlobalParam.user.setName(me);
                } else if (str.equals("email")) {
                    email_TextView.setText(me);
                    GlobalParam.user.setEmail(me);
                } else if (str.equals("phone")) {
                    phone_TextView.setText(me);
                    GlobalParam.user.setPhone(me);
                } else if (str.equals("signature")) {
                    signature_TextView.setText(me);
                    GlobalParam.user.setSignature(me);
                } else if (str.equals("college")) {
                    college_TextView.setText(me);
                    GlobalParam.user.setCollege(me);
                } else if (str.equals("specialty")) {
                    specialty_TextView.setText(me);
                    GlobalParam.user.setSpecialty(me);
                }
                refresh();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showSexDialog() {
        final String item[] = {"男", "女"};
        int index = 0;//默认性别为男
        if (sex != null) {
            index = sex.equals("男") ? 0 : 1;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("性别");
        builder.setSingleChoiceItems(item, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<NameValuePair> paras = new ArrayList<NameValuePair>();
                paras.add(new BasicNameValuePair("operation", "updateUser"));
                paras.add(new BasicNameValuePair("attribute", "sex"));
                paras.add(new BasicNameValuePair("value", item[which].toString()));
                paras.add(new BasicNameValuePair("userID", userID));
                new MyAsyncTask().execute(paras);
                sex_TextView.setText(item[which]);
                GlobalParam.user.setSex(item[which]);
                refresh();
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    //选择照片
    private void choosePhoto() {
        intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        this.startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    /************************************照片相关**************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != data) {
            File CropPhoto = new File(getExternalCacheDir(), "Crop.jpg");//这个是创建一个截取后的图片路径和名称。
            try {
                if (CropPhoto.exists()) {
                    CropPhoto.delete();
                }
                CropPhoto.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent i = new Intent("com.android.camera.action.CROP");
            i.setDataAndType(data.getData(), "image/*");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            }
            i.putExtra("crop", "true");
            i.putExtra("scale", true);
            i.putExtra("aspectX", 1);
            i.putExtra("aspectY", 1);
            i.putExtra("outputX", 100);
            i.putExtra("outputY", 100);
            i.putExtra("return-data", false);
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(CropPhoto));

            this.startActivityForResult(i, 7);
        }
        if (requestCode == 7) {
            bitmap = null;
            try {
                if (data.getData() != null) {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "onActivityResult: " + bitmap);
            if (bitmap == null) {//如果用户取消了，就返回
                return;
            }
            photo_ImageView.setImageBitmap(bitmap);
            pathString = ConstsUrl.upLoad_photo_URL + "&userID="
                    + String.valueOf(user.getUserId()) + "&value=true";
//
            new AsyncTask<Bitmap, Void, String>() {

                @Override
                protected String doInBackground(Bitmap... params) {

                    String end = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "*****";
                    String newName = "pic.jpg";

                    HttpURLConnection conn;
                    String result = null;
                    try {
                        conn = (HttpURLConnection) new URL(pathString)
                                .openConnection();
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.setConnectTimeout(10000);
                        conn.setRequestMethod("POST");
                        conn.setDoOutput(true);
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("Charset", "UTF-8");
                        conn.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);
                        DataOutputStream ds = new DataOutputStream(
                                conn.getOutputStream());
                        ds.writeBytes(twoHyphens + boundary + end);
                        ds.writeBytes("Content-Disposition: form-data; "
                                + "name=\"file1\";filename=\"" + newName
                                + "\"" + end);
                        ds.writeBytes(end);

                        Bitmap bmpCompressed = Bitmap.createScaledBitmap(
                                params[0], 640, 480, true);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bmpCompressed.compress(Bitmap.CompressFormat.JPEG, 100,
                                bos);
                        byte[] data = bos.toByteArray();

                        ds.write(data);

                        ds.writeBytes(end);
                        ds.writeBytes(twoHyphens + boundary + twoHyphens
                                + end);

                        InputStream inputStream = conn.getInputStream();
                        byte[] read;
                        try {
                            read = StreamTool.read(inputStream);
                            result = new String(read, 0, read.length,
                                    "UTF-8");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return result;//获得返回的信息
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    Toast.makeText(Activity_personal_info.this, "头像上传成功", Toast.LENGTH_SHORT).show();
                    String url = ConstsUrl.BASE_URL + user.getPhoto();
                    GlobalParam.mCaches.remove(url);//清除原先的缓存
                    refresh();//发送广播修改主界面
                }
            }.execute(bitmap);
        }

    }

    /************************************刷新信息**************************************************/
    private void refresh() {
        initview();
        //发送广播，此广播注册在MainActivity中
        Intent intent = new Intent(Activity_personal_info.UPDATE_INFO);
        sendBroadcast(intent);
    }

    /************************************网络相关**************************************************/
    public class MyAsyncTask extends AsyncTask<List<NameValuePair>, Void, String> {

        @Override
        protected String doInBackground(List<NameValuePair>[] lists) {
            return AskForInternet.post(ConstsUrl.LOGIN_URL, lists[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                int code = jsonObject.getInt("code");
                if (code == 0)
                    Toast.makeText(Activity_personal_info.this, "修改失败",
                            Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
