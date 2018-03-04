package com.gpstransfer.ant.statemachine;

import com.dsi.ant.channel.AntChannel;
import com.gpstransfer.ant.statemachine.state.*;

public class StateFactory {


    private final AntChannel antChannel;

    public StateFactory(AntChannel antChannel) {
        this.antChannel = antChannel;
    }

    public State getNoState() {
        return new NoState(this.antChannel);
    }

    public State getBusyState() {
        return new BusyState(this.antChannel);
    }

    public State getAuthState() {
        return new AuthState(this.antChannel);
    }

    public State getLinkState() {
        return new LinkState(this.antChannel);
    }

    public State getBurstState() {
        return new BurstState(this.antChannel);
    }

    public State getDirectSendResponseState() {
        return new DirectSendResponseState(this.antChannel);
    }

    public State getEndState() {
        return new EndState(this.antChannel);
    }

    public State getPairingAuthResponseState() {
        return new PairingAuthResponseState(this.antChannel);
    }

    public State getRxFailedState() {
        return new RXFailedState(this.antChannel);
    }

    public State getTxFailedState() {
        return new TXFailedState(this.antChannel);
    }

    public State getTxSuccessState() {
        return new TXSuccessState(this.antChannel);
    }
}
