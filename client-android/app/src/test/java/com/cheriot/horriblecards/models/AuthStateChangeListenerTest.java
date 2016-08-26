package com.cheriot.horriblecards.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Created by cheriot on 8/26/16.
 */
public class AuthStateChangeListenerTest {

    private AuthStateChangeListener authStateChangeListener;
    private int signedInCalled;
    private int signedOutCalled;

    @Before
    public void setUp() {
        signedInCalled = 0;
        signedOutCalled = 0;
        authStateChangeListener = new AuthStateChangeListener() {
            @Override
            public void onSignedIn(FirebaseUser firebaseUser) {
                signedInCalled += 1;
            }

            @Override
            public void onSignedOut() {
                signedOutCalled += 1;
            }
        };

    }

    private static FirebaseAuth withUser() {
        FirebaseAuth mockFirebaseAuth = mock(FirebaseAuth.class);
        FirebaseUser mockFirebaseUser = mock(FirebaseUser.class);
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
        return mockFirebaseAuth;
    }

    private static FirebaseAuth withoutUser() {
        FirebaseAuth mockFirebaseAuth = mock(FirebaseAuth.class);
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(null);
        return mockFirebaseAuth;
    }

    @Test
    public void onAuthStateChange_firstSignedIn() {
        authStateChangeListener.onAuthStateChanged(withUser());
        assertEquals("signedInCalled", signedInCalled, 1);
        assertEquals("signedOutCalled", signedOutCalled, 0);
    }

    @Test
    public void onAuthStateChange_firstSignedOut() {
        authStateChangeListener.onAuthStateChanged(withoutUser());
        assertEquals("signedInCalled", signedInCalled, 0);
        assertEquals("signedOutCalled", signedOutCalled, 1);
    }

    @Test
    public void onAuthStateChange_duplicateSignedIn() {
        authStateChangeListener.onAuthStateChanged(withUser());
        authStateChangeListener.onAuthStateChanged(withUser());
        authStateChangeListener.onAuthStateChanged(withUser());
        authStateChangeListener.onAuthStateChanged(withUser());
        assertEquals("signedInCalled", signedInCalled, 1);
        assertEquals("signedOutCalled", signedOutCalled, 0);
    }

    @Test
    public void onAuthStateChange_duplicateSignedOut() {
        authStateChangeListener.onAuthStateChanged(withoutUser());
        authStateChangeListener.onAuthStateChanged(withoutUser());
        authStateChangeListener.onAuthStateChanged(withoutUser());
        authStateChangeListener.onAuthStateChanged(withoutUser());
        assertEquals("signedInCalled", signedInCalled, 0);
        assertEquals("signedOutCalled", signedOutCalled, 1);
    }

    @Test
    public void onAuthStateChange_signOut() {
        authStateChangeListener.onAuthStateChanged(withUser());
        authStateChangeListener.onAuthStateChanged(withoutUser());
        assertEquals("signedInCalled", signedInCalled, 1);
        assertEquals("signedOutCalled", signedOutCalled, 1);
    }

    @Test
    public void onAuthStateChange_signIn() {
        authStateChangeListener.onAuthStateChanged(withoutUser());
        authStateChangeListener.onAuthStateChanged(withUser());
        assertEquals("signedInCalled", signedInCalled, 1);
        assertEquals("signedOutCalled", signedOutCalled, 1);
    }
}
