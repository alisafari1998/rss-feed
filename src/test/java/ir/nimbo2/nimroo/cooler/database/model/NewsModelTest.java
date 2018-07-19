package ir.nimbo2.nimroo.cooler.database.model;

import ir.nimbo2.nimroo.cooler.database.DatabaseConnection;
import ir.nimbo2.nimroo.cooler.database.UnexpectedSQLBehaviorException;
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

public class NewsModelTest {

    static DatabaseConnection dc;
    NewsModel news;
    NewsRepository repository;
    ConfigModel dummyConfig;

    @BeforeClass
    public static void init() throws Exception {
        dc = DatabaseConnection.getDatabaseConnection();
        dc.setupNewTestDatabase("news_model_test");
        ConfigRepository.getRepository().init();
        NewsRepository.getRepository().init();

        ConfigRepository.getRepository().createConfigTable();
        NewsRepository.getRepository().createNewsTable();

        //Setup for 10LastTitleTest_Site.
        ConfigModel tmp = new ConfigModel();
        tmp.setSite("10LastTitleTest_Site");
        tmp.setId(ConfigRepository.getRepository().insertConfig(tmp));
        for (int i = 0; i < 20; ++i) {
            NewsModel model = NewsModelTest.getDummyModel();
            model.setTitle(i+"_10LastTitle");
            model.getPublishDate().setTime(model.getPublishDate().getTime() - i * 10000);
            model.setConfigId(tmp.getId());
            NewsRepository.getRepository().insertNews(model);
        }


        //Setup for countInDate
        tmp = new ConfigModel();
        tmp.setSite("CountSiteInDate");
        tmp.setId(ConfigRepository.getRepository().insertConfig(tmp));
        for(int i = -3; i < 4; i++) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.add(Calendar.DAY_OF_MONTH, i);
            NewsModel model = NewsModelTest.getDummyModel();
            model.setConfigId(tmp.getId());
            model.setPublishDate(new Timestamp(c.getTimeInMillis()));
            for (int j = 0; j < i+5; ++j) {
                NewsRepository.getRepository().insertNews(model);
            }
        }
    }

    @Before
    public void beforeEach() throws Exception {
        news = new NewsModel();
        repository = NewsRepository.getRepository();
        dummyConfig = ConfigModelTest.getDummyModel();
        dummyConfig.setId(ConfigRepository.getRepository().insertConfig(dummyConfig));
    }

    @Test
    public void createTableTest() {
        try {
            repository.createNewsTable();

            Statement st = dc.getConnection().createStatement();
            st.executeQuery("select * from " + dc.getDatabaseName() + ".news");
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
        news.setId(repository.insertNews(news));

        Statement loadById = dc.getConnection().createStatement();
        ResultSet result = loadById.executeQuery("SELECT * FROM "+ dc.getDatabaseName() + ".news" +
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
        inserted.setId(repository.insertNews(inserted));

        news = repository.loadNews(inserted.getId());
        assertTrue(areEquals(news, inserted));
    }

    @Test
    public void loadLast10NewsTest() throws SQLException {
        List<NewsModel> models = repository.loadLast10NewsBySite("10LastTitleTest_Site");

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
            long count = NewsRepository.getRepository().countNewsBySiteInDate("CountSiteInDate",
                    Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(i)),
                    Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(i+1)));
            assertEquals(i+5, count);
        }
    }

    @AfterClass
    public static void cleanUp() throws SQLException {
        dc.destroyTestDatabase();
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
