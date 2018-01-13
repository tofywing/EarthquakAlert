package itu.android.csc519_earthquake_92689.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Yee on 8/18/17.
 */

public class TimeUtil {

    public static String[] getWeeklyBound(long milliSecs, long range) {
        String[] ans = new String[2];
        // start[7 days back] ... end[1 day ahead]
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        // 7 * 24 * 60 * 60 * 1000
        ans[0] = outputFormat.format(new Date(milliSecs - range));
        // 1 day ahead
        ans[1] = outputFormat.format(new Date(milliSecs + 86400000L));
        return ans;
    }

    public static String convertTimeInFormat(long milliSecs) {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat current = new SimpleDateFormat("M/dd", Locale.US);
        SimpleDateFormat item = new SimpleDateFormat("M/dd", Locale.US);
        if (current.format(new Date(currentTime)).equals(item.format(new Date(milliSecs)))) {
            return new SimpleDateFormat("'Today at' hh:mm a", Locale.US).format(new Date(milliSecs));
        } else {
            SimpleDateFormat outputFormat = new SimpleDateFormat("M/dd 'at' hh:mm a", Locale.US);
            return outputFormat.format(new Date(milliSecs));
        }
    }
}
