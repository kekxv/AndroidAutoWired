package com.example.test_autowired.testClass;

import com.kekxv.AutoWired.AutoWired;
import com.kekxv.AutoWired.IAutoWired;
import com.kekxv.AutoWired.Service;

@Service
public class mSchool extends IAutoWired {
    @AutoWired()
    private IStudent student;
    @AutoWired(Sign = "小红")
    private IStudent student_A;
    @AutoWired(Sign = "小明")
    private IStudent student_B;
    @AutoWired
    private ITeacher teacher;

    @AutoWired(Sign = "Math", Interpretation = "getIHomework")
    private IHomework iHomework;

    public String getIHomework() {
        return "HomeworkMathImpl";
    }

    private mSchool() {
        student.rollCall();
        student_A.rollCall();
        student_B.rollCall();
    }

    public void work() {
        teacher.teach();
        student.learn();
        student_A.learn();
        student_B.learn();

        iHomework.Assignment();
    }

}
