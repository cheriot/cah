package com.cheriot.horriblecards.models.http;

import com.cheriot.horriblecards.models.firebase.AuthTokenService;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cheriot on 8/29/16.
 */
public class AuthInterceptor implements Interceptor {

    public static final String AUTH_HEADER = "Authorization";
    private AuthTokenService mAuthTokenService;

    @Inject
    public AuthInterceptor(AuthTokenService authTokenService) {
        mAuthTokenService = authTokenService;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = mAuthTokenService.getTokenSync();
        Request request = chain.request()
                .newBuilder()
                .addHeader(AUTH_HEADER, token)
                .build();

        return chain.proceed(request);
    }
}
