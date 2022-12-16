package com.xd.spring2.launchstarter.tasks;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.xdroid.spring.codedesign.launchstarter.task.Task;
import com.xdroid.spring.codedesign.log.X_Log;


public class GetDeviceIdTask extends Task {
    private static final String TAG = "GetDeviceIdTask";
    private String mDeviceId;

    @Override
    public boolean needWait() {
        return true;
    }

    @Override
    public void run() {
        // 真正自己的代码
        TelephonyManager tManager = (TelephonyManager) mContext.getSystemService(
                Context.TELEPHONY_SERVICE);

        X_Log.i(TAG, "run: "+Thread.currentThread().getName());


    }
}
