package com.cheriot.horriblecards.models.firebase;

import com.google.firebase.database.DatabaseReference;

import javax.inject.Inject;

/**
 * Created by cheriot on 8/31/16.
 */
public class FirebaseGamePlayers {

    private DatabaseReference mPlayersRef;

    @Inject
    public FirebaseGamePlayers(DatabaseReference gameRef) {
        mPlayersRef = gameRef.child("/players");
    }

    public DatabaseReference getPlayersRef() {
        return mPlayersRef;
    }
}
