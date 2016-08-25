package com.cheriot.horriblecards.modules;

import android.app.Activity;

import com.cheriot.horriblecards.activities.GameView;
import com.cheriot.horriblecards.models.Dealer;
import com.cheriot.horriblecards.models.GameService;

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
    public GameService provideGameService(Dealer dealer) {
        return new GameService((GameView)activity, dealer);
    }
}
