package com.xdroid.spring.codedesign.launchstarter;

import android.content.Context;
import android.os.Looper;


import androidx.annotation.UiThread;

import com.xdroid.spring.codedesign.launchstarter.sort.X_TaskSortUtil;
import com.xdroid.spring.codedesign.launchstarter.stat.X_TaskState;
import com.xdroid.spring.codedesign.launchstarter.task.X_DispatchRunnable;
import com.xdroid.spring.codedesign.launchstarter.task.XDTask;
import com.xdroid.spring.codedesign.launchstarter.task.X_TaskCallBack;
import com.xdroid.spring.codedesign.launchstarter.utils.X_DispatcherLog;
import com.xdroid.spring.codedesign.log.X_Log;
import com.xdroid.spring.codedesign.launchstarter.utils.X_Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 启动器调用类
 */

public class XDTaskLauncher {
    private static final String TAG = "TaskDispatcher";
    private static boolean isDebug = true;
    private long mStartTime;
    private static final int WAITTIME = 10000;
    private static Context sContext;
    private static boolean sIsMainProcess;
    private List<Future> mFutures = new ArrayList<>();
    private static volatile boolean sHasInit;
    private List<XDTask> mAllTasks = new ArrayList<>();
    private List<Class<? extends XDTask>> mClsAllTasks = new ArrayList<>();
    private volatile List<XDTask> mMainThreadTasks = new ArrayList<>();
    private CountDownLatch mCountDownLatch;
    //保存需要Wait的Task的数量
    private AtomicInteger mNeedWaitCount = new AtomicInteger();
    //调用了await的时候还没结束的且需要等待的Task
    private List<XDTask> mNeedWaitTasks = new ArrayList<>();
    //已经结束了的Task
    private volatile List<Class<? extends XDTask>> mFinishedTasks = new ArrayList<>(100);
    private HashMap<Class<? extends XDTask>, ArrayList<XDTask>> mDependedHashMap = new HashMap<>();
    //启动器分析的次数，统计下分析的耗时；
    private AtomicInteger mAnalyseCount = new AtomicInteger();

    private XDTaskLauncher() {
    }

    private static void init(Context context) {
        sContext = Objects.requireNonNull(context);
        sHasInit = true;
        sIsMainProcess = X_Utils.isMainProcess(sContext);
    }

    /**
     * 注意：每次获取的都是新对象
     */
    public static XDTaskLauncher newInstance(Context context) {
        init(context);
        return new XDTaskLauncher();
    }

    public XDTaskLauncher addTask(XDTask task) {
        if (task != null) {
            collectDepends(task);
            mAllTasks.add(task);
            mClsAllTasks.add(task.getClass());
            // 非主线程且需要wait的，主线程不需要CountDownLatch也是同步的
            if (ifNeedWait(task)) {
                mNeedWaitTasks.add(task);
                mNeedWaitCount.getAndIncrement();
            }
        }
        return this;
    }

    public XDTaskLauncher addTasks(XDTask... tasks) {
        for (XDTask task : tasks) {
            if (task != null) {
                collectDepends(task);
                mAllTasks.add(task);
                mClsAllTasks.add(task.getClass());
                // 非主线程且需要wait的，主线程不需要CountDownLatch也是同步的
                if (ifNeedWait(task)) {
                    mNeedWaitTasks.add(task);
                    mNeedWaitCount.getAndIncrement();
                }
            }
        }
        return this;
    }

    private void collectDepends(XDTask task) {
        if (task.dependsOn() != null && task.dependsOn().size() > 0) {
            for (Class<? extends XDTask> cls : task.dependsOn()) {
                if (mDependedHashMap.get(cls) == null) {
                    mDependedHashMap.put(cls, new ArrayList<XDTask>());
                }
                mDependedHashMap.get(cls).add(task);
                if (mFinishedTasks.contains(cls)) {
                    task.satisfy();
                }
            }
        }
    }

    private boolean ifNeedWait(XDTask task) {
        return !task.runOnMainThread() && task.needWait();
    }

