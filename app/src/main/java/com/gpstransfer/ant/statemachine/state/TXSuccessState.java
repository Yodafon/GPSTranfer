package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;

public class TXSuccessState extends State {
    public TXSuccessState(AntChannel antChannel) {
        super(antChannel);
    }

    @Override
    protected void nextState() {
        //no direct next state. Next state will be determined by new incoming message
    }

    @Override
    public void process(byte[] data) {
        log(Log.VERBOSE, getClass().getSimpleName());
        //log(Log.VERBOSE, data);
    }
}
