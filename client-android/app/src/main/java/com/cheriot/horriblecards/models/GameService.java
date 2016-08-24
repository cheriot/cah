package com.cheriot.horriblecards.models;

import android.util.Log;

import com.cheriot.horriblecards.activities.GameView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cheriot on 8/23/16.
 */
public class GameService {

    private static final String LOG_TAG = GameService.class.getSimpleName();
    private final GameView mGameView;
    private Dealer mDealer;

    public GameService(GameView gameView, Dealer dealer) {
        mGameView = gameView;
        if(dealer == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://cards-against-humanity-14b7e.appspot.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mDealer = retrofit.create(Dealer.class);
        } else {
            mDealer = dealer;
        }
    }

    public void createGame(String userId) {
        Call<GameIdentifier> call = mDealer.createGame(userId);
        call.enqueue(new Callback<GameIdentifier>() {
            @Override
            public void onResponse(Call<GameIdentifier> call, Response<GameIdentifier> response) {
                mGameView.displayGameUrl(response.body().getGameKey());
                // subscribe to the game
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
