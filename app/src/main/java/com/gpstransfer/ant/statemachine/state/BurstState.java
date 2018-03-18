package com.gpstransfer.ant.statemachine.state;

import android.os.RemoteException;
import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.channel.AntCommandFailedException;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
    private String filename;

    public BurstState(AntChannel antChannel, ChannelChangedListener channelListener) {
        super(antChannel, channelListener);
    }

    @Override
    public boolean nextState() {
        try {
            log(Log.VERBOSE, "Download success. Channel closing...");
            antChannel.close();
            log(Log.VERBOSE, "Channel closed");
            writeFile();
        } catch (RemoteException | AntCommandFailedException e) {
            return false;
        }
        return true;
    }

    private void writeFile() {
        log(Log.VERBOSE, "File writing...");
        if (!hadFileNameReceived) {
            filename = "Waypoint";
        }
        File logFile = new File("/sdcard/" + filename + ".gpx");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //kiszedni a filenév responset
        //kiszedni a headert minden blokkból
        //kiszedni a vég nullákat
        //ha 0x0D akkor skip következő 12 byteot
        //ha 0x00 akkor skip
        try (FileOutputStream fos = new FileOutputStream(logFile, true)) {


            for (int i = 0; i < dataBytes.size(); i++) {
                if (dataBytes.get(i) == (byte) 0x44 && dataBytes.get(i + 1) == (byte) 0x0D) {
                    i = i + 11;
                } else {
                    if (dataBytes.get(i) != 0x00)
                        fos.write(dataBytes.get(i));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        log(Log.VERBOSE, "File saved: " + filename + ".gpx");

    }

    private void extractFileName(List<Byte> data) {
        List<Byte> collect = new ArrayList<>();
        for (int i = 19; data.get(i) != 0x00; i++) {
            collect.add(data.get(i));
        }
        byte[] ts = new byte[collect.size()];
        for (int i = 0; i < collect.size(); i++) {
            ts[i] = collect.get(i);
        }
        filename = new String(ts, Charset.forName("ISO-8859-2"));
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

        if (blockCounter == 2) {  //utolso ket bajtot kell nezmi ami a tenlyeges adat meretet jeloli sor vegi 0-k nelkul es header nelkul
            totalSizeByte = (data[2] & 0xFF) * 256;
            totalSizeByte += (data[1] & 0xFF);
            log(Log.VERBOSE, "Total size in bytes: " + totalSizeByte);

        }

        if (data[0] == (byte) 0xE0) { //if filename response: reduce total byte size with 1
            totalSizeByte++;
            responseCounter--; //doesnt count the filename response
            hadFileNameReceived = true;
            extractFileName(currentBlockBytes);
        }


        //A0: ha az elozo block 60-al jött
        //E0: ha az elozo block 40-el jott
        //C0: ha az elozo block 20-el jott

        if (data[0] == (byte) 0xE0 || data[0] == (byte) 0xA0 || data[0] == (byte) 0xC0) {//remove the tailing 0x00
            dataBytes.addAll(currentBlockBytes.stream().filter(item -> !item.equals((byte) 0x00)).collect(Collectors.toList()));
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