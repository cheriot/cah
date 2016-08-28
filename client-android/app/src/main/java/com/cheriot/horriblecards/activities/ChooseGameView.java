package com.cheriot.horriblecards.activities;

import com.cheriot.horriblecards.models.AuthStateListener;

/**
 * Created by cheriot on 8/24/16.
 */
public interface ChooseGameView extends AuthStateListener {
    void startGame(String gameKey);
    void displayError(String msg);
}
