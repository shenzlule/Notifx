package com.tonni.notifx.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ForexNewsScraper {

//    public static void main(String[] args) throws IOException {
//
//    }

    public static List<ForexNewsItem> extractTableData(Document doc) {
        List<ForexNewsItem> newsItems = new ArrayList<>();
        Element table = doc.selectFirst(".calendar__table");

        if (table != null) {
            Elements rows = table.select("tr.calendar__row");
            String dateTracker = "";
            String hour = "";

            for (Element row : rows) {
                Elements cells = row.select("td");

                if (cells.size() >= 6) {
                    Element timeElement;
                    if (cells.get(1).text().isEmpty()) {
                        timeElement = cells.get(0);
                    } else {
                        timeElement = cells.get(1);
                    }
                    String time = dateTracker + " " + timeElement.text();
                    String name = cells.get(5).text();
                    String currency = cells.get(3).text();
                    String status = row.selectFirst(".calendar__impact").selectFirst("span").classNames().toString().split("-")[1];
                    String isDone = "Not";

                    ForexNewsItem newsItem = new ForexNewsItem(time, name, currency, status, isDone);
                    newsItems.add(newsItem);
                } else if (cells.size() == 1) {
                    dateTracker = cells.get(0).text();
                }
            }
        }

        return newsItems;
    }

    public static class ForexNewsItem {
        private String time;
        private String name;
        private String currency;
        private String status;
        private String isDone;

        public ForexNewsItem(String time, String name, String currency, String status, String isDone) {
            this.time = time;
            this.name = name;
            this.currency = currency;
            this.status = status;
            this.isDone = isDone;
        }

        @Override
        public String toString() {
            return "ForexNewsItem{" +
                    "time='" + time + '\'' +
                    ", name='" + name + '\'' +
                    ", currency='" + currency + '\'' +
                    ", status='" + status + '\'' +
                    ", isDone='" + isDone + '\'' +
                    '}';
        }
    }
}
