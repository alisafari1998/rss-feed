package ir.nimbo2.nimroo.cooler.Processors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class NewsProcessor {
    static String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36";
    public static String getNews(String link, String config) throws NoNewsBodyFoundException, IOException {
        Document document = null;
        Jsoup.connect(link).validateTLSCertificates(false).userAgent(userAgent).get();
        document = Jsoup.connect(link).validateTLSCertificates(false).get();

        String[] configParts = config.split("#");
        int configBeginPart = 0;
        Element lastElement = document.select("body").first();

        if (configParts[0].contains("=")) {
            if (document.select(configParts[0]).size() == 0) {
                throw new NoNewsBodyFoundException("");
            }

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
