package ir.nimbo2.nimroo.cooler.database.repositories;

import ir.nimbo2.nimroo.cooler.Config;
import ir.nimbo2.nimroo.cooler.database.DatabaseConnection;
import ir.nimbo2.nimroo.cooler.database.UnexpectedSQLBehaviorException;
import ir.nimbo2.nimroo.cooler.database.model.ConfigModel;
import ir.nimbo2.nimroo.cooler.database.model.ConfigRepositoryTest;
import ir.nimbo2.nimroo.cooler.database.model.NewsModel;
import ir.nimbo2.nimroo.cooler.database.repository.ConfigRepository;
import ir.nimbo2.nimroo.cooler.database.repository.NewsRepository;
import org.junit.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NewsRepositoryTest {

    static DatabaseConnection dc;
    NewsModel news;
    static final String databaseName = Config.getDatabaseName() + "_" + "news_model_test";
    static NewsRepository newsRepository = new NewsRepository(databaseName);
    static ConfigRepository configRepository = new ConfigRepository(databaseName);
    ConfigModel dummyConfig;

    @BeforeClass
    public static void init() throws Exception {
        dc = DatabaseConnection.getDatabaseConnection();
        dc.init();
        Statement statement = dc.getConnection().createStatement();
        statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName +
                " CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");

        configRepository.createConfigTable();
        newsRepository.createNewsTable();

        //Setup for 10LastTitleTest_Site.
        ConfigModel tmp = new ConfigModel();
        tmp.setSite("10LastTitleTest_Site");
        tmp.setId(configRepository.insertConfig(tmp));
        for (int i = 0; i < 20; ++i) {
            NewsModel model = NewsRepositoryTest.getDummyModel();
            model.setTitle(i+"_10LastTitle");
            model.getPublishDate().setTime(model.getPublishDate().getTime() - i * 10000);
            model.setConfigId(tmp.getId());
            newsRepository.insertNews(model);
        }

        //Setup for countInDate
        tmp = new ConfigModel();
        tmp.setSite("CountSiteInDate");
        tmp.setId(configRepository.insertConfig(tmp));
        for(int i = -3; i < 4; i++) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.add(Calendar.DAY_OF_MONTH, i);
            NewsModel model = NewsRepositoryTest.getDummyModel();
            model.setConfigId(tmp.getId());
            model.setPublishDate(new Timestamp(c.getTimeInMillis()));
            for (int j = 0; j < i+5; ++j) {
                newsRepository.insertNews(model);
            }
        }
    }

    @Before
    public void beforeEach() throws Exception {
        news = new NewsModel();
        dummyConfig = ConfigRepositoryTest.getDummyModel();
        dummyConfig.setId(configRepository.insertConfig(dummyConfig));
    }

    @Test
    public void createTableTest() {
        try {
            newsRepository.createNewsTable();

            Statement st = dc.getConnection().createStatement();
            st.executeQuery("select * from " + databaseName + ".news");
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
        news.setConfigId(dummyConfig.getId());
        news.setId(newsRepository.insertNews(news));

        Statement loadById = dc.getConnection().createStatement();
        ResultSet result = loadById.executeQuery("SELECT * FROM "+ databaseName + ".news" +
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

        NewsModel inserted = getDummyModel();
        inserted.setConfigId(dummyConfig.getId());
        inserted.setId(newsRepository.insertNews(inserted));

        news = newsRepository.loadNews(inserted.getId());
        assertTrue(areEquals(news, inserted));
    }

    @Test
    public void loadLast10NewsTest() throws SQLException {
        List<NewsModel> models = newsRepository.loadLast10NewsBySite("10LastTitleTest_Site");

        for(int i = 0; i < 10; i++) {
            assertEquals(models.get(i).getTitle(), i+"_10LastTitle");
            Assert.assertNotNull(models.get(i).getDescription());
            Assert.assertNotNull(models.get(i).getPublishDate());
            Assert.assertNotNull(models.get(i).getNewsBody());
        }

    }

    @Test
    public void countNewsBySiteInDate() throws SQLException, UnexpectedSQLBehaviorException {
        for (int i = -3; i < 4; ++i) {
            long count = newsRepository.countNewsBySiteInDate("CountSiteInDate",
                    Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(i)),
                    Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(i+1)));
            assertEquals(i+5, count);
        }
    }

    @AfterClass
    public static void cleanUp() throws SQLException {
        Statement statement = dc.getConnection().createStatement();
        statement.execute("DROP DATABASE IF EXISTS " + databaseName);
    }

    /**
     * @implNote this function doesn't do anything about config_id since it's a foreign key...
     * @return
     */
    public static NewsModel getDummyModel() {

        NewsModel model = new NewsModel();
        String tmp = "_(سلام ؟!)ـ" + System.currentTimeMillis();
        model.setTitle("title" + tmp);
        model.setLink("link" + tmp);
        model.setDescription("description" + tmp);
        model.setPublishDate(Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
        model.setNewsBody("body" + tmp);
        return model;

    }

    public boolean areEquals(NewsModel newsModel1, NewsModel newsModel2) {
        return newsModel2.getId() == newsModel1.getId() && newsModel2.getTitle().equals(newsModel1.getTitle())
                && newsModel2.getLink().equals(newsModel1.getLink())
                && newsModel2.getPublishDate().equals(newsModel1.getPublishDate())
                && newsModel2.getNewsBody().equals(newsModel1.getNewsBody())
                && newsModel2.getConfigId() == newsModel1.getConfigId();
    }

}
