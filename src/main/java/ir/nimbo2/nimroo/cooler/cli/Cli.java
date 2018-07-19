package ir.nimbo2.nimroo.cooler.cli;

import ir.nimbo2.nimroo.cooler.controller.Controller;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Cli {
    private Controller controller = Controller.getControllerInstance();
    private Scanner scanner;
    private String choice = "";
    private boolean exit;
    private boolean valid;
    private String websiteUrl, rssUrl, config;
    private String partOfNews;
    private String dateString;
    private Date parsedDate;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Cli() {
        scanner = new Scanner(System.in);
    }

    public void run() {
        while (!choice.equals("6")) {                       // while(true) ?
            System.out.println("1.Add rss website");
            System.out.println("2.Search news");
            System.out.println("3.Get last 10 news");
            System.out.println("4.Get number of a website news for this day");
            System.out.println("5.Get number of a website news for a specific day");
            System.out.println("6.Exit");
            choice = scanner.next();
            exit = false;
            valid = false;
            switch (choice) {
                case "1":
                    setWebsiteUrl();
                    if (exit) {
                        break;
                    }

                    setRssUrl();
                    if (exit) {
                        break;
                    }

                    setConfig();
                    if (exit) {
                        break;
                    }

                    controller.addRssWebsite(websiteUrl, rssUrl, config);
                    break;

                case "2":
                    setPartOfNews();
                    if (exit) {
                        break;
                    }
                    System.out.println("1.Search in title");
                    System.out.println("2.Search in body");
                    System.out.println("3.Search in title & body");
                    String searchType = scanner.next();
                    switch (searchType) {
                        case "1":
                            controller.searchNewsByTitle(partOfNews);
                            break;
                        case "2":
                            controller.searchNewsByBody(partOfNews);
                            break;
                        case "3":
                            controller.searchNews(partOfNews);
                            break;
                        default:
                            invalidInput();
                            break;
                    }
                    break;

                case "3":
                    setWebsiteUrl();
                    if (exit) {
                        break;
                    }

                    controller.getLastTenNews(websiteUrl);
                    break;

                case "4":
                    setWebsiteUrl();
                    if (exit) {
                        break;
                    }

                    controller.getThisDayNewsNumber(websiteUrl);
                    break;

                case "5":
                    setWebsiteUrl();
                    if (exit) {
                        break;
                    }

                    setDate();
                    if (exit) {
                        break;
                    }

                    controller.getADayNewsNumber(websiteUrl, new Timestamp(parsedDate.getTime()));
                    break;

                case "6":
                    break;
                default:
                    invalidInput();
                    break;
            }
        }
    }

    private void setWebsiteUrl() {
        valid = false;
        while (!valid) {
            System.out.print("Enter website url");
            System.out.println(" (or type exit)");
            websiteUrl = scanner.next();
            if (websiteUrl.equals("exit")){
                exit = true;
                return;
            }
            if (!(valid = urlChecker(websiteUrl))){
                invalidInput();
            }
        }
    }

    private void setRssUrl() {
        valid = false;
        while (!valid) {
            System.out.print("Enter rss website url");
            System.out.println(" (or type exit)");
            rssUrl = scanner.next();
            if (rssUrl.equals("exit")) {
                exit = true;
                return;
            }
            if (!(valid = urlChecker(rssUrl))){
                System.out.println("invalid url");
            }
        }
    }

    private void setConfig() {
        valid = false;
        while (!valid) {
            System.out.print("Enter website config");
            System.out.println(" (or type exit)");
            config = scanner.next();
            if (config.equals("exit")) {
                exit = true;
                return;
            }
            if (!(valid = configChecker(config))){
                System.out.println("invalid config");
            }
        }
    }

    private void setPartOfNews() {
        System.out.print("Enter part of the news");
        System.out.println(" (or type exit)");
        partOfNews = scanner.next();
        if (partOfNews.equals("exit")){
            exit = true;
            return;
        }
    }

    private void setDate() {
        valid = false;
        while (!valid) {
            System.out.print("Enter Date");
            System.out.println(" (or type exit)");
            dateString = scanner.next();
            if (dateString.equals("exit")) {
                exit = true;
                return;
            }
            if (!(valid = dateChecker(dateString))) {
                System.out.println("invalid date");
            }
        }
        parsedDate = dateParser(dateString);
    }

    //TODO
    boolean urlChecker(String url) {
        return true;/*url.matches("(http://|https://)([a-zA-Z0-9]+)\\.[a-z]{3}(/[a-zA-Z0-9])*");*/
    }

    boolean configChecker(String config) {
        return true;
    }

    boolean dateChecker(String date) {
        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    Date dateParser(String date) {
        Date ans = null;
        try {
            ans = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ans;
    }

    private void invalidInput() {
        System.out.println("invalid input.");
    }
}
