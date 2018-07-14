package ir.nimbo2.nimroo.cooler.database.model;

import ir.nimbo2.nimroo.cooler.Config;
import ir.nimbo2.nimroo.cooler.database.DatabaseConnection;
import ir.nimbo2.nimroo.cooler.database.UnexpectedSQLBehaviorException;
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

    static DatabaseConnection dc = new DatabaseConnection();
    static ConfigModel configModel;
    ArrayList<ConfigModel> dbContent = new ArrayList<>();

    @BeforeClass
    public static void connectToDB() throws Exception {
        configModel = new ConfigModel();
        Config.DATABASE_NAME += System.currentTimeMillis();
        dc.init();
    }

    @Before
    public void setUp() throws Exception {
        configModel = new ConfigModel();
    }

    @Test
    public void createTableTest() {

        try {
            Statement st = dc.getConnection().createStatement();
            st.executeQuery("select * from "+Config.DATABASE_NAME + ".config");
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
        configModel.setId(dc.insertConfig(configModel));
        dbContent.add(configModel);
        ConfigModel inserted = new ConfigModel();
        Statement st = dc.getConnection().createStatement();
        ResultSet result = st.executeQuery("SELECT * FROM " + Config.DATABASE_NAME +
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
      inserted.setId(dc.insertConfig(inserted));
      dbContent.add(inserted);

      configModel = dc.loadConfig(inserted.getId());

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
            toInsert.setId(dc.insertConfig(toInsert));
            dbContent.add(toInsert);
        }

        List<ConfigModel> configs = dc.loadAllConfigs();
        assertEquals(configs.size(), dbContent.size());
        assertEquals(configs, dbContent);
    }

    private ConfigModel getDummyModel() {
        ConfigModel cm = new ConfigModel();
        String tmp = "_" + System.currentTimeMillis();
        cm.setRSSLink("link" + tmp);
        cm.setSite("site" + tmp);
        cm.setConfig("config" + tmp);
        return cm;
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Statement st = dc.getConnection().createStatement();
        st.execute("DROP DATABASE IF EXISTS " + Config.DATABASE_NAME);
    }
}
