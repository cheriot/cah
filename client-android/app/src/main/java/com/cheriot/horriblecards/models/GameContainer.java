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

    private AppComponent mAppComponent;
    private DatabaseReference mRootRef;

    private String mGameKey;
    private GameComponent mGameComponent;

    @Inject
    public GameContainer(AppComponent appComponent, DatabaseReference rootRef) {
        mAppComponent = appComponent;
        mRootRef = rootRef;
    }

    public GameComponent playGame(String gameKey) {
        if(!gameKey.equals(mGameKey)) {
            mGameKey = gameKey;
            mGameComponent = mAppComponent.newGameComponent(new GameModule(mRootRef, mGameKey));
        }
        Timber.d("playGame %s.", gameKey);
        return mGameComponent;
    }

}
