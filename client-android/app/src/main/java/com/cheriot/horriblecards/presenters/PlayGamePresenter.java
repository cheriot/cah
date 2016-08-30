package com.cheriot.horriblecards.presenters;

import com.cheriot.horriblecards.R;
import com.cheriot.horriblecards.activities.PlayGameView;
import com.cheriot.horriblecards.models.FirebaseGame;
import com.cheriot.horriblecards.models.Player;
import com.cheriot.horriblecards.models.TaskResultListener;
import com.cheriot.horriblecards.recycler.PlayerViewHolder;
import com.cheriot.horriblecards.recycler.PlayersRecyclerAdapter;

import javax.inject.Inject;

/**
 * Created by cheriot on 8/29/16.
 */
public class PlayGamePresenter {

    private PlayGameView mPlayGameView;
    private FirebaseGame mFirebaseGame;

    @Inject
    public PlayGamePresenter(FirebaseGame mFirebaseGame) {
        this.mFirebaseGame = mFirebaseGame;
    }

    public void startPlaying(String gameKey) {
        mFirebaseGame.subscribeToGame(gameKey);
        mFirebaseGame.gameCode(new TaskResultListener<String>() {
            @Override
            public void onSuccess(String gameCode) {
                getPlayGameView().displayGameCode(gameCode);
                getPlayGameView().displayPlayers(playersAdapter());
            }

            @Override
            public void onError(Throwable t) {
                getPlayGameView().displayError("Error accessing game.");
            }
        });
    }

    private PlayersRecyclerAdapter playersAdapter() {
        return new PlayersRecyclerAdapter(
                Player.class,
                R.layout.list_item_player,
                PlayerViewHolder.class,
                mFirebaseGame.players());
    }

    private PlayGameView getPlayGameView() {
        return mPlayGameView;
    }

    public void setPlayGameView(PlayGameView playGameView) {
        mPlayGameView = playGameView;
    }
}
