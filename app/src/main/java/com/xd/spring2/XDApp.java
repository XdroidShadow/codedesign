package com.xd.spring2;

import android.app.Application;
import android.util.Log;

public class XDApp extends Application {
    private static final String TAG = "XDApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");


        init();
    }

    private void init() {

    }


}
