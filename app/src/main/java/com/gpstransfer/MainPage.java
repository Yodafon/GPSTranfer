package com.gpstransfer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.dsi.ant.channel.ChannelNotAvailableException;
import com.dsi.ant.channel.UnsupportedFeatureException;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.ChannelService;
import com.gpstransfer.ant.ChannelService.ChannelServiceComm;

public class MainPage extends AppCompatActivity {
    private boolean mChannelServiceBound = false;

    private static final String LOGGER = MainPage.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_page);
        if (!mChannelServiceBound) doBindChannelService();
        initButton();
    }

    private void initButton() {
        //Register Add Channel Button handler
        Button button_openChannel = (Button) findViewById(R.id.button_start);
        button_openChannel.setEnabled(true);
        button_openChannel.setOnClickListener((View v) -> {
            startReceiving();
        });

        Button button_closeChannel = (Button) findViewById(R.id.button_stop);
        button_closeChannel.setEnabled(true);
        button_closeChannel.setOnClickListener((View v) -> {
            stopReceiving();
        });
    }

    private void stopReceiving() {
        if (null != channelService) {
            channelService.closeAntChannel();
        }
    }

    private void startReceiving() {
        if (null != channelService) {
            try {
                channelService.startReceiving();
            } catch (ChannelNotAvailableException | UnsupportedFeatureException e) {
                Toast.makeText(this, "Channel Not Available", Toast.LENGTH_SHORT).show();
                Log.e(LOGGER, "Channel not available", e);
                return;
            }
        }
    }

    private ChannelServiceComm channelService;


    private void doBindChannelService() {

        // Binds to ChannelService. ChannelService binds and manages connection between the
        // app and the ANT Radio Service
        Intent bindIntent = new Intent(this, ChannelService.class);
        startService(bindIntent);
        mChannelServiceBound = bindService(bindIntent, mChannelServiceConnection, Context.BIND_AUTO_CREATE);

        if (!mChannelServiceBound)   //If the bind returns false, run the unbind method to update the GUI
            doUnbindChannelService();


    }


    private void doUnbindChannelService() {

        if (mChannelServiceBound) {
            unbindService(mChannelServiceConnection);

            mChannelServiceBound = false;
        }
    }

    private ServiceConnection mChannelServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder) {

            channelService = (ChannelServiceComm) serviceBinder;

            // Sets a listener that handles channel events
            channelService.setOnChannelChangedListener(new ChannelChangedListener() {
                // Occurs when a channel has new info/data
                @Override
                public void onRefreshLog(final String message) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView viewById = (TextView) findViewById(R.id.log_window);
                            viewById.append(message + "\n");
                        }
                    });
                }


                // Updates the UI to allow/disallow acquiring new channels
                @Override
                public void onRefreshProgressBar(int percent) {
                    // Enable Add Channel button and Master/Slave toggle if
                    // adding channels is allowed
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            // Clearing and disabling when disconnecting from ChannelService
            channelService = null;

            ((Button) findViewById(R.id.button_start)).setEnabled(true);
            ((Button) findViewById(R.id.button_stop)).setEnabled(false);
        }
    };


}
