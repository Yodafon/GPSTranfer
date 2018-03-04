package com.gpstransfer.ant.statemachine.dispatcher;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.message.fromant.ChannelEventMessage;
import com.dsi.ant.message.fromant.MessageFromAntType;
import com.dsi.ant.message.ipc.AntMessageParcel;
import com.gpstransfer.ant.statemachine.StateFactory;
import com.gpstransfer.ant.statemachine.state.State;

public class StateDispatcher {

    private AntChannel antChannel;
    private State linkState;
    private State authState;
    private State busyState;
    private State noState;
    private State burstState;
    private State directSendResponseState;
    private State endState;
    private State pairingAuthResponseState;
    private State rxFailedState;
    private State txFailedState;
    private State txSuccessState;

    public StateDispatcher(AntChannel antChannel) {
        this.antChannel = antChannel;
        StateFactory stateFactory = new StateFactory(this.antChannel);
        noState = stateFactory.getNoState();
        busyState = stateFactory.getBusyState();
        authState = stateFactory.getAuthState();
        linkState = stateFactory.getLinkState();
        burstState = stateFactory.getBurstState();
        directSendResponseState = stateFactory.getDirectSendResponseState();
        endState = stateFactory.getEndState();
        pairingAuthResponseState = stateFactory.getPairingAuthResponseState();
        rxFailedState = stateFactory.getRxFailedState();
        txFailedState = stateFactory.getTxFailedState();
        txSuccessState = stateFactory.getTxSuccessState();
    }


    public void dispatch(MessageFromAntType messageType, AntMessageParcel antParcel) {

        switch (messageType) {
            case BROADCAST_DATA:
                dispatchBroadcastData(antParcel);
                break;
            case ACKNOWLEDGED_DATA:
                break;
            case CHANNEL_EVENT:
                ChannelEventMessage eventMessage = new ChannelEventMessage(antParcel);

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
                //         channelId = new ChannelIdMessage(antParcel).getChannelId();
                //       log(Log.VERBOSE, channelId.toString());
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


    private void dispatchBroadcastData(AntMessageParcel antParcel) {
        switch (antParcel.getMessageContent()[2]) {
            case 0x00:
                noState.process(antParcel.getMessageContent());
                break;
            case 0x01:
                linkState.process(antParcel.getMessageContent());
                break;
            case 0x02:
                authState.process(antParcel.getMessageContent());
                break;
            case 0x03:
                busyState.process(antParcel.getMessageContent());
                break;
        }

    }

    private void log(int loglevel, String logMessage) {
        log(loglevel, logMessage, null);
    }

    private void log(int priority, String logMessage, Throwable e) {
        String stacktrace = e != null ? "\n" + Log.getStackTraceString(e) : "";
        Log.println(priority, LOGGER, logMessage + stacktrace);
        channelListener.onRefreshLog(logMessage + stacktrace);
    }
}
