package com.xd.spring2.launchstarter.tasks;


import android.util.Log;

import com.xdroid.spring.codedesign.launchstarter.task.MainTask;
import com.xdroid.spring.codedesign.launchstarter.task.Task;
import com.xdroid.spring.codedesign.log.X_Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InitFrescoTask extends Task {
    private static final String TAG = "InitFrescoTask";


    @Override
    public List<Class<? extends Task>> dependsOn() {
        return new ArrayList<Class<? extends Task>>() {
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
