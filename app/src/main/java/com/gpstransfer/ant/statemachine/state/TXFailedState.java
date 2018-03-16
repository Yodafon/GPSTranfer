package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.Result;

public class TXFailedState extends State {

    public TXFailedState(AntChannel antChannel, ChannelChangedListener channelListener) {
        super(antChannel, channelListener);
    }

    @Override
    public boolean nextState() {
        //previousState.nextState();
        return true;
    }

    @Override
    public Result process(byte[] data) {
        log(Log.VERBOSE, getClass().getSimpleName());
        nextState();
        return Result.SUCCESS;
    }
}
