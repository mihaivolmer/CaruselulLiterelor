package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {

    public static final String DEXONLINE_BASE_ADDRESS = "https://dexonline.ro/definitie/";
    public static final String OUTPUT_MESSAGE_OK = " is OK";
    public static final String OUTPUT_MESSAGE_BAD = " is BAD";
    private static final float START = 50;

    private TextView resultTextView;
    private TextView timer;
    private TextView score;
    private int numberOfLeters = 0;
    private ArrayList<String> responseWords = new ArrayList<String>();

    //check if the response enteredWord is the same with the one searched => redirect to another enteredWord
    //is not allowed
    //PROBLEM: words written without diacritics are redirected to the right page
    //but the redirect is good; the enteredWord exists
    private void validateWord(String word, String outputMessage, String responseWord) {
        boolean wordExistsInList = false;
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

            } else {
                resultTextView.setText("Please, try another enteredWord!");
            }
        } else {
            resultTextView.setText(outputMessage + OUTPUT_MESSAGE_BAD);
        }
    }

    private class WordSearchThread extends Thread {
        private final String enteredWord;

        public WordSearchThread(String enteredWord) {
            this.enteredWord = enteredWord;
        }



        private HttpResponse getHttpResponse() throws IOException {
            String wordUrl = MyActivity.DEXONLINE_BASE_ADDRESS + enteredWord;

            //execute http get using http client
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(wordUrl);
            return httpClient.execute(httpGet);

        }

        @Override
        public void run() {

            try {

                //execute http get using http client
                HttpResponse httpResponse = getHttpResponse();

                //status got from get
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                //it statusCode is OK(200) means the get method retrieved content
                if (statusCode == HttpStatus.SC_OK) {
                    final String responseWord = WordUtils.getWord(httpResponse);
                    Log.i("Response word", responseWord);

                    //when updating the UI you need to do it from the main thread, that means
                    //you need to call runOnUiThread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String outputMessage = "Word " + enteredWord;

                            validateWord(enteredWord, outputMessage, responseWord);
                        }
                    });

                } else {
                    Log.i(enteredWord, " does NOT exist!");
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


            //call the thread to search the enteredWord
            new WordSearchThread(wordEntered.getText().toString()).start();

            //clear textField
            wordEntered.setText("");
        }
    }


    private class ButtonClickListenerStartButton implements Button.OnClickListener {


        @Override
        public void onClick(View view) {

            setContentView(R.layout.httpget);
            generateLetters();

            resultTextView = (TextView) findViewById(R.id.result_text);
            score = (TextView) findViewById(R.id.countPoints);
            timer = (TextView) findViewById(R.id.timer);
            long startTime = 60000;
            long interval = 1000;
            MalibuCountDownTimer countDownTimer = new MalibuCountDownTimer(startTime, interval);
            timer.setText(timer.getText() + String.valueOf(startTime));
            initHttpGetFrame();
            countDownTimer.start();


        }
    }

    private void generateLetters() {

        Generator letterGenerator = new Generator();

        List<Character> letterList = letterGenerator.getLetters();

        LinearLayout myLayout = (LinearLayout) findViewById(R.id.my_layout);


        int listSize = letterList.size();
        TextView[] pairs=new TextView[listSize];


        boolean arrivedToLimit = true;
        boolean beginNextRow = false;
        for(int l = 0; l < listSize; l++)
        {


            pairs[l] = new TextView(this);
            pairs[l].setWidth(100);
            pairs[l].setHeight(100);

            //set coordinates
            if (l >= 1) {

                if (pairs[l-1].getX() >= 250) {
                    arrivedToLimit = false;

                }

                if (arrivedToLimit) {
                    pairs[l].setX(pairs[l - 1].getX() + 50);
                } else {
                    if (!beginNextRow) {
                        Log.i("=======getX:", String.valueOf(pairs[l].getX()));
                        pairs[l].setX(-400);
                        pairs[l].setY(150);
                        beginNextRow = true;
                    } else {
                        pairs[l].setY(150);
                        pairs[l].setX(pairs[l-1].getX() + 50);
                    }
                }
            } else {
                pairs[l].setX(START);
            }

            Log.i("getX:", String.valueOf(pairs[l].getX()));
            Log.i("getY:", String.valueOf(pairs[l].getY()));

            pairs[l].setTextSize(20);
            pairs[l].setGravity(Gravity.CENTER);
            pairs[l].setId(l);
            pairs[l].setBackground(getResources().getDrawable(R.drawable.circle));
            pairs[l].setText(letterList.get(l).toString());
            myLayout.addView(pairs[l]);
        }

    }

//    private class ButtonClickListenerResetButton implements Button.OnClickListener {
//
//
//        @Override
//        public void onClick(View view) {
//            setContentView(R.layout.httpget);
//
//            score = (TextView) findViewById(R.id.countPoints);
//            timer = (TextView) findViewById(R.id.timer);
//            countDownTimer = new MalibuCountDownTimer(startTime, interval);
//            timer.setText(timer.getText() + String.valueOf(startTime));
//            initHttpGetFrame();
//            countDownTimer.start();
//            numberOfLeters = 0;
//
//        }
//    }


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
