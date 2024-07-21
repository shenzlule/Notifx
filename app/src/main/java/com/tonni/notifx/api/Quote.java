package com.tonni.notifx.api;

public class Quote {

    private String base_currency;
    private String quote_currency;
    private double bid;
    private double ask;
    private double mid;
    private String instrument;

    // Add getters and setters

    public String getBaseCurrency() {
        return base_currency;
    }

    public void setBaseCurrency(String base_currency) {
        this.base_currency = base_currency;
    }

    public String getQuoteCurrency() {
        return quote_currency;
    }

    public void setQuoteCurrency(String quote_currency) {
        this.quote_currency = quote_currency;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getBase_currency() {
        return base_currency;
    }

    public void setBase_currency(String base_currency) {
        this.base_currency = base_currency;
    }

    public String getQuote_currency() {
        return quote_currency;
    }

    public void setQuote_currency(String quote_currency) {
        this.quote_currency = quote_currency;
    }

    public double getMid() {
        return mid;
    }

    public void setMid(double mid) {
        this.mid = mid;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "base_currency='" + base_currency + '\'' +
                ", quote_currency='" + quote_currency + '\'' +
                ", bid=" + bid +
                ", ask=" + ask +
                ", mid=" + mid +
                ", instrument='" + instrument + '\'' +
                '}';
    }
}
