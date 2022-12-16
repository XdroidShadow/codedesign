package com.xd.spring2;

import android.app.Application;
import android.util.Log;

import com.xd.spring2.launchstarter.tasks.GetDeviceIdTask;
import com.xd.spring2.launchstarter.tasks.InitAMapTask;
import com.xd.spring2.launchstarter.tasks.InitBuglyTask;
import com.xd.spring2.launchstarter.tasks.InitFrescoTask;
import com.xd.spring2.launchstarter.tasks.InitJPushTask;
import com.xdroid.spring.codedesign.launchstarter.TaskDispatcher;

public class XDApp extends Application {
    private static final String TAG = "XDApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate1: ");

        TaskDispatcher.newInstance(this).addTasks(
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


}
