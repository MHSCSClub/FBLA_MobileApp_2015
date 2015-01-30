package com.aakportfolio.www.fbla2015;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
        new AlertDialog.Builder(this)
                .setTitle("Email Organizer")
                .setMessage("Are you sure you want to send an email to the event organizer (email address: " + e.getContactEmail() + ")?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:" + e.getContactEmail()));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Questions about an event: " + e);
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please explain your questions here.");

                        try {
                            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(eventDummy.this,
                                    "No email clients installed.\nYou may manually email this address: " + e.getContactEmail(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
}
