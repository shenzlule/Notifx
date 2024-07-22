package com.tonni.notifx.models;

public class TrackRefresh {

    private static int NewsFrag=0;
    private static int PendingFrag=0;
    private static int FilledFrag=0;



    public static int getNewsFrag() {
        return NewsFrag;
    }

    public static void setNewsFrag(int newsFrag) {
        NewsFrag = newsFrag;
    }

    public static int getPendingFrag() {
        return PendingFrag;
    }

    public static void setPendingFrag(int pendingFrag) {
        PendingFrag = pendingFrag;
    }


    public static int getFilledFrag() {
        return FilledFrag;
    }

    public static void setFilledFrag(int filledFrag) {
        FilledFrag = filledFrag;
    }
}
