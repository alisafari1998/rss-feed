package ir.nimbo2.nimroo.cooler.database.model;

import ir.nimbo2.nimroo.cooler.Config;
import ir.nimbo2.nimroo.cooler.database.DatabaseConnection;
import ir.nimbo2.nimroo.cooler.database.UnexpectedSQLBehaviorException;
import ir.nimbo2.nimroo.cooler.database.repository.ConfigRepository;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.List;
import static org.junit.Assert.assertEquals;

public class ConfigModelTest {

    ConfigModel configModel;
    static ConfigRepository configRepository;
    static DatabaseConnection dc;
    ArrayList<ConfigModel> dbContent = new ArrayList<>();
    static String databaseName = Config.getDatabaseName() + "_config_model_test";
    @BeforeClass
    public static void connectToDB() throws Exception {
        dc = DatabaseConnection.getDatabaseConnection();
        dc.init();
        Statement statement = dc.getConnection().createStatement();
        statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName +
                " CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");

        configRepository = new ConfigRepository(databaseName);
        configRepository.createConfigTable();
    }

    @Before
    public void setUp() throws Exception {
        configModel = new ConfigModel();
    }

    @Test
    public void createTableTest() throws SQLException {
        try (Statement st = dc.getConnection().createStatement()) {
            st.executeQuery("select * from "+ databaseName + ".config");
        } catch (SQLException e) {
            e.printStackTrace();
            assert false;
        } catch (Exception e) {
            assert false;
        }

        assert true;
    }

    @Test
    public void insertTest() throws SQLException, UnexpectedSQLBehaviorException {
        configModel = getDummyModel();
        configModel.setId(configRepository.insertConfig(configModel));
        dbContent.add(configModel);
        ConfigModel inserted = new ConfigModel();
        Statement st = dc.getConnection().createStatement();
        ResultSet result = st.executeQuery("SELECT * FROM " + databaseName +
                ".config" + " WHERE id=" + configModel.getId());

        if (result.next()) {
            inserted.setId(result.getLong(1));
            inserted.setSite(result.getString(2));
            inserted.setRSSLink(result.getString(3));
            inserted.setConfig(result.getString(4));
            inserted.setDateConfig(result.getString("date_config"));

            assertEquals(inserted, configModel);
        }
        else {
            assert false;
        }

    }

    /**
     * TODO This is dependent test.
     */
    @Test
    public void load() throws Exception {
      ConfigModel inserted = getDummyModel();
      inserted.setId(configRepository.insertConfig(inserted));
      dbContent.add(inserted);

      configModel = configRepository.loadConfig(inserted.getId());

      assertEquals(configModel, inserted);
    }

    /**
     * TODO This is dependent test.
     */
    @Test
    public void loadAllTest() throws SQLException, UnexpectedSQLBehaviorException {
        ConfigModel toInsert;
        for (int i = 0; i < 10; i++) {
            toInsert = getDummyModel();
            toInsert.setId(configRepository.insertConfig(toInsert));
            dbContent.add(toInsert);
        }

        List<ConfigModel> configs = configRepository.loadAllConfigs();
        assertEquals(configs.size(), dbContent.size());
        assertEquals(configs, dbContent);
    }

    @Test
    public void updateLatestNewsTest() throws SQLException, UnexpectedSQLBehaviorException {
        ConfigModel cm = getDummyModel();
        cm.setId(configRepository.insertConfig(cm));
        cm.setLatestNews("booooo");

        configRepository.updateLatestNews(cm);

        ConfigModel tmp = configRepository.loadConfig(cm.getId());
        assertEquals(tmp, cm);
    }

    @Test
    public void loadLatestNewsTest() throws SQLException, UnexpectedSQLBehaviorException {
        ConfigModel cm = getDummyModel();
        cm.setLatestNews("bam bam bam");
        cm.setId(configRepository.insertConfig(cm));
        configRepository.updateLatestNews(cm);
        ConfigModel tmp = configRepository.loadLatestNews(cm.getId());
        assertEquals(tmp.getLatestNews(), cm.getLatestNews());
    }

    public static ConfigModel getDummyModel() {
        ConfigModel cm = new ConfigModel();
        String tmp = "_" + System.currentTimeMillis();
        cm.setRSSLink("link" + tmp);
        cm.setSite("site" + tmp);
        cm.setConfig("config" + tmp);
        cm.setDateConfig("EEE, dd MMM yyyy HH:mm:ss zzz");
        return cm;
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Statement statement = dc.getConnection().createStatement();
        statement.execute("DROP DATABASE IF EXISTS " + databaseName);
    }
}
