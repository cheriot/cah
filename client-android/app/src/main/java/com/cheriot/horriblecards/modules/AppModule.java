package com.cheriot.horriblecards.modules;

import android.app.Application;

import com.cheriot.horriblecards.models.AuthService;
import com.cheriot.horriblecards.models.Dealer;
import com.cheriot.horriblecards.models.DealerService;
import com.cheriot.horriblecards.models.http.AuthInterceptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cheriot on 8/25/16.
 */
@Module
public class AppModule {

    private Application mApplication;

    public AppModule(Application mApplication) {
        this.mApplication = mApplication;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    AuthService providesAuthService(FirebaseAuth firebaseAuth) {
        return new AuthService(firebaseAuth);
    }

    @Provides
    @Singleton
    FirebaseAuth providesFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(AuthInterceptor authInterceptor) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://cards-against-humanity-14b7e.appspot.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    Dealer providesDealer(Retrofit retrofit) {
        return retrofit.create(Dealer.class);
    }

    @Provides
    @Singleton
    public DealerService provideDealerService(Dealer dealer, AuthService authService) {
        return new DealerService(dealer, authService);
    }

    @Provides
    @Singleton
    public DatabaseReference providesDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
