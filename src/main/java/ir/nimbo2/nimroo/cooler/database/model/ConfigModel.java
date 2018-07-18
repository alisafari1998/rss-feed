package ir.nimbo2.nimroo.cooler.database.model;

import ir.nimbo2.nimroo.cooler.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigModel {

    private long id;
    private String site;
    private String rssLink;
    private String dateConfig;

    /**
     * TODO This needs real documentation.
     */
    String config;


    String latestNews;

    public ConfigModel() {

    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof ConfigModel)) {
            return false;
        }

        ConfigModel cm = (ConfigModel)o;
        return cm.id == id && cm.config.equals(config) && cm.site.equals(site) && cm.rssLink.equals(rssLink) && cm.dateConfig.equals(dateConfig);
    }

    public long getId() { return id; }

    public String getConfig() {
        return config;
    }

    public String getRSSLink() {
        return rssLink;
    }

    public String getSite() {
        return site;
    }

    public String getDateConfig() {return dateConfig;}

    public void setId(long id) {
        this.id = id;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public void setRSSLink(String rss) {
        this.rssLink = rss;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public void setDateConfig(String dateConfig) {this.dateConfig = dateConfig;}

    public String getLatestNews() {
        return latestNews;
    }

    public void setLatestNews(String latestNews) {
        this.latestNews = latestNews;
    }
}