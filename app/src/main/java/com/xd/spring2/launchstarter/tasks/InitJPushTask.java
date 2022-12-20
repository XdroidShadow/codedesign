package com.xd.spring2.launchstarter.tasks;

import com.xdroid.spring.codedesign.launchstarter.task.XDTask;
import com.xdroid.spring.codedesign.log.X_Log;

import java.util.ArrayList;
import java.util.List;


/**
 * 需要在getDeviceId之后执行
 */
public class InitJPushTask extends XDTask {
    private static final String TAG = "InitJPushTask";

    /**
     * 使用class进行配置依赖关系
     */
    @Override
    public List<Class<? extends XDTask>> dependsOn() {
        List<Class<? extends XDTask>> task = new ArrayList<>();
        task.add(GetDeviceIdTask.class);
        return task;
    }

    @Override
    public void run() {
        X_Log.i(TAG, "run: "+Thread.currentThread().getName());
    }
}
