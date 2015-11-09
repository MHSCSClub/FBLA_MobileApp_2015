//Filename: MainActivity.java
/**
 * This is the starting activity for this android app.
 * Upon launching the app, this file is used.
 *
 * Created by Andrew on 1/19/2015.
 * Version 2.0 released 5/4/2015.
 *
 * @author Andrew Katz
 * @version 2.0
 */
package com.aakportfolio.www.fbla2015;

//Import Section

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

//End of Imports

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //This arraylist contains events from file
    private ArrayList<MHSEvent> Events;

    //ListView on main page
    private ListView LV;

    //This string allows us to update data only once per day
    private String lastUpdate;

    /**
     * This is the starting method (like main)
     * It runs when the application is loaded into memory
     *
     * @param savedInstanceState Pass to super
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Android setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("MHSEvents", MODE_PRIVATE);
        lastUpdate = prefs.getString(MHSConstants.lastUpdatePrefName, "");

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

        //Display our icon in the title bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.drawable.ic_launcher);
        }

        //If we are in portrait, setup the ViewFlipper (banner)
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ViewFlipper banner = (ViewFlipper) findViewById(R.id.viewFlipper);
            banner.setOnClickListener(this);
            banner.setInAnimation(this, R.animator.fade_in);
            banner.setOutAnimation(this, R.animator.fade_out);
            banner.setAnimateFirstView(false);
            banner.startFlipping();
        }
        //Fill listview for initial time
        fillListView();
    }

    /**
     * This method is called after onCreate, when an app is in memory and reopened,
     * and the orientation is changed.
     * We will fill/refill the listview here so if user
     * resumes app the next day, old events will be purged.
     */
    @Override
    protected void onResume() {
        //Always call the super class
        super.onResume();

        //See if the listview was refilled today, and update performed
        if (!new SimpleDateFormat("MM/dd/yyyy").format(new Date()).equals(lastUpdate)) {
            //If we didn't update today and refill today
            //Set update to today
            lastUpdate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
            //Let's check if there is internet
            if (isNetworkAvailable()) { //Check if there is internet
                //If we have internet, perform an update.
                update();
            }
            //Refill to remove old events and add new ones
            //Fill the listview
            fillListView();
        }
    }

    /**
     * This method is called whenever the view is hidden (another view opened, etc.)
     * We commit the preference just in case, to prevent needless re-updates
     */
    @Override
    public void onPause() {
        //Call super first
        super.onPause();

        //Save update date
        SharedPreferences.Editor editor = getSharedPreferences("MHSEvents", MODE_PRIVATE).edit();
        editor.putString(MHSConstants.lastUpdatePrefName, lastUpdate);
        editor.commit();
    }

    /**
     * This method is called before the app is closed. We make sure to save the update date here
     */
    @Override
    protected void onDestroy() {
        //Save update date
        SharedPreferences.Editor editor = getSharedPreferences("MHSEvents", MODE_PRIVATE).edit();
        editor.putString(MHSConstants.lastUpdatePrefName, lastUpdate);
        editor.commit();

        //Call super last, to ensure that we commit
        super.onDestroy();
    }

    /**
     * This method first removes events that shouldn't be shown,
     * then sorts the events that remain
     */
    private void orderAndRemoveEvents() {
        //Save variables for the current date
        Date date = new Date();
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date)),
                month = Integer.parseInt(new SimpleDateFormat("MM").format(date)),
                day = Integer.parseInt(new SimpleDateFormat("dd").format(date));

        //Go through the arraylist of all events. If it shouldn't be shown, remove it
        for (int i = 0; i < Events.size(); i++) {
            if (!Events.get(i).showEvent(month, day, year)) {
                Events.remove(i);
                //Fix position in the arraylist to prevent off-by-one bug
                i--;
            }
        }

        //Use Collections.sort to sort our ArrayList, using our compareTo method
        Collections.sort(Events);
    }

    /**
     * This method reads lines from the event list, and returns the string
     * containing all the lines
     *
     * @return Lines from calendar file
     */
    private String readLines() {
        //Try a maximum of three times to read lines from the file.
        //If we fail, reset the file to default, and try again
        for (int i = 0; i < 3; i++) {
            try {
                //Create a string to hold the file lines
                String fileLines = "";
                //Open file from internal storage
                Scanner scan = new Scanner(openFileInput(MHSConstants.calName));

                //First line is just headers, so just throw it away.
                scan.nextLine();

                //Read lines until none are left, and append to string
                while (scan.hasNextLine()) {
                    String tmp = scan.nextLine() + "\n";
                    fileLines += tmp;
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
     * and puts them into an ArrayList of events. Just an overloaded header for simplicity
     */
    private void readFileIntoArrayList() {
        readFileIntoArrayList(0);
    }

    /**
     * This function actually reads the lines of the file into the ArrayList
     * @param tries try number, to prevent infinite recursion
     */
    private void readFileIntoArrayList(int tries) {
        //Start by initializing the arraylist
        Events = new ArrayList<>(0);

        if (!fileExists(MHSConstants.calName)) {
            //If the CSV doesn't exist, make a new one
            makeCSV();
        }


        for (String str : readLines().split("\n")) {
            //Split the line at commas, except those in quotes (using this regular expression)
            String[] line = str.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            //If we have the right number of fields
            if (line.length == 8) {
                //Split the line and make a new event from it, removing quotes
                for (int i = 0; i < line.length; i++) {
                    //Remove any quotes now that lines are split, for better readability
                    if(line[i].charAt(0) == '"' && line[i].charAt(line[i].length() - 1) == '"'){
                        line[i] = line[i].substring(1, line[i].length() - 1);
                    }
                }
                //Add the new event
                Events.add(new MHSEvent(line));
            }
        }

        orderAndRemoveEvents();
        if (Events.size() == 0) {
            //If there are no events after sorting, or if the file was empty
            if (tries < 2) {
                //If we haven't tried twice yet...
                //Tell the user we are resetting...
                Toast.makeText(this, R.string.eventError,
                        Toast.LENGTH_SHORT).show();

                //Then reset
                makeCSV();

                //And try again
                readFileIntoArrayList(tries + 1);
            } else {
                //If we went through twice and still have no events,
                //Something is wrong.

                //Tell the user we have no events
                Toast.makeText(this, R.string.noEvents,
                        Toast.LENGTH_LONG).show();

                //Reset the events list, to be safe
                Events = new ArrayList<>();

                //Then add a fake event to remind the user about the problem.
                Events.add(new MHSEvent("Cannot load events",
                        "Something bad happened and we cannot load events. Please check your " +
                                "date and time settings and try an update.",
                        new SimpleDateFormat("MM/dd/yyyy").format(new Date()), "none",
                        new SimpleDateFormat("MM/dd/yyyy").format(new Date()), "none",
                        "none", "none"));
            }
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
            outputStream = openFileOutput(MHSConstants.calName, Context.MODE_PRIVATE);

            //Write bytes from string
            outputStream.write(fileLines.getBytes());

            //Flush and close output
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            if (MHSConstants.debug) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method updates calender database stored on internal storage
     */
    private void update() {
        //Create and show a dialog to tell teh user we are updating
        //Download the file in an async task.
        AsyncTask<Void, Void, Void> task = new Downloader(this);

        //Run the previously defined task
        task.execute((Void[]) null);
        lastUpdate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
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
     * @return whether action was performed
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
                if (!openURL("fb://facewebmodal/f?href="
                        + "http://www.facebook.com/MamaroneckPublicSchools")) {
                    //If facebook isn't installed, try again with browser
                    openURL("http://www.facebook.com/MamaroneckPublicSchools");
                }
                return true;
            case R.id.action_twitter:
                //For twitter button, try to open twitter app to Mamkschools
                if (!openURL("twitter://user?screen_name=MamaroneckED")) {
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
     * @return If the URL was opened
     */
    private boolean openURL(String url) {
        try {
            //Try to open page with browser or other app
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
            return true;
        } catch (Exception e) {
            //If error, print stack and return false
            if (MHSConstants.debug) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * @return If there is a network connection available
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * @param fname File name to check
     * @return If the file exists or not
     */
    public boolean fileExists(String fname) {
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }


    /**
     * onClick, called when something clickable is tapped
     * @param view View that was clicked
     */
    public void onClick(View view) {
        //See what view was clicked
        switch (view.getId()) {
            case R.id.viewFlipper:
                //If we are in portrait, pause/unpause viewflipper
                if (getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT) {
                    ViewFlipper banner = (ViewFlipper) view;
                    //Are we flipping now?
                    if (banner.isFlipping()) {
                        //If yes, stop
                        banner.stopFlipping();
                    } else {
                        //If no, show next image, then start flipping
                        banner.showNext();
                        banner.startFlipping();
                    }
                }
                break;
            default:
                break;
        }

    }
}