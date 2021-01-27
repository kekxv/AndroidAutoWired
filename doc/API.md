# API 文档

1. 扫描所有(或手动传入)带有 `@Service` 的自动注入类
1. 手动或继承`IAutoWired` 自动调用 `IAutoWired.inject(this);` 进行注入。
1. 为保证`private`也能注入成功；通过反射以及`setAccessible(true);`修改权限进行`newInstance()`以及`赋值`。

## 说明

建议结合仓库源码以及 [Usage.md](Usage.md) 一起查看

## Service : @interface

用于扫描标记，当使用自动扫描的方式，会查找带有 `@Service` 标记的类进行记录，用于后期进行自动注入创建实例类。

也可以通过手动注入的方式进行传入。

### 参数 service

是否是服务类型，如果为 `true` ； 则自动调用 `start` 无参函数 ； 默认为 `false`。

## AutoWired : @interface

标记为需要自动注入。如果一个对象需要自动注入，则需要添加该注解，将会自动查找实例类并注入。

### 参数 Sign

使用不同的 `Sign` 可以创建不同的对象，默认为空。同一个`Sign`则会注入同一个对象。

### 参数 dependencies

是否在构造函数依赖，如果设置为 `true` 则会在其他之后注入。默认为`false`。

### 参数 Interpretation

当自动注入类型为 `interface` 情况下作用；用于获取注入 `interface` 的对应 `class`，**如果只有一个`class`，将会忽略该标注**。

## InjectView : @interface

需要 `setContentView` 之后调用 `IAutoWired.inject`；**只支持 `Activity` 或者带有自动注入 `Activity` 的对象**。

该注解将会自动查找相应 `View` 并进行赋值。

### 参数 value

指定查找的的 `R.id.*` ，例如 `@InjectView(R.id.text)`。

## IAutoWired : class

注入工具类；可以直接 `extends IAutoWired` 自动调用注册当前对象。

### IAutoWired

可以直接 `extends IAutoWired` 将自动调用注册以及注入当前对象，对 `new` 也起作用。

### init 自动扫描初始化

初始化，并且自动扫描所有 `@Service` 类型。
```java
public static void init(Context context);
```

### init 手动指定类型

初始化，手动指定类型，且不进行自动扫描
```java
public static void init(Class<?>[] list);
```

### registered 注册对象

手动注册对象，多用于注册`Context.class`与`Activity.class`；例如：`IAutoWired.registered(Context.class, this);`;`IAutoWired.registered(Activity.class, this);`

```java
/**
 * 手动注册 Sign 为 空 的 对象
 *
 * @param cls    对象类
 * @param object 对象
 */
public static void registered(Class<?> cls, Object object);
/**
 * 手动注册对象
 *
 * @param cls    对象类
 * @param object 对象
 * @param Sign   sign
 */
public static void registered(Class<?> cls, Object object, String Sign);
```

### inject 注入对象

将会将 `source` 对象内的带有`@AutoWired`进行注入。

```java
public static void inject(Object source);
```

### injectView 注入 View 对象

将会将 `source` 对象内的带有`@injectView`进行注入。

```java
public static void InjectView(Activity activity);
```




