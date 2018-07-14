package ir.nimbo2.nimroo.cooler;


import ir.nimbo2.nimroo.cooler.network.HttpRequest;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class HtmlReaderTest {
    @Test
    public void getHtmlStringTest(){
        String s = HttpRequest.getHtmlString("https://www.farsnews.com/news/13970420000614/%D8%A2%D8%AE%D8%B1%D9%8A%D9%86-%D9%81%D8%B1%D8%B5%D8%AA-%D8%AD%D8%B6%D9%88%D8%B1-%D8%AF%D8%B1-%D8%B3%D9%88%D9%BE%D8%B1-%D9%84%D9%8A%DA%AF-%D9%83%D8%A7%D8%B1%D8%A7%D8%AA%D9%87-%D8%A7%D8%B9%D9%84%D8%A7%D9%85-%D8%B4%D8%AF");
        //System.out.println(s);
    }
}
