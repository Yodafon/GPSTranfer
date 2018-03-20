package com.gpstransfer.ant;

import android.util.Log;

public class Logger {

    private static final String LOGGER = Logger.class.getSimpleName();


    public static void log(int priority, String logMessage, Throwable e, ChannelChangedListener channelListener) {
//        new Runnable() {
//            @Override
//            public void run() {
        String stacktrace = e != null ? "\n" + Log.getStackTraceString(e) : "";
        Log.println(priority, LOGGER, logMessage + stacktrace);
        channelListener.onRefreshLog(logMessage + stacktrace);
//                    }
//        }.run();
    }
}
