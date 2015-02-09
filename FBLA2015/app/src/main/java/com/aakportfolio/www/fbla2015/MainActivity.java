package com.aakportfolio.www.fbla2015;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class MainActivity extends ActionBarActivity {
    ListView LV;
    ArrayList<MHSEvent> Events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.title_activity_main);

        LV = (ListView) findViewById(R.id.listView);
        fillListView();

        LV.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                sendMessage(arg1, position);
            }
        });
        ((ImageView) findViewById(R.id.imageView)).setImageResource(R.drawable.p1);
        ((ImageView) findViewById(R.id.imageView2)).setImageResource(R.drawable.p2);
        ((ImageView) findViewById(R.id.imageView3)).setImageResource(R.drawable.p3);
    }

    private void orderAndRemoveEvents() {
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date())),
                month = Integer.parseInt(new SimpleDateFormat("MM").format(new Date())),
                day = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        for (int i = 0; i < Events.size(); i++) {
            if (!Events.get(i).showEvent(month, day, year)) {
                Events.remove(i);
                i--;
            }
        }
        //TODO then sort them by date...
    }

    private void fillListView() {
        String s = "";
        boolean tryAgain = true;
        while (tryAgain) {
            try {
                FileInputStream fis = openFileInput("cal.csv");
                Scanner scan = new Scanner(fis);
                while (scan.hasNextLine()) {
                    s += scan.nextLine() + "\n";
                }
                tryAgain = false;
            } catch (Exception e) {
                makeCSV();
            }
        }
        String[] fileLines = s.split("\n");
        Events = new ArrayList<MHSEvent>();
        while (Events.size() == 0) {
            for (String str : fileLines) {
                if (str.split(",,").length == 6) Events.add(new MHSEvent(str.split(",,")));
            }
            if (Events.size() == 0) {
                makeCSV();
                tryAgain = true;
                while (tryAgain) {
                    try {
                        FileInputStream fis = openFileInput("cal.csv");
                        Scanner scan = new Scanner(fis);
                        while (scan.hasNextLine()) {
                            s += scan.nextLine() + "\n";
                        }
                        tryAgain = false;
                    } catch (Exception e) {
                        makeCSV();
                    }
                }
                fileLines = s.split("\n");
            }
        }
        orderAndRemoveEvents();
        String[] EventArray = new String[Events.size()];
        for (int i = 0; i < Events.size(); i++) {
            EventArray[i] = Events.get(i) + "";
        }
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (MHSEvent item : Events) {
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("title", item.toString());
            datum.put("date", item.getEventStartDate());
            data.add(datum);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "date"},
                new int[]{android.R.id.text1,
                        android.R.id.text2});
        LV.setAdapter(adapter);
    }

    private void makeCSV() {
        Scanner s = new Scanner(new InputStreamReader(getResources().openRawResource(R.raw.cal)));
        String cal = "";
        while (s.hasNextLine()) {
            cal += s.nextLine() + "\n";
        }
        String fileName = "cal.csv";
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
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            try {
                                String fileName = "cal.csv";

                                URL website = new URL("http://ichart.finance.yahoo.com/table.csv?s=AAPL");
                                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                                FileOutputStream outputStream;
                                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                                outputStream.getChannel().transferFrom(rbc, 0, 10 * 1024 * 1024);
                                outputStream.close();
                                rbc.close();
                                Toast.makeText(a, "Update completed.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(a, "Could not download event list.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Toast.makeText(a, "Could not download event list.", Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void sendMessage(View view, int arrPOS) {
        Intent intent = new Intent(this, eventDummy.class);
        //We will try to launch the activity instead with what was selected from
        intent.putExtra("event", Events.get(arrPOS));
        startActivity(intent);
    }
}
