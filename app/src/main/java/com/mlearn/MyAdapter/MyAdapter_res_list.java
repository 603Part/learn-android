package com.mlearn.MyAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mlearn.ZipUtil.ZipUtils;
import com.mlearn.Entity.MaterialInfo;
import com.mlearn.MyActivity.Activity_FileManager;
import com.mlearn.MyActivity.R;
import com.mlearn.Services.DownLoadResService;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class MyAdapter_res_list extends RecyclerView.Adapter<MyAdapter_res_list.ViewHolder> {
    private static final String TAG = "AIRCode";
    boolean isStop = true;//用来限制ui的变化
    private List<MaterialInfo> datas;
    private Context context;

    private List<ViewHolder> holderList;

    public MyAdapter_res_list(List<MaterialInfo> datas, Context context) {
        if (datas == null) datas = new ArrayList<>();
        this.datas = datas;
        this.context = context;
        if(this.holderList == null) {
            this.holderList = new ArrayList<ViewHolder>();
        } else {
            this.holderList.clear();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.res_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        this.holderList.add(holder);

        Log.e(TAG, "public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)");

        holder.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MaterialInfo materialInfo = datas.get(position);
                materialInfo.setPosition(position);
                Log.e("downBtn", "获得的position是：" + position);
                if (holder.btnStart.getText().equals("查看")) {
                    playZip(materialInfo.getSavePath(), v.getContext());
                } else if (holder.btnStart.getText().equals("下载")) {
                    if (materialInfo.getState().equals("") || materialInfo.getState().equals("暂停")) {
                        isStop = false;
                        holder.progressBar.setVisibility(View.VISIBLE);
                        holder.btnStart.setEnabled(false);
                        new sleepAsync().execute(holder.btnStop);
                        Intent intent = new Intent(context, DownLoadResService.class);
                        intent.setAction(DownLoadResService.ACTION_START);
                        intent.putExtra("materialInfo", materialInfo);
                        context.startService(intent);

                        materialInfo.setState("下载中");
                        notifyItemChanged(position, "AIRCode");//局部更新，否则按钮将会不停闪烁
                    }
                }
            }
        });
        holder.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MaterialInfo materialInfo = datas.get(position);
                materialInfo.setPosition(position);
                Log.e("downBtn", "获得的position是：" + position);
                if (holder.btnStop.getText().equals("删除")) {
                    boolean flag1 = ZipUtils.DeleteFile(materialInfo.getSavePath());
                    boolean flag2 = ZipUtils.DeleteFolder(materialInfo.getSaveDir() + materialInfo.getResTitle());
                    if (flag1 && flag2) {
                        Toast.makeText(context, "文件已删除", Toast.LENGTH_SHORT).show();
                        holder.btnStart.setEnabled(true);
                        holder.btnStart.setText("下载");
                        holder.btnStop.setEnabled(false);
                        holder.btnStop.setText("暂停");
                    } else {
                        Toast.makeText(context, "文件删除失败", Toast.LENGTH_SHORT).show();
                    }
                } else if (holder.btnStop.getText().equals("暂停")) {
                    if (materialInfo.getState().equals("下载中")) {
                        isStop = true;
                        holder.btnStop.setEnabled(false);
                        new sleepAsync().execute(holder.btnStart);
                        Intent intent = new Intent(context, DownLoadResService.class);
                        intent.setAction(DownLoadResService.ACTION_STOP);
                        intent.putExtra("materialInfo", materialInfo);
                        context.startService(intent);
                        materialInfo.setState("暂停");
                        notifyItemChanged(position, "AIRCode");//局部更新，否则按钮将会不停闪烁
                    }
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MaterialInfo materialInfo = datas.get(position);
                materialInfo.setPosition(position);
                playZip(materialInfo.getSavePath(), v.getContext());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MaterialInfo materialInfo = datas.get(position);
        holder.textName.setText(materialInfo.getResTitle());
        holder.textState.setText(materialInfo.getState());
        holder.progressBar.setProgress(materialInfo.getProgress());
//        String file_info = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(myHomework.getSubTime()) + " ";
        String file_info = materialInfo.getPublishTime().substring(0, materialInfo.getPublishTime().lastIndexOf(":"));
        long fileSize = materialInfo.getSize();
        if (fileSize > 1024 * 1024) {
            float size = fileSize / (1024f * 1024f);
            file_info += "\n" + new DecimalFormat("#.00").format(size) + "MB";
        } else if (fileSize >= 1024) {
            float size = fileSize / 1024;
            file_info += "\n" + new DecimalFormat("#.00").format(size) + "KB";
        } else {
            file_info += "\n" + fileSize + "B";
        }
        holder.resInfo.setText(file_info);

        if (materialInfo.isExistLocal()) {
            holder.btnStart.setEnabled(true);
            holder.btnStart.setText("查看");
            holder.btnStop.setEnabled(true);
            holder.btnStop.setText("删除");
        } else {
            holder.btnStart.setEnabled(true);
            holder.btnStart.setText("下载");
            holder.btnStop.setEnabled(false);
            holder.btnStop.setText("暂停");
        }
    }

    public void updateProgress(int position, int progress) {
        datas.get(position).setProgress(progress);
        if (progress == 100) {
            datas.get(position).setState("下载完成");

        } else if (isStop == false) {//就是没有暂停
            datas.get(position).setState("下载中");

        }
        notifyItemChanged(position, "AIRCode");//局部更新，否则按钮将会不停闪烁
    }

    public void refreshButon(int postion) {
        this.holderList.get(postion).btnStart.setText("下载");
        this.holderList.get(postion).btnStart.setEnabled(false);
        this.holderList.get(postion).btnStop.setText("暂停");
        this.holderList.get(postion).btnStop.setEnabled(true);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress(datas.get(position).getProgress());
            holder.textState.setText(datas.get(position).getState());
            if (holder.btnStart.getTag().equals("1")) {
                holder.btnStart.setEnabled(false);
                holder.btnStop.setEnabled(true);
                holder.btnStart.setText("下载");
                holder.btnStop.setText("暂停");
                holder.btnStart.setTag("0");
                datas.get(position).setState("下载中");
                holder.textState.setText("下载中");
            }
            if (datas.get(position).getState().equals("下载完成")) {
                holder.progressBar.setVisibility(View.GONE);
                holder.btnStart.setEnabled(true);
                holder.btnStop.setEnabled(true);
                holder.btnStart.setText("查看");
                holder.btnStop.setText("删除");
            }
        }
    }

    public void playZip(String param, Context context) {
        File file = new File(param);
        if (!file.exists()) {
            Toast.makeText(context, "请先点击下载再查看", Toast.LENGTH_SHORT).show();
            return;
        }

        String outputPath = param.substring(0, param.lastIndexOf("."));
        try {
            ZipUtils.UnZipFolder(param, outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "已经解压到" + outputPath, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(context, Activity_FileManager.class);
        intent.putExtra("RootPath", outputPath);
        intent.putExtra("LookRoot", true);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        if (datas == null) return 0;
        return datas.size();
    }

    /**************************************viewholder********************************************************/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textState;
        ProgressBar progressBar;
        Button btnStart;
        Button btnStop;
        TextView resInfo;
        TextView promptView;
        View itemView;

        public ViewHolder(View convertView) {
            super(convertView);

            itemView = convertView;
            textName = convertView.findViewById(R.id.res_resName);
            textState = convertView.findViewById(R.id.res_state);
            progressBar = convertView.findViewById(R.id.res_progressBar);
            btnStart = convertView.findViewById(R.id.res_start_btn);
            btnStop = convertView.findViewById(R.id.res_stop_btn);
            resInfo = convertView.findViewById(R.id.res_info);

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
            button.setEnabled(true);
        }
    }
}
