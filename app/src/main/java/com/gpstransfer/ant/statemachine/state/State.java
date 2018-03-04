package com.gpstransfer.ant.statemachine.state;

import android.os.RemoteException;
import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.channel.AntCommandFailedException;

public abstract class State {

    protected static State previousState;

    protected AntChannel antChannel;


    public State(AntChannel antChannel) {
        this.antChannel = antChannel;
    }


    protected abstract void nextState();

    public abstract void process(byte[] data);

    protected void sendAckData(byte[] data) {
        ((Runnable) () -> {
            try {
                antChannel.startSendAcknowledgedData(data);
                log(Log.VERBOSE, hexToString(data));
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (AntCommandFailedException e) {
                e.printStackTrace();
            }
        }).run();
    }

    protected void sendBurstData(byte[] data) {
        ((Runnable) () -> {
            try {
                antChannel.burstTransfer(data);
                log(Log.VERBOSE, hexToString(data));
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (AntCommandFailedException e) {
                e.printStackTrace();
            }
        }).run();
    }


    protected void log(int loglevel, String logMessage) {
        log(loglevel, logMessage, null);
    }

    protected void log(int loglevel, byte[] logMessage) {
        log(loglevel, logMessage, null);
    }

    protected void log(int priority, String logMessage, Throwable e) {
        String stacktrace = e != null ? "\n" + Log.getStackTraceString(e) : "";
        Log.println(priority, LOGGER, logMessage + stacktrace);
        channelListener.onRefreshLog(logMessage + stacktrace);
    }

}
