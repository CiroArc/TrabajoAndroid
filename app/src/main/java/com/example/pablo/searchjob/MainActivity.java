package com.example.pablo.searchjob;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.pablo.searchjob.data.JobPostDbContract.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private JobsAdapter jobsAdapter;
    private static final int JOBS_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //button back
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        

        jobsAdapter = new JobsAdapter(this, null, false);
        ListView listView = (ListView)findViewById(R.id.job_post_list_view);
        listView.setAdapter(jobsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)parent.getItemAtPosition(position);

                //System.out.println("click");
                Intent intent = new Intent(MainActivity.this, JobDetailActivity.class);

                intent.putExtra("ID", cursor.getLong(0));
                intent.putExtra("TITLE", cursor.getString(1));
                intent.putExtra("DESCRIPTION", cursor.getString(2));
                intent.putExtra("PHONE", "025444");
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(JOBS_LOADER, null, this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, JobEntry.CONTENT_URI,
                new String[] {JobEntry._ID, JobEntry.COLUMN_TITLE, JobEntry.COLUMN_DESCRIPTION, JobEntry.COLUMN_POSTED_DATE},
                null, null, JobEntry.COLUMN_POSTED_DATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        jobsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        jobsAdapter.swapCursor(null);
    }

// action menu buttons
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    public void syncAction(MenuItem item) {
        Toast toast = Toast.makeText(this, "Sincronizando...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
        NetworkOperationAsyncTask asyncTask = new NetworkOperationAsyncTask();
        asyncTask.execute();
    }

    public void cerrarapp(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Esta seguro que desea salir de la aplicación?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void NewPost(MenuItem item) {

        Intent intent1= new Intent(MainActivity.this,JobPostActivity.class);
        startActivity(intent1);

       /* title_txt_new="Prueba Ciro";
        description_txt_new=" Att Cir";
        phone_txt_new="71060777";

        */
    }
    //Post


//Profe
    private class NetworkOperationAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // This is my URL: http://dipandroid-ucb.herokuapp.com/work_posts.json
            HttpURLConnection urlConnection = null; // HttpURLConnection Object
            BufferedReader reader = null; // A BufferedReader in order to read the data as a file
            Uri buildUri = Uri.parse("http://dipandroid-ucb.herokuapp.com").buildUpon() // Build the URL using the Uri class
                    .appendPath("work_posts.json").build();
            try {
                URL url = new URL(buildUri.toString()); // Create a new URL

                urlConnection = (HttpURLConnection) url.openConnection(); // Get a HTTP connection
                urlConnection.setRequestMethod("GET"); // I'm using GET to query the server
                urlConnection.addRequestProperty("Content-Type", "application/json"); // The MIME type is JSON
                urlConnection.connect(); // Connect!! to the cloud!!!

                // Methods in order to read a text file (In this case the query from the server)
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();

                // Save the data in a String
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                saveJSONToDatabase(buffer.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }

                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }

            }
            return null;
        }

        private void saveJSONToDatabase(String json) throws JSONException {
            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {
                JSONObject jobPostJSON = array.getJSONObject(i);
                int id = jobPostJSON.getInt("id");
                String title = jobPostJSON.getString("title");
                String description = jobPostJSON.getString("description");
                String postedDate = jobPostJSON.getString("posted_date");
                ContentValues contentValues = new ContentValues();

                contentValues.put(JobEntry._ID, id);
                contentValues.put(JobEntry.COLUMN_TITLE, title);
                contentValues.put(JobEntry.COLUMN_DESCRIPTION, description);
                contentValues.put(JobEntry.COLUMN_POSTED_DATE, postedDate);

                getContentResolver().insert(JobEntry.CONTENT_URI, contentValues);

                JSONArray contactsJSON = jobPostJSON.getJSONArray("contacts");
                for (int j = 0; j < contactsJSON.length(); j++) {
                    String contact = contactsJSON.getString(j);
                    ContentValues contactContentValues = new ContentValues();

                    contactContentValues.put(ContactEntry.COLUMN_NUMBER, contact);
                    contactContentValues.put(ContactEntry.COLUMN_JOB_ID, id);

                    getContentResolver().insert(ContactEntry.CONTENT_URI, contactContentValues);
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getSupportLoaderManager().restartLoader(JOBS_LOADER, null, MainActivity.this);
        }
    }
}
