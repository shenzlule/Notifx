package com.tonni.notifx.Utils;

public class TimeConverter {

    public static String convertHours(double hours) {
        // Constants for time units
        final int HOURS_IN_A_DAY = 24;
        final int HOURS_IN_A_WEEK = 7 * HOURS_IN_A_DAY;
        final int HOURS_IN_A_MONTH = 30 * HOURS_IN_A_DAY; // Approximate month
        final int HOURS_IN_A_YEAR = 365 * HOURS_IN_A_DAY; // Approximate year

        // Calculate the number of each time unit
        int years = (int) (hours / HOURS_IN_A_YEAR);
        hours %= HOURS_IN_A_YEAR;

        int months = (int) (hours / HOURS_IN_A_MONTH);
        hours %= HOURS_IN_A_MONTH;

        int weeks = (int) (hours / HOURS_IN_A_WEEK);
        hours %= HOURS_IN_A_WEEK;

        int days = (int) (hours / HOURS_IN_A_DAY);
        hours %= HOURS_IN_A_DAY;

        int hrs = (int) hours;
        int minutes = (int) ((hours - hrs) * 60);

        // Build the output string based on the highest time unit
        StringBuilder result = new StringBuilder();

        if (years > 0) {
            result.append(years).append("Y ");
        }
        if (months > 0 || result.length() > 0) {
            result.append(months).append("M ");
        }
        if (weeks > 0 || result.length() > 0) {
            result.append(weeks).append("W ");
        }
        if (days > 0 || result.length() > 0) {
            result.append(days).append("D ");
        }
        if (hrs > 0 || result.length() > 0) {
            result.append(hrs).append("h ");
        }
        if (minutes > 0 || result.length() > 0) {
            result.append(minutes).append("m ");
        }

        return result.toString().trim();
    }


}

