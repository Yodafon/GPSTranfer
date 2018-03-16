package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.Result;

public class FileNameResponseState extends BurstState {


    public FileNameResponseState(AntChannel antChannel, ChannelChangedListener channelListener) {
        super(antChannel, channelListener);
    }

    @Override
    public boolean nextState() {
        log(Log.VERBOSE, "Sending download request...");
        byte[] package1 = new byte[]{0x44, 0x0D, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00,
                0x06, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00};
        return sendBurstData(package1);
    }

    @Override
    public Result process(byte[] data) {
        previousState = this;
        log(Log.VERBOSE, getClass().getSimpleName());
        blockCounter++;

        if (blockCounter >= 3) {
            for (int i = (blockCounter == 3 ? 4 : 1); i < 9; i++) { //copy data without block counter
                if (data[i] != 0x00) { //
                    dataBytes.add(data[i]);
                }
            }
        }

        if (data[0] == (byte) 0xE0) {
            nextState();
            return Result.SUCCESS;
        }
        return Result.IN_PROGRESS;
    }

    @Override
    public void reset() {
        dataBytes.clear();
        blockCounter = 0;
        blockSizeByte = 0;
    }
}
