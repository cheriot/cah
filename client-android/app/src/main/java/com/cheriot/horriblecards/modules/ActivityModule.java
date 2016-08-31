package com.cheriot.horriblecards.modules;

import android.app.Activity;

import dagger.Module;

/**
 * Created by cheriot on 8/25/16.
 */
@Module
public class ActivityModule {
    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }
}
