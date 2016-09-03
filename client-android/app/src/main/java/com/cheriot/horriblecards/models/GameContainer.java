package com.cheriot.horriblecards.models;

import com.cheriot.horriblecards.modules.AppComponent;
import com.cheriot.horriblecards.modules.GameComponent;
import com.cheriot.horriblecards.modules.GameModule;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by cheriot on 8/31/16.
 */
public class GameContainer {

    private final AppComponent mAppComponent;
    private final DatabaseReference mRootRef;
    private final AuthService mAuthService;

    private String mGameKey;
    private GameComponent mGameComponent;

    @Inject
    public GameContainer(AppComponent appComponent, DatabaseReference rootRef, AuthService authService) {
        mAppComponent = appComponent;
        mRootRef = rootRef;
        mAuthService = authService;
    }

    public GameComponent playGame(String gameKey) {
        if(!gameKey.equals(mGameKey)) {
            startGame(gameKey);
        }
        Timber.d("playGame %s.", gameKey);
        return mGameComponent;
    }

    private void startGame(String gameKey) {
        Timber.d("startPlaying %s.", gameKey);
        mGameKey = gameKey;
        try {
            String uid = mAuthService.requireUid();
            mGameComponent = mAppComponent.newGameComponent(new GameModule(mRootRef, mGameKey, uid));
        } catch(AuthService.UnauthenticatedException ua) {
            Timber.e(ua, "Unable to start the game without an authenticated user.");
        }

    }

    public String getGameKey() {
        return mGameKey;
    }

}
