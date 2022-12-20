package com.xdroid.spring.codedesign.launchstarter;

import android.os.Looper;
import android.os.MessageQueue;


import com.xdroid.spring.codedesign.launchstarter.task.X_DispatchRunnable;
import com.xdroid.spring.codedesign.launchstarter.task.XDTask;
import com.xdroid.spring.codedesign.log.X_Log;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 用于执行需要延迟加载的任务，让系统空闲的时候再执行
 * 一次只执行一个任务
 */
public class XDTaskLauncherDelayed {
    private static final String TAG = "XDTaskLauncherDelayed";

    private Queue<XDTask> mDelayTasks = new LinkedList<>();

    /**
     * return !mDelayTasks.isEmpty();
     * 返回false的话IdleHandler就会被移除
     */
    private MessageQueue.IdleHandler mIdleHandler = new MessageQueue.IdleHandler() {
        @Override
        public boolean queueIdle() {
            if (mDelayTasks.size() > 0) {
                XDTask task = mDelayTasks.poll();
                new X_DispatchRunnable(task).run();
            }
            //当任务队列被执行完时，退出
            return !mDelayTasks.isEmpty();
        }
    };

    public XDTaskLauncherDelayed addTask(XDTask task) {
        mDelayTasks.add(task);
        return this;
    }

    /**
     * 开启
     */
    public void start() {
        if (Thread.currentThread().getName().equals("main")) {
            Looper.myQueue().addIdleHandler(mIdleHandler);
        }else {
            X_Log.e(TAG,"延迟启动器只能在主线程执行！");
        }
    }

}
