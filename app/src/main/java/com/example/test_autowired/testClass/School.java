package com.example.test_autowired.testClass;

import com.example.test_autowired.AutoWired.IAutoWired;
import com.example.test_autowired.AutoWired.AutoWired;
import com.example.test_autowired.AutoWired.Service;


@Service
public class School extends IAutoWired {
    @AutoWired()
    private IStudent student;
    @AutoWired(Sign = "小红")
    private IStudent student_A;
    @AutoWired(Sign = "小明")
    private IStudent student_B;
    @AutoWired
    private ITeacher teacher;

    public void work(){
        teacher.teach();
        student.learn();
        student_A.learn();
        student_B.learn();
    }

}
