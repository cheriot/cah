package com.cheriot.horriblecards.models;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import timber.log.Timber;

/**
 * Created by cheriot on 8/26/16.
 */
public abstract class AuthStateChangeListener implements FirebaseAuth.AuthStateListener {
    private Boolean signedIn;

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(signedIn != null && signedIn == (user != null)) {
            Timber.d("onAuthStateChange duplicate %s %s", signedIn, user);
            return;
        }
        else if (user != null) {
            Timber.d("onSignedIn:%s", user.getUid());
            signedIn = true;
            onSignedIn(user);
        } else {
            Timber.d("onSignedOut");
            signedIn = false;
            onSignedOut();
        }
    }

    public abstract void onSignedIn(FirebaseUser firebaseUser);

    public abstract void onSignedOut();
}
