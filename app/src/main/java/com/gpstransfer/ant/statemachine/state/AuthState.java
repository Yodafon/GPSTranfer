package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.Result;

public class AuthState extends State {

    static byte[] package1 = new byte[]{0x44, 0x0D, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00,
            0x06, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00};

    public AuthState(AntChannel antChannel, ChannelChangedListener channelListener) {
        super(antChannel, channelListener);
    }


    @Override
    public boolean nextState() {
        log(Log.VERBOSE, "Sending download request...");
        return sendBurstData(package1);
    }

    @Override
    public Result process(byte[] data) {
        previousState = this;
        log(Log.VERBOSE, getClass().getSimpleName());
        nextState();
        return Result.SUCCESS;
    }
}
