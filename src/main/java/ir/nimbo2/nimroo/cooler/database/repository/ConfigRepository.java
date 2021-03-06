package ir.nimbo2.nimroo.cooler.database.repository;

import ir.nimbo2.nimroo.cooler.Config;
import ir.nimbo2.nimroo.cooler.database.DatabaseConnection;
import ir.nimbo2.nimroo.cooler.database.UnexpectedSQLBehaviorException;
import ir.nimbo2.nimroo.cooler.database.model.ConfigModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfigRepository {


    private String createConfigTableQuery;
    private String insertConfigQuery;
    private String loadConfigQuery;
    private String loadAllConfigsQuery;
    private String updateLatestNewsQuery;
    private String loadLatestNewsQuery;

    public ConfigRepository(String databaseName) {
        createConfigTableQuery = "CREATE TABLE IF NOT  EXISTS "
                + databaseName
                + ".config (id INTEGER NOT NULL AUTO_INCREMENT, PRIMARY KEY (id),"
                + "site VARCHAR(512) NOT NULL, rss VARCHAR(1024), config TEXT, latest_news VARCHAR(4096) DEFAULT null, date_config VARCHAR(32))";

        insertConfigQuery = "INSERT INTO "+ databaseName +".config" +
                " (site, rss, config, date_config) VALUES (?, ?, ?, ?)";

        loadConfigQuery = "SELECT * FROM " + databaseName + ".config WHERE id=?";

        loadAllConfigsQuery = "SELECT * FROM " + databaseName + ".config";

        updateLatestNewsQuery = "UPDATE "+ databaseName + ".config SET latest_news=? WHERE id=?";

        loadLatestNewsQuery = "SELECT latest_news FROM " + databaseName + ".config WHERE id=?";
    }

    public void createConfigTable() throws SQLException {

        try (Connection c = getConnection(); PreparedStatement createTable = c.prepareStatement(createConfigTableQuery)) {
            createTable.executeUpdate();
        }

    }

    /**
     *
     * @param configModel
     * @return configModel's id
     * If for any reason result would be empty return is -1.
     * @throws SQLException
     */
    public long insertConfig(ConfigModel configModel) throws SQLException, UnexpectedSQLBehaviorException {

        try(Connection c = getConnection(); PreparedStatement insertPS = c.prepareStatement(insertConfigQuery)) {
            insertPS.setString(1, configModel.getSite());
            insertPS.setString(2, configModel.getRSSLink());
            insertPS.setString(3, configModel.getConfig());
            insertPS.setString(4, configModel.getDateConfig());
            insertPS.executeUpdate();

            ResultSet result = insertPS.getGeneratedKeys();
            if (result.next()) {
                return result.getLong(1);
            }
            throw new UnexpectedSQLBehaviorException("Empty insert resultSet !!!");
        }
    }

    public ConfigModel loadConfig(long id) throws SQLException {

        try(Connection c = getConnection(); PreparedStatement loadPS = c.prepareStatement(loadConfigQuery)) {
            loadPS.setLong(1, id);
            ResultSet result = loadPS.executeQuery();

            if(result.next()) {
                return loadConfigModelFromResultSet(result);
            }
            else {
                return null;
            }
        }
    }

    public List<ConfigModel> loadAllConfigs() throws SQLException {

        try(Connection c = getConnection(); PreparedStatement loadAllPS = c.prepareStatement(loadAllConfigsQuery)) {

            ArrayList<ConfigModel> list = new ArrayList<>();
            ResultSet result = loadAllPS.executeQuery();

            while (result.next()) {
                ConfigModel current = loadConfigModelFromResultSet(result);
                list.add(current);
            }
            return list;
        }
    }

    public void updateLatestNews(ConfigModel configModel) throws SQLException {

        try(Connection c = getConnection(); PreparedStatement updateLatestNews = c.prepareStatement(updateLatestNewsQuery)){

            updateLatestNews.setString(1, configModel.getLatestNews());
            updateLatestNews.setLong(2, configModel.getId());
            updateLatestNews.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ConfigModel loadLatestNews(long id) throws SQLException {
        ConfigModel cm = new ConfigModel();

        try(Connection c = getConnection(); PreparedStatement loadLatestNews = c.prepareStatement(loadLatestNewsQuery)) {
            loadLatestNews.setLong(1, id);
            ResultSet result = loadLatestNews.executeQuery();
            cm.setId(id);
            if (result.next()) {
                cm.setLatestNews(result.getString("latest_news"));
            }
        }
        return cm;
    }

    private ConfigModel loadConfigModelFromResultSet(ResultSet result) throws SQLException {
        ConfigModel model = new ConfigModel();

        model.setId(result.getLong("id"));
        model.setSite(result.getString("site"));
        model.setRSSLink(result.getString("rss"));
        model.setConfig(result.getString("config"));
        model.setLatestNews(result.getString("latest_news"));
        model.setDateConfig(result.getString("date_config"));


        return model;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getDatabaseConnection().getConnection();
    }
}
