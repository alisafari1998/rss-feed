package ir.nimbo2.nimroo.cooler.concurency;

import ir.nimbo2.nimroo.cooler.Processors.RSSFeedProcessor;
import ir.nimbo2.nimroo.cooler.database.model.ConfigModel;
import ir.nimbo2.nimroo.cooler.database.model.NewsModel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SiteTask implements Runnable {

    ConfigModel configModel;
    private ScheduledExecutorService htmlExecutor;
    private ScheduledExecutorService rssExecutor;

    public SiteTask(ConfigModel configModel, ScheduledExecutorService htmlExecutor, ScheduledExecutorService rssExecutor) {
        this.configModel = configModel;
        this.htmlExecutor = htmlExecutor;
        this.rssExecutor = rssExecutor;
    }

    @Override
    public void run() {
        RSSFeedProcessor rssFeedProcessor = null;

        try {
            rssFeedProcessor = new RSSFeedProcessor(new URL(configModel.getRSSLink()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        try {
            //Download and process rss xml.
            rssFeedProcessor.process();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Network problem !!!!!");
            return;
        }

        for (NewsModel news : rssFeedProcessor.getResults()) {
            if (news.getLink() == null) {
                continue;
            }

            news.setConfigId(configModel.getId());

//            if

            htmlExecutor.submit(new NewsTask(news, configModel.getConfig(), htmlExecutor));
        }
    }
}