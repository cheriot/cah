package com.cheriot.horriblecards.presenters;

import com.cheriot.horriblecards.activities.ChooseGameView;
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

    ChooseGameView chooseGameView;

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
                getChooseGameView().displayError("Error creating a new Game.");
            }
        });
    }

    public void findGame(String gameId) {
        mFirebaseGame.fetchGameCode(gameId, new TaskResultListener<String>() {
            @Override
            public void onSuccess(String gameCode) {
                getChooseGameView().displayGameUrl(gameCode);
            }

            @Override
            public void onError(Exception e) {
                getChooseGameView().displayError("Error accessing game.");
            }
        });
    }

    public void onStart() {
        mAuthService.addAuthStateListener(chooseGameView);
    }

    public void onStop() {
        mAuthService.removeAuthStateListener(chooseGameView);
    }

    public ChooseGameView getChooseGameView() {
        return chooseGameView;
    }

    public void setChooseGameView(ChooseGameView chooseGameView) {
        this.chooseGameView = chooseGameView;
    }
}
