package com.cheriot.horriblecards.models;

/**
 * Created by cheriot on 8/30/16.
 */
public class Player {
    private String displayName;
    private boolean connected;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
