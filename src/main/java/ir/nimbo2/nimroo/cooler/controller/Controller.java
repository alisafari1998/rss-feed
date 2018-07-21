package ir.nimbo2.nimroo.cooler.controller;

import ir.nimbo2.nimroo.cooler.Config;
import ir.nimbo2.nimroo.cooler.concurency.SiteTask;
import ir.nimbo2.nimroo.cooler.database.UnexpectedSQLBehaviorException;
import ir.nimbo2.nimroo.cooler.database.model.ConfigModel;
import ir.nimbo2.nimroo.cooler.database.model.NewsModel;
import ir.nimbo2.nimroo.cooler.database.repository.ConfigRepository;
import ir.nimbo2.nimroo.cooler.database.repository.NewsRepository;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {
    private Logger logger = Logger.getLogger(Controller.class);

    private ScheduledExecutorService rssExecutor;

    private ScheduledExecutorService htmlExecutor;
    private static Controller controllerInstance = new Controller();
    public static Controller getControllerInstance() {
        return controllerInstance;
    }
    private NewsRepository newsRepository;
    private ConfigRepository configRepository;

    private Controller() {
        rssExecutor = Executors.newScheduledThreadPool(Config.getRssExecutorServiceSize());
        htmlExecutor = Executors.newScheduledThreadPool(Config.getSiteExecutorServiceSize());
        newsRepository = new NewsRepository(Config.getDatabaseName());
        configRepository = new ConfigRepository(Config.getDatabaseName());
    }

    public void addRssWebsite(String websiteUrl, String rssUrl, String config, String dateConfig) {
        ConfigModel configModel = new ConfigModel();
        configModel.setSite(websiteUrl);
        configModel.setRSSLink(rssUrl);
        configModel.setConfig(config);
        configModel.setDateConfig(dateConfig);

        try {
            configRepository.insertConfig(configModel);
        } catch (SQLException | UnexpectedSQLBehaviorException e) {
            e.printStackTrace();
        }

    }

    public void searchNewsByTitle(String partOfNews) {
        HashSet<NewsModel> newsByTitle = new HashSet<>();
        try {
            newsByTitle = newsRepository.searchInTitle(partOfNews);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (NewsModel newsModel: newsByTitle) {
            System.out.println(newsModel);
        }
    }

    public void searchNewsByBody(String partOfNews) {
        HashSet<NewsModel> newsByBody = new HashSet<>();

        try {
            newsByBody = newsRepository.searchInBody(partOfNews);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (NewsModel newsModel: newsByBody) {
            System.out.println(newsModel);
        }
    }

    public void searchNews(String partOfNews) {
        HashSet<NewsModel> newsByTitle = new HashSet<>();
        HashSet<NewsModel> newsByBody = new HashSet<>();
        try {
            newsByTitle = newsRepository.searchInTitle(partOfNews);
            newsByBody = newsRepository.searchInBody(partOfNews);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        newsByBody.addAll(newsByTitle);
        for (NewsModel newsModel: newsByBody) {
            System.out.println(newsModel);
        }
    }

    public void getLastTenNews(String websiteUrl) {
        List<NewsModel> lastTenNews = null;
        try {
            lastTenNews = newsRepository.loadLast10NewsBySite(websiteUrl);
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
            System.out.println(newsRepository.countTodayNewsBySiteInDate(websiteUrl));
        } catch (SQLException e) {      // todo logger
            e.printStackTrace();
        } catch (UnexpectedSQLBehaviorException e) {
            e.printStackTrace();
        }
    }

    public void getADayNewsNumber(String websiteUrl, Timestamp date) {
        try {
            System.out.println(newsRepository.countADayNewsBySiteInDate(websiteUrl, date));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnexpectedSQLBehaviorException e) {
            e.printStackTrace();
        }
    }

    // this method loads website configs and update news from them
    public void start() {
        List<ConfigModel> sites = null;
        try {
            sites = configRepository.loadAllConfigs();
        } catch (SQLException e) {
            logger.debug("Exception", e);
            return;
        }

        for (ConfigModel configModel : sites) {
            logger.info("Going through site: " + configModel.getSite());
            rssExecutor.scheduleWithFixedDelay(new SiteTask(configModel, htmlExecutor, rssExecutor, newsRepository, configRepository), 0, 1, TimeUnit.MINUTES);
        }
    }
}
