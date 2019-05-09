package com.mlearn.MyAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mlearn.GlobalParams.GlobalParam;
import com.mlearn.HttpUtil.ConstsUrl;
import com.mlearn.HttpUtil.ImageLoader;
import com.mlearn.Entity.StudentCourse;
import com.mlearn.MyActivity.Activity_test_answer;
import com.mlearn.MyActivity.Activity_test_question;
import com.mlearn.MyActivity.R;

import java.util.List;

public class MyAdapter_test_course_list extends RecyclerView.Adapter<MyAdapter_test_course_list.ViewHolder> {
    private String TAG = "AIRCode";

    private List<StudentCourse> relationList;
    private ImageLoader imageLoader;
    private Intent intent;//用于跳转到测试题activity

    public MyAdapter_test_course_list(List<StudentCourse> relationList) {
        this.relationList = relationList;
    }

    @Override
    public MyAdapter_test_course_list.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_course_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.testView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                StudentCourse sc = relationList.get(position);
                String ans = sc.getStudentAnswer();
                GlobalParam.courseID = sc.getCourseID();//修改全局变量中的课程id
                if (ans.equals("")) {//没有回答，跳转到答题部分
                    intent = new Intent(v.getContext(), Activity_test_question.class);
                    v.getContext().startActivity(intent);
                } else {//已作答，跳转到答案部分
                    Log.e("AIRCode", "已作答，跳转到答案部分");
                    intent = new Intent(v.getContext(), Activity_test_answer.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("studentAnswer", sc.getStudentAnswer());
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(MyAdapter_test_course_list.ViewHolder holder, int position) {
        StudentCourse studentCourse = relationList.get(position);
        String path = "res/course/" + String.valueOf(studentCourse.getCourseID()) + "/cover.jpg";
        String url = ConstsUrl.BASE_URL + path;
        holder.coverImage.setTag(url);
        imageLoader = new ImageLoader();
        imageLoader.showImageByAsyncTask(holder.coverImage, url);//设置封面

        int courseID = studentCourse.getCourseID();
        int studentGrade = studentCourse.getStudentGrade();
        String studentAnswer = studentCourse.getStudentAnswer();
        String courseName = GlobalParam.cNameMap.get(courseID);//通过课程id在全局变量的键值对中拿到课程名
        String testNumber = String.valueOf(studentCourse.getTestNumber());

        holder.testCourseName.setText(courseName);
        if (studentAnswer.length() == 0) {//0说明没考
            holder.testCourseState.setText("测试状态：未测试");
        } else {
            holder.testCourseState.setText("测试状态：已测试");
        }
        holder.testCourseNumber.setText(testNumber + "题");
        holder.testCourseGrade.setText(studentGrade + "分");
    }

    @Override
    public int getItemCount() {
        return relationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage;
        TextView testCourseState;
        TextView testCourseGrade;
        TextView testCourseName;
        TextView testCourseNumber;

        View testView;//用于添加点击事件

        public ViewHolder(View itemView) {
            super(itemView);

            testView = itemView;

            coverImage = itemView.findViewById(R.id.test_course_cover_view);
            testCourseState = itemView.findViewById(R.id.test_course_state);
            testCourseGrade = itemView.findViewById(R.id.test_course_grade);
            testCourseName = itemView.findViewById(R.id.test_course_name_view);
            testCourseNumber = itemView.findViewById(R.id.test_course_number);
        }
    }
}
