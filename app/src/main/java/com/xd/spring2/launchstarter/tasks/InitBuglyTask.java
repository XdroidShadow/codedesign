package com.xd.spring2.launchstarter.tasks;


import com.xdroid.spring.codedesign.launchstarter.task.XDMainTask;
import com.xdroid.spring.codedesign.log.X_Log;

public class InitBuglyTask extends XDMainTask {
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
