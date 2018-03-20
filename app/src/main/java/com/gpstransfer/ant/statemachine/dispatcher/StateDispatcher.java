package com.gpstransfer.ant.statemachine.dispatcher;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.message.fromant.ChannelEventMessage;
import com.dsi.ant.message.fromant.MessageFromAntType;
import com.dsi.ant.message.ipc.AntMessageParcel;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.Logger;
import com.gpstransfer.ant.statemachine.Result;
import com.gpstransfer.ant.statemachine.StateFactory;
import com.gpstransfer.ant.statemachine.state.*;

import static com.gpstransfer.ant.ChannelController.LINK_PERIOD;

public class StateDispatcher {

    private static final String LOGGER = StateDispatcher.class.getSimpleName();


    private final ChannelChangedListener channelListener;
    private AntChannel antChannel;
    private State linkState;
    private State authState;
    private State busyState;
    private State noState;
    private BurstState burstState;
    private State fileNameResponseState;
    private State directSendResponseState;
    private State endState;
    private State pairingAuthResponseState;
    private State rxFailedState;
    private State txFailedState;
    private State txSuccessState;
    private State currentState;
    private int counter = 0;


    public StateDispatcher(AntChannel antChannel, ChannelChangedListener channelListener) {
        this.channelListener = channelListener;
        this.antChannel = antChannel;
        StateFactory stateFactory = new StateFactory(this.antChannel, this.channelListener);
        noState = stateFactory.getNoState();
        busyState = stateFactory.getBusyState();
        authState = stateFactory.getAuthState();
        linkState = stateFactory.getLinkState();
        burstState = stateFactory.getBurstState();
        directSendResponseState = stateFactory.getDirectSendResponseState();
        endState = stateFactory.getEndState();
        rxFailedState = stateFactory.getRxFailedState();
        txFailedState = stateFactory.getTxFailedState();
        txSuccessState = stateFactory.getTxSuccessState();
        currentState = noState;
    }


    public void dispatch(MessageFromAntType messageType, AntMessageParcel antParcel) {
        //log(Log.VERBOSE, currentState.getClass().getSimpleName());
        byte[] data = antParcel.getMessageContent();
        switch (messageType) {
            case BROADCAST_DATA:
                counter++;
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
                        rxFailedState.process(data);
                        break;
                    }
                    case RX_FAIL_GO_TO_SEARCH: {
                        break;
                    }
                    case TRANSFER_RX_FAILED: {
                        if (currentState instanceof BurstState) {
                            currentState.reset();
                        }
                        break;
                    }
                    case TRANSFER_TX_COMPLETED: {
                        txSuccessState.process(data);
                        break;
                    }
                    case TRANSFER_TX_FAILED: {
                        if (currentState instanceof BurstState) {
                            burstState.nextDataBlock();
                        } else {

                            currentState.nextState();
                        }
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
                if (data[1] == 0x43 && data[2] == LINK_PERIOD && data[3] == 0x03) {
                    busyState.process(data);
                    break;
                }
                if (data[1] == 0x43 && data[2] == LINK_PERIOD && data[3] == 0x02) {
                    busyState.process(data);
                    break;
                }

                if ((data[1] == (byte) 0x44 && data[2] == (byte) 0x8D) || currentState instanceof DirectSendResponseState) {
                    directSendResponseState.process(data);
                    break;
                }
                currentState = burstState;
                Result process = burstState.process(data);
                if (Result.SUCCESS.equals(process)) {
                    burstState.nextState();
                }

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
        if (counter > 15)
            switch (antParcel.getMessageContent()[3]) {
                case 0x00:
                    noState.process(antParcel.getMessageContent());
                    break;
                case 0x01:
                    if (currentState instanceof NoState) {
                        linkState.process(antParcel.getMessageContent());
                        currentState = linkState;
                    }
                    break;
                case 0x02:
                    if (currentState instanceof LinkState) {
                        authState.process(antParcel.getMessageContent());
                        currentState = authState;
                    }
                    break;
                case 0x03:
                    busyState.process(antParcel.getMessageContent());
                    break;
            }
    }


    private void log(int loglevel, String logMessage) {
        log(loglevel, logMessage, null);
    }

    public void log(int priority, String logMessage, Throwable e) {
        Logger.log(priority, logMessage, e, channelListener);
    }
}
