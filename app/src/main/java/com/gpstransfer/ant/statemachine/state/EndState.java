package com.gpstransfer.ant.statemachine.state;

import android.os.RemoteException;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.channel.AntCommandFailedException;

public class EndState extends State {
    public EndState(AntChannel antChannel) {
        super(antChannel);
    }

    @Override
    protected void nextState() {
        //no next state
    }

    @Override
    public void process(byte[] data) {
        try {
            antChannel.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AntCommandFailedException e) {
            e.printStackTrace();
        }
    }
}
