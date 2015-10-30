package com.example.myapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MyActivity extends Activity {


    public static final String OCW_BASE_INTERNET_ADDRESS = "https://dexonline.ro/definitie/";
    public static final String OUTPUT_MESSAGE_OK = " is OK";
    public static final String OUTPUT_MESSAGE_BAD = " is BAD";


    private TextView resultTextView;
    private ButtonClickListener buttonClickListener = new ButtonClickListener();

    private class WordSearchThread extends Thread {


        private final String word;

        public WordSearchThread(String word) {
            this.word = word;
        }

        @Override
        public void run() {

            try {
                String wordUrl = OCW_BASE_INTERNET_ADDRESS + word;

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(wordUrl);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                if (statusCode == 200) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String displayedText = EntityUtils.toString(httpEntity);

                    //parse html
                    Document document = Jsoup.parse(displayedText);
                    String[] wordsRetrieved = document.title().split("\\s");

                    final String responseWord = wordsRetrieved[0];
                    Log.i("+++++++++word", word);

                    //when updating the UI you need to do it from the main thread, that means
                    //you need to call runOnUiThread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String outputMessage = "Word " + word;

                            //check if the response word is the same with the one searched => redirect to another word
                            //is not allowed
                            if (responseWord.equals(word)) {
                                Log.i("Word " + word, " is OK");
                                resultTextView.setText(outputMessage + OUTPUT_MESSAGE_OK);
                            } else {
                                resultTextView.setText(outputMessage + OUTPUT_MESSAGE_BAD);
                            }
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
            TextView greetingTextView = (TextView) findViewById(R.id.welcome_text);

//            greetingTextView.setAlpha(1);

            Log.i("=====word entered: ", wordEntered.getText().toString());

            new WordSearchThread(wordEntered.getText().toString()).start();


            //fade in and out effect
//            AlphaAnimation fadeOutEffect = new AlphaAnimation(1.0f, 0.0f);
//            fadeOutEffect.setDuration(1500);
//            greetingTextView.setAnimation(fadeOutEffect);
//
//            AlphaAnimation fadeInEffect = new AlphaAnimation(0.0f, 1.0f);
//            fadeInEffect.setDuration(1500);
//            greetingTextView.setAnimation(fadeInEffect);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.httpget);

        resultTextView = (TextView) findViewById(R.id.result_text);
//        new WordSearchThread("kkkk").start();
//        new WordSearchThread("floare").start();
//        new WordSearchThread("jajaj").start();

        initApp();

    }


    private void initApp() {
        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(buttonClickListener);
    }

}
