package com.xd.spring2.launchstarter.tasks;


import com.xdroid.spring.codedesign.launchstarter.task.XDChildThreadTask;
import com.xdroid.spring.codedesign.log.X_Log;

import java.util.ArrayList;
import java.util.List;

public class InitFrescoTask extends XDChildThreadTask {
    private static final String TAG = "InitFrescoTask";


    @Override
    public List<Class<? extends XDChildThreadTask>> dependsOn() {
        return new ArrayList<Class<? extends XDChildThreadTask>>() {
            {
                add(GetDeviceIdTask.class);
                add(InitAMapTask.class);
                add(InitBuglyTask.class);
                add(InitJPushTask.class);
            }
        };
    }

    @Override
    public void run() {
        X_Log.i(TAG, "run: " + Thread.currentThread().getName());
    }

}
