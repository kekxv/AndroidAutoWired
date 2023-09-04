# 使用方法

先按照 引入方式 [Introduce](doc/Introduce.md) 引入。

API 文档参照 [API](doc/API.md)。

## 使用`Web`功能

先注册一个`Controller`对象，例如：
```java
@Service
@Controller("/test")
public class TestController {

  @RequestMapping({"/hello"})
  public Object hello() throws JSONException {
    JSONObject json = new JSONObject();
    json.put("code", 200);
    json.put("message", "success");
    json.put("data", "");
    json.put("ok", true);
    return json;
  }
}
```

然后使用自动或手动方式注入对象：
```java
  @Override
protected void onCreate(Bundle savedInstanceState) {
    ...
    // 自动扫描
    // IAutoWired.init(this);
    // 手动设置
    IAutoWired.init(new Class<?>[]{
        TestController.class,
    });
    ...
}
```

最后一步，启动`web`服务，不建议在UI线程启动`start`：
```java
WebService.makeWebservice(this, 8081);
new Thread(() -> {
  try {
    WebService.instance().start();
  } catch (IOException e) {
    throw new RuntimeException(e);
  }
}).start();
```

以上的例子中，我们注册了一个 `8081` 端口的 `http` 服务，包含接口 `/test/hello`，请求`ip地址:8081/test/hello`，将返回内容：`{"code": 200,"message": "success","data": "","ok": true}`

## 创建基础接口

### 教师 ITeacher

```java
public interface ITeacher {
    void teach();
}
```

### 学生 IStudent

```java
public interface IStudent {
    void setName(String name);

    void learn();

    void rollCall();
}
```

### 作业 IHomework

```java
public interface IHomework {
    void Assignment();
}
```

## 创建实现类

### 教师 TeacherImpl

```java
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
```

### 学生 StudentImpl

```java
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
```

### 作业

#### 数学 HomeworkMathImpl

```java
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
```

#### 语文 HomeworkChineseImpl

```java
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
```

## 学校 

1. School

```java
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import com.example.test_autowired.R;
import com.example.test_autowired.testClass.Impl.HomeworkChineseImpl;
import com.kekxv.AutoWired.AutoWired;
import com.kekxv.AutoWired.IAutoWired;
import com.kekxv.AutoWired.InjectView;
import com.kekxv.AutoWired.Service;
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
    @AutoWired
    Context context;
    @AutoWired
    Activity activity;

    @SuppressLint("NonConstantResourceId")
    @InjectView(R.id.text)
    TextView text;

    @AutoWired(Sign = "Chinese", Interpretation = "getIHomework")
    private IHomework iHomework;

    Class<?> getIHomework() {
        return HomeworkChineseImpl.class;
    }

    public School() {
        student.rollCall();
        student_A.rollCall();
        student_B.rollCall();
    }

    public void work() {
        teacher.teach();
        student.learn();
        student_A.learn();
        student_B.learn();
        Log.d("context", context.toString());
        if (text != null) {
            text.setText("开始上课");
        }
        iHomework.Assignment();
    }

}
```

2. mSchool

```java
import com.kekxv.AutoWired.AutoWired;
import com.kekxv.AutoWired.IAutoWired;
import com.kekxv.AutoWired.Service;
@Service
public class mSchool extends IAutoWired {
    @AutoWired()
    public IStudent student;
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
```

## 主类

```java
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.test_autowired.testClass.IStudent;
import com.example.test_autowired.testClass.Impl.HomeworkChineseImpl;
import com.example.test_autowired.testClass.Impl.HomeworkMathImpl;
import com.example.test_autowired.testClass.Impl.StudentImpl;
import com.example.test_autowired.testClass.Impl.TeacherImpl;
import com.example.test_autowired.testClass.School;
import com.example.test_autowired.testClass.mSchool;
import com.kekxv.AutoWired.AutoWired;
import com.kekxv.AutoWired.IAutoWired;
import com.kekxv.AutoWired.InjectView;

public class MainActivity extends AppCompatActivity {
    @AutoWired
    School school;
    @AutoWired
    mSchool mSchool;
    @AutoWired()
    private IStudent student;
    @AutoWired(Sign = "小红")
    private IStudent student_A;
    @AutoWired(Sign = "小明")
    private IStudent student_B;
    @SuppressLint("NonConstantResourceId")
    @InjectView(R.id.text)
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 自动扫描
        // IAutoWired.init(this);
        // 手动设置
        IAutoWired.init(new Class<?>[]{
                StudentImpl.class,
                TeacherImpl.class,
                mSchool.class,
                School.class,
                HomeworkChineseImpl.class,
                HomeworkMathImpl.class,
        });
        // 将注册 Context 自动注入
        IAutoWired.registered(Context.class, this);

        IAutoWired.inject(this);

        // 后注册测试 将注册 Activity 自动注入
        IAutoWired.registered(Activity.class, this);

        student.setName("小兰");
        student_A.setName("小红");
        student_B.setName("小明");

        school.work();
        mSchool.work();
    }
}
```

## 测试输出

在例子中，`school`与`mSchool`包括其内部的`@AutoWired`均自动注入成功，执行后输出结果为：

```shell
I/StudentImpl: >>>>>>>>>>>>>> student  进入课堂
I/chatty: uid=10152(com.example.test_autowired) identical 1 line
I/StudentImpl: >>>>>>>>>>>>>> student  进入课堂
I/TeacherImpl: >>>>>>>>>>>>>> teacher teach 开始上课 
I/HomeworkChineseImpl: >>>>>>>>>>>>>> 语文作业
I/StudentImpl: >>>>>>>>>>>>>> student  here
I/chatty: uid=10152(com.example.test_autowired) identical 1 line
I/StudentImpl: >>>>>>>>>>>>>> student  here
I/HomeworkMathImpl: >>>>>>>>>>>>>> 数学作业
I/StudentImpl: >>>>>>>>>>>>>> student  here
I/chatty: uid=10152(com.example.test_autowired) identical 1 line
I/StudentImpl: >>>>>>>>>>>>>> student  here
I/TeacherImpl: >>>>>>>>>>>>>> teacher teach 0 
I/StudentImpl: >>>>>>>>>>>>>> student 小兰 learn 0
    >>>>>>>>>>>>>> student 小红 learn 0
I/StudentImpl: >>>>>>>>>>>>>> student 小明 learn 0
D/context: com.example.test_autowired.MainActivity@4d23216
I/HomeworkMathImpl: >>>>>>>>>>>>>> 布置语文作业
I/TeacherImpl: >>>>>>>>>>>>>> teacher teach 1 
I/StudentImpl: >>>>>>>>>>>>>> student 小兰 learn 1
    >>>>>>>>>>>>>> student 小红 learn 1
I/StudentImpl: >>>>>>>>>>>>>> student 小明 learn 1
I/HomeworkMathImpl: >>>>>>>>>>>>>> 布置数学作业
```
