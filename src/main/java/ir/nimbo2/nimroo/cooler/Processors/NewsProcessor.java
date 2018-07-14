package ir.nimbo2.nimroo.cooler.Processors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class NewsProcessor {

    public static String getNews(String site, String link, String config) {
        Document document = null;
        try {
            document = Jsoup.connect(link).validateTLSCertificates(false).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] configParts = config.split("#");
        int configBeginPart = 0;
        Element lastElement = document.select("body").first();

        if (configParts[0].contains("=")) {
            lastElement = document.select(configParts[0]).get(0);   // tag[search inside]  --> tag[attr=value]
            configBeginPart++;
        }

        for (int i = configBeginPart; i < configParts.length; i++) {
            int tagNumber = Integer.parseInt(configParts[i]);
            Elements childElements = lastElement.children();

            lastElement = childElements.get(tagNumber);
        }

        return lastElement.text();
    }
}
