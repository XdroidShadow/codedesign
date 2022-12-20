package com.xdroid.spring.codedesign.launchstarter.stat;


import com.xdroid.spring.codedesign.launchstarter.utils.X_DispatcherLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class X_TaskState {

    private static volatile String sCurrentSituation = "";
    private static List<X_TaskStateBean> sBeans = new ArrayList<>();
    private static AtomicInteger sTaskDoneCount = new AtomicInteger();
    private static boolean sOpenLaunchStat = false;// 是否开启统计


    public static String getCurrentSituation() {
        return sCurrentSituation;
    }

    public static void setCurrentSituation(String currentSituation) {
        if (!sOpenLaunchStat) {
            return;
        }
        X_DispatcherLog.i("currentSituation   " + currentSituation);
        sCurrentSituation = currentSituation;
        setLaunchStat();
    }

    public static void markTaskDone() {
        sTaskDoneCount.getAndIncrement();
    }

    public static void setLaunchStat() {
        X_TaskStateBean bean = new X_TaskStateBean();
        bean.setSituation(sCurrentSituation);
        bean.setCount(sTaskDoneCount.get());
        sBeans.add(bean);
        sTaskDoneCount = new AtomicInteger(0);
    }

}
