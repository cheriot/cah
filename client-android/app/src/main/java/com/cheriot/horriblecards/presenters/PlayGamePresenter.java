package com.cheriot.horriblecards.presenters;

import com.cheriot.horriblecards.activities.PlayGameView;
import com.cheriot.horriblecards.models.FirebaseGame;
import com.cheriot.horriblecards.models.TaskResultListener;

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

    public void findGame(String gameKey) {
        mFirebaseGame.fetchGameCode(gameKey, new TaskResultListener<String>() {
            @Override
            public void onSuccess(String gameCode) {
                getPlayGameView().displayGame(gameCode);
            }

            @Override
            public void onError(Exception e) {
                getPlayGameView().displayError("Error accessing game.");
            }
        });
    }

    private PlayGameView getPlayGameView() {
        return mPlayGameView;
    }

    public void setPlayGameView(PlayGameView playGameView) {
        mPlayGameView = playGameView;
    }
}
