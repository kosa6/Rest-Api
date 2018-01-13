package com.restapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    private Button startButton , showButton;
    private ProgressBar progressBar;
    private TextView nameOfUser, userName,id,typeOfUser,followers;
    private ImageView avatar;
    private String[] information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        onClickButton();
        onClickShowButton();
    }
    private void initUI(){
        startButton = findViewById(R.id.startButton);
        showButton = findViewById(R.id.showInfo);
        showButton.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        nameOfUser = findViewById(R.id.nameOfAccount);
        userName = findViewById(R.id.userName);
        id = findViewById(R.id.id);
        typeOfUser = findViewById(R.id.typeOfUser);
        followers = findViewById(R.id.followers);
        avatar = findViewById(R.id.avatar);
        information = new String[5];
    }
    private void onClickButton(){
        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startAsyncTask();
            }
        });
    }
    private void onClickShowButton(){
        showButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setUpInformation();
            }
        });
    }
    private void setUpInformation(){
        userName.setText("Name: "+information[0]);
        id.setText("Id: "+information[1]);
        typeOfUser.setText("Type of account: "+information[3]);
        followers.setText("Number of followers: "+information[4]);
        new DownloadImageTask(avatar).execute(information[2]);
    }
    private void startAsyncTask(){
        if(!nameOfUser.getText().toString().isEmpty()){
            MyAsyncTask myAsyncTask = new MyAsyncTask();
            myAsyncTask.execute(nameOfUser.getText().toString());
        }
        else{
            Toast.makeText(MainActivity.this,"User name is ether null or empty", Toast.LENGTH_LONG).show();
            Log.e("MainActivity","User name is ether null or empty");
        }
    }
    private class MyAsyncTask extends AsyncTask<String, Integer, String[]> {

        JsonReader jsonReader;
        HttpsURLConnection myConnection;
        @Override
        protected String[] doInBackground(String... integers) {
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
                    String[] info;
                    String[] what = {"login","id","avatar_url","type","followers","oo"};
                    info = extractFromJson(jsonReader,what);
                    return info;
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
            userName.setVisibility(View.GONE);
            id.setVisibility(View.GONE);
            typeOfUser.setVisibility(View.GONE);
            followers.setVisibility(View.GONE);
            avatar.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            if( isCancelled() ) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            userName.setVisibility(View.VISIBLE);
            id.setVisibility(View.VISIBLE);
            typeOfUser.setVisibility(View.VISIBLE);
            followers.setVisibility(View.VISIBLE);
            avatar.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
            information = result;
            showButton.setVisibility(View.VISIBLE);
        }
        private String[] extractFromJson(JsonReader jsonReader, String[] what){
            try {
                String[] values = new String[what.length];
                int index = 0 ;
                jsonReader.beginObject(); // Start processing the JSON object
                while (jsonReader.hasNext()) { // Loop through all keys
                    String key = jsonReader.nextName(); // Fetch the next key
                    if (key.equals(what[index])) { // Check if desired key
                        // Fetch the value as a String
                        try{
                            values[index] = jsonReader.nextString();
                            Log.i("valuse",values[index]);
                        }catch(IllegalStateException e){
                            values[index] = "null";
                        }
                        index++;
                        // Do something with the value
                        // ...
                    } else {
                        jsonReader.skipValue(); // Skip values of other keys
                    }
                }
                return values;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        private DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
