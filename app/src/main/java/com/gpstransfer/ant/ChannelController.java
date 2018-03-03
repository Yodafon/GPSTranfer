package com.gpstransfer.ant;

import android.os.RemoteException;
import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.channel.AntCommandFailedException;
import com.dsi.ant.channel.IAntChannelEventHandler;
import com.dsi.ant.message.ChannelId;
import com.dsi.ant.message.ChannelType;
import com.dsi.ant.message.fromant.ChannelEventMessage;
import com.dsi.ant.message.fromant.ChannelIdMessage;
import com.dsi.ant.message.fromant.MessageFromAntType;
import com.dsi.ant.message.ipc.AntMessageParcel;

public class ChannelController {

    private static final String LOGGER = ChannelController.class.getSimpleName();
    private static final int FREQUENCY = 50;
    private static final int PERIOD = 4096; //8hz
    public static final int LINK_PERIOD = 0x24;

    private ChannelChangedListener channelListener;

    private AntChannel antChannel;
    private ChannelId channelId;
    private boolean mIsOpen = false;

    public ChannelController(AntChannel antChannel, boolean isMaster, ChannelChangedListener channelListener) {
        this.antChannel = antChannel;
        channelId = new ChannelId(0, 0, 0, true); // TODO 0,0,0? for pairing???
        this.channelListener = channelListener;
        mIsOpen = openChannel();
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
        String stacktrace = e != null ? "\n" + Log.getStackTraceString(e) : "";
        Log.println(priority, LOGGER, logMessage + stacktrace);
        channelListener.onRefreshLog(logMessage + stacktrace);
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
            channelListener.onRefreshLog(antParcel.toString());
            log(Log.VERBOSE, antParcel.toString());
            // Switching on message type to handle different types of messages
            switch (messageType) {
                // If data message, construct from parcel and update channel data
                case BROADCAST_DATA:
                    break;
                case ACKNOWLEDGED_DATA:
                    break;
                case CHANNEL_EVENT:
                    // Constructing channel event message from parcel
                    ChannelEventMessage eventMessage = new ChannelEventMessage(antParcel);
                    // Switching on event code to handle the different types of channel events
                    switch (eventMessage.getEventCode()) {
                        case TX: {
                            break;
                        }
                        case RX_SEARCH_TIMEOUT: {
                            break;
                        }

                        case CHANNEL_CLOSED: {
                            break;
                        }
                        case CHANNEL_COLLISION: {
                            break;
                        }
                        case RX_FAIL: {
                            break;
                        }
                        case RX_FAIL_GO_TO_SEARCH: {
                            break;
                        }
                        case TRANSFER_RX_FAILED: {
                            break;
                        }
                        case TRANSFER_TX_COMPLETED: {
                            break;
                        }
                        case TRANSFER_TX_FAILED: {
                            break;
                        }
                        case TRANSFER_TX_START: {
                            break;
                        }
                        case UNKNOWN: {
                            break;
                        }
                    }
                    break;
                case ANT_VERSION: {
                    break;
                }
                case BURST_TRANSFER_DATA: {
                    break;
                }
                case CAPABILITIES:
                    break;
                case CHANNEL_ID:
                    channelId = new ChannelIdMessage(antParcel).getChannelId();
                    log(Log.VERBOSE, channelId.toString());
                    break;
                case CHANNEL_RESPONSE:
                    break;
                case CHANNEL_STATUS:
                    break;
                case SERIAL_NUMBER:
                    break;
                case OTHER:
                    break;
                default: {
                    log(Log.VERBOSE, antParcel.toString());
                    break;
                }

            }
        }
    }

    public void close() {
        if (null != antChannel) {

            mIsOpen = false;
            antChannel.release();

        }
        Log.d(LOGGER, "Channel Closed");
        channelListener.onRefreshLog("Channel Closed");
    }


}
