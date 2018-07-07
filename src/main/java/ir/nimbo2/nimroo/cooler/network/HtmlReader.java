package ir.nimbo2.nimroo.cooler.network;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HtmlReader {
    public static String htmlString(String url) {
        /*String content = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(url));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                content += str;
            }
            bufferedReader.close();
        } catch (IOException e) {
        }
        return content;*/

        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL urlObject = new URL(url);
            URLConnection urlConnection = urlObject.openConnection();

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
        } catch (IOException e) {
        }
        return stringBuilder.toString();
    }
}
