package com.example.pablo.searchjob;

import android.content.ContentUris;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import static com.example.pablo.searchjob.data.JobPostDbContract.*;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class JobDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CONTACTS_LOADER = 1;
    private PhoneAdapter phoneAdapter;
    private long jobId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        TextView idTextView = (TextView)findViewById(R.id.job_id_text_view);
        TextView titleTextView = (TextView)findViewById(R.id.job_title_text_view);
        TextView detailTextView = (TextView)findViewById(R.id.job_detail_text_view);
        //ListView phoneListView = (ListView) findViewById(R.id.job_phone_view);

        idTextView.setText(Long.toString(getIntent().getLongExtra("ID", 0)));
        jobId = getIntent().getLongExtra("ID", 0);
        titleTextView.setText(getIntent().getStringExtra("TITLE"));
        detailTextView.setText(getIntent().getStringExtra("DESCRIPTION"));

        //titleTextView.setText(Long.toString(jobId));

        getSupportActionBar().setTitle(getIntent().getStringExtra("TITLE"));

        //phoneTextView.setText(getIntent().getStringExtra("PHONE"));
        phoneAdapter = new PhoneAdapter(this, null, false);
        ListView phoneListView = (ListView) findViewById(R.id.job_phone_view);
        phoneListView.setAdapter(phoneAdapter);
        getSupportLoaderManager().initLoader(CONTACTS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //return null;
        return new CursorLoader(this,
                ContactEntry.CONTENT_URI,
                new String[]{ContactEntry._ID, ContactEntry.COLUMN_NUMBER},ContactEntry.COLUMN_JOB_ID + "=?" ,new String[]{Long.toString(jobId)},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        phoneAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        phoneAdapter.swapCursor(null);

    }

}
