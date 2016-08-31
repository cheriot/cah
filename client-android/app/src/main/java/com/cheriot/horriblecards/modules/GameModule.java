package com.cheriot.horriblecards.modules;

import com.cheriot.horriblecards.models.FirebaseGame;
import com.cheriot.horriblecards.models.firebase.FirebaseGamePlayers;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by cheriot on 8/31/16.
 */
@Module
public class GameModule {

    private DatabaseReference mRootRef;
    private String mGameKey;

    public GameModule(DatabaseReference rootRef, String gameKey) {
        mRootRef = rootRef;
        mGameKey = gameKey;
    }

    @Provides
    @GameScope
    @Named("gameKey")
    public String providesGameKey() {
        return mGameKey;
    }

    @Provides
    @GameScope
    public FirebaseGamePlayers providesGamePlayers(FirebaseGame firebaseGame) {
        return new FirebaseGamePlayers(firebaseGame.getGameRef());
    }
}
