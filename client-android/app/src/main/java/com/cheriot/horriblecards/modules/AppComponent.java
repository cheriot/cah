package com.cheriot.horriblecards.modules;

import com.cheriot.horriblecards.App;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by cheriot on 8/25/16.
 */
@Singleton
@Component(modules={AppModule.class})
public interface AppComponent {

    void inject(App app);

    ActivityComponent newActivityComponent(ActivityModule activityModule);

    GameComponent newGameComponent(GameModule gameModule);

}
