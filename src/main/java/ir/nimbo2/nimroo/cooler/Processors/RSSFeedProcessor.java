package ir.nimbo2.nimroo.cooler.Processors;

import ir.nimbo2.nimroo.cooler.database.model.NewsModel;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RSSFeedProcessor {

    private String content;
    private URL url;
    private List<NewsModel> data = new ArrayList<>();
    private final DateFormat [] DATE_FORMATS = {
            new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z"),
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz"),
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss"),
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss'z'")
    };

    public RSSFeedProcessor(String content) {
        this.content = content;
    }

    public RSSFeedProcessor(URL link) {
        this.url = link;
    }

    /**
     * It processes the data inside rss feed and creates a List of HashMaps witch is accessible from getResults method.
     */
    public void process() throws IOException {
        Document document = null;

        if (url != null) {
            document = Jsoup.connect(String.valueOf(url)).parser(Parser.xmlParser()).get();
        } else {
            document = Jsoup.parse(content, "", Parser.xmlParser());
        }

        Elements items = document.getElementsByTag("item");
        for(int i = 0; i < items.size(); i++) {
            NewsModel tmp = new NewsModel();
            for(int j = 1; j < items.get(i).getAllElements().size(); j++) {
                String key = items.get(i).getAllElements().get(j).tagName();
                String value = items.get(i).getAllElements().get(j).text();

                switch (key) {
                    case ("title"):
                        tmp.setTitle(value);
                        break;
                    case ("link"):
                        tmp.setLink(value);
                        break;
                    case ("description"):
                        tmp.setDescription(value);
                        break;
                    case ("pubdate"):
                        tmp.setPublishDate(convertDate(value));
                        break;
                    case("pubDate"):
                        tmp.setPublishDate(convertDate(value));
                        break;
                }

            }
            data.add(tmp);
        }

    }

    private Timestamp convertDate(String date) {
        for(DateFormat df: DATE_FORMATS) {
            try {
                df.parse(date);
                return new Timestamp(df.parse(date).getTime());
            } catch (ParseException e){}
        }
        return null;
    }

    public List<NewsModel> getResults() {
        return data;
    }
}
