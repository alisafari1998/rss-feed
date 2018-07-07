package ir.nimbo2.nimroo.cooler;


import ir.nimbo2.nimroo.cooler.network.HtmlReader;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class HtmlReaderTest {
    @Test
    public void htmlStringTest(){
        String s = HtmlReader.htmlString("http://example.com");
        System.out.println(s);
    }
}
