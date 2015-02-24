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
        super.onCreate(savedInstanceState);                     //Let android do the normal things
        Intent intent = getIntent();                            //Get our intent
        setContentView(R.layout.activity_event_dummy);          //Set our content to XML
        myEvent = (MHSEvent) intent.getSerializableExtra("event");    //Get our event
        setTitle(myEvent.toString());                                       //Set title to event title
        TextView dateTextView =                                 //Date text view
                (TextView) findViewById(R.id.dateView);         //Get date textview from resources
        dateTextView.setText(myEvent.getEventDates());                //Set dateview text from event
        TextView descriptionTextView =                          //Textview for description
                (TextView) findViewById(R.id.descriptionView);  //Get textview from resources
        descriptionTextView.setText(myEvent.getEventDescription());   //Set text to event description
        Button emailBtn =                                       //Email button
                (Button) findViewById(R.id.emailButton);        //Get from resources
        emailBtn.setClickable(myEvent.showEmail());                   //See if it should exist
        emailBtn.setVisibility(myEvent.showEmail() ?                  //Set viability...
                View.VISIBLE : View.GONE);                      //based on clickability
        emailBtn.setOnClickListener(this);                      //Set the listener to us
        Button shareBtn = (Button) findViewById(R.id.shareBtn); //Get share button from resources
        shareBtn.setOnClickListener(this);                      //Set listener to us
        Button calBtn = (Button) findViewById(R.id.calBtn);     //Get calender button
        calBtn.setOnClickListener(this);                        //Set the listener to us
    }

    /**
     * onClick method handles button clicks
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {                                    //Get id of view
            case R.id.emailButton:                              //If email button...
                doEmail();                                      //Do email things...
                break;                                          //then end!
            case R.id.shareBtn:                                 //If share button...
                doShare();                                      //Do share things...
                break;                                          //then end!
            case R.id.calBtn:                                   //If calender button...
                doCal();                                        //do cal things...
                break;                                          //then end!
            default:
                //Do nothing
        }
    }

    /**
     * This method handles the email
     */
    private void doEmail() {
        new AlertDialog.Builder(this)                           //Create an alert dialog...
                .setTitle("Email Organizer")                    //With email organizer title
                .setMessage("Are you sure you want to send " +  //And question...
                        "an email to the event organizer " +    //for the user
                        "(email address: " +                    //...
                        myEvent.getContactEmail() + ")?")             //...
                .setPositiveButton(android.R.string.yes,        //And Yes button
                        new DialogInterface.OnClickListener() { //With an onClick...
                            public void onClick(DialogInterface dialog, //to send email
                                                int which) {

                                if (!isGMailInstalled()) {              //Check if GMail is installed
                                    Toast.makeText(eventDummy.this,     //Alert the user if it isn't
                                            "No email clients" +
                                                    " installed" +
                                                    ".\nYou may " +
                                                    "manually " +
                                                    "email this " +
                                                    "address: " +
                                                    myEvent.getContactEmail(),
                                            Toast.LENGTH_LONG).show();
                                } else {                                //If it is installed...
                                    Intent emailIntent =
                                            new Intent(Intent
                                                    .ACTION_SENDTO);    //Create send to intent
                                    emailIntent.setData(
                                            Uri.parse("mailto:" + myEvent
                                                    .getContactEmail()));//Get contact email
                                    emailIntent.putExtra(
                                            Intent.EXTRA_SUBJECT,
                                            "Questions about an " +
                                                    "event: " + myEvent);     //Set default data
                                    emailIntent.putExtra(Intent
                                            .EXTRA_TEXT, "Please " +
                                            "explain your questions" +
                                            " here.");                  //...
                                    try {
                                        startActivity(Intent
                                                .createChooser(         //Create email app chooser
                                                        emailIntent,
                                                        "Please " +
                                                                "select " +
                                                                "email" +
                                                                " application"));
                                    } catch (android.content
                                            .ActivityNotFoundException ex) {
                                        Toast.makeText(eventDummy.this,
                                                "No email clients " +
                                                        "installed.\nYou" +
                                                        " may manually email " +
                                                        "this address: "
                                                        + myEvent.getContactEmail(),  //Notify user if cannot email
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface
                                                        dialog,
                                                int which) {
                                // do nothing
                            }
                        })
                .setIcon(android.R.drawable.ic_dialog_email)    //Set dialog icon
                .show();                                        //and show dialog
    }

    private void doShare() {
        String message = "Check out this event at MHS: "
                + myEvent.getEventName() + " (" +
                myEvent.getEventStartDate() + ")";                    //Share event message
        try {
            Intent share = new Intent(Intent.ACTION_SEND);      //Try to share event
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(share,
                    "Please choose an application to " +
                            "share event with"));               //Ask how to share
        } catch (Exception e) {
            Toast.makeText(this, "No sharable " +
                            "applications found...",
                    Toast.LENGTH_SHORT).show();                 //If cannot share, tell user
        }
    }

    /**
     * This method handles the calander
     */
    private void doCal(){
        try {
            Intent calIntent =
                    new Intent(Intent.ACTION_INSERT);           //Prepare calander event
            calIntent.setType("vnd.android.cursor.item/event"); //Set type
            calIntent.putExtra(CalendarContract.Events.TITLE,   //Set title from event
                    myEvent.getEventName())
                    .putExtra(CalendarContract
                                    .Events.DESCRIPTION,
                            myEvent.getEventDescription())            //Set description from event
                    .putExtra(CalendarContract
                            .EXTRA_EVENT_ALL_DAY, myEvent.getIsAllDay())      //Set if all day from event
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,  //Set start time
                            myEvent.getCalStart().getTimeInMillis()
                                    + myEvent.startMS())                      //From event date and time
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            myEvent.getCalEnd().getTimeInMillis()
                                    + myEvent.endMS());                       //End tie from event
            startActivity(calIntent);                           //Start activity
        } catch (Exception e) {
            e.printStackTrace();                                //On error print stack
            Toast.makeText(this, "Error creating " +
                            "calendar  event. Is calendar" +
                            " installed and working?",
                    Toast.LENGTH_SHORT).show();                 //Tell user if failure
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
            getPackageManager()
                    .getApplicationInfo(
                            "com.google.android.gm", 0);        //See if gmail is installed
            return true;                                        //Return yes
        } catch (PackageManager.NameNotFoundException e) {      //If not...
            return false;                                       //Return no!s
        }
    }
}
