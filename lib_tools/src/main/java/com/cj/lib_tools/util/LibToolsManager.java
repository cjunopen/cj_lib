package com.cj.lib_tools.util;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import timber.log.Timber;

public class LibToolsManager {
    /**
     * 初始化
     */
    public static void init(){
        Timber.plant(new MyDebugTree());

        RxJavaPlugins.setErrorHandler(throwable -> {
            throwable.printStackTrace();
        });
    }

    public static class MyDebugTree extends Timber.DebugTree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (tag != null) {
                String threadName = Thread.currentThread().getName();
                if (threadName.length() > 7){
                    threadName = threadName.substring(0, 7);
                }
                tag = "<" + threadName + "> " + tag;
            }
            super.log(priority, tag, message, t);
        }
        @Override
        protected String createStackElementTag(StackTraceElement element) {
            return super.createStackElementTag(element) + ".java:" + element.getLineNumber();  //日志显示行号
        }
    }


}
