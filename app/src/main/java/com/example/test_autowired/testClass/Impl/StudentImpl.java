package com.example.test_autowired.testClass.Impl;

import android.util.Log;

import com.example.test_autowired.testClass.IStudent;
import com.kekxv.AutoWired.Service;

@Service(service = true)
public class StudentImpl implements IStudent {
    int count = 0;
    String name = "";

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void start(){
        Log.i("StudentImpl", String.format(">>>>>>>>>>>>>> student %s 进入课堂", name));
    }

    public void learn() {
        Log.i("StudentImpl", String.format(">>>>>>>>>>>>>> student %s learn %d", name, count++));
    }

    public void rollCall() {
        Log.i("StudentImpl", String.format(">>>>>>>>>>>>>> student %s here", name));
    }
}
