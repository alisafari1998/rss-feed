package ir.nimbo2.nimroo.cooler.Processors;

import ir.nimbo2.nimroo.cooler.database.model.NewsModel;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RSSFeedProcessor {

    private String content;
    private List<NewsModel> data = new ArrayList<>();
    private final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    public RSSFeedProcessor(String content) {
        this.content = content;
    }

    /**
     * It processes the data inside rss feed and creates a List of HashMaps witch is accessible from getResults method.
     */
    public void process() {

        DocumentBuilder documentBuilder;
        Document doc;
        NodeList nl;

        try {

            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = documentBuilder.parse(new InputSource(new StringReader(content)));
            doc.getDocumentElement().normalize();
            nl = doc.getElementsByTagName("item");

            for (int i = 0; i < nl.getLength(); i++) {
                NewsModel tmp = new NewsModel();
                for (int j = 0; j < nl.item(i).getChildNodes().getLength(); j++) {
                    String key = nl.item(i).getChildNodes().item(j).getNodeName();
                    String value = nl.item(i).getChildNodes().item(j).getTextContent();
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
                        case ("pubDate"):
                            tmp.setPublishDate(new java.sql.Date(DATE_FORMAT.parse(value).getTime()));
                            break;
                    }
                }
                //TODO Handle dateless sites...
                data.add(tmp);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public List<NewsModel> getResults() {
        return data;
    }
}
