package com.cheriot.horriblecards.presenters;

import com.cheriot.horriblecards.activities.ChooseGameView;
import com.cheriot.horriblecards.models.AuthService;
import com.cheriot.horriblecards.models.DealerService;
import com.cheriot.horriblecards.models.TaskResultListener;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by cheriot on 8/27/16.
 */
public class ChooseGamePresenter {

    private DealerService mDealerService;
    private AuthService mAuthService;

    ChooseGameView chooseGameView;

    @Inject
    public ChooseGamePresenter(DealerService dealerService, AuthService authService) {
        mDealerService = dealerService;
        mAuthService = authService;
    }

    public void createGame() {
        mDealerService.createGame(new TaskResultListener<String>() {
            @Override
            public void onSuccess(String gameId) {
                Timber.d("Have game id %s.", gameId);
                getChooseGameView().startGame(gameId);
            }

            @Override
            public void onError(Throwable e) {
                getChooseGameView().displayError("Error creating a new game.");
            }
        });
    }

    public void joinGame(String inviteCode) {
        Timber.d("joinGame %s", inviteCode);
        mDealerService.joinGame(inviteCode, new TaskResultListener<String>() {
            @Override
            public void onSuccess(String result) {
                getChooseGameView().startGame(result);
            }

            @Override
            public void onError(Throwable e) {
                getChooseGameView().displayError("Error joining game.");
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
