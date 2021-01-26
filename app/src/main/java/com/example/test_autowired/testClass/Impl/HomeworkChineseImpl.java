package com.example.test_autowired.testClass.Impl;

import android.util.Log;

import com.example.test_autowired.testClass.IHomework;
import com.kekxv.AutoWired.IAutoWired;
import com.kekxv.AutoWired.Service;

@Service
public class HomeworkChineseImpl extends IAutoWired implements IHomework {
    public HomeworkChineseImpl() {
        Log.i("HomeworkChineseImpl", ">>>>>>>>>>>>>> 语文作业");
    }

    public void Assignment() {
        Log.i("HomeworkMathImpl", ">>>>>>>>>>>>>> 布置语文作业");
    }
}
