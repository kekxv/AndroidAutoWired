package com.example.test_autowired.testClass.Impl;

import android.util.Log;

import com.example.test_autowired.testClass.ITeacher;
import com.kekxv.AutoWired.Service;

@Service(service = true)
public class TeacherImpl implements ITeacher {
    int count = 0;

    public void teach() {
        Log.i("TeacherImpl", String.format(">>>>>>>>>>>>>> teacher teach %d \n", count++));
    }

    public void start() {
        Log.i("TeacherImpl", ">>>>>>>>>>>>>> teacher teach 开始上课 \n");
    }
}
