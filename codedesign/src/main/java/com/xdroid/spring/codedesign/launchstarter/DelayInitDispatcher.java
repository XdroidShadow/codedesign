package com.xdroid.spring.codedesign.launchstarter;

import android.os.Looper;
import android.os.MessageQueue;


import com.xdroid.spring.codedesign.launchstarter.task.DispatchRunnable;
import com.xdroid.spring.codedesign.launchstarter.task.Task;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 用于执行需要延迟加载的任务，让系统空闲的时候再执行
 * 一次只执行一个任务
 */
public class DelayInitDispatcher {

    private Queue<Task> mDelayTasks = new LinkedList<>();

    /**
     * return !mDelayTasks.isEmpty();
     * 返回false的话IdleHandler就会被移除
     */
    private MessageQueue.IdleHandler mIdleHandler = new MessageQueue.IdleHandler() {
        @Override
        public boolean queueIdle() {
            if (mDelayTasks.size() > 0) {
                Task task = mDelayTasks.poll();
                new DispatchRunnable(task).run();
            }
            //当任务队列被执行完时，退出
            return !mDelayTasks.isEmpty();
        }
    };

    public DelayInitDispatcher addTask(Task task) {
        mDelayTasks.add(task);
        return this;
    }

    /**
     * 开启
     */
    public void start() {
        Looper.myQueue().addIdleHandler(mIdleHandler);
    }

}
