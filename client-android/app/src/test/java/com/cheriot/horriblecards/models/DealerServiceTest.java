package com.cheriot.horriblecards.models;

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
public class DealerServiceTest {

    private DealerService mDealerService;
    @Mock TaskResultListener mMockTaskResultListener;
    @Mock Dealer mMockDealer;
    @Mock AuthService mAuthService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mDealerService = new DealerService(mMockDealer, mAuthService);
    }

    @Test
    public void createGame_success() {
        String token = "fakeToken";
        when(mAuthService.getToken()).thenReturn(token);

        final Call<GameIdentifier> mockCall = mock(Call.class);
        when(mMockDealer.createGame(anyString())).thenAnswer(new Answer<Call>() {
            @Override
            public Call answer(InvocationOnMock invocation) throws Throwable {
                return mockCall;
            }
        });

        mDealerService.createGame(mMockTaskResultListener);

        verify(mMockDealer).createGame(token);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCall).enqueue(callbackCaptor.capture());

        // Call the callback passed to Retrofit.
        String gameKey = "fake-game-key";
        GameIdentifier body = new GameIdentifier();
        body.setGameKey(gameKey);
        callbackCaptor.getValue().onResponse(null, Response.success(body));

        verify(mMockTaskResultListener).onSuccess(gameKey);
    }
}
