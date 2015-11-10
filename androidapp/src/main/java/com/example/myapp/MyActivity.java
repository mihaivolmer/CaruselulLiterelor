package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
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
import java.util.ArrayList;
import java.util.regex.Pattern;
import android.os.CountDownTimer;
import android.view.inputmethod.InputMethodManager;
import org.w3c.dom.Text;

public class MyActivity extends Activity {

    public static final String DEXONLINE_BASE_ADDRESS = "https://dexonline.ro/definitie/";
    public static final String OUTPUT_MESSAGE_OK = " is OK";
    public static final String OUTPUT_MESSAGE_BAD = " is BAD";

    private TextView resultTextView;
    private TextView timer;
    private TextView score;
    private final long startTime = 60000;
    private final long interval = 1000;
    private MalibuCountDownTimer countDownTimer;
    private int numberOfLeters = 0;
    private ArrayList<String> responseWords = new ArrayList<String>();
    private boolean wordExistsInList = false;

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

                for (String s : responseWords) {
                    if (s.equals(responseWord)) {
                        wordExistsInList = true;
                    }
                }

                if (!wordExistsInList) {
                    responseWords.add(responseWord);
                    Log.i("Word " + word, " is OK");
                    resultTextView.setText(outputMessage + OUTPUT_MESSAGE_OK);
                    numberOfLeters = numberOfLeters + calculateScore(responseWord);
                    score.setText(" Score: " + numberOfLeters);
                    wordExistsInList = false;

                } else {
                    resultTextView.setText("Please, try another word!");
                    wordExistsInList = false;
                }
            } else {
                resultTextView.setText(outputMessage + OUTPUT_MESSAGE_BAD);
                wordExistsInList = false;
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
            // Close virtual keyboard
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);


            //call the thread to search the word
            new WordSearchThread(wordEntered.getText().toString()).start();

            //clear textField
            wordEntered.setText("");
        }
    }


    private class ButtonClickListenerStartButton implements Button.OnClickListener {


        @Override
        public void onClick(View view) {
            setContentView(R.layout.httpget);
            resultTextView = (TextView) findViewById(R.id.result_text);
            score = (TextView) findViewById(R.id.countPoints);
            timer = (TextView) findViewById(R.id.timer);
            countDownTimer = new MalibuCountDownTimer(startTime, interval);
            timer.setText(timer.getText() + String.valueOf(startTime));
            initHttpGetFrame();
            countDownTimer.start();


        }
    }

    private class ButtonClickListenerResetButton implements Button.OnClickListener {


        @Override
        public void onClick(View view) {
            setContentView(R.layout.httpget);

            score = (TextView) findViewById(R.id.countPoints);
            timer = (TextView) findViewById(R.id.timer);
            countDownTimer = new MalibuCountDownTimer(startTime, interval);
            timer.setText(timer.getText() + String.valueOf(startTime));
            initHttpGetFrame();
            countDownTimer.start();
            numberOfLeters = 0;

        }
    }


    // CountDownTimer class
    public class MalibuCountDownTimer extends CountDownTimer {

        public MalibuCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            timer.setText("Time's up!");
            //startActivity(new Intent(MyActivity.this,Pop.class));
            setContentView(R.layout.popwindow);
            TextView scoreView = (TextView) findViewById(R.id.scoreView);
            scoreView.setText("Score: " + numberOfLeters);
            initAppResetButton();


        }

        @Override
        public void onTick(long millisUntilFinished) {
            timer.setText("Time remaining:" + (int) (millisUntilFinished / 1000));


        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
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
        ButtonClickListenerStartButton buttonClickListener = new ButtonClickListenerStartButton();
        Button submitButton = (Button) findViewById(R.id.start_game);
        submitButton.setOnClickListener(buttonClickListener);
    }

    private void initHttpGetFrame() {
        score.setText(" Score: " + numberOfLeters);

        ButtonClickListener buttonClickListener = new ButtonClickListener();
        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(buttonClickListener);

    }

    private void initAppResetButton() {
        numberOfLeters = 0;
        score.setText(" Score: " + numberOfLeters);

        ButtonClickListenerStartButton buttonClickListener = new ButtonClickListenerStartButton();
        Button submitButton = (Button) findViewById(R.id.restartButton);
        submitButton.setOnClickListener(buttonClickListener);
    }




    private int calculateScore(String s) {
        int nr = 0;
        String commonChars = "";
        String t = "xzhj";
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (t.indexOf(ch) != -1) {
                commonChars = commonChars + ch;
            }
        }

        if (commonChars.length() == 0) {
            nr = s.length();
        } else if (s.length() > 6) {
            nr = s.length() + 3 * commonChars.length();

        } else nr = s.length() + commonChars.length();

        return nr;
    }
}
