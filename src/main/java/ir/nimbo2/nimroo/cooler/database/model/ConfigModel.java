package ir.nimbo2.nimroo.cooler.database.model;

import ir.nimbo2.nimroo.cooler.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigModel extends SQLModel {

    long id;
    String site;
    String rssLink;

    /**
     * TODO This needs real documentation.
     */
    String config;

    private PreparedStatement loadPS;
    private PreparedStatement loadAllPS;
    private PreparedStatement insertPS;
    private PreparedStatement updatePS;



    public ConfigModel() {
        super("config");
    }

    @Override
    public boolean init(Connection connection) {

        try {
            createTablePS = connection.prepareStatement("CREATE TABLE IF NOT  EXISTS " + Config.DATABASE_NAME
                    + "." + modelName
                    + " (id INTEGER NOT NULL AUTO_INCREMENT, PRIMARY KEY (id), "
                    + "site VARCHAR(512) NOT NULL, rss VARCHAR(1024), config TEXT)");
            loadPS = connection.prepareStatement("SELECT * FROM " + Config.DATABASE_NAME + "." + modelName
                    +" WHERE id=?");
            loadAllPS = connection.prepareStatement("SELECT * FROM " + Config.DATABASE_NAME + "." + modelName);
            insertPS = connection.prepareStatement("INSERT INTO "+ Config.DATABASE_NAME +"."+ modelName +
                    "(site, rss, config) VALUES (?, ?, ?)");
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return true;
    }

    @Override
    public boolean insert() {

        try {
            insertPS.setString(1, site);
            insertPS.setString(2, rssLink);
            insertPS.setString(3, config);
            insertPS.executeUpdate();

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
        return false;
    }


    @Override
    public boolean load() {

        try {
            loadPS.setLong(1, id);

            ResultSet result = loadPS.executeQuery();

            if(result.next()) {
                loadFromResultSet(result);
            }

            } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public List<ConfigModel> loadAll() {
        try {

            ArrayList<ConfigModel> list = new ArrayList<>();
            ResultSet result = loadAllPS.executeQuery();

            while (result.next()) {
                ConfigModel current = new ConfigModel();
                current.loadFromResultSet(result);
                list.add(current);
            }

            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadFromResultSet(ResultSet result) throws SQLException {
        id = result.getLong("id");
        site = result.getString("site");
        rssLink = result.getString("rss");
        config = result.getString("config");
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof ConfigModel))
            return false;

        ConfigModel cm = (ConfigModel)o;
        return cm.id == id && cm.config.equals(config) && cm.site.equals(site) && cm.rssLink.equals(rssLink);
    }
}