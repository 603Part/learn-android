package com.mlearn.MyAdapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mlearn.GlobalParams.GlobalParam;
import com.mlearn.HttpUtil.ConstsUrl;
import com.mlearn.HttpUtil.ImageLoader;
import com.mlearn.Entity.Course;
import com.mlearn.MyActivity.Activity_course_detail;
import com.mlearn.MyActivity.Activity_course_res_list;
import com.mlearn.MyActivity.Activity_homework;
import com.mlearn.MyActivity.R;

import java.util.List;

public class MyAdaper_my_course extends RecyclerView.Adapter<MyAdaper_my_course.ViewHolder> {
    private static final String TAG = "MyAdaper_my_course";

    private ImageLoader imageLoader;
    private List<Course> courses;

    public MyAdaper_my_course(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Course course = courses.get(position);
                Intent intent;
                if (GlobalParam.state.equals("course")) {
                    intent = new Intent(v.getContext(), Activity_course_detail.class);
                    intent.putExtra("course", course);
                    v.getContext().startActivity(intent);
                } else if (GlobalParam.state.equals("resource")) {
                    intent = new Intent(v.getContext(), Activity_course_res_list.class);
                    intent.putExtra("course", course);
                    v.getContext().startActivity(intent);
                } else if (GlobalParam.state.equals("homework")) {
                    intent = new Intent(v.getContext(), Activity_homework.class);
                    intent.putExtra("course", course);
                    v.getContext().startActivity(intent);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course course = courses.get(position);
        String coverUrl = ConstsUrl.BASE_URL + course.getCourseUrl();
        holder.cCover.setTag(coverUrl);
        imageLoader = new ImageLoader();
        imageLoader.showImageByAsyncTask(holder.cCover, coverUrl);
        holder.cName.setText(course.getCourseName());
        holder.teacherName.setText(course.getTeacherName());
        holder.cAbs.setText(course.getCourseAbstract());
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    /**************************************************************************************/
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cName, teacherName, cAbs;
        ImageView cCover;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            cName = itemView.findViewById(R.id.course_name_view);
            teacherName = itemView.findViewById(R.id.course_teacher_view);
            cAbs = itemView.findViewById(R.id.course_abstract_view);
            cCover = itemView.findViewById(R.id.course_cover_view);
        }
    }
}
