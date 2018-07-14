package ir.nimbo2.nimroo.cooler.database.model;

import ir.nimbo2.nimroo.cooler.Config;

import java.sql.*;

public class NewsModel extends SQLModel {

    public long id;
    public String title;
    public String description;
    public Date publishDate;
    public String link;
    public String newsBody;

    private PreparedStatement loadPS;
    private PreparedStatement insertPS;
    private PreparedStatement updatePS;

    public NewsModel() {
        super("news");
    }

    @Override
    public boolean init(Connection connection) {
        try {
            createTablePS = connection.prepareStatement("CREATE TABLE IF NOT  EXISTS "+ Config.DATABASE_NAME +"."+ modelName +"  (" +
                    "id INTEGER NOT NULL AUTO_INCREMENT, PRIMARY KEY (id)," +
                    "title VARCHAR (255)," +
                    "link  VARCHAR (255) NOT NULL," +
                    "description  VARCHAR (1024)," +
                    "publish_date  DATE," +
                    "news_body MEDIUMTEXT)");
            loadPS = connection.prepareStatement("SELECT * FROM "+ Config.DATABASE_NAME + "." + modelName + " WHERE id=?");
            insertPS = connection.prepareStatement("INSERT INTO "+ Config.DATABASE_NAME +"."+ modelName +
                    " (title, link, description, publish_date, news_body) VALUES (?, ?, ?, ?, ?)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean insert() {
        try {

            insertPS.setString(1, title);
            insertPS.setString(2, link);
            insertPS.setString(3, description);
            insertPS.setDate(4, publishDate);
            insertPS.setString(5, newsBody);

            id = insertPS.executeUpdate();
            ResultSet result = insertPS.getGeneratedKeys();
            if (result.next()) {
                id = result.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean update() {
        System.err.println("Not implemented yet. NewsModel.update()");
        return false;
    }

    @Override
    public boolean load() {
        try {
            loadPS.setLong(1, id);
            ResultSet result = loadPS.executeQuery();
            if(result.next()) {
                title = result.getString("title");
                link = result.getString("link");
                description = result.getString("description");
                publishDate = result.getDate("publish_date");
                newsBody = result.getString("news_body");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof NewsModel))
            return false;

        NewsModel news = (NewsModel)o;
        return news.id == id && news.title.equals(title) && news.link.equals(link)
                && news.publishDate.equals(publishDate) && news.newsBody.equals(newsBody);
    }

}
