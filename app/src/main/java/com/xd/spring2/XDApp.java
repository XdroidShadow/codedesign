package com.xd.spring2;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.xd.spring2.launchstarter.tasks.GetDeviceIdTask;
import com.xd.spring2.launchstarter.tasks.InitAMapTask;
import com.xd.spring2.launchstarter.tasks.InitBuglyTask;
import com.xd.spring2.launchstarter.tasks.InitFrescoTask;
import com.xd.spring2.launchstarter.tasks.InitJPushTask;
import com.xdroid.spring.codedesign.launchstarter.XDTaskLauncher;

public class XDApp extends Application {
    private static final String TAG = "XDApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate1: ");

        XDTaskLauncher.newInstance(this).addTasks(
                new InitJPushTask(),
                new InitFrescoTask(),
                new InitAMapTask(),
                new InitBuglyTask(),
                new GetDeviceIdTask()
        ).start();

        Log.e(TAG, "onCreate2: ");

        init();
    }

    private void init() {

    }

    @Override
    protected void attachBaseContext(Context base) {

    }

    @Override
    public Context getApplicationContext() {
        return this;
    }


}
