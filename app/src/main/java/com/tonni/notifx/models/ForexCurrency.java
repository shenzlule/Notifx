package com.tonni.notifx.models;

public class ForexCurrency {
    private String baseCurrency;
    private String quoteCurrency;
    private double mid;
    private int alertNumber;
    private  int pipNumber;
    private  int position;


    public ForexCurrency(String baseCurrency, String quoteCurrency, double mid, int alertNumber, int pipNumber, int position) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.mid = mid;
        this.alertNumber = alertNumber;
        this.pipNumber = pipNumber;
        this.position = position;
    }

    public int getPipNumber() {
        return pipNumber;
    }

    public void setPipNumber(int pipNumber) {
        this.pipNumber = pipNumber;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public double getMid() {
        return mid;
    }

    public void setMid(double mid) {
        this.mid = mid;
    }

    public int getAlertNumber() {
        return alertNumber;
    }

    public void setAlertNumber(int alertNumber) {
        this.alertNumber = alertNumber;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
