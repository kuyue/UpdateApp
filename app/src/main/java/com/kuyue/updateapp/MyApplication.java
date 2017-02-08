package com.kuyue.updateapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by sen young on 2017/2/8 17:03.
 * 邮箱:595327086@qq.com.
 */
public class MyApplication extends Application {


    private Context mContext;

    private static MyApplication app;

    //单例模式:获取全局的context
    public static MyApplication getInstance() {
        return app;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        app = this;
    }
}
