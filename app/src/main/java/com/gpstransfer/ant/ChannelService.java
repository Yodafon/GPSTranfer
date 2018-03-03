package com.gpstransfer.ant;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.dsi.ant.AntService;
import com.dsi.ant.channel.*;

public class ChannelService extends Service {

    private ChannelChangedListener mListener;
    private ChannelController channelController = null;

    private boolean mAntRadioServiceBound;

    private static final String LOGGER = ChannelService.class.getSimpleName();

    private AntRadioServiceConnection mAntRadioServiceConnection = new AntRadioServiceConnection();


    AntChannel acquireChannel() throws ChannelNotAvailableException, UnsupportedFeatureException {
        AntChannel mAntChannel = null;
        AntChannelProvider antChannelProvider = mAntRadioServiceConnection.getmAntChannelProvider();
        if (null != antChannelProvider) {
            try {
                /*
                 * If applications require a channel with specific capabilities
                 * (event buffering, background scanning etc.), a Capabilities
                 * object should be created and then the specific capabilities
                 * required set to true. Applications can specify both required
                 * and desired Capabilities with both being passed in
                 * acquireChannel(context, PredefinedNetwork,
                 * requiredCapabilities, desiredCapabilities).
                 */
                mAntChannel = antChannelProvider.acquireChannel(this, PredefinedNetwork.ANT_FS);
            } catch (RemoteException e) {
                Log.e(LOGGER, "ACP Remote Ex", e);
            }
        }
        return mAntChannel;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new ChannelServiceComm();
    }

    /**
     * The interface used to communicate with the ChannelService
     */
    public class ChannelServiceComm extends Binder {
        /**
         * Sets the listener to be used for channel changed event callbacks.
         *
         * @param listener The listener that will receive events
         */
        public void setOnChannelChangedListener(ChannelChangedListener listener) {
            mListener = listener;
        }


        public void closeAntChannel() {
            closeChannel();
        }

        public void startReceiving() throws UnsupportedFeatureException, ChannelNotAvailableException {
            createNewChannel(true);
        }
    }


    private void closeChannel() {
        channelController.close();

    }

    public boolean createNewChannel(final boolean isMaster) throws ChannelNotAvailableException, UnsupportedFeatureException {

        // Acquiring a channel from ANT Radio Service
        AntChannel antChannel = acquireChannel();

        if (null != antChannel) {
            // Constructing a controller that will manage and control the channel
            channelController = new ChannelController(antChannel, isMaster, mListener);
        }


        if (null == channelController) return false;

        return true;
    }


    private void doBindAntRadioService() {


        // Creating the intent and calling context.bindService() is handled by
        // the static bindService() method in AntService
        mAntRadioServiceBound = AntService.bindService(this, mAntRadioServiceConnection);
    }

    private void doUnbindAntRadioService() {

        if (mAntRadioServiceBound) {
            try {
                unbindService(mAntRadioServiceConnection);
            } catch (IllegalArgumentException e) {
                // Not bound, that's what we want anyway
            }

            mAntRadioServiceBound = false;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAntRadioServiceBound = false;

        doBindAntRadioService();

    }

    @Override
    public void onDestroy() {
        closeChannel();
        doUnbindAntRadioService();
        mAntRadioServiceConnection.setmAntChannelProvider(null);

        super.onDestroy();
    }
}
