package com.cheriot.horriblecards.activities;

import com.cheriot.horriblecards.recycler.PlayersRecyclerAdapter;

/**
 * Created by cheriot on 8/29/16.
 */
public interface PlayGameView {
    void displayGameCode(String gameCode);
    void displayPlayers(PlayersRecyclerAdapter adapter);
    void displayError(String msg);
}
