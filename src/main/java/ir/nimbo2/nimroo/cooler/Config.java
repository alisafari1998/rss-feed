package ir.nimbo2.nimroo.cooler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private Config(){}

    private static String DATABASE_NAME = "rssfeed";
    private static String DATABASE_USER = "root";
    private static String DATABASE_PASSWORD = "boogh";
    private static String MY_SQL_CONNECTION_ADDRESS = "jdbc:mysql://localhost/"
            + "?useUnicode=yes&characterEncoding=UTF-8";
    private static short DATABASE_CONNECTION_POOL_MIN = 5;
    private static short DATABASE_CONNECTION_POOL_MAX = 15;
    private static short RSS_EXECUTOR_SERVICE_SIZE = 5;
    private static short SITE_EXECUTOR_SERVICE_SIZE = 10;

    public static void load() {
        String appConfigPath = "app.properties";
        Properties properties = new Properties();

        try(FileInputStream fis = new FileInputStream(appConfigPath)) {
            properties.load(fis);
            Config.DATABASE_USER = properties.getProperty("database.username");
            Config.DATABASE_PASSWORD = properties.getProperty("database.password");
            Config.MY_SQL_CONNECTION_ADDRESS = properties.getProperty("database.address");

            Config.DATABASE_CONNECTION_POOL_MIN = Short.parseShort(properties.getProperty("database.pool.min"));
            Config.DATABASE_CONNECTION_POOL_MAX = Short.parseShort(properties.getProperty("database.pool.max"));

            Config.RSS_EXECUTOR_SERVICE_SIZE = Short.parseShort(properties.getProperty("rss.pool.size"));
            Config.SITE_EXECUTOR_SERVICE_SIZE = Short.parseShort(properties.getProperty("site.pool.size"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String getDatabaseName() {
        return DATABASE_NAME;
    }

    public static String getDatabaseUser() {
        return DATABASE_USER;
    }

    public static String getDatabasePassword() {
        return DATABASE_PASSWORD;
    }

    public static String getMySqlConnectionAddress() {
        return MY_SQL_CONNECTION_ADDRESS;
    }

    public static short getDatabaseConnectionPoolMin() {
        return DATABASE_CONNECTION_POOL_MIN;
    }

    public static short getDatabaseConnectionPoolMax() {
        return DATABASE_CONNECTION_POOL_MAX;
    }

    public static short getRssExecutorServiceSize() {
        return RSS_EXECUTOR_SERVICE_SIZE;
    }

    public static short getSiteExecutorServiceSize() {
        return SITE_EXECUTOR_SERVICE_SIZE;
    }
}
