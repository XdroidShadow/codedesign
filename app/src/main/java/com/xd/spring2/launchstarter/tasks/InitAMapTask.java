package com.xd.spring2.launchstarter.tasks;


import android.content.Context;
import android.telephony.TelephonyManager;

import com.xdroid.spring.codedesign.launchstarter.task.Task;
import com.xdroid.spring.codedesign.log.X_Log;

public class InitAMapTask extends Task {
    private static final String TAG = "InitAMapTask";

    @Override
    public boolean needWait() {
        return false;
    }

    @Override
    public void run() {
        int count = 0;
        for (int i = 0; i < 100000; i++) {
            count++;
        }
        X_Log.i(TAG, "run: " + "cout = " + count + " " + Thread.currentThread().getName());
    }
}
