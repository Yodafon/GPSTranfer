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
import android.widget.*;
import com.dsi.ant.channel.ChannelNotAvailableException;
import com.dsi.ant.channel.UnsupportedFeatureException;
import com.gpstransfer.ant.ChannelChangedListener;
import com.gpstransfer.ant.ChannelService;
import com.gpstransfer.ant.ChannelService.ChannelServiceComm;

public class MainPage extends AppCompatActivity {
    private boolean mChannelServiceBound = false;

    private static final String LOGGER = MainPage.class.getSimpleName();
    private boolean isDebugEnabled = true;

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
            ((Switch) findViewById(R.id.debug_switch)).setEnabled(true);
        }

    }

    private void startReceiving() {

        ScrollView viewById = (ScrollView) findViewById(R.id.scroll_window);
        TextView childAt = (TextView) viewById.getChildAt(0);
        isDebugEnabled = ((Switch) findViewById(R.id.debug_switch)).isChecked();
        ((Switch) findViewById(R.id.debug_switch)).setEnabled(false);

        childAt.setText("");
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
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
                public void onRefreshLog(int priority, final String message) {
                    if (isDebugEnabled == false) {
                        if (Log.INFO == priority) {
                            log(message);
                        }
                    } else {
                        log(message);
                    }
                }


                // Updates the UI to allow/disallow acquiring new channels
                @Override
                public void onRefreshProgressBar(int percent) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                            progressBar.setMax(100);
                            progressBar.setProgress(percent, true);
                        }
                    });

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

    private void log(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView viewById = (TextView) findViewById(R.id.log_window);
                viewById.append(message + "\n");
                ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_window);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
    }


}
