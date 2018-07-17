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
    ConfigRepository repository;
    static DatabaseConnection dc;
    ArrayList<ConfigModel> dbContent = new ArrayList<>();

    @BeforeClass
    public static void connectToDB() throws Exception {
        dc = DatabaseConnection.getDatabaseConnection();
	    dc.setupNewTestDatabase("config_model_test");
        ConfigRepository.getRepository().createConfigTable();
    }

    @Before
    public void setUp() throws Exception {
        configModel = new ConfigModel();
        repository = ConfigRepository.getRepository();
    }

    @Test
    public void createTableTest() throws SQLException {

        try (Statement st = dc.getConnection().createStatement()){
            st.executeQuery("select * from "+ dc.getDatabaseName() + ".config");
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
        configModel.setId(repository.insertConfig(configModel));
        dbContent.add(configModel);
        ConfigModel inserted = new ConfigModel();
        Statement st = dc.getConnection().createStatement();
        ResultSet result = st.executeQuery("SELECT * FROM " + dc.getDatabaseName() +
                ".config" + " WHERE id=" + configModel.getId());

        if (result.next()) {
            inserted.setId(result.getLong(1));
            inserted.setSite(result.getString(2));
            inserted.setRSSLink(result.getString(3));
            inserted.setConfig(result.getString(4));

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
      inserted.setId(repository.insertConfig(inserted));
      dbContent.add(inserted);

      configModel = repository.loadConfig(inserted.getId());

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
            toInsert.setId(repository.insertConfig(toInsert));
            dbContent.add(toInsert);
        }

        List<ConfigModel> configs = repository.loadAllConfigs();
        assertEquals(configs.size(), dbContent.size());
        assertEquals(configs, dbContent);
    }

    public static ConfigModel getDummyModel() {
        ConfigModel cm = new ConfigModel();
        String tmp = "_" + System.currentTimeMillis();
        cm.setRSSLink("link" + tmp);
        cm.setSite("site" + tmp);
        cm.setConfig("config" + tmp);
        return cm;
    }

    @AfterClass
    public static void tearDown() throws Exception {
        dc.destroyTestDatabase();
    }
}
