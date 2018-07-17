package ir.nimbo2.nimroo.cooler.concurency;

import ir.nimbo2.nimroo.cooler.Processors.NewsProcessor;
import ir.nimbo2.nimroo.cooler.Processors.NoNewsBodyFoundException;
import ir.nimbo2.nimroo.cooler.database.UnexpectedSQLBehaviorException;
import ir.nimbo2.nimroo.cooler.database.model.NewsModel;
import ir.nimbo2.nimroo.cooler.database.repository.NewsRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NewsTask implements Runnable {

    NewsModel news;
    String config;
    private ScheduledExecutorService htmlExecutor;


    public NewsTask(NewsModel news, String config, ScheduledExecutorService htmlExecutor) {
        this.news = news;
        this.config = config;
        this.htmlExecutor = htmlExecutor;
    }

    @Override
    public void run() {
        System.out.println(news.getLink());
        String newsBody = null;
        try {
            newsBody = NewsProcessor.getNews(news.getLink(), config);
        } catch (NoNewsBodyFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Network problem !!!!!" + "\n" + news.getLink());

            htmlExecutor.schedule(this, 1, TimeUnit.MINUTES);
            return;
        }

        news.setNewsBody(newsBody);

        try {
            NewsRepository.getRepository().insertNews(news);
        } catch (UnexpectedSQLBehaviorException e) {
            e.printStackTrace();
            System.out.println(":||||||||");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database access error");
        }

    }
}
