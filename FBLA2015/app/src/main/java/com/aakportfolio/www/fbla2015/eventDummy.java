//Filename: eventDummy.java
/**
 * This is the activity for each event in this android app.
 * Upon selecting an event, this loads, and is changed based on the event.
 * @author Andrew Katz
 * @version 1.0
 */
package com.aakportfolio.www.fbla2015;

//Import section

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
//End of import section

public class eventDummy extends ActionBarActivity implements View.OnClickListener {
    //Event passed from MainActivity
    private MHSEvent myEvent;

    /**
     * This method runs on launch of the activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Standard android setup
        super.onCreate(savedInstanceState);                     //Let android do the normal things
        setContentView(R.layout.activity_event_dummy);

        //Get event passed to this activity
        Intent intent = getIntent();
        myEvent = (MHSEvent) intent.getSerializableExtra("event");

        //Setup UI components based on event
        {
            //Set title
            setTitle(myEvent.toString());

            //Show Dates
            TextView dateTextView = (TextView) findViewById(R.id.dateView);
            dateTextView.setText(myEvent.getEventDates());

            //Show Description
            TextView descriptionTextView = (TextView) findViewById(R.id.descriptionView);
            descriptionTextView.setText(myEvent.getEventDescription());

            //See if email button should be shown, set accordingly
            Button emailBtn = (Button) findViewById(R.id.emailButton);
            emailBtn.setClickable(myEvent.showEmail());
            emailBtn.setVisibility(myEvent.showEmail() ? View.VISIBLE : View.GONE);
            emailBtn.setOnClickListener(this);

            //Setup share button and calender button
            Button shareBtn = (Button) findViewById(R.id.shareBtn);
            shareBtn.setOnClickListener(this);
            Button calBtn = (Button) findViewById(R.id.calBtn);
            calBtn.setOnClickListener(this);
        }
    }

    /**
     * onClick method handles button clicks
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        //Call method based on what button was clicked
        switch (v.getId()) {
            case R.id.emailButton:
                doEmail();
                break;
            case R.id.shareBtn:
                doShare();
                break;
            case R.id.calBtn:
                doCal();
                break;
            default:
                //Do nothing
        }
    }

    /**
     * This method handles the email
     */
    private void doEmail() {
        //Setup dialog
        AlertDialog.Builder emailDialog = new AlertDialog.Builder(this);
        //Set title and message
        emailDialog.setTitle("Email Organizer");
        emailDialog.setMessage("Are you sure you want to send an email to the event organizer " +
                "(email address: " + myEvent.getContactEmail() + ")?");
        //Set buttons
        emailDialog.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendEmail();
                    }
                });
        emailDialog.setNegativeButton(android.R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        //Show dialog
        emailDialog.setIcon(android.R.drawable.ic_dialog_email).create().show();
    }

    /**
     * This method will send the email
     */
    private void sendEmail() {
        if (!isGMailInstalled()) {
            //If gmail isn't installed, tell user we cannot email
            Toast.makeText(eventDummy.this, "No email clients installed\nYou may manually " +
                    "email this address: " + myEvent.getContactEmail(), Toast.LENGTH_LONG)
                    .show();
        } else {
            //If gmail is installed, we can send email

            //Create intent to send email
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

            //Set data
            emailIntent.setData(Uri.parse("mailto:" + myEvent.getContactEmail()));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Questions about an event: " + myEvent);
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Please explain your questions here.");
            try {
                //Create a chooser in case multiple email apps are installed
                startActivity(Intent.createChooser(emailIntent, "Please select email application"));
            } catch (android.content.ActivityNotFoundException ex) {
                //If no email apps are installed, (and we shouldn't actually
                // reach this, but just in case) tell the user
                Toast.makeText(eventDummy.this, "No email clients stalled.\nYou may manually email "
                        + "this address: " + myEvent.getContactEmail(), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    /**
     * This method handles sharing the event
     */
    private void doShare() {
        //Create a string with our sharing message
        String message = "Check out this event at MHS: " + myEvent.getEventName() + " (" +
                myEvent.getEventStartDate() + ")";

        //Try to share the event by creating a text intent, and starting a chooser
        try {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(share, "Please choose an application to share" +
                    " event with"));
        } catch (Exception e) {
            //If we don't have anything to share with, just tell the user
            Toast.makeText(this, "No sharable applications found...", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method handles the calander
     */
    private void doCal() {
        try {
            //Try to crate a calander intent from event, and launch it
            Intent calIntent =
                    new Intent(Intent.ACTION_INSERT);
            calIntent.setType("vnd.android.cursor.item/event");
            calIntent.putExtra(CalendarContract.Events.TITLE, myEvent.getEventName());
            calIntent.putExtra(CalendarContract.Events.DESCRIPTION, myEvent.getEventDescription());
            calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, myEvent.getIsAllDay());
            calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, myEvent.getCalStart()
                    .getTimeInMillis() + myEvent.startMS());
            calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, myEvent.getCalEnd()
                    .getTimeInMillis() + myEvent.endMS());
            startActivity(calIntent);
        } catch (Exception e) {
            //If this fails, (which it shouldn't), tell user
            e.printStackTrace();
            Toast.makeText(this, "Error creating calendar  event. Is calendar installed and " +
                    "working?", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This is a helper method for doEmail()
     * It tells it if gmail is installed
     *
     * @return if gmail is installed
     */
    private boolean isGMailInstalled() {
        try {
            //Try to get the gmail app. if works say true
            getPackageManager().getApplicationInfo("com.google.android.gm", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            //If it doesn't work, say false
            return false;
        }
    }
}