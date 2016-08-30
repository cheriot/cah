package com.cheriot.horriblecards.models;

/**
 * Created by cheriot on 8/24/16.
 */
public interface TaskResultListener<T> {
    void onSuccess(T result);
    void onError(Throwable e);
}
