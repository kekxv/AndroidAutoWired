package com.example.test_autowired;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kekxv.AutoWired.IAutoWired;
import com.kekxv.AutoWired.AutoWired;
import com.example.test_autowired.testClass.IStudent;
import com.example.test_autowired.testClass.School;
import com.example.test_autowired.testClass.mSchool;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IAutoWired.init(this);
        IAutoWired.inject(this);

        student.setName("小兰");
        student_A.setName("小红");
        student_B.setName("小明");

        school.work();
        mSchool.work();

    }
}