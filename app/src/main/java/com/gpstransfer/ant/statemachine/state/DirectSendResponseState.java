package com.gpstransfer.ant.statemachine.state;

import com.dsi.ant.channel.AntChannel;

/*
*
* 0x8D respons after 0x0D request (DirectSend)
*
* */
public class DirectSendResponseState extends State {
    public DirectSendResponseState(AntChannel antChannel) {
        super(antChannel);
    }

    @Override
    public void nextState() {

    }

    @Override
    public void process(byte[] data) {

    }
}
