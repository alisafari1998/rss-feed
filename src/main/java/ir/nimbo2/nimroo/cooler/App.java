package ir.nimbo2.nimroo.cooler;

import ir.nimbo2.nimroo.cooler.network.HttpRequest;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String url = "https://www.yjc.ir/fa/news/6594270/%D8%AD%D8%B6%D9%88%D8%B1-%D9%86%D8%AA%D8%A7%D9%86%DB%8C%D8%A7%D9%87%D9%88-%D8%AF%D8%B1-%D8%B1%D9%88%D8%B3%DB%8C%D9%87-%D8%AA%D8%A7%D8%AB%DB%8C%D8%B1%DB%8C-%D8%A8%D8%B1-%D9%85%D8%A7%D9%85%D9%88%D8%B1%DB%8C%D8%AA-%D8%A7%D8%B3%D8%AA%D8%B1%D8%A7%D8%AA%DA%98%DB%8C%DA%A9-%D9%85%D8%A7-%D9%86%D8%AF%D8%A7%D8%B1%D8%AF";
        String s = HttpRequest.getHtmlString(url);
        //System.out.println("booooo");
        System.out.println(s);
    }
}
