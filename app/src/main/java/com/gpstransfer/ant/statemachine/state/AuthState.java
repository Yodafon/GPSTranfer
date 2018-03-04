package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;

public class AuthState extends State {


    public AuthState(AntChannel antChannel) {
        super(antChannel);
    }


    @Override
    public void nextState() {
        log(Log.VERBOSE, "Sending download request...");
        byte[] package1 = new byte[]{0x00, 0x44, 0x0D, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00};
        byte[] package2 = new byte[]{(byte) 0xA0, 0x06, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00};
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
