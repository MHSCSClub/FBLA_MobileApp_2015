package com.aakportfolio.www.fbla2015;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains variables that are commonly used throughout the app for easy access.
 * Created by Andrew Katz on 5/1/2015.
 */
public final class MHSConstants {
    //Preference name
    public static final String lastUpdatePrefName = "lastUpdatePref";

    //Calandar Filename
    public static final String calName = "cal.csv";

    //File download URL. Can be changed to the school if needed.
    //Currently uses a link from github due to lack of access to school webserver
    public static final String downloadURL = "http://aakatz3.github.io/2015MamkFBLAApp/cal.csv";

    //Debug mode (print stack traces and logs)
    public static final boolean debug = true;

    //Image map used to get images for event types
    private static final Map<String, Integer> imageMap = makeMap();

    /**
     * Makes the hashmap for images
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
     *
     * @param type Type of event to look up from hashmap
     * @return Either the image for the type, or if type doesn't exist, the default
     */
    public static int getImg(String type) {
        Integer ret = imageMap.get(type);
        return ret != null ? ret : imageMap.get("none");
    }
}
