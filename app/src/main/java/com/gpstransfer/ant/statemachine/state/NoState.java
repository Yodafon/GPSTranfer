package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.Result;

public class NoState extends State {

    public NoState(AntChannel antChannel, ChannelChangedListener channelListener) {
        super(antChannel, channelListener);
    }

    @Override
    public boolean nextState() {
        byte[] data = new byte[]{0x44, 0x02, 0x32, 0x04, 0x00, 0x00, 0x00, 0x00};
        return sendAckData(data);
    }

    @Override
    public Result process(byte[] data) {
        previousState = this;
        log(Log.VERBOSE, getClass().getSimpleName());
        nextState();
        return Result.SUCCESS;
    }
}
