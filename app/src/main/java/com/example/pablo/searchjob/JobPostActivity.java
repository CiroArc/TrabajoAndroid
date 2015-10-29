package com.example.pablo.searchjob;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ciro on 22/10/2015.
 */
public class JobPostActivity extends AppCompatActivity {
    private String title_txt_new;
    private String description_txt_new;
    private String phone_txt_new;
    private TextView TitleText, DetailText, PhoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post);

        TitleText = (EditText)findViewById(R.id.job_id_Title);
        DetailText = (EditText)findViewById(R.id.job_id_Detail);
        PhoneText = (EditText)findViewById(R.id.job_id_phone);

    }

    public void SendPost(View view) {
        title_txt_new = TitleText.getText().toString();
        description_txt_new = DetailText.getText().toString();
        phone_txt_new = PhoneText.getText().toString();
        if (title_txt_new.matches(""))
            Toast.makeText(getBaseContext(), "Por favor ingrese un Titulo", Toast.LENGTH_LONG).show();
        else {
        HttpAsyncTask asyncTaskh = new HttpAsyncTask();
        asyncTaskh.execute();
        };
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            //POST /work_posts.json
            //Data:{"work_post":{"title":"Title of the Job Post","description":"Text with the description","contacts":["Phone1", "Phone2", ..., "PhoneN"]}}
            String json = "";
            try {

                JSONObject work_Data = new JSONObject();
                JSONObject work_post = new JSONObject();
                JSONArray work_contacts = new JSONArray();

                work_contacts.put(phone_txt_new);

                work_post.put("description",description_txt_new);
                work_post.put("title",title_txt_new);
                work_post.put("contacts",work_contacts);
                work_Data.put("work_post",work_post);
                json = work_Data.toString();
                System.out.println(json);
                //System.out.println(json);
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }

            if(json != "") {
                // Enviamos los datos
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                Uri buildUri = Uri.parse("http://dipandroid-ucb.herokuapp.com").buildUpon().appendPath("work_posts.json").build();
                try {
                    URL url = new URL(buildUri.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST"); // I'm using GET to query the server
                    urlConnection.addRequestProperty("Content-Type", "application/json"); // The MIME type is JSON
                    urlConnection.setDoOutput(true);
                    urlConnection.setFixedLengthStreamingMode(json.getBytes().length);
                    urlConnection.connect();

                    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

                    out.write(json.getBytes());
                    out.flush();
                    out.close();

                    InputStream inputStream = urlConnection.getInputStream();

                    if (inputStream != null)
                        result = "Se envio correctamente";
                    else
                        result = "Error de Envio..!";

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            } else result = "Ingrese Datos..!";

            return result;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Datos Enviados!", Toast.LENGTH_LONG).show();
            JobPostActivity.this.finish();


        }
    }
}
