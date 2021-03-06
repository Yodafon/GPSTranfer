package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.Result;

public class BusyState extends State {


    public BusyState(AntChannel antChannel, ChannelChangedListener channelListener) {
        super(antChannel, channelListener);
    }

    @Override
    public boolean nextState() {
        return true;

    }

    @Override
    public Result process(byte[] data) {
        log(Log.VERBOSE, getClass().getSimpleName());
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        nextState();
        return Result.SUCCESS;
    }
}
