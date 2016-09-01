package com.cheriot.horriblecards.models;

import com.cheriot.horriblecards.modules.GameScope;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;

/**
 * Created by cheriot on 8/27/16.
 */
@GameScope
public class FirebaseGame {

    private DatabaseReference mGameRef;

    @Inject
    public FirebaseGame(DatabaseReference rootRef, @Named("gameKey") String gameKey) {
        mGameRef = rootRef.child("/games/"+gameKey);
        Timber.d("mGameRef %s %s.", rootRef, mGameRef.getKey());
    }

    public void findInviteCode(final TaskResultListener<String> listener) {
        mGameRef.child("/inviteCode")
               .addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               Timber.d("Found game. %s", dataSnapshot.getValue());
               listener.onSuccess((String)dataSnapshot.getValue());
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {
               Timber.e(databaseError.toException(), "Error %d %s", databaseError.getCode(), databaseError.getMessage());
               listener.onError(databaseError.toException());
           }
       });
    }

    public DatabaseReference getGameRef() {
        return mGameRef;
    }
}
