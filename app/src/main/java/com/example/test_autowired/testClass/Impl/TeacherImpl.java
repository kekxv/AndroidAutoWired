package com.example.test_autowired.testClass.Impl;

import android.util.Log;

import com.example.test_autowired.AutoWired.Service;
import com.example.test_autowired.testClass.ITeacher;

@Service
public class TeacherImpl implements ITeacher {
    int count = 0;

    public void teach() {
        Log.i("TeacherImpl", String.format(">>>>>>>>>>>>>> teacher teach %d \n", count++));
    }

}
