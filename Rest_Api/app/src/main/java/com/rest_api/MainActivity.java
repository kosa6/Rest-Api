package com.rest_api;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private ThreadCount thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thread = new ThreadCount();
        thread.execute();
    }

    private class ThreadCount extends AsyncTask<JsonReader, Integer, JsonReader> {

        JsonReader jsonReader = null;
        boolean done = false;

        protected JsonReader doInBackground(JsonReader... number) {
            URL allegroRestApi = null;
            try {
                allegroRestApi = new URL("https://api.github.com/");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                HttpsURLConnection myConnection = (HttpsURLConnection) allegroRestApi.openConnection();
                myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");
                if (myConnection.getResponseCode() == 200) {
                    // Success
                    // Further processing here
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =  new InputStreamReader(responseBody, "UTF-8");
                    jsonReader = new JsonReader(responseBodyReader);
                    Toast.makeText(MainActivity.this,"Niby dziala", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(MainActivity.this,"Error", Toast.LENGTH_SHORT);
                    // Error handling code goes here
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonReader;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(JsonReader result) {
            done = true;
        }
    }
}
