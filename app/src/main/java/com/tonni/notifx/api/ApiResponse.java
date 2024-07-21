package com.tonni.notifx.api;

import java.util.List;
import com.tonni.notifx.api.Quote;

public class ApiResponse {
    private String endpoint;
    private List<Quote> quotes;
    private String requested_time;
    private long timestamp;

    // Add getters and setters

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }

    public String getRequestedTime() {
        return requested_time;
    }

    public void setRequestedTime(String requested_time) {
        this.requested_time = requested_time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Endpoint: ").append(endpoint).append("\n");
        result.append("Requested Time: ").append(requested_time).append("\n");
        result.append("Timestamp: ").append(timestamp).append("\n");
        result.append("Quotes:\n");
        for (Quote quote : quotes) {
            result.append(quote.toString()).append("\n");
        }
        return result.toString();
    }
}

