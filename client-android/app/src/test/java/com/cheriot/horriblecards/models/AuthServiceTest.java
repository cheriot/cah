package com.cheriot.horriblecards.models;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

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

        // Now verify that on login we fetch the token. Grab the listener for that async call.
        Task mockGetTokenTask = mock(Task.class);
        when(mFirebaseUser.getToken(anyBoolean())).thenReturn(mockGetTokenTask);

        authStateListener.onAuthStateChanged(mFirebaseAuth);

        // When the token becomes available, we're logged in.
        String token = "fake-token";
        ArgumentCaptor<OnCompleteListener> getTokenListenerArgCapture = ArgumentCaptor.forClass(OnCompleteListener.class);
        verify(mockGetTokenTask).addOnCompleteListener(getTokenListenerArgCapture.capture());
        when(mockGetTokenTask.isSuccessful()).thenReturn(true);
        GetTokenResult mockGetTokenResult = mock(GetTokenResult.class);
        when(mockGetTokenResult.getToken()).thenReturn(token);
        when(mockGetTokenTask.getResult()).thenReturn(mockGetTokenResult);
        getTokenListenerArgCapture.getValue().onComplete(mockGetTokenTask);

        assertEquals("Returns uid.", uid, mAuthService.getUid());
        assertNotNull("Has token.", mAuthService.getToken());
        assertTrue("Is signed in.", mAuthService.isAuthenticated());
    }

    @Test
    public void testAddAuthStateListener_signedIn() {
        // Set signed in without mocking the firebase api.
        mAuthService.mFirebaseUser = mFirebaseUser;
        mAuthService.mFirebaseToken = "fake-token";

        AuthStateListener mockAuthStateListener = mock(AuthStateListener.class);
        mAuthService.addAuthStateListener(mockAuthStateListener);
        // Immediately tell the listener that the current state is signed in.
        verify(mockAuthStateListener).onSignedIn();
    }

    @Test
    public void testAddAuthStaetListener_signedOut() {
        AuthStateListener mockAuthStateListener = mock(AuthStateListener.class);
        mAuthService.addAuthStateListener(mockAuthStateListener);
        // Immediately tell the listener that the current state is signed in.
        verify(mockAuthStateListener).onSignedOut();
    }
}
