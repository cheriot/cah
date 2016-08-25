package com.cheriot.horriblecards.modules;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Scope for objects that will live as long as the Activity instance.
 * Created by cheriot on 8/25/16.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScope { }
