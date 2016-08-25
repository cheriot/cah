package com.cheriot.horriblecards.modules;

import android.app.Application;

import com.cheriot.horriblecards.models.AuthenticationService;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
    AuthenticationService providesAuthenticationService(FirebaseAuth firebaseAuth) {
        return new AuthenticationService(firebaseAuth);
    }

    @Provides
    @Singleton
    FirebaseAuth providesFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}
