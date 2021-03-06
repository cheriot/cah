package com.cheriot.horriblecards.models.http;

import com.cheriot.horriblecards.models.GameIdentifier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;

/**
 * Use okhttp's MockWebServer to test Dealer and figure out how Retrofit works.
 * Created by cheriot on 8/23/16.
 */
public class DealerUnitTest {

    private Dealer dealer;
    private MockWebServer server;

    @Before
    public void setUp() {
        server = new MockWebServer();
        HttpUrl url = server.url("/");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url.toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dealer = retrofit.create(Dealer.class);
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void createGame_200() throws IOException, InterruptedException {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"gameKey\": \"mock-game-key\"}");
        server.enqueue(mockResponse);

        Call<GameIdentifier> call = dealer.createGame();
        Response<GameIdentifier> response = call.execute();
        assertEquals("200", 200, response.code());
        assertEquals("Received gameKey.", "mock-game-key", response.body().getGameKey());

        RecordedRequest request = server.takeRequest();
        assertEquals("Request POST.", "POST", request.getMethod());
        assertEquals("Request path.", "/api/v1/games", request.getPath());
    }

    @Test
    public void createGame_Error() {
        // TODO define behavior
    }
}
