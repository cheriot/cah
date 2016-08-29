package com.cheriot.horriblecards.models;

import com.google.firebase.auth.FirebaseUser;

/**
 * These are called when state is known and will often not reflect user actions.
 *
 * Created by cheriot on 8/24/16.
 */
public interface AuthStateListener {
    void onSignedIn(FirebaseUser firebaseUser);
    void onSignedOut();
}
