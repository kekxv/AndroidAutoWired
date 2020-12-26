package com.example.test_autowired.testClass.Impl;

import android.util.Log;

import com.example.test_autowired.AutoWired.Service;
import com.example.test_autowired.testClass.IStudent;

@Service
public class StudentImpl implements IStudent {
    int count = 0;
    String name = "";

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void learn() {
        Log.i("StudentImpl", String.format(">>>>>>>>>>>>>> student %s learn %d", name, count++));
    }
}
