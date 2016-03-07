package com.shubham.popularmovie.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by shubham on 29/2/16.
 */
public class Utility {

    /**
     * Static method to check network availability
     *
     * @param context Context of the calling class
     */

    public static boolean getNetworkState(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    public static String parseDateTime(String date, String sourceFormat, String targetFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(sourceFormat);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date strDate = sdf.parse(date);

            SimpleDateFormat sdf2 = new SimpleDateFormat(targetFormat);
            sdf2.setTimeZone(TimeZone.getDefault());
            return sdf2.format(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
    }



}
