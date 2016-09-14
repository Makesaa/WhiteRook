package com.brillio.maheshsundaram.anim;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity  {
    Animation slideUpAnimation, slideDownAnimation;
    ImageView imageView;
    TextView signup;
    EditText phone , password;
    Button login;
    private String TAG="MainActivity";
    private HashMap<String, String> paramsData= new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        signup = (TextView) findViewById(R.id.buttSignUp);

        phone=(EditText) findViewById(R.id.editPhone);
        password=(EditText) findViewById(R.id.editPassword);
        login=(Button) findViewById(R.id.buttLogin);

        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up_animation);

        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_animation);
        setOnClickListener();

    }

    private void setOnClickListener() {
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                Log.i("id", "" + id);
                if (id == R.id.buttSignUp) {
                    startActivity(new Intent(MainActivity.this, SignupActivity.class));
                    // overridePendingTransition(R.anim.slide_up_animation, R.anim.slide_down_animation);
                } else
                    Log.i("Something", "" + view.getId());

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                Log.i("Login", "" + id);
               paramsData.put("Phone", phone.getText().toString());
               paramsData.put("password", password.getText().toString());
               new AsyncHttpTask(paramsData).execute();
            }
        });
    }

    public void startSlideUpAnimation(View view) {
        imageView.startAnimation(slideUpAnimation);
    }

    public void startSlideDownAnimation(View view) {
        imageView.startAnimation(slideDownAnimation);
    }
    class AsyncHttpTask extends AsyncTask<String, Void, String> {
        private HashMap<String, String> paramsData;
        ProgressDialog pb = new ProgressDialog(MainActivity.this);
        private String url;

        private String response;

        public AsyncHttpTask( HashMap<String, String> params) {
          this. paramsData = params;
            this.url = url;
            //this.paramsData.put("appID", APP_ID);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           pb.show();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;

            Integer result = 0;
            try {

                /* forming th java.net.URL object */
                JSONObject paramObject = new JSONObject(this.paramsData);
                URL requestUrl = new URL(Utility.BASEURL+"userLogin");
                Log.e(TAG, Utility.BASEURL+"userLogin");
                Log.e(TAG, paramObject.toString());
                urlConnection = (HttpURLConnection) requestUrl.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setFixedLengthStreamingMode(paramObject.toString().getBytes().length);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                /* optional request header */
                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.setRequestMethod("POST");
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(paramObject.toString());
                out.close();
                int statusCode = urlConnection.getResponseCode();
                Log.d(TAG, "statuscode " + statusCode);
                if (statusCode == 200 || statusCode == 400) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    response = convertInputStreamToString(inputStream);
                    result = 1; // Successful

                } else {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    response = convertInputStreamToString(inputStream);
                    parseResult(response);
                    result = 0; //"Failed to fetch data!";
                }


            } catch (Exception e) {
                System.out.print(e.toString());

            }
            return response; //"Failed to fetch data!";
        }


        @Override
        protected void onPostExecute(String result) {
            pb.dismiss();
            /* Download complete. Lets update UI */
            Log.d(TAG, ""+result);
            Toast.makeText(MainActivity.this,"result "+result, Toast.LENGTH_LONG).show();
//            if (result == 1) {
//
//             //   onSuccessFull(true);
//            } else {
//                Log.e(TAG, "Failed to fetch data!");
//              // onSuccessFull(false);
//            }
        }
    }

    private String convertInputStreamToString(InputStream inputStream) {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String result = "";
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }
            bufferedReader.close();
            /* Close Stream */
            if (null != inputStream) {
                inputStream.close();
            }
        } catch (IOException e) {
            return result;

        }
        return sb.toString();
    }

    private JSONObject parseResult(String result) {

        try {
            JSONObject response = new JSONObject(result);
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
