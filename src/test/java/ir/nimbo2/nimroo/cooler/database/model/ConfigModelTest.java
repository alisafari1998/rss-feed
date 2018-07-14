package ir.nimbo2.nimroo.cooler.database.model;

import ir.nimbo2.nimroo.cooler.Config;
import ir.nimbo2.nimroo.cooler.database.DatabaseConnection;
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
        configModel.init(dc.getConnection());
        configModel.createTable();
    }

    @Before
    public void setUp() throws Exception {
        configModel = new ConfigModel();
        configModel.init(dc.getConnection());
    }

    @Test
    public void createTableTest() {

        Statement st = null;

        try {
            st = dc.getConnection().createStatement();
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
    public void insertTest() {

        fillWithDummyData(configModel);
        configModel.insert();
        dbContent.add(configModel);
        ConfigModel inserted = new ConfigModel();
        try {
            Statement st = dc.getConnection().createStatement();
            ResultSet result = st.executeQuery("SELECT * FROM " + Config.DATABASE_NAME + ".config"
                    + " WHERE id="+configModel.id);

            if (result.next()) {
                inserted.id = result.getLong(1);
                inserted.site = result.getString(2);
                inserted.rssLink = result.getString(3);
                inserted.config = result.getString(4);

                assertEquals(inserted, configModel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * TODO This is dependent test.
     */
    @Test
    public void load() throws Exception {

      ConfigModel inserted = new ConfigModel();
      inserted.init(dc.getConnection());
      fillWithDummyData(inserted);
      inserted.insert();
      dbContent.add(inserted);

      configModel.id = inserted.id;
      configModel.load();
      assertEquals(configModel, inserted);

    }

    /**
     * TODO This is dependent test.
     */
    @Test
    public void loadAllTest() {

        ConfigModel toInsert;
        for (int i = 0; i < 10; i++) {
            toInsert = new ConfigModel();
            toInsert.init(dc.getConnection());
            fillWithDummyData(toInsert);
            toInsert.insert();
            dbContent.add(toInsert);
        }

        ConfigModel configModel = new ConfigModel();
        configModel.init(dc.getConnection());
        List<ConfigModel> configs = configModel.loadAll();
        assertEquals(configs.size(), dbContent.size());
        assertEquals(configs, dbContent);
    }

    private void fillWithDummyData(ConfigModel cm) {
        String tmp = "_" + System.currentTimeMillis();

        cm.rssLink = "link" + tmp;
        cm.site = "site" + tmp;
        cm.config = "config" + tmp;
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Statement st = dc.getConnection().createStatement();
        st.execute("DROP DATABASE IF EXISTS " + Config.DATABASE_NAME);
    }
}
