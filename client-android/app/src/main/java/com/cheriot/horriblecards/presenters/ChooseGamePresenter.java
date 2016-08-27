package com.cheriot.horriblecards.presenters;

import com.cheriot.horriblecards.activities.GameView;
import com.cheriot.horriblecards.models.AuthService;
import com.cheriot.horriblecards.models.DealerService;
import com.cheriot.horriblecards.models.FirebaseGame;
import com.cheriot.horriblecards.models.TaskResultListener;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by cheriot on 8/27/16.
 */
public class ChooseGamePresenter {

    @Inject DealerService mDealerService;
    @Inject FirebaseGame mFirebaseGame;
    @Inject AuthService mAuthService;

    GameView gameView;

    @Inject
    public ChooseGamePresenter(DealerService dealerService, FirebaseGame firebaseGame, AuthService authService) {
        mDealerService = dealerService;
        mFirebaseGame = firebaseGame;
        mAuthService = authService;
    }

    public void createGame() {
        mDealerService.createGame(new TaskResultListener<String>() {
            @Override
            public void onSuccess(String gameId) {
                Timber.d("Have game id %s.", gameId);
                findGame(gameId);
            }

            @Override
            public void onError(Exception e) {
                getGameView().displayError("Error creating a new Game.");
            }
        });
    }

    public void findGame(String gameId) {
        mFirebaseGame.fetchGameCode(gameId, new TaskResultListener<String>() {
            @Override
            public void onSuccess(String gameCode) {
                getGameView().displayGameUrl(gameCode);
            }

            @Override
            public void onError(Exception e) {
                getGameView().displayError("Error accessing game.");
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
