package com.tonni.notifx.models;

import java.util.ArrayList;
import java.util.Calendar;

public class PendingPrice {

    private  long id;
    private  String price;
    private  String pair;
    private  String pair_visible;
    private   String date;
    private   String note;
    private String filled;
    private   String date_filled;
    private   String direction;
    private   int posFromCurrency;
    private   int isChainActive;
    private ArrayList<Long> chainlist;
    private  int is_chain_Pending;

    public PendingPrice(String price, String pair, String pair_visible, String date, String note, String filled, String date_filled, String direction, int posFromCurrency) {
        this.id= System.currentTimeMillis();
        this.price = price;
        this.pair = pair;
        this.pair_visible = pair_visible;
        this.date = date;
        this.note = note;
        this.filled = filled;
        this.date_filled = date_filled;
        this.direction = direction;
        this.posFromCurrency = posFromCurrency;
        this.isChainActive = 0;
        this.chainlist = null;
        this.is_chain_Pending=0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getFilled() {
        return filled;
    }

    public void setFilled(String filled) {
        this.filled = filled;
    }

    public String getDate_filled() {
        return date_filled;
    }

    public void setDate_filled(String date_filled) {
        this.date_filled = date_filled;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPair_visible() {
        return pair_visible;
    }

    public void setPair_visible(String pair_visible) {
        this.pair_visible = pair_visible;
    }


    public int getPosFromCurrency() {
        return posFromCurrency;
    }

    public void setPosFromCurrency(int posFromCurrency) {
        this.posFromCurrency = posFromCurrency;
    }


    public int getIsChainActive() {
        return isChainActive;
    }

    public void setIsChainActive(int isChainActive) {
        this.isChainActive = isChainActive;
    }

    public ArrayList<Long> getChainlist() {
        return chainlist;
    }

    public void setChainlist(ArrayList<Long> chainlist) {
        this.chainlist = chainlist;
    }

    public int getIs_chain_Pending() {
        return is_chain_Pending;
    }

    public void setIs_chain_Pending(int is_chain_Pending) {
        this.is_chain_Pending = is_chain_Pending;
    }
}
