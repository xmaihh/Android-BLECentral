package com.ble.central.annotation;

import android.app.Activity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class ListenerInvocationHandler implements InvocationHandler {

    //activity 真实对象
    private Activity activity;
    private Map<String, Method> methodMap;

    public ListenerInvocationHandler(Activity activity, Map<String, Method> methodMap) {
        this.activity = activity;
        this.methodMap = methodMap;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        Method method1 = methodMap.get(name);
        // 决定是否需要使用代理
        if (method1 != null) {
            return method1.invoke(activity, args);
        } else {
            return method1.invoke(proxy, args);
        }
    }
}
