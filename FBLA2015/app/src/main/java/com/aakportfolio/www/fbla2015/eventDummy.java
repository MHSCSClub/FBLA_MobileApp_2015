package com.aakportfolio.www.fbla2015;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class eventDummy extends ActionBarActivity implements View.OnClickListener {
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
        emailBtn.setOnClickListener(this);
        Button shareBtn = (Button) findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(this);
        Button calBtn = (Button) findViewById(R.id.calBtn);
        calBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emailButton:
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
                break;
            case R.id.shareBtn:
                String message = "Check out this event at MHS: " + e.getEventName() + " (" + e.getEventStartDate() + ")";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "How would you like to share the event?"));
                break;
            case R.id.calBtn:
                try {
                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                    calIntent.setType("vnd.android.cursor.item/event");
                    calIntent.putExtra(CalendarContract.Events.TITLE, e.getEventName());
                    calIntent.putExtra(CalendarContract.Events.DESCRIPTION, e.getEventDescription());
                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, e.getIsAllDay());
                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            e.getCalStart().getTimeInMillis() + e.startMS());
                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            e.getCalEnd().getTimeInMillis() + e.endMS());
                    startActivity(calIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error creating calender  event. Is calender installed and working?", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                //Do nothing
        }
    }

    public boolean isGMailInstalled() {
        try {
            getPackageManager().getApplicationInfo("com.google.android.gm", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
