package com.example.test_autowired.testClass.Impl;

import android.util.Log;

import com.example.test_autowired.AutoWired.Service;
import com.example.test_autowired.testClass.IStudent;

@Service
public class StudentImpl implements IStudent {
    int count = 0;

    public void learn() {
        Log.i("StudentImpl", String.format(">>>>>>>>>>>>>> student learn %d \n", count++));
    }
}
