package ir.nimbo2.nimroo.cooler;

import ir.nimbo2.nimroo.cooler.cli.Cli;
import ir.nimbo2.nimroo.cooler.controller.Controller;
import ir.nimbo2.nimroo.cooler.database.DatabaseConnection;
import ir.nimbo2.nimroo.cooler.database.repository.ConfigRepository;
import ir.nimbo2.nimroo.cooler.database.repository.NewsRepository;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.naming.NamingException;
import java.beans.PropertyVetoException;
import java.sql.SQLException;

/**
 * Hello world!
 *
 */
public class App {
    //static Logger logger = Logger.getLogger(App.class);

    public static void main( String[] args ) {
        Config.load();
        PropertyConfigurator.configure("log4j.properties");
        //logger.error("Log4j appender configuration is successful !!");

        Cli cli = new Cli();
        try {
            DatabaseConnection.getDatabaseConnection().init();
            NewsRepository newsRepository = new NewsRepository(Config.getDatabaseName());
            newsRepository.createNewsTable();
            ConfigRepository configRepository = new ConfigRepository(Config.getDatabaseName());
            configRepository.createConfigTable();
        } catch (SQLException | NamingException | PropertyVetoException e) {
            e.printStackTrace();
        }

        Controller.getControllerInstance().start();
        cli.run();
    }
}
