package ir.nimbo2.nimroo.cooler.concurency;

import ir.nimbo2.nimroo.cooler.Processors.RSSFeedProcessor;
import ir.nimbo2.nimroo.cooler.database.model.ConfigModel;
import ir.nimbo2.nimroo.cooler.database.model.NewsModel;
import org.apache.log4j.Logger;
import org.apache.log4j.pattern.LogEvent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SiteTask implements Runnable {
    Logger logger = Logger.getLogger(SiteTask.class);

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
            //e.printStackTrace();
            logger.debug("Exception", e);
            return;
        }

        try {
            //Download and process rss xml.
            rssFeedProcessor.process();
        } catch (IOException e) {
            //e.printStackTrace();
            logger.debug("Exception", e);
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