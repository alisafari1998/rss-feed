package ir.nimbo2.nimroo.cooler.controller;

import ir.nimbo2.nimroo.cooler.concurency.SiteTask;
import ir.nimbo2.nimroo.cooler.database.UnexpectedSQLBehaviorException;
import ir.nimbo2.nimroo.cooler.database.model.ConfigModel;
import ir.nimbo2.nimroo.cooler.database.model.NewsModel;
import ir.nimbo2.nimroo.cooler.database.repository.ConfigRepository;
import ir.nimbo2.nimroo.cooler.database.repository.NewsRepository;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {
    Logger logger = Logger.getLogger(Controller.class);

    private ScheduledExecutorService rssExecutor;

    private ScheduledExecutorService htmlExecutor;
    private static Controller controllerInstance = new Controller();

    public static Controller getControllerInstance() {
        return controllerInstance;
    }

    private Controller() {
        rssExecutor = Executors.newScheduledThreadPool(5);
        htmlExecutor = Executors.newScheduledThreadPool(10);
    }

    public void addRssWebsite(String websiteUrl, String rssUrl, String config) {
        ConfigModel configModel = new ConfigModel();
        configModel.setSite(websiteUrl);
        configModel.setRSSLink(rssUrl);
        configModel.setConfig(config);

        ConfigRepository configRepository = new ConfigRepository();
        try {
            configRepository.insertConfig(configModel);
        } catch (SQLException | UnexpectedSQLBehaviorException e) {
            e.printStackTrace();
        }

    }

    public void searchNews(String partOfNews) {

    }

    public void getLastTenNews(String websiteUrl) {
        List<NewsModel> lastTenNews = null;
        try {
            lastTenNews = NewsRepository.getRepository().loadLast10NewsBySite(websiteUrl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (NewsModel newsModel: lastTenNews) {
            System.out.println(newsModel);
            System.out.println("-------------------------------------------------------------------------------------\n");
        }
    }

    public void getThisDayNewsNumber(String websiteUrl) {
        try {
            System.out.println(NewsRepository.getRepository().countTodayNewsBySiteInDate(websiteUrl));
        } catch (SQLException e) {      // todo logger
            e.printStackTrace();
        } catch (UnexpectedSQLBehaviorException e) {
            e.printStackTrace();
        }
    }

    public void getADayNewsNumber(String websiteUrl, Timestamp date) {
        try {
            System.out.println(NewsRepository.getRepository().countADayNewsBySiteInDate(websiteUrl, date));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnexpectedSQLBehaviorException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        List<ConfigModel> sites = null;
        try {
            sites = ConfigRepository.getRepository().loadAllConfigs();
        } catch (SQLException e) {
            //e.printStackTrace();
            logger.debug("Exception", e);
            return;
        }

        for (ConfigModel configModel : sites) {

            rssExecutor.scheduleWithFixedDelay(new SiteTask(configModel, htmlExecutor, rssExecutor), 0, 1, TimeUnit.MINUTES);

        }
    }
}
