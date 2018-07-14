package ir.nimbo2.nimroo.cooler.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.nio.charset.StandardCharsets;

public class HtmlReader {

    public static String getHtmlString(String url) {



        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL urlObject = new URL(url);
            HttpsURLConnection urlConnection = (HttpsURLConnection)urlObject.openConnection();

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
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
