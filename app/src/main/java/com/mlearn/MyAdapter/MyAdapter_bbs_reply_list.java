package com.mlearn.MyAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
import com.mlearn.Entity.ReplyPost;
import com.mlearn.MyActivity.R;
import com.rey.material.app.BottomSheetDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.biubiubiu.justifytext.library.JustifyTextView;


public class MyAdapter_bbs_reply_list extends RecyclerView.Adapter<MyAdapter_bbs_reply_list.ViewHolder> {
    private ImageLoader imageLoader;
    private List<ReplyPost> replyPosts;
    private Button dialog_del_btn;
    private Context context;

    public MyAdapter_bbs_reply_list(List<ReplyPost> replyPosts) {
        this.replyPosts = replyPosts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bbs_reply_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                context = v.getContext();
                int position = holder.getAdapterPosition();
                ReplyPost replyPost = replyPosts.get(position);
                showDialog(v, replyPost.getReplyID(), replyPost.getUserNumber());
                return false;
            }
        });

        return holder;
    }

    private void showDialog(View v, final int replyID, final String userNumber) {
        final BottomSheetDialog dialog = new BottomSheetDialog(v.getContext());
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_reply_list, null);
        dialog.inDuration(300);
        dialog.outDuration(300);
        dialog.setContentView(dialogView);
        dialog_del_btn = dialogView.findViewById(R.id.dialog_reply_delete_view);
        dialog_del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userNumber.equals(GlobalParam.user.getsId())) {
                    Toast.makeText(v.getContext(), "只能删除自己的回复", Toast.LENGTH_LONG).show();
                    return;
                }
                deleteRep(v, replyID);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteRep(View v, final int replyID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setMessage("确定删除吗？");
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<NameValuePair> paras = new ArrayList<NameValuePair>();
                paras.add(new BasicNameValuePair("operation", "deleteReply"));
                paras.add(new BasicNameValuePair("replyID", String.valueOf(replyID)));
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
        ReplyPost replyPost = replyPosts.get(position);
        String userName = replyPost.getUserName();
        String userNumber = replyPost.getUserNumber();
        String content = replyPost.getReplyContent();
        String time = replyPost.getReplyTime();
        int userType = replyPost.getUserType();
        String url = ConstsUrl.BASE_URL+replyPost.getUserPhotoURL();
        if (userType == 1) {//如果是老师
            holder.userSignature.setText("老师");
        } else if (userType == 0) {//如果是学生
            holder.userSignature.setText("学生");
        }
        holder.userNumberView.setText(userName+":"+userNumber);
        holder.imageView.setTag(url);
        imageLoader = new ImageLoader();
        imageLoader.showImageByAsyncTask(holder.imageView, url);//设置头像

        holder.replyTime.setText(time);
        holder.justifyTextView.setText(content + "\n");//必须加上换行，否则最后一行格式不正确
    }

    @Override
    public int getItemCount() {
        return replyPosts.size();
    }

    /************************************ViewHolder******************************************************/
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView userNumberView;
        TextView userSignature;
        TextView replyTime;
        JustifyTextView justifyTextView;//用于显示内容

        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.reply_item_user_photo);
            userNumberView = itemView.findViewById(R.id.reply_item_user_name);
            userSignature = itemView.findViewById(R.id.reply_item_user_signature);
            replyTime = itemView.findViewById(R.id.reply_item_send_time);
            justifyTextView = itemView.findViewById(R.id.reply_item_just_content);
            view = itemView;
        }
    }

    /************************************网络相关**************************************************/
    public class MyAsyncTask extends AsyncTask<List<NameValuePair>, Void, String> {

        @Override
        protected String doInBackground(List<NameValuePair>[] lists) {
            return AskForInternet.post(ConstsUrl.BBS_URL, lists[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    Intent intent = new Intent("refreshReply");
                    context.sendBroadcast(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
