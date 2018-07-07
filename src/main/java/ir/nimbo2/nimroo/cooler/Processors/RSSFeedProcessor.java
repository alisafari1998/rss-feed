package ir.nimbo2.nimroo.cooler.Processors;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RSSFeedProcessor {

    private String content;
    private List<HashMap<String,String>> data = new ArrayList<>();

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
                HashMap<String, String> tmp = new HashMap<>();
                for (int j = 0; j < nl.item(i).getChildNodes().getLength(); j++) {
                    String key = nl.item(i).getChildNodes().item(j).getNodeName();

                    if (key.equals("title") || key.equals("link") || key.equals("description") || key.equals("pubDate"))
                        tmp.put(key, nl.item(i).getChildNodes().item(j).getTextContent());
                }
                data.add(tmp);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<HashMap<String, String>> getResults() {
        return data;
    }
}
