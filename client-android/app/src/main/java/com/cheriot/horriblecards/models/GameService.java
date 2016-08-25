package com.cheriot.horriblecards.models;

import android.util.Log;

import com.cheriot.horriblecards.activities.GameView;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Convert to a Presenter and survive config changes.
 * https://medium.com/@czyrux/presenter-surviving-orientation-changes-with-loaders-6da6d86ffbbf
 *
 * Created by cheriot on 8/23/16.
 */
public class GameService {

    private static final String LOG_TAG = GameService.class.getSimpleName();
    private final GameView mGameView;
    private Dealer mDealer;

    @Inject
    public GameService(GameView gameView, Dealer dealer) {
        mGameView = gameView;
        mDealer = dealer;
    }

    public void createGame(String userId) {
        Call<GameIdentifier> call = mDealer.createGame(userId);
        call.enqueue(new Callback<GameIdentifier>() {
            @Override
            public void onResponse(Call<GameIdentifier> call, Response<GameIdentifier> response) {
                mGameView.displayGameUrl(response.body().getGameKey());
                // TODO subscribe to the game
            }

            @Override
            public void onFailure(Call<GameIdentifier> call, Throwable t) {
                Log.e(LOG_TAG, "Failed to start game.", t);
            }
        });
    }

    public void startGame() {
        // Creator only.
    }

    public void joinGame(String gameKey) {
    }
}
