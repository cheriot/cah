package com.cheriot.horriblecards.models;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cheriot on 8/24/16.
 */
public class AuthService {

    FirebaseAuth mAuth;
    AuthStateChangeListener mAuthListener;
    List<AuthStateListener> mAuthStateListeners;
    FirebaseUser mFirebaseUser;

    public AuthService(FirebaseAuth auth) {
        Timber.d("construct");
        mAuth = auth;
        mAuthStateListeners = new ArrayList<>();
        mAuthListener = new AuthStateChangeListener() {
            @Override
            public void onSignedIn(FirebaseUser user) {
                setSignedIn(user);
            }

            @Override
            public void onSignedOut() {
                setSignedOut();
                signInAnonymously();
            }
        };
    }

    public boolean isAuthenticated() {
        return mFirebaseUser != null;
    }

    private void setSignedIn(FirebaseUser firebaseUser) {
        mFirebaseUser = firebaseUser;
        Timber.d("setSignedIn %s", firebaseUser.getUid());
        for(AuthStateListener listener : mAuthStateListeners) {
            listener.onSignedIn(mFirebaseUser);
        }
    }

    private void setSignedOut() {
        mFirebaseUser = null;
        for(AuthStateListener listener : mAuthStateListeners) {
            listener.onSignedOut();
        }
    }

    public void addAuthStateListener(AuthStateListener listener) {
        if(isAuthenticated()) listener.onSignedIn(mFirebaseUser);
        else listener.onSignedOut();
        mAuthStateListeners.add(listener);
    }

    public void removeAuthStateListener(AuthStateListener listener) {
        mAuthStateListeners.remove(listener);
    }

    public String getUid() {
        return mFirebaseUser.getUid();
    }

    public void start() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void signOut() {
        mAuth.signOut();
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Timber.e(task.getException(), "Signed in anonymously.");
                        } else {
                            Timber.e(task.getException(), "Unable to sign in anonymously.");
                        }
                    }
                });
    }
}
