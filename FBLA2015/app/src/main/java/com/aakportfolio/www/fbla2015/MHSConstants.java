package com.aakportfolio.www.fbla2015;

import java.util.HashMap;
import java.util.Map;

/**
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

    private static final int[] images = {R.drawable.ic_launcher, R.mipmap.ic_pace,
            R.mipmap.ic_music, R.mipmap.ic_test, R.mipmap.ic_sports,
            R.mipmap.ic_school, R.mipmap.ic_art, R.mipmap.ic_research,
            R.mipmap.ic_graduation, R.mipmap.ic_dance, R.mipmap.ic_award,
            R.mipmap.ic_fbla};

    private static final String[] types = {"none", "pace",
            "music", "test", "sports",
            "school", "art", "research","graduation", "dance", "award",
            "fbla"};

    private static Map<String, Integer> imageMap;

    private static HashMap<String, Integer> makeMap() {
        HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < Math.min(types.length, images.length); i++) {
            map.put(types[i], images[i]);
        }
        return map;
    }

    public static int getImg(String type) {
        if (imageMap == null) {
            imageMap = makeMap();
        }
        Integer ret = imageMap.get(type);
        return ret != null ? ret : imageMap.get("none");
    }
}
