package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;

public class PairingAuthResponseState extends State {
    public PairingAuthResponseState(AntChannel antChannel) {
        super(antChannel);
    }

    @Override
    protected void nextState() {
        log(Log.VERBOSE, "Sending passkey auth request...");
        byte[] package1 = new byte[]{0x00, 0x44, 0x04, 0x03, 0x08, 0x00, 0x00, 0x00, 0x00};
        byte[] package2 = new byte[]{(byte) 0xA0, (byte) 0xD1, 0x58, (byte) 0x84, (byte) 0xA9, 0x6C, 0x3D, 0x1C, (byte) 0x96};
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
