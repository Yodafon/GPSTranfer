package com.gpstransfer.ant.statemachine.state;

import android.os.RemoteException;
import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.channel.AntCommandFailedException;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.Result;

import java.util.LinkedList;

public class BurstState extends State {

    protected int blockCounter;
    protected int blockSize;
    private boolean hadFileNameReceived;
    protected int totalSizeByte = -1;
    protected int blockSizeByte;
    protected LinkedList<Byte> dataBytes = new LinkedList<>();
    protected LinkedList<Byte> currentBlockBytes = new LinkedList<>();
    private int currentBlockCounter;
    private int responseCounter = 0;

    public BurstState(AntChannel antChannel, ChannelChangedListener channelListener) {
        super(antChannel, channelListener);
    }

    @Override
    public boolean nextState() {
        try {
            log(Log.VERBOSE, "Download success. Channel closing...");
            antChannel.close();

            log(Log.VERBOSE, "Channel closed");
        } catch (RemoteException | AntCommandFailedException e) {
            return false;
        }
        return true;
    }

    @Override
    public Result process(byte[] data) {
        previousState = this;
        log(Log.VERBOSE, getClass().getSimpleName());
        blockCounter++;
        currentBlockCounter++;
        for (int i = 1; i < data.length; i++) {
            currentBlockBytes.add(data[i]);
        }

        if (blockCounter == 2) {
            totalSizeByte = (data[2] & 0xFF) * 256;
            totalSizeByte += (data[1] & 0xFF);
            log(Log.VERBOSE, "Total size in bytes: " + totalSizeByte);

        }

        if (data[0] == (byte) 0xE0) { //if filename response: reduce total byte size with 1
            totalSizeByte++;
            responseCounter--; //doesnt count the filename response
            hadFileNameReceived = true;
        }


        if (data[0] == (byte) 0xE0 || data[0] == (byte) 0xA0) {
            if (data[0] == (byte) 0xA0) {
                dataBytes.addAll(currentBlockBytes);
            }
            responseCounter++;
            currentBlockCounter = 0;
            currentBlockBytes.clear();

            nextDataBlock();
            if (dataBytes.size() == totalSizeByte + (responseCounter * 12)) { //only real data without size informations
                log(Log.VERBOSE, "Total size in bytes: " + totalSizeByte);
                log(Log.VERBOSE, "DATA RECEIVED: " + dataBytes.size() + " bytes");
                return Result.SUCCESS;
            }
        }


        return Result.IN_PROGRESS;
    }

    @Override
    public void reset() {
        log(Log.VERBOSE, "Reset... Total received bytes: " + dataBytes.size());
        currentBlockBytes.clear();
        blockCounter -= currentBlockCounter; //remove failed bytes and counters
        currentBlockCounter = 0; //reset actual block counter
    }

    public void nextDataBlock() {
        log(Log.VERBOSE, "Continue download request...");
        log(Log.VERBOSE, "DATA RECEIVED: " + dataBytes.size() + " bytes");
        currentBlockCounter = 0;
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] package1 = new byte[]{0x44, 0x0D, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00,
                0x06, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00};
        sendBurstData(package1);
    }
}