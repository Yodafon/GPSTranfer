package com.gpstransfer.ant.statemachine.state;

import android.util.Log;
import com.dsi.ant.channel.AntChannel;

import java.util.LinkedList;

public class BurstState extends State {

    private int blockCounter;
    private int blockSize;
    private int totalSizeByte;
    private int blockSizeByte;
    private LinkedList<Byte> dataBytes = new LinkedList<>();

    public BurstState(AntChannel antChannel) {
        super(antChannel);
    }

    @Override
    public void nextState() {

    }

    @Override
    public void process(byte[] data) {
        previousState = this;
        log(Log.VERBOSE, getClass().getSimpleName());
        blockCounter++;
        if (blockCounter >= 2) {
            for (int i = (blockCounter == 2 ? 5 : 1); i < 9; i++) { //copy data without block counter
                if (data[i] != 0x00) { //
                    dataBytes.add(data[i]);
                }
            }
        }
        if (data[2] == 0x0D) {
            blockSize = (((blockSize & data[8]) << 8) & data[7]) + 1;
        }
        if (blockCounter == 2) { //calc total size and block size bytes from second data block
            totalSizeByte = (totalSizeByte & data[2] << 8) & data[1];
            blockSizeByte += ((data[3] << 8) & data[4]);
        }

        if ((data[0] == 0xA0) && (blockSize == blockCounter)) {
            nextDataBlock();
        }
        if (dataBytes.size() == (totalSizeByte - 4)) { //only real data without size informations
            nextState();
        }
    }

    public void reset() {
        dataBytes.subList()
        blockCounter = 0;
        blockSizeByte
    }

    private void nextDataBlock() {
        log(Log.VERBOSE, "Continue download request...");
        blockCounter = 0;
        byte[] package1 = new byte[]{0x00, 0x44, 0x0D, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00};
        byte[] package2 = new byte[]{(byte) 0xA0, 0x06, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00};
        sendBurstData(package1);
        sendBurstData(package2);
    }
}
