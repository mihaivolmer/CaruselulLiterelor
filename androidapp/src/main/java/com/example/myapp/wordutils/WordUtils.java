package com.example.myapp.wordutils;

import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Created by Laura on 11.11.15.
 */
public class WordUtils {

    //removes diacritics
    public static String convertWord(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    //retrieves the word fromt the http response
    public static String getWord(HttpResponse httpResponse) throws IOException {
        String responseBody = getWordFromResponse(httpResponse);

        //parse html to get the word: the word is in the title if exists
        Document document = Jsoup.parse(responseBody);
        String[] wordsRetrieved = document.title().split("\\s");

        //word needs to be without diacritics
        return convertWord(wordsRetrieved[0]);
    }

    //get text from response, UTF-8 is needed for the words with diacritics
    protected static String getWordFromResponse(HttpResponse httpResponse) throws IOException {

        InputStream inputStream = httpResponse.getEntity().getContent();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder str = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            str.append(line);
        }
        inputStream.close();

        return str.toString();
    }
}
