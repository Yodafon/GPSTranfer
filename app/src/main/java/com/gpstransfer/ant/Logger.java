package com.gpstransfer.ant;

import android.util.Log;

public class Logger {

    protected void log(int priority, String logMessage, Throwable e) {
        new Runnable() {
            @Override
            public void run() {
                String stacktrace = e != null ? "\n" + Log.getStackTraceString(e) : "";
                //Log.println(priority, LOGGER, logMessage + stacktrace);
                //channelListener.onRefreshLog(logMessage + stacktrace);
            }
        }.run();
    }

}
