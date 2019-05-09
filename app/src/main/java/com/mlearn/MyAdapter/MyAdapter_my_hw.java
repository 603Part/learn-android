package com.mlearn.MyAdapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mlearn.Entity.StuHomework;
import com.mlearn.MyActivity.Activity_FileManager;
import com.mlearn.MyActivity.Activity_homework;
import com.mlearn.MyActivity.R;
import com.mlearn.Services.DownLoadMyHwService;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter_my_hw extends RecyclerView.Adapter<MyAdapter_my_hw.ViewHolder> {
    private static final String TAG = "MyAdapter_course_hw";
    boolean isStop = true;//用来限制ui的变化
    private List<StuHomework> myhomeworkList;
    private Context context;

    private StuHomework myHomework;
    private FragmentActivity fragmentActivity;  //专门用于显示是否删除的对话框
    private ViewHolder holder;


    public MyAdapter_my_hw(List<StuHomework> myhomeworkList, FragmentActivity fragmentActivity) {
        if (myhomeworkList == null) myhomeworkList = new ArrayList<>();
        this.myhomeworkList = myhomeworkList;
        this.context = fragmentActivity.getApplicationContext();
        this.fragmentActivity = fragmentActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_homework_item, parent, false);
        holder = new ViewHolder(view);

        holder.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                StuHomework myHomework = myhomeworkList.get(position);
                myHomework.setPosition(position);
                Log.e("downBtn", "获得的position是：" + position);
                if (holder.btnStart.getText().equals("查看")) {
                    if (!playMyHwFile(myHomework.getSavePath(), v.getContext())) {
                        holder.btnStart.setText("下载");
                    }
                } else if (holder.btnStart.getText().equals("下载")) {
                    if (myHomework.getState().equals("") || myHomework.getState().equals("暂停")) {
                        isStop = false;
                        holder.progressBar.setVisibility(View.VISIBLE);
                        holder.btnStart.setText("暂停");
                        holder.btnDel.setEnabled(false);
                        Intent intent = new Intent(context, DownLoadMyHwService.class);
                        intent.setAction(DownLoadMyHwService.ACTION_START);
                        intent.putExtra("myHomework", myHomework);
                        context.startService(intent);

                        Log.e(TAG, "onClick: ");
                        myHomework.setState("下载中");
                        notifyItemChanged(position, "AIRCode");//局部更新，否则按钮将会不停闪烁
                    }
                } else if (holder.btnStart.getText().equals("暂停")) {
                    if (myHomework.getState().equals("下载中")) {
                        isStop = true;
                        holder.btnStart.setText("下载");
                        holder.btnDel.setEnabled(true);
                        Intent intent = new Intent(context, DownLoadMyHwService.class);
                        intent.setAction(DownLoadMyHwService.ACTION_STOP);
                        intent.putExtra("myHomework", myHomework);
                        context.startService(intent);

                        myHomework.setState("暂停");
                        notifyItemChanged(position, "AIRCode");//局部更新，否则按钮将会不停闪烁
                    }
                }
            }
        });
        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                StuHomework myHomework = myhomeworkList.get(position);
                myHomework.setPosition(position);
                Log.e("downBtn", "获得的position是：" + position);
                if (holder.btnDel.getText().equals("删除")) {
                    MyAdapter_my_hw.this.myHomework = myHomework;
                    deleteDialog();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                StuHomework myHomework = myhomeworkList.get(position);
                myHomework.setPosition(position);
                playMyHwFile(myHomework.getSavePath(), v.getContext());
            }
        });

        return holder;
    }

    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
        builder.setCancelable(false);
        File file = new File(MyAdapter_my_hw.this.myHomework.getSavePath());
        if (file.exists()) {
            builder.setTitle("仅删除本地下载文件或删除整个提交，确认删除吗？");
            builder.setNegativeButton("删除文件", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Activity_homework.DELETE_MY_HW_FILE);
                    intent.putExtra("shwID", MyAdapter_my_hw.this.myHomework.getShwID());
                    intent.putExtra("savePath", MyAdapter_my_hw.this.myHomework.getSavePath());
                    fragmentActivity.sendBroadcast(intent);
                }
            });
            builder.setNeutralButton("删除提交", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Activity_homework.DELETE_MY_HW);
                    intent.putExtra("shwID", MyAdapter_my_hw.this.myHomework.getShwID());
                    intent.putExtra("savePath", MyAdapter_my_hw.this.myHomework.getSavePath());
                    fragmentActivity.sendBroadcast(intent);
                }
            });
            builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

        } else {
            builder.setTitle("删除整个提交，确认删除吗？");
            builder.setNeutralButton("删除提交", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Activity_homework.DELETE_MY_HW);
                    intent.putExtra("shwID", MyAdapter_my_hw.this.myHomework.getShwID());
                    intent.putExtra("savePath", MyAdapter_my_hw.this.myHomework.getSavePath());
                    fragmentActivity.sendBroadcast(intent);
                    Log.e(TAG, "onClick: " + intent.getAction());
                }
            });
            builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }

        builder.create().show();
    }

    public void refreshButttonText(String text) {
//        this.holder.btnStart.setText(text);
    }

    public void refreshButon() {
        this.holder.btnStart.setText("暂停");
        this.holder.btnStart.setEnabled(true);
        this.holder.btnDel.setText("删除");
        this.holder.btnDel.setEnabled(false);
    }


    @Override
    public void onBindViewHolder(final MyAdapter_my_hw.ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder: 下拉刷新1");
        final StuHomework myHomework = myhomeworkList.get(position);
        holder.textName.setText(myHomework.getStuWorkTitle());
        holder.textState.setText(myHomework.getState());
        holder.textTitle.setText(myHomework.getHwTitle());
        holder.progressBar.setProgress(myHomework.getProgress());
//        String file_info = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(myHomework.getSubTime()) + " ";
        String file_info = myHomework.getSubTime().substring(0, myHomework.getSubTime().lastIndexOf(" "));
        long fileSize = myHomework.getSize();
        if (fileSize > 1024 * 1024) {
            float size = fileSize / (1024f * 1024f);
            file_info += "\n" + new DecimalFormat("#.00").format(size) + "MB";
        } else if (fileSize >= 1024) {
            float size = fileSize / 1024;
            file_info += "\n" + new DecimalFormat("#.00").format(size) + "KB";
        } else {
            file_info += "\n" + fileSize + "B";
        }
        holder.myhwInfo.setText(file_info);

        if (myHomework.isExistLocal()) {
            Log.e(TAG, "onBindViewHolder: " + myHomework.getSavePath());
            holder.btnStart.setEnabled(true);
            holder.btnStart.setText("查看");
            holder.btnDel.setEnabled(true);
            holder.btnDel.setText("删除");
        } else if (holder.btnStart.getText().equals("下载")) {
            holder.btnStart.setEnabled(true);
            holder.btnStart.setText("下载");
            holder.btnDel.setEnabled(true);
            holder.btnDel.setText("删除");
        }
    }

    public void updateProgress(int position, int progress) {
        myhomeworkList.get(position).setProgress(progress);
        if (progress == 100) {
            myhomeworkList.get(position).setState("下载完成");
        } else if (isStop == false) {//就是没有暂停
            myhomeworkList.get(position).setState("下载中");
        }
        notifyItemChanged(position, "AIRCode");//局部更新，否则按钮将会不停闪烁
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress(myhomeworkList.get(position).getProgress());
            holder.textState.setText(myhomeworkList.get(position).getState());
            if (holder.btnStart.getTag().equals("1")) {
                holder.btnStart.setEnabled(true);
                holder.btnDel.setEnabled(false);
                holder.btnStart.setText("暂停");
                holder.btnDel.setText("删除");
                holder.btnStart.setTag("0");
                myhomeworkList.get(position).setState("下载中");
                holder.textState.setText("下载中");
            }
            if (myhomeworkList.get(position).getState().equals("下载完成")) {
                holder.progressBar.setVisibility(View.GONE);
                holder.btnStart.setEnabled(true);
                holder.btnDel.setEnabled(true);
                holder.btnStart.setText("查看");
                holder.btnDel.setText("删除");
            }
        }
    }

    public boolean playMyHwFile(String param, Context context) {
        File file = new File(param);
        if (!file.exists()) {
            Toast.makeText(context, "请先点击下载再查看", Toast.LENGTH_SHORT).show();
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, "com.example.mlearn.provider", file);
            intent.setDataAndType(contentUri, Activity_FileManager.getMIMEType(file));
            Log.e(TAG, "playMyHwFile: " + Activity_FileManager.getMIMEType(file));
        } else {
            intent.setDataAndType(Uri.fromFile(file.getParentFile()), Activity_FileManager.getMIMEType(file));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "未知文件类型", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public int getItemCount() {
        if (myhomeworkList == null) return 0;
        return myhomeworkList.size();
    }

    /**********************************viewholder*******************************************************/
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textState;
        TextView textTitle;
        ProgressBar progressBar;
        Button btnStart;
        Button btnDel;
        TextView myhwInfo;
        TextView promptView;
        View itemView;

        public ViewHolder(View convertView) {
            super(convertView);

            itemView = convertView;
            textName = convertView.findViewById(R.id.my_hw_sub_title);
            textState = convertView.findViewById(R.id.my_hw_sub_state);
            textTitle = convertView.findViewById(R.id.my_hw_title);
            progressBar = convertView.findViewById(R.id.my_hw_sub_progressBar);
            btnStart = convertView.findViewById(R.id.my_hw_sub_start_btn);
            btnDel = convertView.findViewById(R.id.my_hw_sub_del_btn);
            myhwInfo = convertView.findViewById(R.id.my_hw_sub_info);
            promptView = convertView.findViewById(R.id.my_hw_title);

            btnStart.setTag("1");//这是用于初始化的标志
        }
    }

    public class sleepAsync extends AsyncTask<Button, Void, Button> {

        @Override
        protected Button doInBackground(Button... buttons) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return buttons[0];
        }

        @Override
        protected void onPostExecute(Button button) {
            super.onPostExecute(button);
            button.setEnabled(false);
        }
    }
}
