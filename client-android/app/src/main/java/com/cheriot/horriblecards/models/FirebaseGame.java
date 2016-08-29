package com.cheriot.horriblecards.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * Created by cheriot on 8/27/16.
 */
@Singleton
public class FirebaseGame {

    @Inject DatabaseReference mRef;

    @Inject
    public FirebaseGame(DatabaseReference ref) {
        mRef = ref;
    }

    public void fetchGameCode(String gameKey, final TaskResultListener<String> listener) {
        Timber.d("Find game code for gameKey %s", gameKey);
       mRef.child("/games/"+gameKey+"/gameCode")
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

}
