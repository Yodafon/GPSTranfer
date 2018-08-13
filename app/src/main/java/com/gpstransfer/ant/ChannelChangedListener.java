package com.gpstransfer.ant;

public interface ChannelChangedListener {
    /**
     * Occurs when a Channel's Info has changed (i.e. a newly created
     * channel, channel has transmitted or received data, or if channel has
     * been closed.
     *
     * @param priority
     * @param message The channel incoming/outcoming message
     */
    void onRefreshLog(int priority, String message);

    void onRefreshProgressBar(int percent);

}
