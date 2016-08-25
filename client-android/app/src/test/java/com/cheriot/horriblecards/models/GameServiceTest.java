package com.cheriot.horriblecards.models;

import com.cheriot.horriblecards.activities.GameView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Mockito.*;

/**
 * Created by cheriot on 8/24/16.
 */
public class GameServiceTest {

    private GameService mGameService;
    @Mock GameView mMockGameView;
    @Mock Dealer mMockDealer;
    @Mock AuthService mAuthService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mGameService = new GameService(mMockGameView, mMockDealer, mAuthService);
    }

    @Test
    public void createGame_success() {
        String token = "fakeToken";
        String userId = "fakeUserId";
        when(mAuthService.getToken()).thenReturn(token);

        final Call<GameIdentifier> mockCall = mock(Call.class);
        when(mMockDealer.createGame(anyString(), anyString())).thenAnswer(new Answer<Call>() {
            @Override
            public Call answer(InvocationOnMock invocation) throws Throwable {
                return mockCall;
            }
        });

        mGameService.createGame(userId);

        verify(mMockDealer).createGame(token, userId);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCall).enqueue(callbackCaptor.capture());

        // Call the callback passed to Retrofit.
        String gameKey = "fake-game-key";
        GameIdentifier body = new GameIdentifier();
        body.setGameKey(gameKey);
        callbackCaptor.getValue().onResponse(null, Response.success(body));

        verify(mMockGameView).displayGameUrl(gameKey);
    }
}
