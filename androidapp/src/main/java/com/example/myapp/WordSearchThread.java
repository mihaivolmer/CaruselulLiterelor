package com.example.myapp;

import android.util.Log;
import com.example.myapp.wordutils.WordUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by Laura on 11.11.15.
 */
public class WordSearchThread extends Thread {
    public static final String DEXONLINE_BASE_ADDRESS = "https://dexonline.ro/definitie/";

    private final String enteredWord;
    private final MainActivity mainActivity;

    public WordSearchThread(String enteredWord, MainActivity mainActivity) {
        this.enteredWord = enteredWord;
        this.mainActivity = mainActivity;
    }


    @Override
    public void run() {

        try {
            HttpResponse httpResponse = getHttpResponse();

            //status got from get
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            //it statusCode is OK(200) means the get method retrieved content
            if (statusCode == HttpStatus.SC_OK) {
                final String responseWord = WordUtils.getWord(httpResponse);
                Log.i("Response enteredWord", responseWord);
                mainActivity.updateUI(true, responseWord, enteredWord);

            } else {
                Log.i(enteredWord, " does NOT exist!");
                mainActivity.updateUI(false, enteredWord, " does NOT exist!");
            }
        } catch (Exception httpException) {
            Log.i("Exception: ", httpException.getMessage());
        }
    }

    private HttpResponse getHttpResponse() throws IOException {
        String wordUrl = DEXONLINE_BASE_ADDRESS + enteredWord;

        //execute http get using http client
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(wordUrl);
        return httpClient.execute(httpGet);

    }
}
