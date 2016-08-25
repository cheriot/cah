package com.cheriot.horriblecards.models;

import com.cheriot.horriblecards.activities.GameView;

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
public class GameService {

    private static final String LOG_TAG = GameService.class.getSimpleName();
    private final GameView mGameView;
    private final Dealer mDealer;
    private final AuthService mAuthService;

    @Inject
    public GameService(GameView gameView, Dealer dealer, AuthService authService) {
        mGameView = gameView;
        mDealer = dealer;
        mAuthService = authService;
    }

    public void createGame() {
        String token = mAuthService.getToken();
        Timber.d("createGame with token %s", token);
        Timber.d("createGame with token of length %d", token.length());
        Call<GameIdentifier> call = mDealer.createGame(token);
        call.enqueue(new Callback<GameIdentifier>() {

            @Override
            public void onResponse(Call<GameIdentifier> call, Response<GameIdentifier> response) {
                // All responses. Success and failure.
                if(response.isSuccessful()) {
                    mGameView.displayGameUrl(response.body().getGameKey());
                } else {
                    try {
                        Timber.e("createGame error response %d %s", response.code(), response.errorBody().string());
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                }
            }

            @Override
            public void onFailure(Call<GameIdentifier> call, Throwable t) {
                // Failures that are not responses from the server.
                Timber.e(t, "Failed to start game.");
            }
        });
    }

    public void startGame() {
        // Creator only.
    }

    public void joinGame(String gameKey) {
    }
}
