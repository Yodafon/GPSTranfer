package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.Result;

public class LinkState extends State {
    static byte[] package1 = new byte[]{0x44, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    public LinkState(AntChannel antChannel, ChannelChangedListener channelListener) {
        super(antChannel, channelListener);
    }

    @Override
    public boolean nextState() {
        log(Log.VERBOSE, "Sending auth pairing request...");
        // byte[] package1 = new byte[]{0x44, 0x04, 0x02, 0x08, 0x00, 0x00, 0x00, 0x00,
        //   0x4C, 0x41, 0x43, 0x4F, 0x4B, 0x41, 0x30, 0x31};
        return sendAckData(package1);
    }

    @Override
    public Result process(byte[] data) {
        previousState = this;
        log(Log.VERBOSE, getClass().getSimpleName());
        nextState();
        return Result.SUCCESS;
    }
}
