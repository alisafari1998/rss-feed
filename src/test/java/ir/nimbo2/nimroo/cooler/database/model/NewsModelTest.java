package ir.nimbo2.nimroo.cooler.database.model;

import ir.nimbo2.nimroo.cooler.Config;
import ir.nimbo2.nimroo.cooler.database.DatabaseConnection;
import ir.nimbo2.nimroo.cooler.database.repository.NewsRepository;
import org.junit.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;

public class NewsModelTest {

    static DatabaseConnection dc;
    NewsModel news;
    NewsRepository repository;

    @BeforeClass
    public static void init() throws Exception {
        Config.DATABASE_NAME += System.currentTimeMillis();
        dc = new DatabaseConnection();
        dc.init();
        NewsRepository.getRepository().createNewsTable();
    }

    @Before
    public void beforeEach() throws Exception {
        news = new NewsModel();
        repository = NewsRepository.getRepository();
    }

    @Test
    public void createTableTest() {
        try {
            repository.createNewsTable();

            Statement st = dc.getConnection().createStatement();
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

        news = getDummyModel();
        news.setId(repository.insertNews(news));

        Statement loadById = dc.getConnection().createStatement();
        ResultSet result = loadById.executeQuery("SELECT * FROM "+ Config.DATABASE_NAME + ".news" +
                " WHERE id="+news.getId());

        if (result.next()) {
            assertEquals(result.getLong("id"), news.getId());
            assertEquals(result.getString("title"), news.getTitle());
            assertEquals(result.getString("link"), news.getLink());
            assertEquals(result.getString("description"), news.getDescription());
            assertEquals(result.getTimestamp("publish_date"), news.getPublishDate());
            assertEquals(result.getString("news_body"), news.getNewsBody());
        }
        else {
            Assert.assertFalse("Item with id: " + news.getId() + " not found.", true);
        }
    }

    /**
     * TODO This is dependent test.
     */
    @Test
    public void loadTest() throws Exception {

        NewsModel inserted = getDummyModel();;
        inserted.setId(repository.insertNews(inserted));

        news = repository.loadNews(inserted.getId());
        assertEquals(news, inserted);
    }

    @AfterClass
    public static void cleanUp() throws SQLException {
        Statement st = dc.getConnection().createStatement();
        st.execute("DROP DATABASE IF EXISTS " + Config.DATABASE_NAME);
    }

    public NewsModel getDummyModel() {

        NewsModel model = new NewsModel();
        String tmp = "_(سلام ؟!)ـ" + System.currentTimeMillis();
        model.setTitle("title" + tmp);
        model.setLink("link" + tmp);
        model.setDescription("description" + tmp);
        model.setPublishDate(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
        model.setNewsBody("body" + tmp);
        return model;

    }

}
