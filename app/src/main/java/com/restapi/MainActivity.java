package com.restapi;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private ProgressBar progressBar;
    private TextView nameOfUser;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        onClickButton();
    }
    private void initUI(){
        startButton = findViewById(R.id.startButton);
        progressBar = findViewById(R.id.progressBar);
        nameOfUser = findViewById(R.id.nameOfAccount);
        listView = findViewById(R.id.listView);
    }
    private void onClickButton(){
        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                MyAsyncTask myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute(nameOfUser.getText().toString());
            }
        });
    }
    private class MyAsyncTask extends AsyncTask<String, Integer, String> {

        JsonReader jsonReader;
        HttpsURLConnection myConnection;
        @Override
        protected String doInBackground(String... integers) {
            try {
                URL githubEndpoint = new URL("https://api.github.com/users/"+integers[0]);
                myConnection = (HttpsURLConnection) githubEndpoint.openConnection();
                myConnection.setRequestProperty("kosa6", "my-rest-app-v0.1");
                myConnection.getResponseMessage();
                if (myConnection.getResponseCode() == 200) {
                    // Success
                    // Further processing here
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                    jsonReader = new JsonReader(responseBodyReader);
                    Log.i("Tag","1");
                    return extractFromJson(jsonReader,"bio");
                } else {
                    Log.d("Tag","wrong user name");
                    // Error handling code goes here
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if( isCancelled() ) {
                return;
            }

            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
        }
        private String extractFromJson(JsonReader jsonReader, String what){
            try {
                jsonReader.beginObject(); // Start processing the JSON object
                while (jsonReader.hasNext()) { // Loop through all keys
                    String key = jsonReader.nextName(); // Fetch the next key
                    if (key.equals(what)) { // Check if desired key
                        // Fetch the value as a String
                        String value;
                        try{
                            value= jsonReader.nextString();
                        }catch(IllegalStateException e){
                            value = "null";
                        }
                        return value;
                        // Do something with the value
                        // ...
                    } else {
                        jsonReader.skipValue(); // Skip values of other keys
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
