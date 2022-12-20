package com.xd.spring2;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xd.spring2.launchstarter.tasks.GetDeviceIdTask;
import com.xd.spring2.launchstarter.tasks.InitAMapTask;
import com.xdroid.spring.codedesign.launchstarter.XDTaskLauncherDelayed;

public class XDTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 延迟加载
     */
    private void testLoadDelay() {
        XDTaskLauncherDelayed delayInitDispatcher = new XDTaskLauncherDelayed();
        delayInitDispatcher.addTask(new GetDeviceIdTask())
                .addTask(new InitAMapTask())
                .start();
    }


}
