package ir.nimbo2.nimroo.cooler.database.repository;


import ir.nimbo2.nimroo.cooler.Config;
import ir.nimbo2.nimroo.cooler.database.DatabaseConnection;
import ir.nimbo2.nimroo.cooler.database.UnexpectedSQLBehaviorException;
import ir.nimbo2.nimroo.cooler.database.model.NewsModel;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class NewsRepository {

    private String createNewsTableQuery;
    private String insertNewsQuery;
    private String loadNewsQuery;
    private static NewsRepository REPO;
    private String loadLast10NewsBySiteQuery;
    private String countNewsBySiteInDateQuery;
    private String searchInTitleQuery;
    private String searchInBodyQuery;

    private NewsRepository() {
    }

    public void init() {
        String databaseName = DatabaseConnection.getDatabaseConnection().getDatabaseName();
        createNewsTableQuery = "CREATE TABLE IF NOT  EXISTS "+
                databaseName +".news (id INTEGER NOT NULL AUTO_INCREMENT, PRIMARY KEY (id)," +
                "title VARCHAR (255), link VARCHAR (255) NOT NULL, description VARCHAR (1024)," +
                "publish_date DATETIME, news_body MEDIUMTEXT," +
                "config_id INTEGER NOT NULL, FOREIGN KEY (config_id) REFERENCES config(id))";

        insertNewsQuery = "INSERT INTO "+ databaseName +".news " +
                "(title, link, description, publish_date, news_body, config_id) VALUES (?, ?, ?, ?, ?, ?)";

        loadNewsQuery = "SELECT * FROM "+ databaseName + ".news" + " WHERE id=?";

        loadLast10NewsBySiteQuery = "SELECT title, description, news_body, publish_date FROM " + databaseName +
                ".news as news INNER JOIN "+ databaseName +".config as config ON news.config_id=config.id WHERE config.site=? ORDER BY publish_date DESC LIMIT 10";

        countNewsBySiteInDateQuery = "SELECT COUNT(*) AS row_count FROM " + databaseName +
                ".news as news INNER JOIN "+ databaseName +
                ".config as config ON news.config_id=config.id WHERE config.site=? AND publish_date BETWEEN ? AND ?";
        searchInTitleQuery = "select * from " + Config.DATABASE_NAME + ".news" + "WHERE title LIKE \"%?%\"";
        searchInBodyQuery = "select * from " + Config.DATABASE_NAME + ".news" + "WHERE body LIKE \"%?%\"";

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

    public List<NewsModel> loadLast10NewsBySite(String site) throws SQLException {
        Connection c = getConnection();
        try(PreparedStatement preparedStatement = c.prepareStatement(loadLast10NewsBySiteQuery)) {
            preparedStatement.setString(1, site);
            ResultSet result = preparedStatement.executeQuery();
            List<NewsModel> finalResult = new ArrayList<>();
            while(result.next()) {
                NewsModel newsModel = new NewsModel();
                newsModel.setTitle(result.getString("title"));
                newsModel.setDescription(result.getString("description"));
                newsModel.setPublishDate(result.getTimestamp("publish_date"));
                newsModel.setNewsBody(result.getString("news_body"));
                finalResult.add(newsModel);
            }
            return finalResult;
        }
        finally {
            c.close();
        }
    }

    public long countNewsBySiteInDate(String site, Timestamp after, Timestamp before) throws SQLException, UnexpectedSQLBehaviorException {
        Connection c = getConnection();
        try(PreparedStatement preparedStatement = c.prepareStatement(countNewsBySiteInDateQuery)) {
            preparedStatement.setString(1, site);
            preparedStatement.setTimestamp(2, after);
            preparedStatement.setTimestamp(3, before);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                return result.getInt("row_count");
            }

            throw new UnexpectedSQLBehaviorException("Empty insert resultSet !!!");
        }
        finally{
            c.close();
        }

    }

    public long countTodayNewsBySiteInDate(String site) throws SQLException, UnexpectedSQLBehaviorException {
        return countNewsBySiteInDate(site, Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(0)),
                Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(1)));
    }

    public long countADayNewsBySiteInDate(String site, Timestamp begin) throws SQLException, UnexpectedSQLBehaviorException {
        return countNewsBySiteInDate(site, begin, Timestamp.valueOf(begin.toLocalDateTime().plusDays(1)));
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

    private List<NewsModel> searchNews(String toSearch, SearchType type) throws SQLException {
        Connection c = getConnection();
        ArrayList<NewsModel> searchResult = new ArrayList<>();
        try(PreparedStatement preparedStatement = c.prepareStatement(type == SearchType.TITLE_SEARCH ? searchInTitleQuery : searchInBodyQuery)) {
            preparedStatement.setString(1, toSearch);
            ResultSet result = preparedStatement.executeQuery();
            while(result.next()) {
                searchResult.add(convertToNewsModel(result));
            }

            return searchResult;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<NewsModel> searchInTitle(String toSearch) throws SQLException {
        return searchNews(toSearch, SearchType.TITLE_SEARCH);
    }

    public List<NewsModel> searchInBody(String toSearch) throws SQLException {
        return searchNews(toSearch, SearchType.BODY_SEARCH);
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

    private enum SearchType {TITLE_SEARCH, BODY_SEARCH}

}
