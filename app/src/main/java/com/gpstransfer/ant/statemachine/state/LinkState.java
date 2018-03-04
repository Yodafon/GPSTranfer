package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;

public class LinkState extends State {

    public LinkState(AntChannel antChannel) {
        super(antChannel);
    }

    @Override
    protected void nextState() {
        log(Log.VERBOSE, "Sending auth pairing request...");
        byte[] package1 = new byte[]{0x00, 0x44, 0x04, 0x02, 0x08, 0x00, 0x00, 0x00, 0x00};
        byte[] package2 = new byte[]{(byte) 0xA0, 0x4C, 0x41, 0x43, 0x4F, 0x4B, 0x41, 0x30, 0x31};
        sendBurstData(package1);
        sendBurstData(package2);
    }

    @Override
    public void process(byte[] data) {
        previousState = this;
        log(Log.VERBOSE, getClass().getSimpleName());
        nextState();
    }
}
