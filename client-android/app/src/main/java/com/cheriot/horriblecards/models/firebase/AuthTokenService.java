package com.cheriot.horriblecards.models.firebase;

import android.support.annotation.NonNull;

import com.cheriot.horriblecards.models.AuthService;
import com.cheriot.horriblecards.models.AuthStateListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Request a firebase token and #wait() until it is available.
 *
 * This will need to be the best tested class in the project.
 *
 * Created by cheriot on 8/29/16.
 */
public class AuthTokenService {

    private AuthService mAuthService;
    private FirebaseUser mFirebaseUser;
    private RuntimeException tokenRequestException;
    private boolean issuedTokenRequest;
    private String token;

    @Inject
    public AuthTokenService(AuthService authService) {
        mAuthService = authService;
        issuedTokenRequest = false;

        Timber.d("Registering AuthStateListener.");
        mAuthService.addAuthStateListener(new AuthStateListener() {
            @Override
            public void onSignedIn(FirebaseUser firebaseUser) {
                signIn(firebaseUser);
            }

            @Override
            public void onSignedOut() {
                Timber.i("Signed out. Authenticated server access is blocked.");
                signOut();
            }
        });
    }

    public synchronized String getTokenSync() {
        while(token == null) {
            if(tokenRequestException != null) throw tokenRequestException;

            try {
                Timber.d("Waiting in getTokenSync.");
                wait(); // ** WAIT **
            } catch(InterruptedException ie) {
                Timber.e(ie, "Interrupted while waiting for a firebase token.");
            }
        }
        return token;
    }

    private synchronized void requestToken() {
        Timber.d("getToken request");

        // The Firebase library becomes finicky when multiple tokens are requested.
        if(issuedTokenRequest) return;
        issuedTokenRequest = true;

        final Object lock = this;
        mFirebaseUser.getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if(task.isSuccessful()) {
                    Timber.d("getToken result %s", task.getResult().getToken());
                    token = task.getResult().getToken();
                    synchronized (lock) {
                        lock.notifyAll(); // ** NOTIFY **
                    }
                } else {
                    Timber.e(task.getException(), "Error: requestToken failed.");
                    tokenRequestException = new RuntimeException("requestToken failed.", task.getException());
                }
                issuedTokenRequest = false;
            }
        });
    }

    private synchronized void signIn(FirebaseUser firebaseUser) {
        mFirebaseUser = firebaseUser;
        tokenRequestException = null;
        requestToken();
    }

    private synchronized void signOut() {
        mFirebaseUser = null;
        tokenRequestException = null;
        issuedTokenRequest = false;
    }
}
