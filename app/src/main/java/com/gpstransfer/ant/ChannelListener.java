package com.gpstransfer.ant;

public class ChannelListener {

    private final ChannelChangedListener channelChangedListener;

    public ChannelListener(ChannelChangedListener channelChangedListener) {
        this.channelChangedListener = channelChangedListener;
    }

    public void onLogChanged(String message) {
        channelChangedListener.onRefreshLog(message);
    }


}
