package com.example.test_autowired;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.test_autowired.AutoWired.IAutoWired;
import com.example.test_autowired.AutoWired.Autowired;
import com.example.test_autowired.testClass.School;
import com.example.test_autowired.testClass.mSchool;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {

    @Autowired
    School school;
    @Autowired
    mSchool mSchool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IAutoWired.init(this);
        IAutoWired.inject(this);


        school.work();
        mSchool.work();

    }
}