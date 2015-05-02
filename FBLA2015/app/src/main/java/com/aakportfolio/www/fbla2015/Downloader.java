package com.aakportfolio.www.fbla2015;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Andrew Katz on 5/1/2015.
 */
public class Downloader extends AsyncTask<Void, Void, Void> {
    //This variable holds the main activity, and the context for the progress dialog, etc.
    private MainActivity m;

    public Downloader(MainActivity ma) {
        m = ma;
        progress = ProgressDialog.show(m, "Downloading updated events...",
                "Please wait, downloading...", true, true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Downloader.this.cancel(true);
                    }
                });
    }

    //This boolean will keep track of whether we fail or not
    private boolean fail;
    private ProgressDialog progress;

    @Override
    protected void onPreExecute() {

        fail = true;

    }

    /**
     * Main background method. This is where the download happens
     *
     * @param params Required
     * @return null
     */
    @Override
    protected Void doInBackground(Void... params) {
        //Try to download
        try {
            //Connect to website
            URL website = new URL(MHSConstants.downloadURL);
            URLConnection connection = website.openConnection();
            connection.connect();
            InputStream input = new BufferedInputStream(website.openStream());

            //Create output stream to write file to internal storage
            FileOutputStream outputStream = m.openFileOutput(MHSConstants.calName + 1,
                    Context.MODE_PRIVATE);

            //This array will hold the downloaded data
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

            //Copy now that we have fully downloaded
            copy(m.openFileInput(MHSConstants.calName + 1),
                    m.openFileOutput(MHSConstants.calName, Context.MODE_PRIVATE));
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
     *
     * @param result we do not care
     */
    @Override
    protected void onPostExecute(Void result) {
        //See if we failed, and tell the user what happened
        if (fail) {
            //Tell them we failed
            Toast.makeText(m, "Could not download event list.",
                    Toast.LENGTH_SHORT).show();
        } else {
            //Tell the user we succeeded

            Toast.makeText(m,
                    "Update completed.", Toast.LENGTH_SHORT).show();
            //Refill the listview
            m.fillListView();
        }
        //Dismiss progress bar
        progress.dismiss();
    }

    public void copy(FileInputStream in, FileOutputStream out) throws IOException {

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
