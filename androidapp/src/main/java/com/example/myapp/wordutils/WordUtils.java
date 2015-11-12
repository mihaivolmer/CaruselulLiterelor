package com.example.myapp.wordutils;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Laura on 11.11.15.
 */
public class WordUtils {
    private static ArrayList<String> responseWordsList = new ArrayList<String>();


    private static boolean isWordInList(String responseWord, ArrayList<String> responseWords) {
        for (String s : responseWords) {
            if (s.equals(responseWord)) {
                return true;
            }
        }
        return false;
    }

    //validates returned word
    public static boolean isFinalWordOk(String word, String responseWord) {

        // checks if response word is the same with the one entered
        // (Player does not use diacritics)-->checks if response word without diacritics is the same with
        // the one entered

        word = word.toUpperCase();

        if (responseWord.equals(word) || responseWord.equals(convertWord(word)) || word.equals(convertWord(responseWord))) {

            boolean wordExistsInList = isWordInList(responseWord, responseWordsList);

            if (!wordExistsInList) {
                responseWordsList.add(responseWord);
                Log.i("words list", responseWordsList.toString());
                Log.i("Word " + word, " is OK");

                return true;
            }
        }
        return false;
    }

    //checks if the entered word uses only once each letter given
    //EXAMPLE: H T M D E C X B L,  word BEBE is NOT ok because there is one B and one E
    public static boolean isEnteredWordOk(List<Character> letters, String enteredWord) {

        for (int i = 0; i < enteredWord.length(); i++) {
            //!!! CASE SENSITIVE
            Character currentLetter = enteredWord.toUpperCase().charAt(i);
            if (!letters.contains(currentLetter)) {
                return false;
            } else {
                //remove letter from the list
                letters.remove(currentLetter);
            }
        }
        return true;
    }

    //removes diacritics
    public static String convertWord(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    //remove accents
    public static String convertWordNoAccents(String str) {
//        StringUtils.stripAccents("ÉéÁáÍíÓóÚú");

        String finalString ="";
        for (int i = 0; i < str.length(); i++) {
            Character currentLetter = str.charAt(i);
            switch(currentLetter) {
                case 'É':
                    finalString = finalString.concat("E");
                    break;
                case 'Á':
                    finalString = finalString.concat("A");
                    break;
                case 'Ó':
                    finalString = finalString.concat("O");
                    break;
                case 'Ú':
                    finalString = finalString.concat("U");
                    break;
                case 'Í':
                    finalString =finalString.concat("I");
                    break;
                case 'Ắ':
                    finalString =finalString.concat("Ă");
                    break;
                default:
                    finalString = finalString.concat(String.valueOf(currentLetter));
            }
        }
        return finalString;
    }

    //retrieves the word from the http response
    public static String getWord(HttpResponse httpResponse) throws IOException {
        String responseBody = getWordFromResponse(httpResponse);

        //parse html to get the word: the word is <div> <txt> <p> <span> <b>
        Document document = Jsoup.parse(responseBody);

        Element divElement = document.select("div.txt").first();
        Element pElement = divElement.select("p").first();
        Element spanElement = pElement.select("span").first();
        Element bElement = spanElement.select("b").first();
        String[] array = bElement.text().split("[-[0-9],\\s]+");

        String elementString = convertWordNoAccents(array[0]);
        Log.i("elementString", elementString);

        return elementString.toUpperCase();
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
