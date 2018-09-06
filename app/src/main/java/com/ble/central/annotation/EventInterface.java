package com.ble.central.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 创建者:
 * @data 创建时间: 2018-9-6
 * @Description 描述: 对所有的事件点击进行扩展
 * @Verion 版本:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface EventInterface {
    /**
     * 设置监听的方法
     *
     * @return
     */
    String listenerSetter();

    /**
     * 事件类型
     *
     * @return
     */
    Class listenerType();

    /**
     * 回调方法,事件被触发后,执行回调方法名称
     *
     * @return
     */
    String callbackMethod();
}
