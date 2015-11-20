package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import com.example.myapp.androidutils.EnterWordButton;
import com.example.myapp.androidutils.LetterGraphicSetup;
import com.example.myapp.wordutils.Generator;
import com.example.myapp.wordutils.WordUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public static final String OUTPUT_MESSAGE_OK = " is OK";
    public static final String OUTPUT_MESSAGE_BAD = "Please, try another word!";

    private TextView resultTextView;
    private TextView timer;
    private TextView score;
    private int numberOfLeters = 0;

    private List<Character> letterList;


    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(this.getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
    }

    //check if the response enteredWord is the same with the one searched => redirect to another enteredWord
    //is not allowed
    public void validateWord(String word, String outputMessage, String responseWord) {
        boolean finalWordOk = WordUtils.isFinalWordOk(word, responseWord);

        if (finalWordOk) {
            resultTextView.setText(outputMessage + OUTPUT_MESSAGE_OK);
            numberOfLeters = numberOfLeters + calculateScore(responseWord);
            score.setText(" Score: " + numberOfLeters);
        } else {
            resultTextView.setText(OUTPUT_MESSAGE_BAD);
        }
    }

    //when updating the UI you need to do it from the main thread, that means
    //you need to call runOnUiThread
    public void updateUI(boolean success, final String responseWord, final String enteredWord) {

        if (success) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String outputMessage = "Word " + enteredWord;

                    validateWord(enteredWord, outputMessage, responseWord);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resultTextView.setText("Not found!");
                }
            });
        }
    }

    //update UI before searching the word if first verification fails
    public void updateUIFailFast(final String outputMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultTextView.setText(outputMessage);
            }
        });
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

            //initiate the main frame
            initHttpGetFrame();
            countDownTimer.start();
        }
    }

    //creates LetterGraphicSetup object for generating the letter_layout
    private void generateLetters() {
        letterList = new Generator().getLetters();
        new LetterGraphicSetup(this, letterList);
    }

    // CountDownTimer class
    private class MalibuCountDownTimer extends CountDownTimer {

        public MalibuCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            timer.setText("Time's up!");
            //startActivity(new Intent(MainActivity.this,Pop.class));
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
        showKeyboard();
        ButtonClickListenerStartButton buttonClickListener = new ButtonClickListenerStartButton();
        Button submitButton = (Button) findViewById(R.id.start_game);
        submitButton.setOnClickListener(buttonClickListener);


    }

    private void initHttpGetFrame() {
        score.setText(" Score: " + numberOfLeters);

        WordUtils.clearWordList();
        EnterWordButton enterWordButton = new EnterWordButton(this);
        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(enterWordButton);

    }

    private void initAppResetButton() {
        numberOfLeters = 0;
        score.setText(" Score: " + numberOfLeters);

        ButtonClickListenerStartButton buttonClickListener = new ButtonClickListenerStartButton();
        Button submitButton = (Button) findViewById(R.id.restartButton);
        submitButton.setOnClickListener(buttonClickListener);
    }

    private int calculateScore(String s) {
        int nr;
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

    public List<Character> getLetterList() {
        return new ArrayList<Character>(letterList);
    }
}
