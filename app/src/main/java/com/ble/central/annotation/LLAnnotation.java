package com.ble.central.annotation;

import android.app.Activity;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 创建者:
 * @data 创建时间: 2018-9-6
 * @Description 描述: 自定义注解 替代findViewById
 * @Verion 版本: 1.0
 */
public class LLAnnotation {
    public static void bind(Activity activity) {
        // 注入布局
        bindLayout(activity);
        // 注入视图
        bindViews(activity);
        // 注入事件
        bindEvents(activity);
    }

    /**
     * 注入事件
     *
     * @param activity
     */
    private static void bindEvents(Activity activity) {
        // 获取activity的Class
        Class myClass = activity.getClass();
        // // 通过Class获取所有方法
        Method myMethod[] = myClass.getDeclaredMethods();
        for (Method method : myMethod) {
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                Class<?> annotationType = annotation.annotationType();
                // 获取字段的注解
                EventInterface eventInterface = annotationType.getAnnotation(EventInterface.class);
                if (eventInterface == null) {
                    continue;
                }
                // 获取字段注解的参数
                String listenerSetter = eventInterface.listenerSetter();
                Class listenerType = eventInterface.listenerType();
                String callbackMethod = eventInterface.callbackMethod();
                // 获取注解事件的控件对象
                try {
                    Method valueMethod = annotationType.getDeclaredMethod("value");
                    try {
                        int[] viewIds = (int[]) valueMethod.invoke(annotation);
                        for (int viewId : viewIds) {
                            View view = activity.findViewById(viewId);
                            // 反射setOnClickListener方法,这里要用到代理
                            Method setListenerMethod = view.getClass().getMethod(listenerSetter, listenerType);
                            Map methodMap = new HashMap();
                            methodMap.put(callbackMethod, method);
                            InvocationHandler invocationHandler = new ListenerInvocationHandler(activity, methodMap);
                            Object newProxyInstance = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, invocationHandler);
                            setListenerMethod.invoke(view, newProxyInstance);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 注入视图
     *
     * @param activity
     */
    private static void bindViews(Activity activity) {
        // 获取每一个属性上的注解
        Class myClass = activity.getClass();
        // 先拿到里面所有的成员变量
        Field[] myFields = myClass.getDeclaredFields();
        for (Field field : myFields) {
            ViewInit myView = field.getAnnotation(ViewInit.class);
            if (myView != null) {
                // 拿到属性id
                int value = myView.value();
                View view = activity.findViewById(value);
                // 将view赋值给类里面的属性
                try {
                    field.setAccessible(true);  // 为了防止其是私有的,需要设置允许访问
                    field.set(activity, view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 注入布局
     *
     * @param activity
     */
    private static void bindLayout(Activity activity) {
        // 获取自定义类ContentView Inject 上面的注解
        Class myClass = activity.getClass();
        ContentView contentView = (ContentView) myClass.getAnnotation(ContentView.class);
        if (contentView != null) {
            int myLayoutResId = contentView.value();
            activity.setContentView(myLayoutResId);
        }
    }
}
