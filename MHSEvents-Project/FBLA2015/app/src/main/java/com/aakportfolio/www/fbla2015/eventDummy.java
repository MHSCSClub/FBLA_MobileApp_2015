//Filename: eventDummy.java
/**
 * This is the activity for each event in this android app.
 * Upon selecting an event, this loads, and is changed based on the event.
 *
 * Created by Andrew on 1/19/2015.
 * Version 2.0 released 5/4/2015.
 *
 * @author Andrew Katz
 * @version 2.0
 */
package com.aakportfolio.www.fbla2015;

//Import section

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
//End of import section

public class eventDummy extends AppCompatActivity implements View.OnClickListener {
    //Image map used to get images for event types
    private static final HashMap<String, Integer> imageMap = makeMap();

    //Event passed from MainActivity
    private MHSEvent myEvent;

    /**
     * Makes the hashmap for images
     * Used for setting constant
     *
     * @return Completed map
     */
    private static HashMap<String, Integer> makeMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("none", R.drawable.ic_launcher);
        map.put("pace", R.mipmap.ic_pace);
        map.put("music", R.mipmap.ic_music);
        map.put("test", R.mipmap.ic_test);
        map.put("sports", R.mipmap.ic_sports);
        map.put("school", R.mipmap.ic_school);
        map.put("art", R.mipmap.ic_art);
        map.put("research", R.mipmap.ic_research);
        map.put("graduation", R.mipmap.ic_graduation);
        map.put("dance", R.mipmap.ic_dance);
        map.put("award", R.mipmap.ic_award);
        map.put("fbla", R.mipmap.ic_fbla);
        return map;
    }

    /**
     * This method runs on launch of the activity
     *
     * @param savedInstanceState To match superclass
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Standard android setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_dummy);

        //Enable back/up button in action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        }

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

            //Setup event image
            setImage((ImageView) findViewById(R.id.eventImg), myEvent.getEventType());
        }
    }

    /**
     * Sets image based on type of event, using thing in constants
     *
     * @param iv   Imageview to change
     * @param type Type of event
     */
    private void setImage(ImageView iv, String type) {
        iv.setImageResource(imageMap.get(type) != null ? imageMap.get(type) : imageMap.get("none"));
    }

    /**
     * onClick method handles button clicks
     *
     * @param v View that sent onclick
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
        //Create intent to send email
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        //Set the data of the email from the event
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{myEvent.getContactEmail()});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Questions about an event: " + myEvent);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                "Please explain your questions here.");


        try {
            //Start the intent to send email
            startActivity(emailIntent);
        } catch (ActivityNotFoundException ex) {
            //If no email apps are installed, and the chooser crashes, tell user ourselves
            Toast.makeText(eventDummy.this, "No email clients installed.\nYou may manually email "
                    + "this address: " + myEvent.getContactEmail(), Toast.LENGTH_LONG).show();
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
     * This method handles the calendar
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
            if (MHSConstants.debug) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Error creating calendar  event. Is calendar installed and " +
                    "working?", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle the back/home/up button on the action bar
     * @param item Selected actiion item
     * @return If we have an action for it
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home/Back button
            case android.R.id.home:
                //Return to activity in last state by closing this activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
