# 在 Android 应用中使用 Dagger

`Dagger` 效果更好，建议使用，当前项目就作为一个学习的参考项目吧。

[https://developer.android.com/training/dependency-injection/dagger-android?hl=zh-cn](https://developer.android.com/training/dependency-injection/dagger-android?hl=zh-cn)

# 安卓模拟自动注入

源码位置 ： [https://github.com/kekxv/AndroidAutoWired](https://github.com/kekxv/AndroidAutoWired)

代码下载 ： [AndroidAutoWired.zip](https://github.com/kekxv/AndroidAutoWired/archive/master.zip)

API 文档参照 [API](doc/API.md)。

仓库 ： [Repo](https://github.com/kekxv/JavaRepo/packages) 。

引入方式 ： [Introduce](doc/Introduce.md)

使用方式 ： [Usage](doc/Usage.md)


> 更新记录
> - 20210127 修复 `interface` 接口包含变量没有注入问题。
>
> - 20210126 修复重复依赖陷入无限回调的问题。
> - 20210126 增加 `Interpretation` 用于 多个 `interface` 继承类型判断注入对象。
> - 
> - 20210124 修复重复依赖陷入无限回调的问题。
> - 20210124 增加 `InjectView` 自动`findViewById`并赋值；需要 `setContentView` 之后调用。
> - 
> - 20210114 增加`Service`内`service`标记，用于开启调用`start()`。
> - 20210114 自动注入后注入，可在更新之后自动注入之前未找到的注入字段。
> - 20210114 在原有自动扫描的基础上，增加手动传入 `Service` 类。
> - 20201226 增加`Sign`标记，用于区分各个不一样的实例。
> - 20201226 增加`IAutoWired.registered`手动注册，可用于自动注入`Context`之类。

原理说明：

1. 扫描所有(或手动传入)带有 `@Service` 的自动注入类
1. 手动或继承`IAutoWired` 自动调用 `IAutoWired.inject(this);` 进行注入。
1. 为保证`private`也能注入成功；通过反射以及`setAccessible(true);`修改权限进行`newInstance()`以及`赋值`。

```java
Constructor<?> constructor = cla.getDeclaredConstructor();
constructor.setAccessible(true);
constructor.newInstance()
```

```java
Field[] fields = source.getClass().getDeclaredFields();
for (Field field : fields) {
    field.setAccessible(true);
    field.set(source, target);
}
```

本项目是用于模拟自动注入，通过添加注解`@AutoWired`，举个例子：[Usage](doc/Usage.md)

注意：所有自动注入的同类型类，为同一个对象。

为了能够区分对应的自动注入实例 ，则需要添加注解`@Service`，方便`AutoWired`确认。


## 不足之处

1. 目前还是有些不足的地方 ~~，例如当有多个实现类，希望能够根据指定参数或者指定注解，自动注入不一样的实现类，从而更灵活智能，但这个只能看后期项目需求或者是否有时间以及是否有好心人帮忙完善了~~(已在 0.2.6版本加上)。
1. `DexFile`已经被 `API 29` 以上列为过时(`Deprecated`)，这个在扫描当前类时候用上，需要寻找一个替换方案。不过在虚拟机`Android11`上面能够正常使用。


## 参考文档

[仿springboot @Autowired自动注入:https://blog.csdn.net/qq_38527695/article/details/104217397](https://blog.csdn.net/qq_38527695/article/details/104217397)

其他互联网文档

备注：由于找资料比较多和杂，部分资料查看后关闭页面，导致没有加入到参考文档内，如果您发现本文章内有借鉴您的文档的部分，请将您的文献地址提交`Issues`或者提交 PR(`pull requests`)到[https://github.com/kekxv/AndroidAutoWired](https://github.com/kekxv/AndroidAutoWired)

