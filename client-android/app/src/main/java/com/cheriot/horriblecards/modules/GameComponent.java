package com.cheriot.horriblecards.modules;

import com.cheriot.horriblecards.presenters.PlayGamePresenter;

import dagger.Subcomponent;

/**
 * Created by cheriot on 8/31/16.
 */
@GameScope
@Subcomponent(modules={GameModule.class})
public interface GameComponent {

    void inject(PlayGamePresenter playGamePresenter);

}
