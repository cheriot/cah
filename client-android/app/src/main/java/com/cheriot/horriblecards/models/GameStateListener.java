package com.cheriot.horriblecards.models;

/**
 * Created by cheriot on 9/3/16.
 */
public interface GameStateListener {
    void onUnstartedState();
    void onChooseCardState(int round);
    void onErrorState(Throwable t);
}
