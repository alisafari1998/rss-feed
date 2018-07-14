package ir.nimbo2.nimroo.cooler.network;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.nio.charset.StandardCharsets;

public class HtmlReader {

    public static String getHtmlString(String url) {



        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL urlObject = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection)urlObject.openConnection();

//            URLConnection urlConnection = urlObject.openConnection();

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                System.out.println(inputLine);
                stringBuilder.append(inputLine);
                stringBuilder.append("\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
        }

        //System.out.println(stringBuilder);
        return stringBuilder.toString();
    }
}
