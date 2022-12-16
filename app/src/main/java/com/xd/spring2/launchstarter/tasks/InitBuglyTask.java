package com.xd.spring2.launchstarter.tasks;


import android.util.Log;

import com.xdroid.spring.codedesign.launchstarter.task.MainTask;
import com.xdroid.spring.codedesign.launchstarter.task.Task;
import com.xdroid.spring.codedesign.log.X_Log;

public class InitBuglyTask extends MainTask {
    private static final String TAG = "InitBuglyTask";

    @Override
    public boolean needWait() {
        return true;
    }

    @Override
    public void run() {

        X_Log.i(TAG, "run: "+Thread.currentThread().getName());
    }
}
