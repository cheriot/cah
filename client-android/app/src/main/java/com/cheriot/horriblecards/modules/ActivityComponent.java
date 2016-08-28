package com.cheriot.horriblecards.modules;

import com.cheriot.horriblecards.activities.MainActivity;
import com.cheriot.horriblecards.activities.PlayGameActivity;

import dagger.Subcomponent;

/**
 * Created by cheriot on 8/25/16.
 */
@ActivityScope
@Subcomponent(modules={ActivityModule.class})
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(PlayGameActivity activity);
}
