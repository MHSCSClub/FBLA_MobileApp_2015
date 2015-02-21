//Filename: MainActivity.java
/**
 * This is the starting activity for this android app.
 * Upon launching the app, this file is used.
 * @author Andrew Katz
 * @version 1.0
 */
package com.aakportfolio.www.fbla2015;

//Import Section

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.io.FileInputStream;
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

    /**
     * This is the starting method (like main)
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.title_activity_main);     //Set title

        LV = (ListView) findViewById(R.id.listView); //Get our listview so we can edit it
        fillListView(); //Read in our event file, and fill list view

        //This code fragment allows us to detect selections
        LV.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Intent intent = new Intent(MainActivity.this, eventDummy.class); //Get next activity
                intent.putExtra("event", Events.get(position)); //Pass selected event
                startActivity(intent);  //Start the activity
            }
        });
    }

    /**
     * This method first removes events that shouldn't be shown, then sorts the events
     */
    private void orderAndRemoveEvents() {
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())),
                month = Integer.parseInt(new SimpleDateFormat("MM").format(new Date())),
                day = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        for (int i = 0; i < Events.size(); i++) {
            if (!Events.get(i).showEvent(month, day, year)) { //If event shouldn't be shown...
                Events.remove(i); //remove it
                i--; //Fix the position
            }
        }
        Collections.sort(Events); //Sort the ArrayList with compareTo
    }

    /**
     * This method reads lines from cal file, and returns them
     *
     * @return Lines from calander file
     */
    private String readLines() {
        boolean tryAgain = true;
        String s = "";
        while (tryAgain) {
            try {
                FileInputStream fis = openFileInput(calName);
                Scanner scan = new Scanner(fis);
                while (scan.hasNextLine()) {
                    s += scan.nextLine() + "\n";
                }
                tryAgain = false;
            } catch (Exception e) {
                makeCSV();
            }
        }
        return s;
    }

    /**
     *
     */
    private void readFileIntoArrayList() {
        Events = new ArrayList<>(0);
        int count = 0;
        while (Events.size() == 0 && count < 3) {
            String[] fileLines;
            fileLines = readLines().split("\n");
            for (String str : fileLines) {
                if (str.split(",,").length == 7) {
                    Events.add(new MHSEvent(str.split(",,")));
                }
            }
            orderAndRemoveEvents();
            if (Events.size() == 0) {
                Toast.makeText(this, R.string.eventError, Toast.LENGTH_SHORT).show();
                makeCSV();
            }
            count++;
        }
        if (count >= 3 && Events.size() == 0) {
            Toast.makeText(this, R.string.noEvents, Toast.LENGTH_LONG).show();
            Events = new ArrayList<>();
            Events.add(new MHSEvent("Cannot load events",
                    "Something bad happened and we cannot load events. Please check your date and time settings and try an update.",
                    new SimpleDateFormat("MM/dd/yyyy").format(new Date()), "none",
                    new SimpleDateFormat("MM/dd/yyyy").format(new Date()), "none", "none"));
        }

    }

    public void fillListView() {
        readFileIntoArrayList();
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (MHSEvent item : Events) {
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("title", item.toString());
            datum.put("date", item.getEventStartDate().equals(item.getEventEndDate())
                    ? item.getEventStartDate() : item.getEventStartDate() + " - " + item.getEventEndDate());
            data.add(datum);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "date"},
                new int[]{android.R.id.text1,
                        android.R.id.text2});
        LV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //TODO Finish the CSV
    private void makeCSV() {
        Scanner s = new Scanner(new InputStreamReader(getResources().openRawResource(R.raw.cal)));
        String cal = "";
        while (s.hasNextLine()) {
            cal += s.nextLine() + "\n";
        }
        String fileName = calName;
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(cal.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void update() {
        final ProgressDialog progress = ProgressDialog.show(this, "Downloading updated events...",
                "Please wait, downloading...", true);
        final MainActivity a = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // do the thing that takes a long time
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    private boolean fail = true;

                    @Override
                    protected Void doInBackground(Void... params) {
                        int count;
                        try {

                            String fileName = "cal.csv";
                            URL website = new URL(downloadURL);
                            URLConnection connection = website.openConnection();
                            connection.connect();
                            InputStream input = new BufferedInputStream(website.openStream());
                            FileOutputStream outputStream;
                            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                            byte data[] = new byte[1024];
                            while ((count = input.read(data)) != -1) {
                                // writing data to file
                                outputStream.write(data, 0, count);
                            }
                            outputStream.flush();
                            outputStream.close();
                            input.close();
                            fail = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                            fail = true;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (fail) {
                            Toast.makeText(a, "Could not download event list.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(a, "Update completed.", Toast.LENGTH_SHORT).show();
                        }
                        fillListView();
                        progress.dismiss();
                    }
                };
                task.execute((Void[]) null);
            }
        }).start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_update:
                update();
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
