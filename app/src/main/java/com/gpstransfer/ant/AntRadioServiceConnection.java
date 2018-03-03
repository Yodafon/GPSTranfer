package com.gpstransfer.ant;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.dsi.ant.AntService;
import com.dsi.ant.channel.AntChannelProvider;

public class AntRadioServiceConnection implements ServiceConnection {

    private static final String LOGGER = AntRadioServiceConnection.class.getSimpleName();

    private AntService mAntRadioService;
    private AntChannelProvider mAntChannelProvider;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mAntRadioService = new AntService(service);
        try {
            mAntChannelProvider = mAntRadioService.getChannelProvider();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.w(LOGGER, "Binder Died");
        mAntChannelProvider = null;
        mAntRadioService = null;
    }


    public AntChannelProvider getmAntChannelProvider() {
        return mAntChannelProvider;
    }

    public void setmAntChannelProvider(AntChannelProvider mAntChannelProvider) {
        this.mAntChannelProvider = mAntChannelProvider;
    }
}
