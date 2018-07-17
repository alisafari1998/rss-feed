package ir.nimbo2.nimroo.cooler.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import ir.nimbo2.nimroo.cooler.Config;

import java.beans.PropertyVetoException;
import java.sql.*;
import javax.naming.NamingException;

public class DatabaseConnection {
    public static final int MYSQL_DUPLICATE_PK = 1062;
    private static DatabaseConnection instance = new DatabaseConnection();
    private boolean init = false;

    private String createDatabaseQuery;
    private ComboPooledDataSource cpds;

    public DatabaseConnection()  {

    }

    public void init() throws SQLException, NamingException, PropertyVetoException {
        if (init) {
            return;
        }

        cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass("com.mysql.jdbc.Driver"); //loads the jdbc driver
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setJdbcUrl(Config.MY_SQL_CONNECTION_ADDRESS);
        cpds.setUser(Config.DATABASE_USER);
        cpds.setPassword(Config.DATABASE_PASSWORD);

        createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS " + Config.DATABASE_NAME +
                " CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci";

        createDatabases();
        init = true;
    }

    private void createDatabases() throws SQLException {

        try (Statement st = getConnection().createStatement()) {
            System.err.println(st);
            st.executeUpdate(createDatabaseQuery);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Exception in createDatabase closing the connection.");
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
