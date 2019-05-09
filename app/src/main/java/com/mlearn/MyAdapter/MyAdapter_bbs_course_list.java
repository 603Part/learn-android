package com.mlearn.MyAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mlearn.GlobalParams.GlobalParam;
import com.mlearn.HttpUtil.ConstsUrl;
import com.mlearn.HttpUtil.ImageLoader;
import com.mlearn.Entity.StudentCourse;
import com.mlearn.MyActivity.Activity_bbs_post_list;
import com.mlearn.MyActivity.R;

import java.util.HashMap;
import java.util.List;

public class MyAdapter_bbs_course_list extends RecyclerView.Adapter<MyAdapter_bbs_course_list.ViewHolder> {
    private List<StudentCourse> relationList;
    private ImageLoader imageLoader;
    private HashMap<Integer, Integer> courseStuMap, courseThemeNumMap;//键值对，键位课程id，值分别为学生数和帖子数
    private Intent intent;//用于跳转到测试题activity

    /*******************************************构造函数*****************************************************/
    public MyAdapter_bbs_course_list(List<StudentCourse> relationList, HashMap<Integer, Integer> courseStuMap, HashMap<Integer, Integer> courseThemeNumMap) {
        this.relationList = relationList;
        this.courseStuMap = courseStuMap;
        this.courseThemeNumMap = courseThemeNumMap;
    }

    /*****************************************需要实现的方法*************************************************/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bbs_course_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.bbsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                StudentCourse sc = relationList.get(position);
                int courseID = sc.getCourseID();
                int stuNum = courseStuMap.get(courseID);//学生人数
                int themeNum = courseThemeNumMap.get(courseID);//帖子数
                Bundle bundle = new Bundle();
                bundle.putInt("courseID", courseID);
                bundle.putInt("stuNum", stuNum);
                bundle.putInt("themeNum", themeNum);
                intent = new Intent(v.getContext(), Activity_bbs_post_list.class);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StudentCourse studentCourse = relationList.get(position);
        int courseID = studentCourse.getCourseID();
        String path = "res/course/" + String.valueOf(courseID) + "/cover.jpg";
        String url = ConstsUrl.BASE_URL + path;
        holder.coverImage.setTag(url);
        imageLoader = new ImageLoader();
        imageLoader.showImageByAsyncTask(holder.coverImage, url);//设置封面

        String courseName = GlobalParam.cNameMap.get(courseID);//通过课程id在全局变量的键值对中拿到课程名
        int stuNum = courseStuMap.get(courseID);//学生数量
        int noteNum = courseThemeNumMap.get(courseID);//帖子数量

        holder.bbsCourseName.setText(courseName);
        holder.bbsStuNumber.setText("关注：" + stuNum + "人");
        holder.bbsNoteNumber.setText("帖子：" + noteNum);
    }

    @Override
    public int getItemCount() {
        return relationList.size();
    }

    /************************************ViewHolder******************************************************/
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage;
        TextView bbsCourseName;
        TextView bbsStuNumber;//这门课学生的数量
        TextView bbsNoteNumber;//这门课帖子数量

        View bbsView;//用于添加点击事件

        public ViewHolder(View itemView) {
            super(itemView);

            bbsView = itemView;

            coverImage = itemView.findViewById(R.id.bbs_course_cover_view);
            bbsCourseName = itemView.findViewById(R.id.bbs_course_name_view);
            bbsStuNumber = itemView.findViewById(R.id.bbs_stu_num_view);
            bbsNoteNumber = itemView.findViewById(R.id.bbs_note_num_view);
        }
    }
}
