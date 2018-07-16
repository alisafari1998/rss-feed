package ir.nimbo2.nimroo.cooler.cli;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;

import java.util.Scanner;

public class Cli {
    private Scanner scanner = new Scanner(System.in);
    private String choice = "";
    private boolean exit;
    private boolean valid;
    private String websiteUrl, rssUrl, config;
    private String partOfNews;
    private String date;

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
                    // todo
                    break;

                case "2":
                    setPartOfNews();
                    if (exit) {
                        break;
                    }
                    // todo
                    break;

                case "3":
                    setWebsiteUrl();
                    if (exit) {
                        break;
                    }
                    //todo
                    break;

                case "4":
                    setWebsiteUrl();
                    if (exit) {
                        break;
                    }
                    //todo
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
                    break;

                case "6":
                    break;
                default:
                    System.out.println("Invalid input.");
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
                System.out.println("invalid input.");
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
            date = scanner.next();
            if (date.equals("exit")) {
                exit = true;
                return;
            }
            if (!(valid = dateChecker(date))) {
                System.out.println("invalid date");
            }
        }
    }

    //TODO
    private boolean urlChecker(String url) {
        return true;
    }

    private boolean configChecker(String config) {
        return true;
    }

    private boolean dateChecker(String date) {
        return true;
    }
}
