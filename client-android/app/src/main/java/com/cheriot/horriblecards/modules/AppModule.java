package com.cheriot.horriblecards.modules;

import android.app.Application;

import com.cheriot.horriblecards.models.AuthService;
import com.cheriot.horriblecards.models.Dealer;
import com.cheriot.horriblecards.models.DealerService;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
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
    Dealer providesDealer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://cards-against-humanity-14b7e.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(Dealer.class);
    }

    @Provides
    @Singleton
    public DealerService provideDealerService(Dealer dealer, AuthService authService) {
        return new DealerService(dealer, authService);
    }
}
