package com.cheriot.horriblecards.modules;

import android.app.Application;

import com.cheriot.horriblecards.App;
import com.cheriot.horriblecards.InitializeUserProfileAuthListener;
import com.cheriot.horriblecards.models.AuthService;
import com.cheriot.horriblecards.models.DealerService;
import com.cheriot.horriblecards.models.GameContainer;
import com.cheriot.horriblecards.models.http.AuthInterceptor;
import com.cheriot.horriblecards.models.http.Dealer;
import com.cheriot.horriblecards.presenters.PlayGamePresenter;
import com.google.firebase.FirebaseApp;
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

    private App mApp;

    public AppModule(App mApp) {
        this.mApp = mApp;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApp;
    }

    @Provides
    @Singleton
    AuthService providesAuthService(FirebaseAuth firebaseAuth) {
        AuthService authService = new AuthService(firebaseAuth);
        authService.addAuthStateListener(new InitializeUserProfileAuthListener());
        return authService;
    }

    @Provides
    @Singleton
    FirebaseAuth providesFirebaseAuth() {
        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        if(firebaseApp == null) throw new RuntimeException("Where is the app?");
        return FirebaseAuth.getInstance(firebaseApp);
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

    @Provides
    @Singleton
    public GameContainer providesGameContainer(DatabaseReference rootRef, AuthService authService) {
        return new GameContainer(mApp.getAppComponent(), rootRef, authService);
    }

    @Provides
    @Singleton
    public PlayGamePresenter providesGamePlayPresenter(GameContainer gameContainer) {
        return new PlayGamePresenter(gameContainer);
    }
}
