package com.tonni.notifx.models;

import java.util.ArrayList;

public class NewsOrganised {
    private ArrayList<ForexNewsItem> mondayNews;
    private ArrayList<ForexNewsItem> tuesdayNews;
    private ArrayList<ForexNewsItem> wednesdayNews;
    private ArrayList<ForexNewsItem> thursdayNews;
    private ArrayList<ForexNewsItem> fridayNews;
    private ArrayList<ForexNewsItem> saturdayNews;
    private ArrayList<ForexNewsItem> sundayNews;

    public NewsOrganised(ArrayList<ForexNewsItem> mondayNews, ArrayList<ForexNewsItem> tuesdayNews, ArrayList<ForexNewsItem> wednesdayNews, ArrayList<ForexNewsItem> thursdayNews, ArrayList<ForexNewsItem> fridayNews, ArrayList<ForexNewsItem> saturdayNews, ArrayList<ForexNewsItem> sundayNews) {
        this.mondayNews = mondayNews;
        this.tuesdayNews = tuesdayNews;
        this.wednesdayNews = wednesdayNews;
        this.thursdayNews = thursdayNews;
        this.fridayNews = fridayNews;
        this.saturdayNews = saturdayNews;
        this.sundayNews = sundayNews;
    }

    public ArrayList<ForexNewsItem> getMondayNews() {
        return mondayNews;
    }

    public void setMondayNews(ArrayList<ForexNewsItem> mondayNews) {
        this.mondayNews = mondayNews;
    }

    public ArrayList<ForexNewsItem> getTuesdayNews() {
        return tuesdayNews;
    }

    public void setTuesdayNews(ArrayList<ForexNewsItem> tuesdayNews) {
        this.tuesdayNews = tuesdayNews;
    }

    public ArrayList<ForexNewsItem> getWednesdayNews() {
        return wednesdayNews;
    }

    public void setWednesdayNews(ArrayList<ForexNewsItem> wednesdayNews) {
        this.wednesdayNews = wednesdayNews;
    }

    public ArrayList<ForexNewsItem> getThursdayNews() {
        return thursdayNews;
    }

    public void setThursdayNews(ArrayList<ForexNewsItem> thursdayNews) {
        this.thursdayNews = thursdayNews;
    }

    public ArrayList<ForexNewsItem> getFridayNews() {
        return fridayNews;
    }

    public void setFridayNews(ArrayList<ForexNewsItem> fridayNews) {
        this.fridayNews = fridayNews;
    }

    public ArrayList<ForexNewsItem> getSaturdayNews() {
        return saturdayNews;
    }

    public void setSaturdayNews(ArrayList<ForexNewsItem> saturdayNews) {
        this.saturdayNews = saturdayNews;
    }

    public ArrayList<ForexNewsItem> getSundayNews() {
        return sundayNews;
    }

    public void setSundayNews(ArrayList<ForexNewsItem> sundayNews) {
        this.sundayNews = sundayNews;
    }

    @Override
    public String toString() {
        return "NewsOrganised{" +
                "mondayNews=" + mondayNews.toString() +
                ", tuesdayNews=" + tuesdayNews.toString() +
                ", wednesdayNews=" + wednesdayNews.toString() +
                ", thursdayNews=" + thursdayNews.toString() +
                ", fridayNews=" + fridayNews.toString() +
                ", saturdayNews=" + saturdayNews.toString() +
                ", sundayNews=" + sundayNews.toString() +
                '}';
    }


}
