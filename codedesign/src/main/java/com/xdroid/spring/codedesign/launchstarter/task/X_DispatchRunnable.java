package com.xdroid.spring.codedesign.launchstarter.task;

import android.os.Process;

import com.xdroid.spring.codedesign.launchstarter.XDTaskLauncher;
import com.xdroid.spring.codedesign.launchstarter.stat.X_TaskState;
import com.xdroid.spring.codedesign.log.X_Log;

/**
 * 任务真正执行的地方
 */

public class X_DispatchRunnable implements Runnable {
    private static final String TAG = "DispatchRunnable";
    private XDTask mTask;
    private XDTaskLauncher mTaskDispatcher;

    public X_DispatchRunnable(XDTask task) {
        this.mTask = task;
    }

    public X_DispatchRunnable(XDTask task, XDTaskLauncher dispatcher) {
        this.mTask = task;
        this.mTaskDispatcher = dispatcher;
    }

    @Override
    public void run() {
//        TraceCompat.beginSection(mTask.getClass().getSimpleName());

        Process.setThreadPriority(mTask.priority());

        long startTime = System.currentTimeMillis();

        mTask.setWaiting(true);
        mTask.waitToSatisfy();

        long waitTime = System.currentTimeMillis() - startTime;
        startTime = System.currentTimeMillis();

        // 执行Task
        mTask.setRunning(true);
        mTask.run();

        // 执行Task的尾部任务
        Runnable tailRunnable = mTask.getTailRunnable();
        if (tailRunnable != null) {
            tailRunnable.run();
        }

        if (!mTask.needCall() || !mTask.runOnMainThread()) {
            printTaskLog(startTime, waitTime);

            X_TaskState.markTaskDone();
            mTask.setFinished(true);
            if (mTaskDispatcher != null) {
                mTaskDispatcher.satisfyChildren(mTask);
                mTaskDispatcher.markTaskDone(mTask);
            }
            X_Log.i("", mTask.getClass().getSimpleName() + " finish");
        }
//        TraceCompat.endSection();
    }

    /**
     * 打印出来Task执行的日志
     *
     * @param startTime
     * @param waitTime
     */
    private void printTaskLog(long startTime, long waitTime) {
        if (XDTaskLauncher.isDebug()) {
            long runTime = System.currentTimeMillis() - startTime;
            X_Log.i(TAG, "【"+mTask.getClass().getSimpleName()+"】"+"执行耗时: "+ runTime);

//            X_Log.i("", mTask.getClass().getSimpleName() + "  wait " + waitTime + "    run "
//                    + runTime + "   isMain " + (Looper.getMainLooper() == Looper.myLooper())
//                    + "  needWait " + (mTask.needWait() || (Looper.getMainLooper() == Looper.myLooper()))
//                    + "  ThreadId " + Thread.currentThread().getId()
//                    + "  ThreadName " + Thread.currentThread().getName()
//                    + "  Situation  " + TaskStat.getCurrentSituation()
//            );
        }
    }

}
