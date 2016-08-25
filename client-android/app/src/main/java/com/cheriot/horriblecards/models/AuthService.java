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

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private List<AuthStateListener> mAuthStateListeners;
    private FirebaseUser mFirebaseUser;
    private String mFirebaseToken;

    public AuthService(FirebaseAuth auth) {
        mAuth = auth;
        mAuthStateListeners = new ArrayList<>();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Timber.d("onAuthStateChanged:signed_in:%s", user.getUid());
                    setSignedIn(user);
                } else {
                    Timber.d("onAuthStateChanged:signed_out");
                    setSignedOut();
                    signInAnonymously();
                }
            }
        };
    }

    public boolean isAuthenticated() {
        return mFirebaseUser != null && mFirebaseToken != null;
    }

    private void setSignedIn(FirebaseUser firebaseUser) {
        mFirebaseUser = firebaseUser;
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
        mAuthStateListeners.add(listener);
    }

    public void removeAuthStateListener(AuthStateListener listener) {
        mAuthStateListeners.remove(listener);
    }

    private void getToken(final TaskResultListener<String> taskResultListener) {
        mFirebaseUser.getToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if(task.isSuccessful()) {
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

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Timber.d("signInAnonymously:onComplete:%s", task.isSuccessful());

                        if (task.isSuccessful()) {
                            setSignedIn(task.getResult().getUser());
                        } else {
                            setSignedOut();
                            Timber.e(task.getException(), "Unable to sign in anonymously.");
                        }
                    }
                });
    }
}
