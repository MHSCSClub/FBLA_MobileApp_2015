package com.aakportfolio.www.fbla2015;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



public class eventDummy extends ActionBarActivity {
    MHSEvent e;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_event_dummy);
        e = (MHSEvent)intent.getSerializableExtra("event");
        setTitle(e + "");
        TextView dateTextView = (TextView) findViewById(R.id.dateView);
        dateTextView.setText(e.getEventDate());
        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionView);
        descriptionTextView.setText(e.getEventDescription());
    }
    public void sendMessage(View v){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + e.getContactEmail()));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Questions about an event: " + e);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please explain your questions here.");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(eventDummy.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
