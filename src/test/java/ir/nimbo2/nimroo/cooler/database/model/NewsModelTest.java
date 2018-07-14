package ir.nimbo2.nimroo.cooler.database.model;

import ir.nimbo2.nimroo.cooler.Config;
import ir.nimbo2.nimroo.cooler.database.DatabaseConnection;
import org.junit.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class NewsModelTest {

    static DatabaseConnection dc = new DatabaseConnection();
    NewsModel news;

    @BeforeClass
    public static void init() throws Exception {
        Config.DATABASE_NAME += System.currentTimeMillis();
        dc.init();
    }

    @Before
    public void beforeEach() throws Exception {
        news = new NewsModel();
        news.init(dc.getConnection());
        news.createTable();
    }

    @Test
    public void createTableTest() {

        Statement st = null;

        try {
            st = dc.getConnection().createStatement();
            st.executeQuery("select * from " + Config.DATABASE_NAME+ ".news");
        } catch (SQLException e) {
            e.printStackTrace();
            assert false;
        } catch (Exception e) {
            assert false;
        }

        assert true;
    }

    @Test
    public void insertTest() throws Exception {

        fillDummyData(news);
        news.insert();

        Statement loadById = dc.getConnection().createStatement();
        ResultSet result = loadById.executeQuery("SELECT * FROM "+ Config.DATABASE_NAME + ".news" + " WHERE id="+news.id);

        if (result.next()) {
            assertEquals(result.getLong("id"), news.id);
            assertEquals(result.getString("title"), news.title);
            assertEquals(result.getString("link"), news.link);
            assertEquals(result.getString("description"), news.description);
            assertEquals(result.getDate("publish_date"), news.publishDate);
            assertEquals(result.getString("news_body"), news.newsBody);
        }
        else {
            Assert.assertFalse("Item with id: "+news.id+ " not found.", true);
        }
    }

    /**
     * TODO This is dependent test.
     */
    @Test
    public void loadTest() {

        NewsModel inserted = new NewsModel();
        inserted.init(dc.getConnection());
        fillDummyData(inserted);
        inserted.insert();

        news.id = inserted.id;
        news.load();

        assertEquals(news, inserted);

    }

    @AfterClass
    public static void cleanUp() throws SQLException {

        Statement st = dc.getConnection().createStatement();
        st.execute("DROP DATABASE IF EXISTS " + Config.DATABASE_NAME);
    }

    public void fillDummyData(NewsModel model) {

        String tmp = "_(سلام ؟!)ـ" + System.currentTimeMillis();
        model.title = "title" + tmp;
        model.link = "link" + tmp;
        model.description = "description" + tmp;
        model.publishDate = Date.valueOf(LocalDate.now());
        model.newsBody = "body" + tmp;

    }

}
