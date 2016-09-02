package com.cheriot.horriblecards.models;

import com.cheriot.horriblecards.models.http.Dealer;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Convert to a Presenter and survive config changes.
 * https://medium.com/@czyrux/presenter-surviving-orientation-changes-with-loaders-6da6d86ffbbf
 *
 * Created by cheriot on 8/23/16.
 */
public class DealerService {

    private final Dealer mDealer;
    private final AuthService mAuthService;

    @Inject
    public DealerService(Dealer dealer, AuthService authService) {
        mDealer = dealer;
        mAuthService = authService;
    }

    public void createGame(final TaskResultListener<String> listener) {
        Call<GameIdentifier> call = mDealer.createGame();
        call.enqueue(new Callback<GameIdentifier>() {

            @Override
            public void onResponse(Call<GameIdentifier> call, Response<GameIdentifier> response) {
                // All responses. Success and failure.
                if(response.isSuccessful()) {
                    listener.onSuccess(response.body().getGameKey());
                } else {
                    logErrorResponse("createGame", response);
                    listener.onError(null);
                }
            }

            @Override
            public void onFailure(Call<GameIdentifier> call, Throwable t) {
                // Failures that are not responses from the server.
                Timber.e(t, "Failed to start game.");
                listener.onError(t);
            }
        });
    }

    public void joinGame(String inviteCode, final TaskResultListener<String> listener) {
        Timber.d("joinGame request %s.", inviteCode);
        Call<GameIdentifier> call = mDealer.joinGame(inviteCode);
        call.enqueue(new Callback<GameIdentifier>() {
            @Override
            public void onResponse(Call<GameIdentifier> call, Response<GameIdentifier> response) {
                if(response.isSuccessful()) {
                    Timber.d("joinGame response %s", response.raw().body().toString());
                    listener.onSuccess(response.body().getGameKey());
                } else {
                    logErrorResponse("joinGame", response);
                    listener.onError(null);
                }
            }

            @Override
            public void onFailure(Call<GameIdentifier> call, Throwable t) {

            }
        });
    }

    private static void logErrorResponse(String msg, Response response) {
        try {
            Timber.e("%s response %d %s", msg, response.code(), response.errorBody().string());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
