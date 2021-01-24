拦截 interface

```java
public interface HelloWorld {
    public void sayHelloWorld();
}

static class HelloWorldImp implements HelloWorld {
    @Override
    public void sayHelloWorld() {
        System.out.println("Hello World!");
    }
}


public static class MyInterceptor implements Interceptor {
    @Override
    public void after(Object proxy, Object target, Method method, Object[] args) {
        Log.w("MyInterceptor", "反射方法后逻辑");
    }
}

public class InterceptorTest {
    public static void main(String[] args) {
        HelloWorld hwProxy = (HelloWorld) InterceptorJdkProxy.bind(new HelloWorldImp(), MyInterceptor.class.getName());
        hwProxy.sayHelloWorld();
    }
}
```