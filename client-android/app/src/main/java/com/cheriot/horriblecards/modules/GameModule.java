package com.cheriot.horriblecards.modules;

import com.cheriot.horriblecards.models.FirebaseGame;
import com.cheriot.horriblecards.models.firebase.FirebasePlayers;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by cheriot on 8/31/16.
 */
@Module
public class GameModule {

    private final DatabaseReference mRootRef;
    private final String mGameKey;
    private final String mUid;

    public GameModule(DatabaseReference rootRef, String gameKey, String uid) {
        mRootRef = rootRef;
        mGameKey = gameKey;
        mUid = uid;
    }

    @Provides
    @GameScope
    @Named("gameKey")
    public String providesGameKey() {
        return mGameKey;
    }

    @Provides
    @GameScope
    @Named("uid")
    public String providesUid() {
        return mUid;
    }


        @Provides
    @GameScope
    public FirebasePlayers providesGamePlayers(FirebaseGame firebaseGame) {
        return new FirebasePlayers(firebaseGame.getGameRef(), mUid);
    }
}
