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
    public static final int MYSQL_DUPLICATE_PK = 1062;
    private Connection connection;
    private boolean init = false;

    private String createDatabaseQuery;


    public void init() throws SQLException {
        if (init) {
            return;
        }

        createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS " + Config.DATABASE_NAME +
                " CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci";
        createDatabases();
        init = true;
    }

    private void createDatabases() throws SQLException {

        Statement st = null;
        try {
            st = connection.createStatement();
            st.executeUpdate(createDatabaseQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Exception in createDatabase closing the connection.");
            throw e;
        }
        finally{
            st.close();
        }

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

    public Connection getConnection() {
        return connection;
    }
}
