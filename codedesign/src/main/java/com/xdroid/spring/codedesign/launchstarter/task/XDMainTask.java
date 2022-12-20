package com.xdroid.spring.codedesign.launchstarter.task;

public abstract class XDMainTask extends XDTask {

    @Override
    public boolean runOnMainThread() {
        return true;
    }
}
