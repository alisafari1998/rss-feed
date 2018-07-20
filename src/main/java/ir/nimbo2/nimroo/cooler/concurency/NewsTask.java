package ir.nimbo2.nimroo.cooler.concurency;

import ir.nimbo2.nimroo.cooler.Processors.NewsProcessor;
import ir.nimbo2.nimroo.cooler.Processors.NoNewsBodyFoundException;
import ir.nimbo2.nimroo.cooler.database.UnexpectedSQLBehaviorException;
import ir.nimbo2.nimroo.cooler.database.model.NewsModel;
import ir.nimbo2.nimroo.cooler.database.repository.NewsRepository;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NewsTask implements Runnable {
    Logger logger = Logger.getLogger(NewsTask.class);

    NewsModel news;
    String config;
    NewsRepository newsRepository;
    private ScheduledExecutorService htmlExecutor;

    public NewsTask(NewsModel news, String config, ScheduledExecutorService htmlExecutor, NewsRepository newsRepository) {
        this.news = news;
        this.config = config;
        this.htmlExecutor = htmlExecutor;
        this.newsRepository = newsRepository;
    }

    @Override
    public void run() {
        String newsBody = null;
        try {
            newsBody = NewsProcessor.getNews(news.getLink(), config);
        } catch (NoNewsBodyFoundException e) {
            //e.printStackTrace();
            logger.debug("Exception", e);
            return;
        } catch (IOException e) {
            //e.printStackTrace();
            logger.debug("Exception", e);
            System.out.println("Network problem !!!!!" + "\n" + news.getLink());

            htmlExecutor.schedule(this, 1, TimeUnit.MINUTES);
            return;
        }

        news.setNewsBody(newsBody);
        try {
            newsRepository.insertNews(news);
        } catch (UnexpectedSQLBehaviorException e) {
            //e.printStackTrace();
            logger.debug("Exception", e);
            System.out.println(":||||||||");
        } catch (SQLException e) {
            //e.printStackTrace();
            logger.debug("Exception", e);
            System.out.println("Database access error");
        }

    }
}