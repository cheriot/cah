package com.cheriot.horriblecards.models;

/**
 * These are called when state is known and will often not reflect user actions.
 *
 * Created by cheriot on 8/24/16.
 */
public interface AuthenticationStateListener {
    void onSignedIn();
    void onSignedOut();
}
