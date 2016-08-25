package com.cheriot.horriblecards.modules;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by cheriot on 8/25/16.
 */
@Singleton
@Component(modules={AppModule.class})
public interface AppComponent {
    ActivityComponent newActivityComponent(ActivityModule activityModule);
}
