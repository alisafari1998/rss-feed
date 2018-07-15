package ir.nimbo2.nimroo.cooler.database;

import ir.nimbo2.nimroo.cooler.Config;
import ir.nimbo2.nimroo.cooler.database.model.ConfigModel;
import ir.nimbo2.nimroo.cooler.database.model.NewsModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static ir.nimbo2.nimroo.cooler.Config.DATABASE_PASSWORD;
import static ir.nimbo2.nimroo.cooler.Config.DATABASE_USER;
import static ir.nimbo2.nimroo.cooler.Config.MY_SQL_CONNECTION_ADDRESS;

public class DatabaseConnection {

    private Connection connection;
    private boolean init = false;

    public void init() throws SQLException {
        if (init)
            return;

        createDatabases();
        createConfigTable();
        createNewsTable();

        init = true;
    }

    private void createDatabases() throws SQLException {

        Statement st = connection.createStatement();
        st.execute("CREATE DATABASE IF NOT EXISTS " + Config.DATABASE_NAME + " CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");

    }

    public DatabaseConnection() {

        try {
            Class.forName("com.mysql.jdbc.Driver");

            this.connection = DriverManager.getConnection(MY_SQL_CONNECTION_ADDRESS, DATABASE_USER, DATABASE_PASSWORD);

        } catch (SQLException e) {
            System.err.println("This exception that provides information on a database access error or other errors.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createConfigTable() throws SQLException {

        PreparedStatement createTable = connection.prepareStatement("CREATE TABLE IF NOT  EXISTS "
                + Config.DATABASE_NAME
                + ".config"
                + " (id INTEGER NOT NULL AUTO_INCREMENT, PRIMARY KEY (id), "
                + "site VARCHAR(512) NOT NULL, rss VARCHAR(1024), config TEXT)");
        createTable.executeUpdate();
    }

    /**
     *
     * @param configModel
     * @return configModel's id
     * If for any reason result would be empty return is -1.
     * @throws SQLException
     */
    public long insertConfig(ConfigModel configModel) throws SQLException, UnexpectedSQLBehaviorException {

        PreparedStatement insertPS = connection.prepareStatement("INSERT INTO "+ Config.DATABASE_NAME +"."+ "config" +
                "(site, rss, config) VALUES (?, ?, ?)");
        insertPS.setString(1, configModel.getSite());
        insertPS.setString(2, configModel.getRSSLink());
        insertPS.setString(3, configModel.getConfig());
        insertPS.executeUpdate();

        ResultSet result = insertPS.getGeneratedKeys();

        if (result.next()) {
            return result.getLong(1);
        }
        throw new UnexpectedSQLBehaviorException("Empty insert resultSet !!!");
    }

    public ConfigModel loadConfig(long id) throws SQLException {

        PreparedStatement loadPS = connection.prepareStatement("SELECT * FROM " + Config.DATABASE_NAME
                + ".config" + " WHERE id=?");
        loadPS.setLong(1, id);
        ResultSet result = loadPS.executeQuery();
        if(result.next()) {
            return loadConfigModelFromResultSet(result);
        }
        else {
            return null;
        }
    }

    public List<ConfigModel> loadAllConfigs() throws SQLException {
        PreparedStatement loadAllPS = connection.prepareStatement("SELECT * FROM " + Config.DATABASE_NAME + ".config");

        ArrayList<ConfigModel> list = new ArrayList<>();
        ResultSet result = loadAllPS.executeQuery();

        while (result.next()) {
            ConfigModel current = loadConfigModelFromResultSet(result);
            list.add(current);
        }

        return list;
    }

    public void createNewsTable() throws SQLException {
        PreparedStatement createTable = connection.prepareStatement("CREATE TABLE IF NOT  EXISTS "+
                Config.DATABASE_NAME +".news" +"(" + "id INTEGER NOT NULL AUTO_INCREMENT, PRIMARY KEY (id)," +
                "title VARCHAR (255)," + "link  VARCHAR (255) NOT NULL," + "description  VARCHAR (1024),"
                + "publish_date  DATE," + "news_body MEDIUMTEXT)");

        createTable.executeUpdate();
    }

    /**
     *
     * @param newsModel
     * @return newsModel's id
     * If for any reason result would be empty an Exception would be thrown
     * @throws SQLException
     */
    public long insertNews(NewsModel newsModel) throws UnexpectedSQLBehaviorException, SQLException {
        PreparedStatement insertPS = connection.prepareStatement("INSERT INTO "+ Config.DATABASE_NAME +".news " +
                "(title, link, description, publish_date, news_body) VALUES (?, ?, ?, ?, ?)");

        insertPS.setString(1, newsModel.getTitle());
        insertPS.setString(2, newsModel.getLink());
        insertPS.setString(3, newsModel.getDescription());
        insertPS.setDate(4, newsModel.getPublishDate());
        insertPS.setString(5, newsModel.getNewsBody());

        insertPS.executeUpdate();
        ResultSet result = insertPS.getGeneratedKeys();
        if (result.next()) {
            return result.getLong(1);
        }

        throw new UnexpectedSQLBehaviorException("Empty insert resultSet !!!");
    }

    public NewsModel loadNews(long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM "+ Config.DATABASE_NAME + ".news" + " WHERE id="+id);
        ResultSet result = preparedStatement.executeQuery();
        if(result.next()) {
            NewsModel newsModel = new NewsModel();
            newsModel.setId(result.getLong("id"));
            newsModel.setTitle(result.getString("title"));
            newsModel.setLink(result.getString("link"));
            newsModel.setDescription(result.getString("description"));
            newsModel.setPublishDate(result.getDate("publish_date"));
            newsModel.setNewsBody(result.getString("news_body"));
            return newsModel;
        }

        return null;
    }

    private ConfigModel loadConfigModelFromResultSet(ResultSet result) throws SQLException {
        ConfigModel model = new ConfigModel();

        model.setId(result.getLong("id"));
        model.setSite(result.getString("site"));
        model.setRSSLink(result.getString("rss"));
        model.setConfig(result.getString("config"));

        return model;
    }

    public Connection getConnection() {
        return connection;
    }
}
