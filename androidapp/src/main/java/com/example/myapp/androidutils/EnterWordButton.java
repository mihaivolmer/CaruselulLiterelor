package com.example.myapp.androidutils;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.WordSearchThread;
import com.example.myapp.wordutils.WordUtils;

/**
 * Created by Laura on 11.11.15.
 */
public class EnterWordButton implements View.OnClickListener {
    MainActivity mainActivity;

    public EnterWordButton(MainActivity activity) {
        this.mainActivity = activity;
    }


    @Override
    public void onClick(View view) {
        EditText enteredWordEditText = (EditText) mainActivity.findViewById(R.id.enter_word);

        String enteredWordString = enteredWordEditText.getText().toString();
        Log.i("Word entered: ", enteredWordString);

//        //word must have minimum 4 letters
        if (enteredWordString.length() < 4) {
            mainActivity.updateUIFailFast("Minimum 4 letter required!");
            mainActivity.hideKeyboard();
            return;
        }


        // Close virtual keyboard
        mainActivity.hideKeyboard();

        //check if word is composed from the given letters
        boolean wordOk = WordUtils.isEnteredWordOk(mainActivity.getLetterList(), enteredWordString);
        if (wordOk) {
            //call the thread to search the word
            new WordSearchThread(enteredWordString, mainActivity).start();
        } else {
            mainActivity.updateUIFailFast("Use given letters!");
        }
        //clear textField
        enteredWordEditText.setText("");
    }
}
