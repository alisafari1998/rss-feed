package ir.nimbo2.nimroo.cooler.database;

import ir.nimbo2.nimroo.cooler.Config;
import ir.nimbo2.nimroo.cooler.database.model.ConfigModel;
import ir.nimbo2.nimroo.cooler.database.model.NewsModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static ir.nimbo2.nimroo.cooler.Config.DATABASE_PASSWORD;
import static ir.nimbo2.nimroo.cooler.Config.DATABASE_USER;
import static ir.nimbo2.nimroo.cooler.Config.MY_SQL_CONNECTION_ADDRESS;

public class DatabaseConnection {

    private static Connection connection;
    private boolean init = false;

    public boolean init() {
        if (!createDatabases())
            return false;

        NewsModel newsModel = new NewsModel();
        if (!(newsModel.init(connection) && newsModel.createTable()))
            return false;

        ConfigModel configModel = new ConfigModel();
        if (!(configModel.init(connection) && configModel.createTable()))
            return false;

        return true;
    }

    private boolean createDatabases() {

        if (init)
            return true;

        try {
            Statement st = connection.createStatement();
            st.execute("CREATE DATABASE IF NOT EXISTS " + Config.DATABASE_NAME + " CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        init = true;
        return true;
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
