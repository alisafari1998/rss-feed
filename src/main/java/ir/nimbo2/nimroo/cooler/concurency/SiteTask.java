package ir.nimbo2.nimroo.cooler.concurency;

import ir.nimbo2.nimroo.cooler.Processors.RSSFeedProcessor;
import ir.nimbo2.nimroo.cooler.database.model.ConfigModel;
import ir.nimbo2.nimroo.cooler.database.model.NewsModel;
import ir.nimbo2.nimroo.cooler.database.repository.ConfigRepository;
import ir.nimbo2.nimroo.cooler.database.repository.NewsRepository;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;

public class SiteTask implements Runnable {
    Logger logger = Logger.getLogger(SiteTask.class);

    ConfigModel configModel;
    private NewsRepository newsRepository;
    private ConfigRepository configRepository;

    private ScheduledExecutorService htmlExecutor;
    private ScheduledExecutorService rssExecutor;

    public SiteTask(ConfigModel configModel, ScheduledExecutorService htmlExecutor, ScheduledExecutorService rssExecutor
            , NewsRepository newsRepository, ConfigRepository configRepository) {
        this.configModel = configModel;
        this.htmlExecutor = htmlExecutor;
        this.rssExecutor = rssExecutor;
        this.newsRepository = newsRepository;
        this.configRepository = configRepository;
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
            try {
                configRepository.loadLatestNews(configModel.getId());
            } catch (SQLException e) {
                System.err.println("Database access problem !!!");
                logger.debug("Exception", e);
            }

            if(news.getLink() != null && news.getLink().equals(configModel.getLatestNews()))
                break;

            htmlExecutor.submit(new NewsTask(news, configModel.getConfig(), htmlExecutor, newsRepository));
        }

        if (!rssFeedProcessor.getResults().isEmpty()) {
            NewsModel model = rssFeedProcessor.getResults().get(0);
            configModel.setLatestNews(model.getLink());
            try {
                configRepository.updateLatestNews(configModel);
            } catch (SQLException e) {
                System.err.println("Database access problem !!!");
                logger.debug("Exception", e);
            }
        }

    }
}