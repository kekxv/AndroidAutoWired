# 安卓模拟自动注入

源码位置 ： [https://github.com/kekxv/AndroidAutoWired](https://github.com/kekxv/AndroidAutoWired)

代码下载 ： [https://github.com/kekxv/AndroidAutoWired](https://github.com/kekxv/AndroidAutoWired/archive/master.zip)

引入方式： ~~gradle:`implementation 'com.github.kekxv:AndroidAutoWired:0.1.0'`~~ (不知道啥子情况，不清作用)

> 更新记录
> - 20201226 增加`Sign`标记，用于区分各个不一样的实例。
> - 20201226 增加`IAutoWired.registered`手动注册，可用于自动注入`Context`之类。


本项目是用于模拟自动注入，通过添加注解`@AutoWired`，举个例子：

```java
public class School extends IAutoWired {
    @AutoWired()
    private IStudent student;
    @AutoWired(Sign = "小红")
    private IStudent student_A;
    @AutoWired(Sign = "小明")
    private IStudent student_B;
    @AutoWired
    private ITeacher teacher;

    // 手动 IAutoWired.registered 注册的自动注入对象
    @AutoWired
    Context context;
}
```

在`School`初始化的时候，会自动扫描包含`@AutoWired`注解对应的实例，并将其注入。

如果变量类型为接口类，则需要有一个对应的继承类对应能初始化，用于注入，例如：
```java
public interface ITeacher {
    void teach();
}
```

```java
@Service
public class TeacherImpl implements ITeacher {
    int count = 0;

    public void teach() {
        Log.i("TeacherImpl", String.format(">>>>>>>>>>>>>> teacher teach %d \n", count++));
    }

}
```

为了让`School`内`ITeacher`能够成功注入，则需要有一个配套的实现类，例如这里的`TeacherImpl`；这样在需要注入的时候，将会找到`TeacherImpl`为`ITeacher teacher;`自动注入。

注意：所有自动注入的同类型类，为同一个对象。

为了能够区分对应的自动注入实例 ，则需要添加注解`@Service`，方便`AutoWired`确认。

## 使用方法

安卓在使用的时候，需要在主入口类里面调用`IAutoWired.init(this);`用与初始化扫描本包下所有类。

```java
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IAutoWired.init(this);

        // 将注册 Context 自动注入
        IAutoWired.registered(Context.class, this);

        IAutoWired.inject(this);

        student.setName("小兰");
        student_A.setName("小红");
        student_B.setName("小明");

        school.work();
        mSchool.work();

    }
}
```

在例子中，`school`与`mSchool`包括其内部的`@AutoWired`均自动注入成功，执行后输出结果为：

```shell
I/TeacherImpl: >>>>>>>>>>>>>> teacher teach 0
I/StudentImpl: >>>>>>>>>>>>>> student 小兰 learn 0
I/StudentImpl: >>>>>>>>>>>>>> student 小红 learn 0
I/StudentImpl: >>>>>>>>>>>>>> student 小明 learn 0
I/TeacherImpl: >>>>>>>>>>>>>> teacher teach 1
I/StudentImpl: >>>>>>>>>>>>>> student 小兰 learn 1
I/StudentImpl: >>>>>>>>>>>>>> student 小红 learn 1
I/StudentImpl: >>>>>>>>>>>>>> student 小明 learn 1
```

## 不足之处

1. 目前还是有些不足的地方，例如当有多个实现类，希望能够根据指定参数或者指定注解，自动注入不一样的实现类，从而更灵活智能，但这个只能看后期项目需求或者是否有时间以及是否有好心人帮忙完善了。
1. `DexFile`已经被 `API 29` 以上列为过时(`Deprecated`)，这个在扫描当前类时候用上，需要寻找一个替换方案。不过在虚拟机`Android11`上面能够正常使用。


## 参考文档

[仿springboot @Autowired自动注入:https://blog.csdn.net/qq_38527695/article/details/104217397](https://blog.csdn.net/qq_38527695/article/details/104217397)

其他互联网文档

备注：由于找资料比较多和杂，部分资料查看后关闭页面，导致没有加入到参考文档内，如果您发现本文章内有借鉴您的文档的部分，请将您的文献地址提交`Issues`或者提交 PR(`pull requests`)到[https://github.com/kekxv/AndroidAutoWired](https://github.com/kekxv/AndroidAutoWired)

