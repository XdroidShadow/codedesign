package com.xdroid.spring.codedesign.launchstarter.task;

public abstract class XDMainTask extends XDChildThreadTask {

    @Override
    public boolean runOnMainThread() {
        return true;
    }
}
