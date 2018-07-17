package ir.nimbo2.nimroo.cooler.database.repository;


import ir.nimbo2.nimroo.cooler.database.DatabaseConnection;
import ir.nimbo2.nimroo.cooler.database.UnexpectedSQLBehaviorException;
import ir.nimbo2.nimroo.cooler.database.model.NewsModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewsRepository {

    private String createNewsTableQuery;
    private String insertNewsQuery;
    private String loadNewsQuery;
    private static NewsRepository REPO;

    private NewsRepository() {
        String databaseName = DatabaseConnection.getDatabaseConnection().getDatabaseName();
        createNewsTableQuery = "CREATE TABLE IF NOT  EXISTS "+
                databaseName +".news (id INTEGER NOT NULL AUTO_INCREMENT, PRIMARY KEY (id)," +
                "title VARCHAR (255), link VARCHAR (255) NOT NULL, description VARCHAR (1024)," +
                "publish_date DATETIME, news_body MEDIUMTEXT)";

        insertNewsQuery = "INSERT INTO "+ databaseName +".news " +
                "(title, link, description, publish_date, news_body, config_id) VALUES (?, ?, ?, ?, ?, ?)";

        loadNewsQuery = "SELECT * FROM "+ databaseName + ".news" + " WHERE id=?";

    }

    public void createNewsTable() throws SQLException {
        Connection c = getConnection();
        try(PreparedStatement createTable = c.prepareStatement(createNewsTableQuery)) {
            createTable.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            c.close();
        }
    }

    /**
     *
     * @param newsModel
     * @return newsModel's id
     * If for any reason result would be empty an Exception would be thrown
     * @throws SQLException
     */
    public long insertNews(NewsModel newsModel) throws UnexpectedSQLBehaviorException, SQLException {
        Connection c = getConnection();
        try(PreparedStatement insertPS = c.prepareStatement(insertNewsQuery)) {

            insertPS.setString(1, newsModel.getTitle());
            insertPS.setString(2, newsModel.getLink());
            insertPS.setString(3, newsModel.getDescription());
            insertPS.setTimestamp(4, newsModel.getPublishDate());
            insertPS.setString(5, newsModel.getNewsBody());
            insertPS.setLong(6, newsModel.getConfigId());
            insertPS.executeUpdate();
            ResultSet result = insertPS.getGeneratedKeys();
            if (result.next()) {
                return result.getLong(1);
            }

            throw new UnexpectedSQLBehaviorException("Empty insert resultSet !!!");
        }
        finally{
            c.close();
        }
    }

    public NewsModel loadNews(long id) throws SQLException {
        Connection c = getConnection();
        try(PreparedStatement preparedStatement = c.prepareStatement(loadNewsQuery)) {
            preparedStatement.setLong(1, id);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                return convertToNewsModel(result);
            }

            return null;
        }
        finally{
            c.close();
        }
    }

    private NewsModel convertToNewsModel(ResultSet result) throws SQLException {
        NewsModel newsModel = new NewsModel();
        newsModel.setId(result.getLong("id"));
        newsModel.setTitle(result.getString("title"));
        newsModel.setLink(result.getString("link"));
        newsModel.setDescription(result.getString("description"));
        newsModel.setPublishDate(result.getTimestamp("publish_date"));
        newsModel.setNewsBody(result.getString("news_body"));
        newsModel.setConfigId(result.getLong("config_id"));

        return newsModel;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getDatabaseConnection().getConnection();
    }

    public static NewsRepository getRepository() {

        if (REPO == null) {
            synchronized (NewsRepository.class) {
                if (REPO == null) {
                    REPO = new NewsRepository();
                }
            }
        }
        return REPO;
    }
}
