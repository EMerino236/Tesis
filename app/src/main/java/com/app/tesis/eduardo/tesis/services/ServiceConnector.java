package com.app.tesis.eduardo.tesis.services;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Eduardo on 20/07/2016.
 */
public class ServiceConnector {

    public static void getRequest(String path) throws IOException {
        URL url = new URL("http://polls.apiblueprint.org/");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Log.d("asd","asd");
        } finally {
            urlConnection.disconnect();
        }
    }
}
