package com.kuyue.updateapp.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.kuyue.updateapp.Constants;
import com.kuyue.updateapp.R;
import com.kuyue.updateapp.utils.SdcardUtils;
import com.kuyue.updateapp.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

/**
 * 更新应用的服务
 * Created by sen young on 2017/2/8 16:58.
 * 邮箱:595327086@qq.com.
 */

public class UpdateService extends Service {

    private String apkURL;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private int mProceess = -1;
    private SdcardUtils sdcardUtils;

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sdcardUtils = new SdcardUtils();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            notifyUser("下载失败", 0);
            stopSelf();//取消
        } else {
            apkURL = intent.getStringExtra("apkUrl");
            initNotify();
            initFileDir();
            notifyUser("下载开始", 0);
            startDownload();//启动下载
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 创建文件夹
     */
    private void initFileDir() {
        if (SdcardUtils.existSdcard()) {
            if (!sdcardUtils.isFileExist(Constants.FILE_DIR_NAME)) {//不存在就重新创建文件
                sdcardUtils.creatSDDir(Constants.FILE_DIR_NAME);
            }
        } else {
            ToastUtils.getInstance().show("sd卡不存在！");
        }
    }

    private void initNotify() {
        builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(Constants.APP_NAME);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private void startDownload() {
        OkHttpUtils//
                .get()//
                .tag(this)
                .url(apkURL)//
                .build()//
                .execute(new FileCallBack(sdcardUtils.getSDPATH() + "/" + Constants.FILE_DIR_NAME, Constants.APP_NAME)//
                {
                    int fProgress;

                    @Override
                    public void inProgress(float progress, long total, int id) {//下载进度
                        super.inProgress(progress, total, id);
                        fProgress = (int) (100 * progress);
                        if (fProgress != mProceess) {//避免刷新太快，会卡死
                            mProceess = fProgress;
                            notifyUser("正在下载", fProgress);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {//下载失败
                        notifyUser("下载失败", 0);
                        stopSelf();
                    }

                    @Override
                    public void onResponse(File response, int id) {//下载完成
                        notifyUser("下载完成", 100);
                        stopSelf();

                        //下载完成则安装
                        File apkFile = new File(sdcardUtils.getSDPATH() + "/" + Constants.FILE_DIR_NAME + "/" + Constants.APP_NAME);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath()),
                                "application/vnd.android.package-archive");
                        startActivity(intent);
                        if (notificationManager != null) {
                            notificationManager.cancelAll();
                        }

                    }
                });
    }

    private void notifyUser(String result, int process) {
        if (builder == null) {
            return;
        }
        if (process > 0 && process < 100) {
            builder.setProgress(100, process, false);
        } else {
            builder.setProgress(0, 0, false);
        }
        if (process >= 100) {
            builder.setContentIntent(getContentIntent());
        }
        builder.setContentInfo(process + "%");
        builder.setContentText(result);
        notificationManager.notify(0, builder.build());
    }

    private PendingIntent getContentIntent() {
        File apkFile = new File(sdcardUtils.getSDPATH() + "/" + Constants.FILE_DIR_NAME + "/" + Constants.APP_NAME);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath()),
                "application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
