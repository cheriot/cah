package com.cheriot.horriblecards.activities;

import com.cheriot.horriblecards.recycler.PlayersRecyclerAdapter;

/**
 * Created by cheriot on 8/29/16.
 */
public interface PlayGameView {
    void displayInviteCode(String inviteCode);
    void displayPlayers(PlayersRecyclerAdapter adapter);
    void displayStarted();
    void displayStartError();
    void displayError(String msg);
    void displayUnstartedState();
    void displayRound(int roundNumber);
}
