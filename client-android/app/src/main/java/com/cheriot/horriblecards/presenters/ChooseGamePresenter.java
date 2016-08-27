package com.cheriot.horriblecards.presenters;

import com.cheriot.horriblecards.activities.GameView;
import com.cheriot.horriblecards.models.AuthService;
import com.cheriot.horriblecards.models.DealerService;
import com.cheriot.horriblecards.models.TaskResultListener;

import javax.inject.Inject;

/**
 * Created by cheriot on 8/27/16.
 */
public class ChooseGamePresenter {
    @Inject DealerService mDealerService;
    @Inject AuthService mAuthService;

    GameView gameView;

    @Inject
    public ChooseGamePresenter(DealerService mDealerService, AuthService mAuthService) {
        this.mDealerService = mDealerService;
        this.mAuthService = mAuthService;
    }

    public void createGame() {
        mDealerService.createGame(new TaskResultListener<String>() {
            @Override
            public void onSuccess(String result) {
                getGameView().displayGameUrl(result);
            }

            @Override
            public void onError(Exception e) {
                getGameView().displayError("Error creating a new Game.");
            }
        });
    }

    public void onStart() {
        mAuthService.addAuthStateListener(gameView);
    }

    public void onStop() {
        mAuthService.removeAuthStateListener(gameView);
    }

    public GameView getGameView() {
        return gameView;
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }
}