    @UiThread
    public void start() {
        mStartTime = System.currentTimeMillis();
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new RuntimeException("must be called from UiThread");
        }
        if (mAllTasks.size() > 0) {
            mAnalyseCount.getAndIncrement();
            printDependedMsg();
            mAllTasks = X_TaskSortUtil.getSortResult(mAllTasks, mClsAllTasks);
            mCountDownLatch = new CountDownLatch(mNeedWaitCount.get());

            sendAndExecuteAsyncTasks();

            X_Log.i("", "task analyse cost " + (System.currentTimeMillis() - mStartTime) + "  begin main ");
            executeTaskMain();
        }
        X_Log.i("", "task analyse cost startTime cost " + (System.currentTimeMillis() - mStartTime));
        await();
    }

    public void cancel() {
        for (Future future : mFutures) {
            future.cancel(true);
        }
    }

    private void executeTaskMain() {
        mStartTime = System.currentTimeMillis();
        for (XDTask task : mMainThreadTasks) {
            long time = System.currentTimeMillis();
            X_Log.i(TAG, "executeTaskMain: 开始执行");
            new X_DispatchRunnable(task, this).run();
            X_Log.i("", "real main " + task.getClass().getSimpleName() + " cost   " +
                    (System.currentTimeMillis() - time));
        }
        X_Log.i("", "maintask cost " + (System.currentTimeMillis() - mStartTime));
    }

    private void sendAndExecuteAsyncTasks() {
        X_Log.i(TAG, "sendAndExecuteAsyncTasks: 开始执行");
        for (XDTask task : mAllTasks) {
            if (task.onlyInMainProcess() && !sIsMainProcess) {
                markTaskDone(task);
            } else {
                sendTaskReal(task);
            }
            task.setSend(true);
        }
    }

    /**
     * 查看被依赖的信息
     */
    private void printDependedMsg() {
        X_Log.i("", "needWait size : " + (mNeedWaitCount.get()));
        if (false) {
            for (Class<? extends XDTask> cls : mDependedHashMap.keySet()) {
                X_Log.i("", "cls " + cls.getSimpleName() + "   " + mDependedHashMap.get(cls).size());
                for (XDTask task : mDependedHashMap.get(cls)) {
                    X_Log.i("", "cls       " + task.getClass().getSimpleName());
                }
            }
        }
    }

    /**
     * 通知Children一个前置任务已完成
     *
     * @param launchTask
     */
    public void satisfyChildren(XDTask launchTask) {
        ArrayList<XDTask> arrayList = mDependedHashMap.get(launchTask.getClass());
        if (arrayList != null && arrayList.size() > 0) {
            for (XDTask task : arrayList) {
                task.satisfy();
            }
        }
    }

    public void markTaskDone(XDTask task) {
        if (ifNeedWait(task)) {
            mFinishedTasks.add(task.getClass());
            mNeedWaitTasks.remove(task);
            mCountDownLatch.countDown();
            mNeedWaitCount.getAndDecrement();
        }
    }

    private void sendTaskReal(final XDTask task) {
        if (task.runOnMainThread()) {
            mMainThreadTasks.add(task);

            if (task.needCall()) {
                task.setTaskCallBack(new X_TaskCallBack() {
                    @Override
                    public void call() {
                        X_TaskState.markTaskDone();
                        task.setFinished(true);
                        satisfyChildren(task);
                        markTaskDone(task);
                        X_Log.i("", task.getClass().getSimpleName() + " finish");

                        X_Log.i("testLog", "call");
                    }
                });
            }
        } else {
            // 直接发，是否执行取决于具体线程池
            Future future = task.runOn().submit(new X_DispatchRunnable(task, this));
            mFutures.add(future);
        }
    }

    public void executeTask(XDTask task) {
        if (ifNeedWait(task)) {
            mNeedWaitCount.getAndIncrement();
        }
        task.runOn().execute(new X_DispatchRunnable(task, this));
    }

    @UiThread
    private void await() {
        try {
            if (X_DispatcherLog.isDebug()) {
                X_Log.i("", "still has " + mNeedWaitCount.get());
                for (XDTask task : mNeedWaitTasks) {
                    X_Log.i("", "needWait: " + task.getClass().getSimpleName());
                }
            }

            if (mNeedWaitCount.get() > 0) {
                mCountDownLatch.await(WAITTIME, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
        }
    }

    public static Context getContext() {
        return sContext;
    }

    public static boolean isMainProcess() {
        return sIsMainProcess;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static void setIsDebug(boolean b) {
        isDebug = b;
    }
}
