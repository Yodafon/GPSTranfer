package com.gpstransfer.ant.statemachine.state;

import android.os.RemoteException;
import android.util.Log;
import com.dsi.ant.channel.AntChannel;
import com.dsi.ant.channel.AntCommandFailedException;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.statemachine.Data;
import com.gpstransfer.ant.statemachine.Result;
import com.gpstransfer.ant.util.XMLChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class BurstState extends State {

    private boolean hadFileNameReceived;
    protected int totalSizeByte = 0;
    protected LinkedList<Data> dataBytes = new LinkedList<>();
    protected LinkedList<Byte> currentBlockBytes = new LinkedList<>();
    private String filename;
    int totalSizeOfList;
    static byte[] package1 = new byte[]{0x44, 0x0D, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x06, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00};


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
        } finally {
            writeFile();
        }
        return true;
    }

    private void writeFile() {
        log(Log.VERBOSE, "File writing...");
        if (!hadFileNameReceived) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            filename = "Waypoint_" + simpleDateFormat.format(new Date());
        }
        File logFile = new File("/sdcard/" + filename + ".gpx");
        if (logFile.exists()) {
            logFile.delete();
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

            for (Data dataByte : dataBytes) {
                List<Byte> data = dataByte.getData();
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i) == (byte) 0x44 && data.get(i + 1) == (byte) 0x0D) {
                    i = i + 11;
                } else {
                        if (data.get(i) != 0x00)
                            fos.write(data.get(i));
                    }
                }
            }

            FileInputStream fileInputStream = new FileInputStream(logFile);
            if (XMLChecker.checkXml(fileInputStream)) {
                log(Log.VERBOSE, "XML is correct");
            } else {
                log(Log.VERBOSE, "XML is incorrect");
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
        for (int i = 1; i < data.length; i++) {
            currentBlockBytes.add(data[i]);
        }

        //A0: ha az elozo block 60-al jött
        //E0: ha az elozo block 40-el jott
        //C0: ha az elozo block 20-el jott

        if (data[0] == (byte) 0xE0 || data[0] == (byte) 0xA0 || data[0] == (byte) 0xC0) {//remove the tailing 0x00
            if (currentBlockBytes.size() < 17) {
                currentBlockBytes.clear();
                return Result.IN_PROGRESS;
            }
            if (currentBlockBytes.get(8) == (byte) 0xDF && currentBlockBytes.get(9) == 0x05) { //filename response
                totalSizeByte = (currentBlockBytes.get(15) & 0xFF) * 16777216;
                totalSizeByte += (currentBlockBytes.get(14) & 0xFF) * 65536;
                totalSizeByte += (currentBlockBytes.get(13) & 0xFF) * 256;
                totalSizeByte += (currentBlockBytes.get(12) & 0xFF);
                log(Log.VERBOSE, "Total size in bytes: " + totalSizeByte);
                extractFileName(currentBlockBytes);
                hadFileNameReceived = true;
            } else { //don't copy filename data only real data
//                if (hadFileNameReceived == false) {
//                    int sizeByte = ((currentBlockBytes.get(11) & 0xFF) * 256);
//                    totalSizeByte += (sizeByte + (currentBlockBytes.get(10) & 0xFF));
//                    log(Log.VERBOSE, "Sum size in bytes: " + totalSizeByte);
//                }
                //copy whole data without tailing 0s and header
                Data dataBlock = new Data();
                for (int i = 12; i < currentBlockBytes.size(); i++) {
                    if (currentBlockBytes.get(i) != 0x00) {
                        dataBlock.getData().add(currentBlockBytes.get(i));
                    }
                }
                if (!dataBytes.contains(dataBlock)) {
                    dataBytes.add(dataBlock);
                    totalSizeOfList = dataBytes.stream().map(item -> item.getData().size()).reduce((item1, item2) -> item1 + item2).get();
                } else {
                    log(Log.VERBOSE, "Duplicated data -> Ignore!");
                }
            }
            currentBlockBytes.clear();

            nextDataBlock();
            if (totalSizeOfList == totalSizeByte) { //only real data without size informations
                log(Log.VERBOSE, "Total size in bytes: " + totalSizeByte);
                log(Log.VERBOSE, "DATA RECEIVED: " + totalSizeOfList + " bytes");
                return Result.SUCCESS;
            }
        }


        return Result.IN_PROGRESS;
    }


    @Override
    public void reset() {
        log(Log.VERBOSE, "Reset... Total received bytes: " + totalSizeOfList);
        currentBlockBytes.clear();
    }

    public void nextDataBlock() {
        log(Log.VERBOSE, "Continue download request...");
        log(Log.VERBOSE, "DATA RECEIVED: " + totalSizeOfList + " bytes");
        if (hadFileNameReceived == true) {
            int value = (int) ((totalSizeOfList / (float) totalSizeByte) * 100);
            log(Log.VERBOSE, "Progress value: " + value + " percent");
            updateProgressBar(value);
        }
        sendBurstData(package1);
    }
}