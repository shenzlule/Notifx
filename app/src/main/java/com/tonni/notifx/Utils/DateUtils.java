package com.tonni.notifx.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String convertMillisToDateString(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date(millis);
        return sdf.format(date);
    }
}
