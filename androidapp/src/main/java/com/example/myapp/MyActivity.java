package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class MyActivity extends Activity {

    public static final String DEXONLINE_BASE_ADDRESS = "https://dexonline.ro/definitie/";
    public static final String OUTPUT_MESSAGE_OK = " is OK";
    public static final String OUTPUT_MESSAGE_BAD = " is BAD";

    private TextView resultTextView;

    private class WordSearchThread extends Thread {
        private final String word;

        public WordSearchThread(String word) {
            this.word = word;
        }

        //removes diacritics
        public String convertWord(String str) {
            String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(nfdNormalizedString).replaceAll("");
        }

        //retrieves the word fromt the http response
        public String getWord(String httpResponse) {
            //parse html to get the word: the word is in the title if exists
            Document document = Jsoup.parse(httpResponse);
            String[] wordsRetrieved = document.title().split("\\s");

            //word needs to be without diacritics
            return convertWord(wordsRetrieved[0]);
        }

        //get text from response, UTF-8 is needed for the words with diacritics
        private String getHttpResponse(HttpResponse httpResponse) throws IOException {

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

        //check if the response word is the same with the one searched => redirect to another word
        //is not allowed
        //PROBLEM: words written without diacritics are redirected to the right page
        //but the redirect is good; the word exists
        private void printOutputMessage(String outputMessage, String responseWord) {
            if (responseWord.equals(word)) {
                Log.i("Word " + word, " is OK");
                resultTextView.setText(outputMessage + OUTPUT_MESSAGE_OK);
            } else {
                resultTextView.setText(outputMessage + OUTPUT_MESSAGE_BAD);
            }
        }

        @Override
        public void run() {

            try {
                String wordUrl = DEXONLINE_BASE_ADDRESS + word;

                //execute http get using http client
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(wordUrl);
                HttpResponse httpResponse = httpClient.execute(httpGet);

                //status got from get
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                //it statusCode is OK(200) means the get method retrieved content
                if (statusCode == HttpStatus.SC_OK) {
                    String responseBody = getHttpResponse(httpResponse);

                    final String responseWord = getWord(responseBody);
                    Log.i("Response word", word);

                    //when updating the UI you need to do it from the main thread, that means
                    //you need to call runOnUiThread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String outputMessage = "Word " + word;

                            printOutputMessage(outputMessage, responseWord);
                        }
                    });

                } else {
                    Log.i(word, " does NOT exist!");
                    //when updating the UI you need to do it from the main thread, that means
                    //you need to call runOnUiThread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultTextView.setText("Not found");
                        }
                    });
                }
            } catch (Exception httpException) {
                Log.i("Exception: ", httpException.getMessage());
            }
        }
    }

    private class ButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            EditText wordEntered = (EditText) findViewById(R.id.enter_word);
            Log.i("Word entered: ", wordEntered.getText().toString());

            //call the thread to search the word
            new WordSearchThread(wordEntered.getText().toString()).start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.httpget);
        resultTextView = (TextView) findViewById(R.id.result_text);

        initApp();
    }

    //TODO: class for the letter that need to be displayed graphical
    public class WordsView extends View {

        public WordsView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPaint(paint);

            paint.setColor(Color.RED);
            paint.setTextSize(20);
            canvas.drawText("AA", 10, 25, paint);
        }

    }

    private void initApp() {
        ButtonClickListener buttonClickListener = new ButtonClickListener();
        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(buttonClickListener);
    }

}
