package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;

public class NoState extends State {

    public NoState(AntChannel antChannel) {
        super(antChannel);
    }

    @Override
    protected void nextState() {
        byte[] data = new byte[]{0x4f, 0x00, 0x44, 0x02, 0x32, 0x04, 0x00, 0x00, 0x00, 0x00};
        sendAckData(data);
    }

    @Override
    public void process(byte[] data) {
        previousState = this;
        log(Log.VERBOSE, getClass().getSimpleName());
        nextState();
    }
}
