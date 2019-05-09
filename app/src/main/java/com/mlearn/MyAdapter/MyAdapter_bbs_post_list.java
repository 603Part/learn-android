package com.mlearn.MyAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mlearn.GlobalParams.GlobalParam;
import com.mlearn.HttpUtil.AskForInternet;
import com.mlearn.HttpUtil.ConstsUrl;
import com.mlearn.HttpUtil.ImageLoader;
import com.mlearn.Entity.BbsTheme;
import com.mlearn.MyActivity.Activity_bbs_post_list;
import com.mlearn.MyActivity.Activity_bbs_reply_list;
import com.mlearn.MyActivity.R;
import com.rey.material.app.BottomSheetDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mlearn.ZipUtil.ZipUtils.TAG;


public class MyAdapter_bbs_post_list extends RecyclerView.Adapter<MyAdapter_bbs_post_list.ViewHolder> {
    private ImageLoader imageLoader;
    private List<BbsTheme> bbsThemes;
    private Button dialog_del_btn;
    private Context context;

    public MyAdapter_bbs_post_list(List<BbsTheme> bbsThemes) {
        this.bbsThemes = bbsThemes;
        Log.e(TAG, "MyAdapter_bbs_post_list: 评论列表" + bbsThemes);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        /*****************************添加点击事件*****************************************************/
        holder.postView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                BbsTheme bbsTheme = bbsThemes.get(position);
                Intent intent = new Intent(v.getContext(), Activity_bbs_reply_list.class);
                intent.putExtra("bbsTheme", bbsTheme);
                v.getContext().startActivity(intent);
            }
        });
        /*****************************添加长按删除帖子事件*****************************************************/
        holder.postView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                context = v.getContext();
                int position = holder.getAdapterPosition();
                BbsTheme bbsTheme = bbsThemes.get(position);
                showDialog(v, bbsTheme.getPostID(), bbsTheme.getStudentNumber());
                return false;
            }
        });
        return holder;
    }

    private void showDialog(View v, final int postID, final String  studentNumber) {
        final BottomSheetDialog dialog = new BottomSheetDialog(v.getContext());
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_post_list, null);
        dialog.inDuration(300);
        dialog.outDuration(300);
        dialog.setContentView(dialogView);
        dialog_del_btn = dialogView.findViewById(R.id.dialog_post_delete_view);
        dialog_del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!studentNumber.equals(GlobalParam.user.getsId())) {
                    Toast.makeText(v.getContext(), "只能删除自己发布的问题", Toast.LENGTH_LONG).show();
                    return;
                }
                deletePost(v, postID);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deletePost(View v, final int postID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("确定删除吗？");
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<NameValuePair> paras = new ArrayList<NameValuePair>();
                paras.add(new BasicNameValuePair("operation", "deletePost"));
                paras.add(new BasicNameValuePair("postID", String.valueOf(postID)));
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

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BbsTheme bbsTheme = bbsThemes.get(position);
        String name = bbsTheme.getStudentName();
        String number = bbsTheme.getStudentNumber();  //学号
        String time = bbsTheme.getPostTime();
        int replyCount = bbsTheme.getReplyCount();
        String title = bbsTheme.getPostTitle();
        String content = bbsTheme.getPostContent();

        String url = ConstsUrl.BASE_URL + bbsTheme.getStudentPhotoURL();
        holder.userPhoto.setTag(url);
        imageLoader = new ImageLoader();
        imageLoader.showImageByAsyncTask(holder.userPhoto, url);//设置头像
        holder.userName.setText(name + ":" + number);
        holder.postTime.setText(time);
        holder.postTitle.setText(title);
        holder.postContent.setText(content);
        holder.replyCount.setText(replyCount + "回复");
    }

    @Override
    public int getItemCount() {
        return bbsThemes.size();
    }


    /************************************ViewHolder******************************************************/
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userPhoto;
        TextView userName;
        TextView postTime;//发帖时间
        TextView postTitle;//标题
        TextView postContent;//内容
        TextView replyCount;//回复数
        View postView;//用于添加点击事件

        public ViewHolder(View itemView) {
            super(itemView);

            postView = itemView;

            userPhoto = itemView.findViewById(R.id.post_item_user_photo);
            //          userName = itemView.findViewById(R.id.post_item_user_name);
            userName = itemView.findViewById(R.id.post_item_user_name);
            postTime = itemView.findViewById(R.id.post_item_send_time);
            postTitle = itemView.findViewById(R.id.post_item_title);
            postContent = itemView.findViewById(R.id.post_item_content);
            replyCount = itemView.findViewById(R.id.post_item_reply_count);
        }
    }

    /************************************网络相关**************************************************/
    public class MyAsyncTask extends AsyncTask<List<NameValuePair>, Void, String> {

        @Override
        protected String doInBackground(List<NameValuePair>[] lists) {
            return AskForInternet.post(ConstsUrl.BBS_URL, lists[0]);
        }

        //
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    Intent intent = new Intent(Activity_bbs_post_list.REFRESH_POST_LIST_NUM);
                    intent.putExtra("action", "delete");
                    context.sendBroadcast(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
