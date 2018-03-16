package com.gpstransfer.ant.statemachine;

import com.dsi.ant.channel.AntChannel;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.state.*;

public class StateFactory {


    private final AntChannel antChannel;
    private final ChannelChangedListener channelListener;

    public StateFactory(AntChannel antChannel, ChannelChangedListener channelListener) {
        this.antChannel = antChannel;
        this.channelListener = channelListener;
    }

    public State getNoState() {
        return new NoState(this.antChannel, channelListener);
    }

    public State getBusyState() {
        return new BusyState(this.antChannel, channelListener);
    }

    public State getAuthState() {
        return new AuthState(this.antChannel, channelListener);
    }

    public State getLinkState() {
        return new LinkState(this.antChannel, channelListener);
    }

    public BurstState getBurstState() {
        return new BurstState(this.antChannel, channelListener);
    }

    public State getDirectSendResponseState() {
        return new DirectSendResponseState(this.antChannel, channelListener);
    }

    public State getEndState() {
        return new EndState(this.antChannel, channelListener);
    }

    public State getPairingAuthResponseState() {
        return new PairingAuthResponseState(this.antChannel, channelListener);
    }

    public State getRxFailedState() {
        return new RXFailedState(this.antChannel, channelListener);
    }

    public State getTxFailedState() {
        return new TXFailedState(this.antChannel, channelListener);
    }

    public State getTxSuccessState() {
        return new TXSuccessState(this.antChannel, channelListener);
    }

    public State getFileNameResponseState() {
        return new FileNameResponseState(this.antChannel, channelListener);
    }
}
