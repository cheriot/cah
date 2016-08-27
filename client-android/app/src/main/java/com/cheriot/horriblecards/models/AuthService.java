package com.cheriot.horriblecards.models;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

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
    String mFirebaseToken;

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
        return mFirebaseUser != null && mFirebaseToken != null;
    }

    private void setSignedIn(FirebaseUser firebaseUser) {
        mFirebaseUser = firebaseUser;
        Timber.d("setSignedIn %s", firebaseUser.getUid());
        getToken(new TaskResultListener<String>() {
            @Override
            public void onSuccess(String result) {
                mFirebaseToken = result;
            }

            @Override
            public void onError(Exception e) {
                Timber.e(e, "Error getting firebase token.");
            }
        });
        for(AuthStateListener listener : mAuthStateListeners) {
            listener.onSignedIn();
        }
    }

    private void setSignedOut() {
        mFirebaseUser = null;
        mFirebaseToken = null;
        for(AuthStateListener listener : mAuthStateListeners) {
            listener.onSignedOut();
        }
    }

    public void addAuthStateListener(AuthStateListener listener) {
        if(isAuthenticated()) listener.onSignedIn();
        else listener.onSignedOut();
        mAuthStateListeners.add(listener);
    }

    public void removeAuthStateListener(AuthStateListener listener) {
        mAuthStateListeners.remove(listener);
    }

    private void getToken(final TaskResultListener<String> taskResultListener) {
        Timber.d("getToken request");
        mFirebaseUser.getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if(task.isSuccessful()) {
                    Timber.d("getToken result %s", task.getResult().getToken());
                    taskResultListener.onSuccess(task.getResult().getToken());
                } else {
                    taskResultListener.onError(task.getException());
                }
            }
        });
    }

    public String getUid() {
        return mFirebaseUser.getUid();
    }

    public String getToken() {
        return mFirebaseToken;
    }

    public void start() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void stop() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
