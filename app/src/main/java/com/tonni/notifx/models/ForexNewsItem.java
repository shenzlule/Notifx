package com.tonni.notifx.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ForexNewsItem {
    private String time;
    private String name;
    private String currency;
    private String status;
    private String is_done;
    private String isAlarm;


    public ForexNewsItem(String time, String name, String currency, String status, String is_done, String isAlarm) {
        this.time = time;
        this.name = name;
        this.currency = currency;
        this.status = status;
        this.is_done = is_done;
        this.isAlarm = isAlarm;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIs_done() {
        return is_done;
    }

    public void setIs_done(String is_done) {
        this.is_done = is_done;
    }

    public String getIsAlarm() {
        return isAlarm;
    }

    public void setIsAlarm(String isAlarm) {
        this.isAlarm = isAlarm;
    }



    public Calendar getCalendar() throws ParseException {
        SimpleDateFormat sdf;
        Calendar calendar = Calendar.getInstance();

        if (time.contains("All Day")) {
            // Parse all-day event format
            sdf = new SimpleDateFormat("EEE MMM dd", Locale.getDefault());
            Date date = sdf.parse(time.split("-")[0].trim());

            // Set current year
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 9);  // Set to 9 AM for all-day events
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));  // Set current year
        } else {
            // Parse specific time event format
            sdf = new SimpleDateFormat("EEE MMM dd-h:mma", Locale.getDefault());
            Date date = sdf.parse(time);

            // Set current year
            calendar.setTime(date);
            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));  // Set current year
        }

        return calendar;
    }

}
