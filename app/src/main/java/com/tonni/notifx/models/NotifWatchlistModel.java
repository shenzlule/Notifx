package com.tonni.notifx.models;

public class NotifWatchlistModel {

    private int notification_id_watch_list;
    private int notification_Reminder;


    public NotifWatchlistModel(int notification_id_watch_list, int notification_Reminder) {
        this.notification_id_watch_list = notification_id_watch_list;
        this.notification_Reminder = notification_Reminder;
    }

    public int getNotification_Reminder() {
        return notification_Reminder;
    }

    public void setNotification_Reminder(int notification_Reminder) {
        this.notification_Reminder = notification_Reminder;
    }

    public int getNotification_id_watch_list() {
        return notification_id_watch_list;
    }

    public void setNotification_id_watch_list(int notification_id_watch_list) {
        this.notification_id_watch_list = notification_id_watch_list;
    }

}
