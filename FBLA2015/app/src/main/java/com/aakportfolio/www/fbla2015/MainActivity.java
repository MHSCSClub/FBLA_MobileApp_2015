//Filename: MainActivity.java
/**
 * This is the starting activity for this android app.
 * Upon launching the app, this file is used.
 * @author Andrew Katz
 * @version 1.0
 */
package com.aakportfolio.www.fbla2015;

//Import Section

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

//End of Imports

public class MainActivity extends ActionBarActivity {

    //File download URL. Can be changed to the school if needed
    private static final String downloadURL = "http://aakatz3.github.io/2015MamkFBLAApp/cal.csv";

    //Calandar Filename
    private static final String calName = "cal.csv";

    //This arraylist contians events from file
    private ArrayList<MHSEvent> Events;

    //ListView on main page
    private ListView LV;

    //This string allows us to refil listview if app is resumed on different day.
    private String lastRefillDate;

    /**
     * This is the starting method (like main)
     *
     * @param savedInstanceState Pass to super
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Android setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the listview in the app, so we can later manipulate it
        LV = (ListView) findViewById(R.id.listView);

        //Set our onclick listener for the listview (item selected)
        LV.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //Create an intent for the activity that displays events
                Intent intent = new Intent(MainActivity.this, eventDummy.class);

                //Tell the activity what event was selected
                intent.putExtra("event", Events.get(position));

                //Start the activity
                startActivity(intent);
            }
        });

        //Set title of app (slightly different than home screen)
        setTitle(R.string.title_activity_main);

        //Display our icon in the title bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);

        //Set lastUpdateDate to an empty string, so we can update the listview
        lastRefillDate = "";
    }

    /**
     * This method is called after onCreate
     * We will fill/refill the listview here so if user
     * resumes app the next day, old events will be purged.
     */
    @Override
    protected void onResume() {
        //Standard android setup
        super.onResume();

        //See if the listview was refilled today
        if (!new SimpleDateFormat("MM/dd/yyyy").format(new Date()).equals(lastRefillDate)) {

            //If we haven't updated listview today, do so
            //Set refill date to to today
            lastRefillDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());

            //Fill the listview
            fillListView();
        }
    }

    /**
     * This method first removes events that shouldn't be shown,
     * then sorts the events that remain
     */
    private void orderAndRemoveEvents() {
        //Save variables for the current date
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())),
                month = Integer.parseInt(new SimpleDateFormat("MM").format(new Date())),
                day = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        //Go through the arraylist of all events. If it shouldn't be shown, remove it
        for (int i = 0; i < Events.size(); i++) {
            if (!Events.get(i).showEvent(month, day, year)) {
                Events.remove(i);
                //Fix position in the arraylist to prevent off-by-one bug
                i--;
            }
        }

        //Use Timsort to sort our arraylist, using our compareTo method
        Collections.sort(Events);
    }

    /**
     * This method reads lines from the event list, and returns the string
     * containing all the lines
     *
     * @return Lines from calender file
     */
    private String readLines() {
        //Try a maximum of three times to read lines from the file.
        //If we fail, reset the file to default, and try again
        for (int i = 0; i < 3; i++) {
            try {
                //Create a string to hold the file lines
                String fileLines = "";
                //Open file from internal storage
                Scanner scan = new Scanner(openFileInput(calName));

                //Read lines until none are left, and append to string
                while (scan.hasNextLine()) {
                    fileLines += scan.nextLine() + "\n";
                }

                //If we made it to here, we succeeded.
                //Therefore, we can close the file and return the lines
                scan.close();
                return fileLines;
            } catch (Exception e) {
                //If we failed, use the default data
                makeCSV();
            }
        }
        //If we made it here, there is something wrong.
        //We will just return an empty string
        return "";
    }

    /**
     * This function gets the lines from the file
     * and puts them into an arraylist of events
     */
    private void readFileIntoArrayList() {
        //Start by initializing the arraylist
        Events = new ArrayList<>(0);

        //Create a variable to keep track of how many times we go through and try
        //to read events
        int count;
        //Try to read events while our list is empty
        //and we haven't gone through 3 times
        for (count = 0; Events.size() == 0 && count < 3; count++) {
            //Go through each line in the file
            for (String str : readLines().split("\n")) {
                //If we have the right number of fields
                if (str.split(",,").length == 7) {
                    //Split the line and make a new event from it
                    Events.add(new MHSEvent(str.split(",,")));
                }
            }


            orderAndRemoveEvents();

            //If there are no events after sorting, or if the file was empty
            if (Events.size() == 0) {
                //Tell the user we are resetting...
                Toast.makeText(this, R.string.eventError, Toast.LENGTH_SHORT).show();

                //Then reset
                makeCSV();
            }
        }

        //If we went through three times and still have no events,
        //Something is wrong.
        if (count == 3 && Events.size() == 0) {
            //Tell the user we have no events
            Toast.makeText(this, R.string.noEvents, Toast.LENGTH_LONG).show();

            //Reset the events list, to be safe
            Events = new ArrayList<>();

            //Then add a fake event to remind the user about the problem.
            Events.add(new MHSEvent("Cannot load events",
                    "Something bad happened and we cannot load events. Please check your " +
                            "date and time settings and try an update.",
                    new SimpleDateFormat("MM/dd/yyyy").format(new Date()), "none",
                    new SimpleDateFormat("MM/dd/yyyy").format(new Date()), "none", "none"));
        }
    }

    /**
     * This method will refill the listview
     * from the file on the internal storage
     */
    public void fillListView() {
        //Initialize the array
        readFileIntoArrayList();

        //Create an arraylist for our info that will go in the listview
        List<Map<String, String>> data = new ArrayList<>();

        //Go through the events, pull out the relevent info, and save it in the list
        for (MHSEvent item : Events) {
            Map<String, String> datum = new HashMap<>(2);
            datum.put("title", item.toString());

            //If there is one date, show that. If there are separate start
            //and end dates, show both
            datum.put("date", item.getEventStartDate().equals(item.getEventEndDate())
                    ? item.getEventStartDate()
                    : item.getEventStartDate() + " - " + item.getEventEndDate());

            //Add our map to the list
            data.add(datum);
        }

        //Create an adapter for the listview, using the list we created
        SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
                new String[]{"title", "date"}, new int[]{android.R.id.text1, android.R.id.text2});

        //Set the listview's adapter to this one, and update the listview
        LV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    /**
     * This method will create a new csv.
     * Called when none is found, or a corrupted one is detected
     */
    private void makeCSV() {
        //Create a scanner to read the integrated file
        Scanner s = new Scanner(new InputStreamReader(getResources().openRawResource(R.raw.cal)));

        //Create a string to store filelines
        String fileLines = "";

        //Read each line in the file and add it to the string
        while (s.hasNextLine()) {
            fileLines += s.nextLine() + "\n";
        }

        //Close input file
        s.close();

        //Try to write our file to storage
        try {
            //Create output stream to internal storage
            FileOutputStream outputStream;
            outputStream = openFileOutput(calName, Context.MODE_PRIVATE);

            //Write bytes from string
            outputStream.write(fileLines.getBytes());

            //Flush and close output
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method updates calender database stored on internal storage
     */
    private void update() {
        //Create and show a dialog to tell teh user we are updating
        final ProgressDialog progress = ProgressDialog.show(this, "Downloading updated events...",
                "Please wait, downloading...", true);
        //Download the file in an async task.
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            //This boolean will keep track of whether we fail or not
            private boolean fail;

            /**
             * Main background method. This is where the download happens
             * @param params   Required
             * @return null
             */
            @Override
            protected Void doInBackground(Void... params) {
                //Try to download
                try {
                    //Connect to website
                    URL website = new URL(downloadURL);
                    URLConnection connection = website.openConnection();
                    connection.connect();
                    InputStream input = new BufferedInputStream(website.openStream());

                    //Create output stream to write file to internal storage
                    FileOutputStream outputStream = openFileOutput(calName, Context.MODE_PRIVATE);

                    //This array will hold the dowlnoaded data
                    byte data[] = new byte[1024];

                    //Count how much data we downloaded
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // write "count" amount of data to the file
                        outputStream.write(data, 0, count);
                    }

                    //Flush and close the output and input
                    outputStream.flush();
                    outputStream.close();
                    input.close();

                    //Note that we did not fail
                    fail = false;
                } catch (Exception e) {
                    //If we fail, output debug info
                    e.printStackTrace();

                    //Also note that we failed
                    fail = true;
                }
                return null;
            }

            /**
             * This method is called when background task is done
             * @param result  we do not care
             */
            @Override
            protected void onPostExecute(Void result) {
                //See if we failed, and tell the user what happened
                if (fail) {
                    //Tell them we failed
                    Toast.makeText(MainActivity.this, "Could not download event list.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //Tell the user we succeeded
                    Toast.makeText(MainActivity.this,
                            "Update completed.", Toast.LENGTH_SHORT).show();
                    //Refill the listview
                    fillListView();
                }
                //Dismiss progress bar
                progress.dismiss();
            }
        };

        //Run the previously defined task
        task.execute((Void[]) null);
    }

    /**
     * Standard android code for options menu
     *
     * @param menu menu of app
     * @return super's return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Show about box
     */
    private void showAbout() {
        //Create an alert dialog builder for a new alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Set the title and message
        builder.setIcon(R.drawable.ic_launcher)
                .setMessage(R.string.about_message)
                .setTitle(R.string.about_title);

        //Add an OK button
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Close dialog when user presses OK
                        dialog.dismiss();
                    }
                });
        //Create the actual dialog from the builder, then show it.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This method deals with action bar selections
     *
     * @param item selected item
     * @return whether action was preformed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //Get ID of selected item
        int id = item.getItemId();
        switch (id) {
            case R.id.action_update:
                //For update button, try to update
                update();
                return true;
            case R.id.action_about:
                //For about button, show about box
                showAbout();
                return true;
            case R.id.action_facebook:
                //For facebook button, open Mamkschools facebook
                openURL("http://www.facebook.com/MamaroneckPublicSchools");
                return true;
            case R.id.action_twitter:
                //For twitter button, try to open twitter app to Mamkschools
                if (!openURL("twitter://twitter.com/MamaroneckED")) {
                    //If twitter isn't installed, try again with browser
                    openURL("http://www.twitter.com/MamaroneckED");
                }
                return true;
            case R.id.action_website:
                //Open browser to Mamkschools page
                openURL("http://mhs.mamkschools.org/");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Open a URL
     *
     * @param url URL to open
     */
    public boolean openURL(String url) {
        try {
            //Try to open page with browser or other app
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
            return true;
        } catch (Exception e) {
            //If error, print stack and return false
            e.printStackTrace();
            return false;
        }
    }
}
