package com.ble.central;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.ble.central.utils.ToastUtil;

import java.util.Stack;

public class MyApplication extends Application {
    private static MyApplication mApplication;
    private Stack<Activity> activities;

    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.initialize(this, ToastUtil.Mode.NORMAL);
        mApplication = this;
        activities = new Stack<>();
        registerActivityLifecycleCallbacks(new SwitchBackgroundCallbacks());
    }

    public static MyApplication getInstance() {
        return mApplication;
    }

    private class SwitchBackgroundCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            activities.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            activities.remove(activity);
        }
    }

    /**
     * 获取当前的Activity
     *
     * @return
     */
    public Activity getCurActivity() {
        return activities.lastElement();
    }
}
