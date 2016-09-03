package com.cheriot.horriblecards.models.http;

import com.cheriot.horriblecards.BuildConfig;
import com.cheriot.horriblecards.models.GameIdentifier;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * The dealer handles game actions when
 * 1. a player taking that action can cheat
 * 2. putting that action on the server makes it easier to support old clients
 *
 * Created by cheriot on 8/23/16.
 */
public interface Dealer {
    String API_VERSION = "/api/v1";

    @POST(API_VERSION + "/games")
    @Headers("User-Agent: HorribleCards Android " + BuildConfig.VERSION_NAME)
    Call<GameIdentifier> createGame();

    @FormUrlEncoded
    @POST(API_VERSION + "/games/join")
    @Headers("User-Agent: HorribleCards Android " + BuildConfig.VERSION_NAME)
    Call<GameIdentifier> joinGame(@Field("inviteCode") String inviteCode);

    @POST(API_VERSION + "/games/{gameKey}/start")
    Call<BaseResponse> startGame(@Path("gameKey") String gameKey);
}
