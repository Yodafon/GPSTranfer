package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;

public class BusyState extends State {


    public BusyState(AntChannel antChannel) {
        super(antChannel);
    }

    @Override
    protected void nextState() {
        previousState.nextState();
    }

    @Override
    public void process(byte[] data) {
        log(Log.VERBOSE, getClass().getSimpleName());
        nextState();
    }
}
