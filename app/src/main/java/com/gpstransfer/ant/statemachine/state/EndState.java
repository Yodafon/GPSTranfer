package com.gpstransfer.ant.statemachine.state;

import android.os.RemoteException;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.channel.AntCommandFailedException;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.Result;

public class EndState extends State {
    public EndState(AntChannel antChannel, ChannelChangedListener channelListener) {
        super(antChannel, channelListener);
    }

    @Override
    public boolean nextState() {
        //no next state
        return true;
    }

    @Override
    public Result process(byte[] data) {
        try {
            antChannel.close();
        } catch (RemoteException | AntCommandFailedException e) {
            return Result.IN_PROGRESS;
        }
        return Result.SUCCESS;
    }
}
