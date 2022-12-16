package com.xdroid.spring.codedesign.log;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class X_Log {
    private static final String TAG = "XDLog";
    private static final String SEPARATOR = " ";


    public static <T> void e(String tag, T... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append("/");
        for (T info : args) {
            sb.append(info);
            sb.append(SEPARATOR);
        }
        sb.append("\n");
        Log.e(TAG, sb.toString());
    }


    public static <T> void i(String tag, T... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append("/");
        for (T info : args) {
            sb.append(info);
            sb.append(SEPARATOR);
        }
        sb.append("\n");
        Log.i(TAG, sb.toString());
    }


}
