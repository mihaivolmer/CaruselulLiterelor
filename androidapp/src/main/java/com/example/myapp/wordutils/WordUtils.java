package com.example.myapp.wordutils;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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


    public static void clearWordList() {
        responseWordsList.clear();
    }

    private static boolean isWordInList(String responseWord) {
        for (String s : responseWordsList) {
            if (s.equals(responseWord)) {
                return true;
            }
        }
        return false;
    }

    //validates returned word
    public static boolean isFinalWordOk(String word, String responseWord) {

        // checks if response word is the same with the one entered
        word = word.toUpperCase();

        if (responseWord.equals(word)) {

            boolean wordExistsInList = isWordInList(responseWord);

            if (!wordExistsInList) {
                //add the word with diacritics
                responseWordsList.add(word);
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
        int wordLength = enteredWord.length();

        if (isWordInList(enteredWord)) {
            return false;
        }

        //if the word contains diacritics it will be accepted
        for (int i = 0; i < wordLength; i++) {
            //!!! CASE SENSITIVE
            Character currentLetter = enteredWord.toUpperCase().charAt(i);
            Character letterWithoutDiacritics = convertWordNoDiacritics(String.valueOf(currentLetter)).charAt(0);
            if (!letters.contains(letterWithoutDiacritics)) {
                Log.i("Uses given letters", "FALSE");
                return false;
            } else {
                //remove the letter without diacritics from the list
                letters.remove(letterWithoutDiacritics);
            }
        }
        return true;
    }

    //removes diacritics
    public static String convertWordNoDiacritics(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    //remove accents
    public static String convertWordNoAccents(String str) {
        //dexonline gives definition with accents that need to be removed: ÉéÁáÍíÓóÚú

        String finalString = "";
        for (int i = 0; i < str.length(); i++) {
            Character currentLetter = str.charAt(i);
            switch (currentLetter) {
                case 'É':
                case 'é':
                    finalString = finalString.concat("E");
                    break;
                case 'Á':
                case 'á':
                    finalString = finalString.concat("A");
                    break;
                case 'Ó':
                case 'ó':
                    finalString = finalString.concat("O");
                    break;
                case 'Ú':
                case 'ú':
                    finalString = finalString.concat("U");
                    break;
                case 'Í':
                case 'í':
                    finalString = finalString.concat("I");
                    break;
                case 'Ắ':
                    finalString = finalString.concat("Ă");
                    break;
                case 'Ấ':
                    finalString = finalString.concat("Â");
                    break;
                default:
                    finalString = finalString.concat(String.valueOf(currentLetter));
            }
        }
        return finalString;
    }

    //gets all words found for a word search from dexonline
    private static List<String> getWordsForMultipleDefinitions(String responseBody) {
        //parse html to get the word: the word is <div> <txt> <p> <span> <b>
        Document document = Jsoup.parse(responseBody);

        Element divElement = document.select("div.txt").first();

        //multiple definitions
        List<String> responseWords = new ArrayList<String>();
        Elements pElements = divElement.select("p");
        int pElementsSize = pElements.size();

        for (Element element : pElements) {
            Element spanElementTemp = element.select("span").first();
            Element bElementTemp = spanElementTemp.select("b").first();
            String[] arrayTemp = bElementTemp.text().split("[́,!-[0-9],\\s]+");

            String elementString = convertWordNoAccents(arrayTemp[0]);
            responseWords.add(elementString.toUpperCase());
        }

        Log.i("spanElements", responseWords.toString());
        Log.i("spanSize", String.valueOf(pElementsSize));

        return responseWords;
    }

    //retrieves the word from the http response
    public static String getWord(HttpResponse httpResponse, String enteredWord) throws IOException {
        String responseBody = getWordFromResponse(httpResponse);

        List<String> wordDefinitionList = getWordsForMultipleDefinitions(responseBody);

        if (wordDefinitionList.contains(enteredWord)) {
            Log.i("Return word", enteredWord);
            return enteredWord;
        }

        return null;
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
