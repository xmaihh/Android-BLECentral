package com.ble.central.aop.aspect;

import android.util.Log;
import android.view.View;

import com.ble.central.R;
import com.ble.central.utils.ToastUtil;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Calendar;

@Aspect
public class SingleClickAspect {
    static int TIME_TAG = R.id.click_time;
    public static final int MIN_CLICK_DELAY_TIME = 6000;

    @Pointcut("execution(@com.ble.central.aop.aspect.SingleClick * *(..))")//方法切入点
    public void methodAnnotated() {

    }

    @Around("methodAnnotated()")//在连接点进行方法替换
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        View view = null;
        for (Object arg : joinPoint.getArgs())
            if (arg instanceof View) view = (View) arg;
        if (view != null) {
            Object tag = view.getTag(TIME_TAG);
            long lastClickTime = ((tag != null) ? (long) tag : 0);

            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {//过滤掉600毫秒内的连续点击
                view.setTag(TIME_TAG, currentTime);
                joinPoint.proceed();//执行原方法
            } else {
                ToastUtil.show("点击太快!");
                Log.d("chensy", "aroundJoinPoint: ");
            }
        }
    }
}
