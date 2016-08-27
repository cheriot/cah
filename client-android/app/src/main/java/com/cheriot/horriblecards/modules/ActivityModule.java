package com.cheriot.horriblecards.modules;

import android.app.Activity;

import com.cheriot.horriblecards.activities.GameView;
import com.cheriot.horriblecards.models.AuthService;
import com.cheriot.horriblecards.models.Dealer;
import com.cheriot.horriblecards.models.DealerService;

import dagger.Module;
import dagger.Provides;

/**
 * Created by cheriot on 8/25/16.
 */
@Module
public class ActivityModule {
    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @ActivityScope
    @Provides
    public DealerService provideDealerService(Dealer dealer, AuthService authService) {
        return new DealerService((GameView)activity, dealer, authService);
    }
}
