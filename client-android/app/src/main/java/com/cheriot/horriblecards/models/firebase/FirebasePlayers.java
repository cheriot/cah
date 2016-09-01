package com.cheriot.horriblecards.models.firebase;

import com.google.firebase.database.DatabaseReference;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by cheriot on 8/31/16.
 */
public class FirebasePlayers {

    private DatabaseReference mPlayersRef;
    private DatabaseReference mConnectedRef;

    @Inject
    public FirebasePlayers(DatabaseReference gameRef, @Named("uid") String uid) {
        mPlayersRef = gameRef.child("/players");
        mConnectedRef = mPlayersRef .child(uid+"/connected");
    }

    public void setConnected() {
        mConnectedRef.onDisconnect().setValue(false);
        mConnectedRef.setValue(true);
    }

    public DatabaseReference getPlayersRef() {
        return mPlayersRef;
    }
}
