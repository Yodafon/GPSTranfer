package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.Result;

public class PairingAuthResponseState extends State {
    public PairingAuthResponseState(AntChannel antChannel, ChannelChangedListener channelListener) {
        super(antChannel, channelListener);
    }

    @Override
    public boolean nextState() {
        log(Log.VERBOSE, "Sending passkey auth request...");
        byte[] package1 = new byte[]{0x44, 0x04, 0x03, 0x08, 0x00, 0x00, 0x00, 0x00,
                (byte) 0xD1, 0x58, (byte) 0x84, (byte) 0xA9, 0x6C, 0x3D, 0x1C, (byte) 0x96};
        return sendBurstData(package1);
    }

    @Override
    public Result process(byte[] data) {
        previousState = this;
        log(Log.VERBOSE, getClass().getSimpleName());
        if (data[0] == (byte) 0xC0 || data[0] == (byte) 0xE0) { //when all data package has arrived
            nextState();
            return Result.SUCCESS;
        }
        return Result.IN_PROGRESS;
    }
}
