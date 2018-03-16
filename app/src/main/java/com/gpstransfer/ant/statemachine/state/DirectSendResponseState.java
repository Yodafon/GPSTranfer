package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.Result;

/*
*
* 0x8D respons after 0x0D request (DirectSend)
*
* */
public class DirectSendResponseState extends State {
    public DirectSendResponseState(AntChannel antChannel, ChannelChangedListener channelListener) {
        super(antChannel, channelListener);
    }

    @Override
    public boolean nextState() {
        return true;
    }

    @Override
    public Result process(byte[] data) {
        log(Log.VERBOSE, getClass().getSimpleName());
        return Result.IN_PROGRESS;
    }
}
