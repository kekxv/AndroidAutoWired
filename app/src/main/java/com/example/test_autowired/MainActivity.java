package com.example.test_autowired;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.test_autowired.testClass.IStudent;
import com.example.test_autowired.testClass.Impl.StudentImpl;
import com.example.test_autowired.testClass.Impl.TeacherImpl;
import com.example.test_autowired.testClass.School;
import com.example.test_autowired.testClass.mSchool;
import com.kekxv.AutoWired.AutoWired;
import com.kekxv.AutoWired.IAutoWired;
import com.kekxv.AutoWired.InjectView;

public class MainActivity extends AppCompatActivity {

    @AutoWired
    School school;
    @AutoWired
    mSchool mSchool;

    @AutoWired()
    private IStudent student;
    @AutoWired(Sign = "小红")
    private IStudent student_A;
    @AutoWired(Sign = "小明")
    private IStudent student_B;


    @SuppressLint("NonConstantResourceId")
    @InjectView(R.id.text)
    TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 自动扫描
        // IAutoWired.init(this);
        // 手动设置
        IAutoWired.init(new Class<?>[]{
                StudentImpl.class,
                TeacherImpl.class,
                mSchool.class,
                School.class,
        });


        // 将注册 Context 自动注入
        // IAutoWired.registered(Context.class, this);

        IAutoWired.inject(this);

        // 后注册测试 将注册 Context 自动注入
        IAutoWired.registered(Context.class, this);
        IAutoWired.registered(Activity.class, this);

        student.setName("小兰");
        student_A.setName("小红");
        student_B.setName("小明");

        school.work();
        mSchool.work();
    }
}