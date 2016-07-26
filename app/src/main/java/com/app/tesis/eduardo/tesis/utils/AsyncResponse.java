package com.app.tesis.eduardo.tesis.utils;

import org.json.JSONException;

/**
 * Created by Eduardo on 24/07/2016.
 */
public interface AsyncResponse {
    void processFinish(Integer output) throws JSONException;
}
