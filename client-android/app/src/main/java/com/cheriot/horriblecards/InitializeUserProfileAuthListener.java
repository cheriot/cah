package com.cheriot.horriblecards;

import android.support.annotation.NonNull;

import com.cheriot.horriblecards.models.AuthStateListener;
import com.cheriot.horriblecards.models.RandomNamer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import timber.log.Timber;

/**
 * Created by cheriot on 8/29/16.
 */
public class InitializeUserProfileAuthListener implements AuthStateListener {
    @Override
    public void onSignedIn(FirebaseUser firebaseUser) {
        // The profile has already been initialized.
        if (firebaseUser.getDisplayName() != null) return;

        final String name = new RandomNamer().randomName();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Timber.i("Profile successfully initialized. Welcome, %s.", name);
                        } else {
                            Timber.e(task.getException(), "Error initializing profile.");
                        }
                    }
                });
    }

    @Override
    public void onSignedOut() { }
}
