package com.gpstransfer.ant.statemachine.state;

import com.dsi.ant.channel.AntChannel;

public class RXFailedState extends State {
    public RXFailedState(AntChannel antChannel) {
        super(antChannel);
    }

    @Override
    protected void nextState() {

    }

    @Override
    public void process(byte[] data) {
        //burst? reset counters and byte sizes
        if (data[0] == 50) {

        }
    }
}
