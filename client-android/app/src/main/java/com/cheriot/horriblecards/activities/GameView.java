package com.cheriot.horriblecards.activities;

import com.cheriot.horriblecards.models.AuthStateListener;

/**
 * Created by cheriot on 8/24/16.
 */
public interface GameView extends AuthStateListener {
    void displayGameUrl(String gameUrl);
    void displayError(String msg);
}
