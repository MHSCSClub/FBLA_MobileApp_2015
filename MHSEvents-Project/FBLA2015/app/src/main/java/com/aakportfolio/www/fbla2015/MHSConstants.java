package com.aakportfolio.www.fbla2015;


/**
 * This class contains variables that are commonly used throughout the app for easy access.
 *
 * Created by Andrew Katz on 5/1/2015.
 * Version 2.0 released 5/4/2015.
 *
 * @author Andrew Katz
 * @version 2.0
 */
public final class MHSConstants {
    //Preference name
    public static final String lastUpdatePrefName = "lastUpdatePref";

    //Calandar Filename
    public static final String calName = "cal.csv";

    //File download URL. Can be changed to the school if needed.
    //Currently uses a link from GitHub due to lack of access to school web server,
    //and administrative restrictions on the school web server that prevent easy access
    public static final String downloadURL = "http://aakatz3.github.io/2015MamkFBLAApp/cal.csv";

    //Debug mode (print stack traces and logs)
    public static final boolean debug = false;
}
