package com.aakportfolio.www.fbla2015;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class eventDummy extends ActionBarActivity {
    private MHSEvent e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_event_dummy);
        e = (MHSEvent) intent.getSerializableExtra("event");
        setTitle(e + "");
        TextView dateTextView = (TextView) findViewById(R.id.dateView);
        dateTextView.setText(e.getEventDates());
        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionView);
        descriptionTextView.setText(e.getEventDescription());
        Button emailBtn = (Button) findViewById(R.id.emailButton);
        emailBtn.setClickable(e.showEmail());
        emailBtn.setVisibility(e.showEmail() ? View.VISIBLE : View.GONE);
        Log.d("EMAIL-VIS", e.showEmail() + "");
    }

    public void sendMessage(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Email Organizer")
                .setMessage("Are you sure you want to send an email to the event organizer (email address: " + e.getContactEmail() + ")?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (!isGMailInstalled()) {
                            Toast.makeText(eventDummy.this,
                                    "No email clients installed.\nYou may manually email this address: " + e.getContactEmail(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto:" + e.getContactEmail()));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Questions about an event: " + e);
                            emailIntent.putExtra(Intent.EXTRA_TEXT, "Please explain your questions here.");
                            try {
                                startActivity(Intent.createChooser(emailIntent, "Please select email application"));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(eventDummy.this,
                                        "No email clients installed.\nYou may manually email this address: " + e.getContactEmail(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_email)
                .show();

    }

    public boolean isGMailInstalled() {
        try {
            getPackageManager().getApplicationInfo("com.google.android.apps.plus", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
