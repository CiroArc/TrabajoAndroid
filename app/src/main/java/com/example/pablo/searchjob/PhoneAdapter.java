package com.example.pablo.searchjob;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ciro on 23/10/2015.
 */
public class PhoneAdapter extends CursorAdapter {
    public PhoneAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.job_phone_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView phoneTextView = (TextView)view.findViewById(R.id.phone_text_view);

        phoneTextView.setText(cursor.getString(1));
    }
}
