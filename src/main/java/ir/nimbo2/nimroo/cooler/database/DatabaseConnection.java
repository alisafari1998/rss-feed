package ir.nimbo2.nimroo.cooler.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import ir.nimbo2.nimroo.cooler.Config;

import java.beans.PropertyVetoException;
import java.sql.*;
import javax.naming.NamingException;

public class DatabaseConnection {
    private static DatabaseConnection instance = new DatabaseConnection();

    private String createDatabaseQuery;
    private String databaseName = Config.getDatabaseName();

    private ComboPooledDataSource cpds;

    public DatabaseConnection()  {

    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void init() throws SQLException, NamingException, PropertyVetoException {

        cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass("com.mysql.jdbc.Driver"); //loads the jdbc driver
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setJdbcUrl(Config.getMySqlConnectionAddress());
        cpds.setUser(Config.getDatabaseUser());
        cpds.setPassword(Config.getDatabasePassword());
        cpds.setMinPoolSize(Config.getDatabaseConnectionPoolMin());
        cpds.setMaxPoolSize(Config.getDatabaseConnectionPoolMax());

        createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS " + databaseName +
                " CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci";

        createDatabases();
    }

    private void createDatabases() throws SQLException {

        try (Statement st = getConnection().createStatement()) {
            st.executeUpdate(createDatabaseQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(createDatabaseQuery);
            throw e;
        }
    }

    public Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }

    public static DatabaseConnection getDatabaseConnection() {
        return instance;
    }
}
