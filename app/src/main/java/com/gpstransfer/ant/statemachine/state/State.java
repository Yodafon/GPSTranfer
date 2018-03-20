package com.gpstransfer.ant.statemachine.state;

import android.os.RemoteException;
import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.channel.AntCommandFailedException;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.Logger;
import com.gpstransfer.ant.statemachine.Result;

import static com.gpstransfer.ant.ChannelController.PERIOD;

public abstract class State {

    private static String LOGGER;
    protected static State previousState;

    protected AntChannel antChannel;
    private ChannelChangedListener channelListener;


    public State(AntChannel antChannel, ChannelChangedListener channelListener) {
        this.antChannel = antChannel;
        this.channelListener = channelListener;
        LOGGER = this.getClass().getSimpleName();
    }


    public abstract boolean nextState();

    public abstract Result process(byte[] data);

    protected boolean sendAckData(byte[] data) {
            try {
                Thread.sleep((long) ((PERIOD / 32768f) * 1000));
                antChannel.startSendAcknowledgedData(data);
                Thread.sleep((long) ((PERIOD / 32768f) * 1000));
                log(Log.VERBOSE, bytesToHex(data));
            } catch (RemoteException | AntCommandFailedException | InterruptedException e) {
                log(Log.ERROR, "Retry ack sending....", e);
            }
        return true;
    }

    protected boolean sendBurstData(byte[] data) {
            try {
                Thread.sleep(125);
                log(Log.VERBOSE, bytesToHex(data));
                antChannel.burstTransfer(data);
            } catch (RemoteException | AntCommandFailedException e) {
                log(Log.ERROR, "Retry burst sending....", e);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        return true;

    }


    protected void log(int loglevel, String logMessage) {
        log(loglevel, logMessage, null);
    }

    protected void log(int loglevel, byte[] logMessage) {
        log(loglevel, bytesToHex(logMessage), null);
    }

    protected void log(int priority, String logMessage, Throwable e) {
        Logger.log(priority, logMessage, e, channelListener);
    }

    protected void updateProgressBar(int value) {
                channelListener.onRefreshProgressBar(value);
    }

    public void reset() {

    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();


    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

}
