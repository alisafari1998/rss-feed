package ir.nimbo2.nimroo.cooler.database.model;


import ir.nimbo2.nimroo.cooler.database.repository.ConfigRepository;

import java.sql.SQLException;
import java.sql.Timestamp;

public class NewsModel {

    private long id;
    private String title;
    private String description;
    private Timestamp publishDate;
    private String link;
    private String newsBody;
    private long configId;

    public NewsModel() {

    }

    public boolean equals(Object o) {

        if (!(o instanceof NewsModel))
            return false;

        NewsModel news = (NewsModel)o;
        return news.id == id && news.title.equals(title) && news.link.equals(link)
                && news.publishDate.equals(publishDate) && news.newsBody.equals(newsBody)
                && news.configId == configId;
    }

    public String getNewsBody() {
        return newsBody;
    }

    public Timestamp getPublishDate() {
        return publishDate;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public void setNewsBody(String newsBody) {
        this.newsBody = newsBody;
    }

    public void setPublishDate(Timestamp publishDate) {
        this.publishDate = publishDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setConfigId(long configId) {
        this.configId = configId;
    }

    public long getConfigId() {
        return configId;
    }

    public ConfigModel getConfigModel() throws SQLException {
        return ConfigRepository.getRepository().loadConfig(configId);
    }

    @Override
    public String toString() {

        return "NewsModel:" +
                (title!=null ? "\n\tTitle:\t" + title : "") +
                (link!=null ? "\n\tLink:\t" + link : "") +
                (description!=null ?"\n\tDescription:\t" + description : "") +
                (publishDate!=null ? "\n\tPublishDate:\t"+publishDate.toString() : "") +
                (newsBody!=null ? "\n\tNewsBody:\t" + newsBody : "");
    }
}
