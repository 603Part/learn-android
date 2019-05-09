package com.mlearn.MyAdapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mlearn.Entity.Test;
import com.mlearn.MyActivity.R;

import java.util.List;

/**
 * Created by AIRCode on 2018/11/13.
 */

public class MyAdapter_test_result_item extends RecyclerView.Adapter<MyAdapter_test_result_item.ViewHolder> {
    private static final String TAG = "AIRCode";

    private List<Test> testList;
    private Context context;


    /*************************************构造函数*************************************************/
    public MyAdapter_test_result_item(List<Test> testList, Context context) {
        this.testList = testList;
        this.context = context;
    }

    /*************************************实现方法*************************************************/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_item, parent, false);
        final MyAdapter_test_result_item.ViewHolder holder = new MyAdapter_test_result_item.ViewHolder(view);
        ;
        return holder;
    }

    private void disableRadio(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setClickable(false);
        }
        radioGroup.setOnCheckedChangeListener(null);
    }

    private void disableCheckBox(LinearLayout linearLayout) {
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            ((CheckBox) linearLayout.getChildAt(i)).setClickable(false);
            ((CheckBox) linearLayout.getChildAt(i)).setOnCheckedChangeListener(null);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Test test = testList.get(position);
        if (test.getUserAnswer().equals("")) {//用户未作答则清空选项,避免显示错乱
            holder.radioGroup_tf.clearCheck();
            holder.radioGroup_select.clearCheck();
            int checkBox_cnt = holder.linearLayout_mutiselect.getChildCount();
            for (int i = 0; i < checkBox_cnt; i++) {
                ((CheckBox) holder.linearLayout_mutiselect.getChildAt(i)).setChecked(false);
            }
        }
        if (test.getType() == Test.PAN_DUAN) {
            holder.testQuestion.setText(test.getTestContent());
            String[] options = test.getTestOption().trim().split(";");

            char option = 'A';
            RadioButton option_RadioButton;
            for (int i = 0; i < options.length; i++) {
                option_RadioButton = new RadioButton(context);
                option_RadioButton.setText(option + ":" + options[i]);
                option = (char) ((int) option + 1);
                holder.radioGroup_tf.addView(option_RadioButton);
            }

            if (!test.getUserAnswer().equals("未作答")) {//如果显示的不是未作答，则是查看答案，则radiobutton显示用户的答案
                int index = (int) (test.getUserAnswer().charAt(0) - 'A');
                RadioButton radioButton = (RadioButton) holder.radioGroup_tf.getChildAt(index);
                radioButton.setChecked(true);
            }
        } else if (test.getType() == Test.DAN_XUAN) {
            String[] options = test.getTestOption().trim().split(";");

            char option = 'A';
            RadioButton option_RadioButton;
            for (int i = 0; i < options.length; i++) {
                option_RadioButton = new RadioButton(context);
                option_RadioButton.setText(option + ":" + options[i]);
                option = (char) ((int) option + 1);
                holder.radioGroup_tf.addView(option_RadioButton);
            }

            if (!test.getUserAnswer().equals("未作答")) {//如果显示的不是未作答，则是查看答案，则radiobutton显示用户的答案
                int index = (int) (test.getUserAnswer().charAt(0) - 'A');
                RadioButton radioButton = (RadioButton) holder.radioGroup_tf.getChildAt(index);
                radioButton.setChecked(true);
            }
        } else if (test.getType() == Test.DUO_XUAN) {
            String[] options = test.getTestOption().trim().split(";");

            char option = 'A';
            CheckBox option_CheckBox;
            for (int i = 0; i < options.length; i++) {
                option_CheckBox = new CheckBox(context);
                option_CheckBox.setText(option + ":" + options[i]);
                option = (char) ((int) option + 1);
                holder.linearLayout_mutiselect.addView(option_CheckBox);
            }

            if (!test.getUserAnswer().equals("未作答")) {//如果显示的不是未作答，则是查看答案，则radiobutton显示用户的答案
                String[] answers = test.getUserAnswer().trim().split("");
                for (int i = 1; i < answers.length; i++) {
                    int index = (int) (answers[i].charAt(0) - 'A');
                    CheckBox checkBox = (CheckBox) holder.linearLayout_mutiselect.getChildAt(index);
                    checkBox.setChecked(true);
                }
            }
        }

        holder.cutLine.setVisibility(View.VISIBLE);
        holder.showResult.setVisibility(View.VISIBLE);
        holder.showResult.setText("正确答案是：" + test.getTestAnswer() + "  你的答案是：" + test.getUserAnswer());
        if (!test.getTestAnswer().equals(test.getUserAnswer())) {
            holder.showResult.setTextColor(Color.RED);
        }
        disableRadio(holder.radioGroup_select);
        disableRadio(holder.radioGroup_tf);
        disableCheckBox(holder.linearLayout_mutiselect);
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    @Override
    public int getItemViewType(int position) {//必须重载此方法才能避免错乱
        return position;
    }

    /*************************************ViewHolder*************************************************/
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView testQuestion;
        RadioGroup radioGroup_tf;
        RadioGroup radioGroup_select;
        LinearLayout linearLayout_mutiselect;

        View cutLine;//分割线
        TextView showResult;//显示用户答案

        View testView;//用于添加点击事件

        public ViewHolder(View itemView) {
            super(itemView);
            testView = itemView;

            testQuestion = itemView.findViewById(R.id.test_question_view);
            radioGroup_tf = itemView.findViewById(R.id.test_ans_group_tf);
            radioGroup_select = itemView.findViewById(R.id.test_ans_group_select);
            linearLayout_mutiselect = itemView.findViewById(R.id.test_ans_group_multselect);

            cutLine = itemView.findViewById(R.id.test_sec_line);
            showResult = itemView.findViewById(R.id.test_result_view);
        }
    }
}
