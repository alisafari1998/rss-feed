package ir.nimbo2.nimroo.cooler.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Processor {

    public static void main(String[] args) {
        rssProcess("", "https://www.yjc.ir/fa/news/6594270/%D8%AD%D8%B6%D9%88%D8%B1-%D9%86%D8%AA%D8%A7%D9%86%DB%8C%D8%A7%D9%87%D9%88-%D8%AF%D8%B1-%D8%B1%D9%88%D8%B3%DB%8C%D9%87-%D8%AA%D8%A7%D8%AB%DB%8C%D8%B1%DB%8C-%D8%A8%D8%B1-%D9%85%D8%A7%D9%85%D9%88%D8%B1%DB%8C%D8%AA-%D8%A7%D8%B3%D8%AA%D8%B1%D8%A7%D8%AA%DA%98%DB%8C%DA%A9-%D9%85%D8%A7-%D9%86%D8%AF%D8%A7%D8%B1%D8%AF", "");
    }

    public static void rssProcess(String site, String rss, String config) {
        try {
            Document document = Jsoup.connect(rss).get();
            Elements links = document.getElementsByTag("link");
            for (Element link: links){
                String linkHref = link.text();
                System.out.println(linkHref);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
