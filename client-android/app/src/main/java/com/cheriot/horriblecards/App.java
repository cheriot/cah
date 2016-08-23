package com.cheriot.horriblecards;

import android.app.Application;
import android.os.StrictMode;

/**
 * Android Application
 *
 * Created by cheriot on 8/23/16.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Dagger

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
        }
    }
}
