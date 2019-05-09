package com.mlearn.MyAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mlearn.MyActivity.R;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FileListAdapter extends BaseAdapter {
    private ArrayList<File> files;
    private boolean isRoot;
    private LayoutInflater mInflater;

    public FileListAdapter(Context context, ArrayList<File> files, boolean isRoot) {
        this.files = files;
        this.isRoot = isRoot;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.file_list_item, null);
            convertView.setTag(viewHolder);
            viewHolder.file_icon_ImageView = convertView.findViewById(R.id.file_icon);
            viewHolder.file_title_TextView = convertView.findViewById(R.id.file_title);
            viewHolder.file_info_TextView = convertView.findViewById(R.id.file_info);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        File file = (File) getItem(position);
        if (position == 0 && !isRoot) {
            viewHolder.file_icon_ImageView.setImageResource(R.drawable.ic_folder);
            viewHolder.file_title_TextView.setText("...");
            viewHolder.file_info_TextView.setVisibility(View.GONE);
        } else {
            String fileName = file.getName();

            viewHolder.file_title_TextView.setText(fileName);
            if (file.isDirectory()) {
                viewHolder.file_icon_ImageView.setImageResource(R.drawable.ic_folder);
                viewHolder.file_info_TextView.setText("(" + file.listFiles().length + "项)");
            } else {
                viewHolder.file_icon_ImageView.setImageResource(R.drawable.ic_file);
                String file_info = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(file.lastModified()) + " ";
                long fileSize = file.length();
                if (fileSize > 1024 * 1024) {
                    float size = fileSize / (1024f * 1024f);
                    file_info += " " + new DecimalFormat("#.00").format(size) + "MB";
                } else if (fileSize >= 1024) {
                    float size = fileSize / 1024;
                    file_info += " " + new DecimalFormat("#.00").format(size) + "KB";
                } else {
                    file_info += " " + fileSize + "B";
                }
                viewHolder.file_info_TextView.setText(file_info);
            }
        }
        return convertView;
    }


    class ViewHolder {
        ImageView file_icon_ImageView;
        TextView file_title_TextView;
        TextView file_info_TextView; //文件夹为项目数,文件为“日期 大小”
    }
}