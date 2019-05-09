package com.mlearn.MyAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mlearn.Entity.Test;
import com.mlearn.MyActivity.R;

import java.util.List;

/**
 * Created by AIRCode on 2018/11/21.
 */

public class MyAdapter_test_item extends RecyclerView.Adapter<MyAdapter_test_item.ViewHolder> {
    private static final String TAG = "AIRCode";

    private List<Test> testList;
    private Context context;


    /*************************************构造函数*************************************************/
    public MyAdapter_test_item(List<Test> testList, Context context) {
        this.testList = testList;
        this.context = context;
    }

    /*************************************实现方法*************************************************/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test_item, parent, false);
        final MyAdapter_test_item.ViewHolder holder = new MyAdapter_test_item.ViewHolder(view);

        holder.radioGroup_tf.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position = holder.getAdapterPosition();
                Test test = testList.get(position);
                RadioButton checked_RadioButtom = group.findViewById(checkedId);
                String my_option = String.valueOf(checked_RadioButtom.getText());
                test.setUserAnswer(my_option.substring(0, my_option.indexOf(":")));
            }
        });

        holder.radioGroup_select.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position = holder.getAdapterPosition();
                Test test = testList.get(position);
                RadioButton checked_RadioButtom = group.findViewById(checkedId);
                String my_option = String.valueOf(checked_RadioButtom.getText());
                test.setUserAnswer(my_option.substring(0, my_option.indexOf(":")));
            }
        });

        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Test test = testList.get(position);
        if (test.getUserAnswer().equals("")) {//用户未作答则清空选项,避免显示错乱
            holder.radioGroup_tf.clearCheck();
            holder.radioGroup_select.clearCheck();
            int checkBox_cnt = holder.linearLayout_mutiselect.getChildCount();
            for (int i = 0; i < checkBox_cnt; i++) {
                ((CheckBox) holder.linearLayout_mutiselect.getChildAt(i)).setChecked(false);
            }
        }
        holder.testQuestion.setText(test.getTestContent());
        if (test.getType() == Test.PAN_DUAN) {
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
                holder.radioGroup_select.addView(option_RadioButton);
            }

            if (!test.getUserAnswer().equals("未作答")) {//如果显示的不是未作答，则是查看答案，则radiobutton显示用户的答案
                int index = (int) (test.getUserAnswer().charAt(0) - 'A');
                RadioButton radioButton = (RadioButton) holder.radioGroup_select.getChildAt(index);
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
                option_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String option = String.valueOf(buttonView.getText());
                        String new_option = option.substring(0, option.indexOf(":"));
                        String pre_options = test.getUserAnswer();
                        Log.e(TAG, "onCheckedChanged: " + pre_options);
                        if (pre_options.equals("未作答")) {
                            pre_options = "";
                            test.setUserAnswer("");
                        }
                        if (isChecked == true) {
                            test.setUserAnswer(pre_options + new_option);
                        } else {
                            test.setUserAnswer(pre_options.replace(new_option, ""));
                        }
                        Log.e(TAG, "onCheckedChanged: " + test.getUserAnswer());
                    }
                });
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
