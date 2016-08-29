package com.cheriot.horriblecards.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Perhaps extract firebase interaction into a AuthSource class so this one is testable without
 * crazy mocks and package private members.
 *
 * Created by cheriot on 8/24/16.
 */
public class AuthServiceTest {

    private AuthService mAuthService;
    @Mock FirebaseAuth mFirebaseAuth;
    @Mock FirebaseUser mFirebaseUser;
    @Captor ArgumentCaptor<FirebaseAuth.AuthStateListener> authStateListenerArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mAuthService = new AuthService(mFirebaseAuth);
    }

    @Test
    public void testStart_success() {
        mAuthService.start();
        // The firebase api is a terrible thing to mock.

        // Grab the listener we hand to firebase-auth and call it manually.
        verify(mFirebaseAuth).addAuthStateListener(authStateListenerArgumentCaptor.capture());
        FirebaseAuth.AuthStateListener authStateListener = authStateListenerArgumentCaptor.getValue();
        assertNotNull("Captured authStateListener.", authStateListener);

        // From that result, verify the uid.
        String uid = "fake-uid";
        when(mFirebaseAuth.getCurrentUser()).thenReturn(mFirebaseUser);
        when(mFirebaseUser.getUid()).thenReturn(uid);

        // Firebase will call our listener.
        authStateListener.onAuthStateChanged(mFirebaseAuth);

        assertEquals("Returns uid.", uid, mAuthService.getUid());
        assertTrue("Is signed in.", mAuthService.isAuthenticated());
    }

    @Test
    public void testAddAuthStateListener_signedIn() {
        // Set signed in without mocking the firebase api.
        mAuthService.mFirebaseUser = mFirebaseUser;

        AuthStateListener mockAuthStateListener = mock(AuthStateListener.class);
        mAuthService.addAuthStateListener(mockAuthStateListener);
        // Immediately tell the listener that the current state is signed in.
        verify(mockAuthStateListener).onSignedIn(mFirebaseUser);
    }

    @Test
    public void testAddAuthStaetListener_signedOut() {
        AuthStateListener mockAuthStateListener = mock(AuthStateListener.class);
        mAuthService.addAuthStateListener(mockAuthStateListener);
        // Immediately tell the listener that the current state is signed in.
        verify(mockAuthStateListener).onSignedOut();
    }
}
