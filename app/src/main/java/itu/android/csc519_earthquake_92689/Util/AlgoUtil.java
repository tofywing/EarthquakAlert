package itu.android.csc519_earthquake_92689.Util;


import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import itu.android.csc519_earthquake_92689.DownloadTask;

/**
 * Created by Yee on 8/19/17.
 */

public class AlgoUtil {

    //k largest numbers in array
    public static URL[] getSortedEarthquake(JSONArray objects) {
        List<JSONObject> earthquakes = new ArrayList<>();
        for (int i = 0; i < objects.length(); i++) {
            JSONObject object = objects.optJSONObject(i);
            double mag = object.optJSONObject("properties").optDouble("mag");
            if (mag >= 3.0) {
                earthquakes.add(object);
            }
        }
        //Map sorted in values
        Collections.sort(earthquakes, new Comparator<JSONObject>() {
            public int compare(JSONObject o1, JSONObject o2) {
                double v1 = o1.optJSONObject("properties").optDouble("time");
                double v2 = o2.optJSONObject("properties").optDouble("time");
                if (v1 < v2) {
                    return 1;
                } else if (v1 > v2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        URL[] ans = new URL[earthquakes.size()];
        //data size
        for (int i = 0; i < ans.length; i++) {
            try {
                URL url = new URL(earthquakes.get(i).optJSONObject("properties").optString("detail"));
                ans[i] = url;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return ans;
    }

    public static String[] getRequiredPlaceString(String place) {
        // ans[0]: location, ans[1]: miles + location
        String[] ans = new String[2];
        int index = 0;
        char[] arr = place.toCharArray();
        if (Character.isDigit(arr[index])) {
            index++;
            while (index < arr.length) {
                if (Character.isAlphabetic(arr[index])) {
                    break;
                }
                index++;
            }
        }
        int placeIndex = index;
        while (placeIndex < place.length()) {
            //...of [place name]...
            String locationTemp = place.substring(placeIndex);
            if (locationTemp.startsWith("of")) {
                if (placeIndex + 3 < place.length()) {
                    placeIndex += 3;
                }
                break;
            }
            placeIndex++;
        }
        // ans[0]: location
        ans[0] = place.substring(placeIndex);
        if (index > 0) {
            int km = Integer.valueOf(place.substring(0, index));
            String miles = String.format(Locale.US, "%.02f Mi", kmToMiles(km));
            // ans[1]: miles + [rest...];
            ans[1] = miles + " " + place.substring(index + 3);
        } else {
            ans[1] = place;
        }
        return ans;
    }

    public static float kmToMiles(float km) {
        // mile = km * 0.621
        return (float) (km * 0.621);
    }

    public static int calculateMagRedColor(String mag, int interval) {
        //default interval is 7
        double val = Double.valueOf(mag);
        int temp = (int) ((val - 3.0 + 0.1) / 0.5);
        int fix = temp % 0.5 == 0 ? 0 : 1;
        int count = temp - 1 + fix;
        // mag 3.0 red color: 125
        return 125 + interval * count;
    }
}

