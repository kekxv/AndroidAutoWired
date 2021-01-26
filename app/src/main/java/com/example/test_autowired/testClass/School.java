package com.example.test_autowired.testClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.example.test_autowired.R;
import com.example.test_autowired.testClass.Impl.HomeworkChineseImpl;
import com.kekxv.AutoWired.AutoWired;
import com.kekxv.AutoWired.IAutoWired;
import com.kekxv.AutoWired.InjectView;
import com.kekxv.AutoWired.Service;


@Service
public class School extends IAutoWired {
    @AutoWired()
    private IStudent student;
    @AutoWired(Sign = "小红")
    private IStudent student_A;
    @AutoWired(Sign = "小明")
    private IStudent student_B;
    @AutoWired
    private ITeacher teacher;
    @AutoWired
    Context context;
    @AutoWired
    Activity activity;

    @SuppressLint("NonConstantResourceId")
    @InjectView(R.id.text)
    TextView text;

    @AutoWired(Sign = "Chinese", Interpretation = "getIHomework")
    private IHomework iHomework;

    Class<?> getIHomework() {
        return HomeworkChineseImpl.class;
    }

    public School() {
        student.rollCall();
        student_A.rollCall();
        student_B.rollCall();
    }

    public void work() {
        teacher.teach();
        student.learn();
        student_A.learn();
        student_B.learn();
        Log.d("context", context.toString());
        if (text != null) {
            text.setText("开始上课");
        }
        iHomework.Assignment();
    }

}
