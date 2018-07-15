package ir.nimbo2.nimroo.cooler.Processors;

import ir.nimbo2.nimroo.cooler.database.model.ConfigModel;
import ir.nimbo2.nimroo.cooler.database.model.NewsModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class RSSFeedProcessorTest {

    private final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private RSSFeedProcessor processor;

    private final String titles [] = {"وقوع زمین لرزه ۳.۵ ریشتری در «تیتکانلو»ی خراسان\u200Cشمالی",
            "مرحله یک\u200Cچهارم جام\u200Cجهانی روسیه؛ دیدار تیم\u200Cهای سوئد و انگلستان",
            "واکنش هرناندز به تقابل با هازارد: ما بودیم که مسی را حذف کردیم"
    };

    private final String links [] = {
            "https://www.isna.ir/news/97041609155/وقوع-زمین-لرزه-۳-۵-ریشتری-در-تیتکانلو-ی-خراسان-شمالی",
            "https://www.isna.ir/photo/97041609153/مرحله-یک-چهارم-جام-جهانی-روسیه-دیدار-تیم-های-سوئد-و-انگلستان",
            "https://www.isna.ir/news/97041609151/واکنش-هرناندز-به-تقابل-با-هازارد-ما-بودیم-که-مسی-را-حذف-کردیم"
    };

    private final String descriptions [] = {
            "زمین لرزه ۳.۵ ریشتری تیتکانلو در استان خراسان شمالی را لرزاند.",
            "دیدار دو تیم فوتبال سوئد و انگلستان شنبه ۱۶ تیرماه در شهر کازان روسیه آغاز شد.",
            "مدافع تیم ملی فرانسه به رقابت با ادن هازارد واکنش نشان داد."
    };

    private final Date pubDate [] = {
            new Date(DATE_FORMAT.parse("Sat, 07 Jul 2018 14:57:13 GMT").getTime()),
            new Date(DATE_FORMAT.parse("Sat, 07 Jul 2018 14:52:13 GMT").getTime()),
            new Date(DATE_FORMAT.parse("Sat, 07 Jul 2018 14:51:25 GMT").getTime()),
    };

    public RSSFeedProcessorTest() throws ParseException {
    }


    @Before
    public void setUp() throws IOException {

        String text = new String(Files.readAllBytes(Paths.get("resources/isna-rss.xml")), StandardCharsets.UTF_8);
        processor = new RSSFeedProcessor(text);
        processor.process();

    }

    @Test
    public void testProcessingProcedureTitlesCorrectness() {

        assertNotEquals(processor.getResults().size(), 0);
        for (int i = 0; i < processor.getResults().size(); i++) {
            NewsModel item = processor.getResults().get(i);
            assertEquals(item.getTitle(), titles[i]);
        }
    }

    @Test
    public void testProcessingProcedureLinksCorrectness() {

        assertNotEquals(processor.getResults().size(), 0);
        for (int i = 0; i < processor.getResults().size(); i++) {
            NewsModel item = processor.getResults().get(i);
            assertEquals(item.getLink(), links[i]);
        }
    }

    @Test
    public void testProcessingProcedureDescriptionsCorrectness() {

        assertNotEquals(processor.getResults().size(), 0);
        for (int i = 0; i < processor.getResults().size(); i++) {
            NewsModel item = processor.getResults().get(i);
            assertEquals(item.getDescription(), descriptions[i]);
        }
    }

    @Test
    public void testProcessingProcedurePubDateCorrectness() {

        assertNotEquals(processor.getResults().size(), 0);
        for (int i = 0; i < processor.getResults().size(); i++) {
            NewsModel item = processor.getResults().get(i);
            assertEquals(item.getPublishDate(), pubDate[i]);
        }
    }
}
