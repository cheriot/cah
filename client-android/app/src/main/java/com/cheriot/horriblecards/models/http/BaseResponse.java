package com.cheriot.horriblecards.models.http;

/**
 * Base class for server responses.
 *
 * Created by cheriot on 9/1/16.
 */
public class BaseResponse {
    private String error;

    public boolean isError() {
        return error == null;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
