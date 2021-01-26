package com.example.test_autowired.testClass.Impl;

import android.util.Log;

import com.example.test_autowired.testClass.IHomework;
import com.kekxv.AutoWired.IAutoWired;
import com.kekxv.AutoWired.Service;

@Service
public class HomeworkMathImpl extends IAutoWired implements IHomework {
    public HomeworkMathImpl() {
        Log.i("HomeworkMathImpl", ">>>>>>>>>>>>>> 数学作业");
    }
    public void Assignment() {
        Log.i("HomeworkMathImpl", ">>>>>>>>>>>>>> 布置数学作业");
    }
}
