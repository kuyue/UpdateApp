package com.kuyue.updateapp.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.kuyue.updateapp.MyApplication;

/**
 * Toast 工具类
 * Created by sen young on 2016/8/14 14:49.
 */
public class ToastUtils {

    protected volatile static ToastUtils toastUtils = null;

    private ToastUtils() {
    }

    public static ToastUtils getInstance() {
        if (toastUtils == null) {
            synchronized (ToastUtils.class) {
                if (toastUtils == null) {
                    toastUtils = new ToastUtils();
                }
            }
        }
        return toastUtils;
    }

    protected Handler handler = new Handler(Looper.getMainLooper());
    protected Toast toast = null;

    public void show(String msg){
        showToast(MyApplication.getInstance().getContext(),msg, Toast.LENGTH_SHORT);
    }

    /**
     * @param context  调用者context
     * @param content  要显示的内容
     * @param duration 持续时间
     */
    private void showToast(final Context context, final String content, final int duration) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(context, content, duration);
                    toast.show();
                } else {
                    toast.setText(content);
                    toast.setDuration(duration);
                    toast.show();
                }
            }
        });
    }

}
