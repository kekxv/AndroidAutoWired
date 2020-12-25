package com.example.test_autowired.testClass;

import com.example.test_autowired.AutoWired.IAutoWired;
import com.example.test_autowired.AutoWired.Autowired;
import com.example.test_autowired.AutoWired.Service;


@Service
public class School extends IAutoWired {
    @Autowired
    private IStudent student;
    @Autowired
    private ITeacher teacher;

    public void work(){
        teacher.teach();
        student.learn();
    }

}
