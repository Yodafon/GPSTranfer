package com.gpstransfer.ant;

import android.os.RemoteException;
import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.channel.AntCommandFailedException;
import com.dsi.ant.channel.IAntChannelEventHandler;
import com.dsi.ant.message.ChannelId;
import com.dsi.ant.message.ChannelType;
import com.dsi.ant.message.fromant.MessageFromAntType;
import com.dsi.ant.message.ipc.AntMessageParcel;
import com.gpstransfer.ant.statemachine.dispatcher.StateDispatcher;

public class ChannelController {

    private static final String LOGGER = ChannelController.class.getSimpleName();
    private static final int FREQUENCY = 50;
    public static final int PERIOD = 4096; //8hz
    public static final int LINK_PERIOD = 0x24;

    private ChannelChangedListener channelListener;
    StateDispatcher stateDispatcher;

    private AntChannel antChannel;
    private ChannelId channelId;
    private boolean mIsOpen = false;

    public ChannelController(AntChannel antChannel, boolean isMaster, ChannelChangedListener channelListener) {
        this.antChannel = antChannel;
        channelId = new ChannelId(0, 0, 0, false); // TODO 0,0,0? for pairing???
        this.channelListener = channelListener;
        mIsOpen = openChannel();
        stateDispatcher = new StateDispatcher(this.antChannel, this.channelListener);
    }

    private boolean openChannel() {
        if (null != antChannel) {
            if (mIsOpen) {
                log(Log.WARN, "Channel was already open");
            } else {
                ChannelType channelType = (ChannelType.BIDIRECTIONAL_SLAVE); //TODO BIDIRECTIONAL MASTER???
                try {
                    antChannel.setChannelEventHandler(new ChannelEventCallback());
                    antChannel.assign(channelType);
                    antChannel.setChannelId(channelId);
                    antChannel.setRfFrequency(FREQUENCY);
                    antChannel.setPeriod(PERIOD);
                    antChannel.disableEventBuffer(); //TODO needed?
                    antChannel.open();
                    log(Log.VERBOSE, "Opened channel with device number: " + channelId.getDeviceNumber() + " Frequency: " + FREQUENCY + "Period: " + PERIOD);
                    //sendChannelIdRequest();
                } catch (RemoteException | AntCommandFailedException e) {
                    log(Log.ERROR, "Open failed", e);
                    return false;
                }
            }
        } else {
            log(Log.WARN, "No channel available");
            return false;
        }

        return true;
    }

    private void log(int loglevel, String logMessage) {
        log(loglevel, logMessage, null);
    }

    private void log(int priority, String logMessage, Throwable e) {
        Logger.log(priority, logMessage, e, channelListener);
    }

    private void sendChannelIdRequest() throws AntCommandFailedException, RemoteException {
        antChannel.requestChannelId();
    }

    public class ChannelEventCallback implements IAntChannelEventHandler {

        @Override
        public void onChannelDeath() {
            // Display channel death message when channel dies
            log(Log.ERROR, "Channel Death");
        }

        @Override
        public void onReceiveMessage(MessageFromAntType messageType, AntMessageParcel antParcel) {
            log(Log.VERBOSE, antParcel.toString());
            if (stateDispatcher != null) {
                stateDispatcher.dispatch(messageType, antParcel);
            }
        }
    }

    public void close() {
        if (null != antChannel) {

            mIsOpen = false;
            antChannel.release();

            Log.d(LOGGER, "Channel Closed");
            channelListener.onRefreshLog("Channel Closed");
        }
    }


}
