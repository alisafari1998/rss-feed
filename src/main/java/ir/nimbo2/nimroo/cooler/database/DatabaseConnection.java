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
    private String databaseName = Config.DATABASE_NAME;

    private ComboPooledDataSource cpds;

    public DatabaseConnection()  {

    }

    public void setupNewTestDatabase(String testPostfix) throws PropertyVetoException, NamingException, SQLException {

        if (testPostfix == null || testPostfix.isEmpty())
            testPostfix = System.currentTimeMillis() + "";

        databaseName = Config.DATABASE_NAME + "_" + testPostfix;
        init();

    }

    public void destroyTestDatabase() throws SQLException {
        if (databaseName.equals(Config.DATABASE_NAME))
            return;

        Statement st = getConnection().createStatement();
        st.execute("DROP DATABASE IF EXISTS " + databaseName);
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
        cpds.setJdbcUrl(Config.MY_SQL_CONNECTION_ADDRESS);
        cpds.setUser(Config.DATABASE_USER);
        cpds.setPassword(Config.DATABASE_PASSWORD);

        createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS " + databaseName +
                " CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci";

        createDatabases();
    }

    private void createDatabases() throws SQLException {

        try (Statement st = getConnection().createStatement()) {
            System.err.println(st);
            st.executeUpdate(createDatabaseQuery);
//            throw new SQLException();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(createDatabaseQuery);
//            System.err.println("Exception in createDatabase closing the connection.");
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
