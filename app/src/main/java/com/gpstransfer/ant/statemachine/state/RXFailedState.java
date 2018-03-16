package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.Result;

public class RXFailedState extends State {

    public RXFailedState(AntChannel antChannel, ChannelChangedListener channelListener) {
        super(antChannel, channelListener);
    }

    @Override
    public boolean nextState() {
        return true;
    }

    @Override
    public Result process(byte[] data) {
        log(Log.VERBOSE, getClass().getSimpleName());
        //burst: reset counters and byte sizes
        if (data[0] == 50) {
            previousState.reset();
        }
        nextState();
        return Result.SUCCESS;
    }
}
