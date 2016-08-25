package com.cheriot.horriblecards;

import android.app.Activity;
import android.app.Application;
import android.os.StrictMode;

import com.cheriot.horriblecards.modules.ActivityComponent;
import com.cheriot.horriblecards.modules.ActivityModule;
import com.cheriot.horriblecards.modules.AppComponent;
import com.cheriot.horriblecards.modules.AppModule;
import com.cheriot.horriblecards.modules.DaggerAppComponent;

import timber.log.Timber;

/**
 * Android Application
 *
 * Created by cheriot on 8/23/16.
 */
public class App extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        // Crash recording

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDialog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDropBox()
                    .build());
            Timber.plant(new Timber.DebugTree());
        }
    }

    public ActivityComponent newActivityComponent(Activity activity) {
        return appComponent.newActivityComponent(new ActivityModule(activity));
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
