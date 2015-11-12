package com.example.myapp.androidutils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.WordSearchThread;

import java.util.List;

/**
 * Created by Laura on 11.11.15.
 */
public class EnterWordButton implements View.OnClickListener {
    MainActivity mainActivity;
    public  EnterWordButton(MainActivity activity) {
        this.mainActivity = activity;
    }

    //checks if the entered word uses only once each letter given
    //EXAMPLE: H T M D E C X B L,  word BEBE is NOT ok because there is one B and one E
    private boolean isEnteredWordOk (String enteredWord) {
        List<Character> letters = mainActivity.getLetterList();

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

    @Override
    public void onClick(View view) {
        EditText enteredWordEditText = (EditText) mainActivity.findViewById(R.id.enter_word);

        String enteredWordString = enteredWordEditText.getText().toString();

        Log.i("Word entered: ", enteredWordString);
        // Close virtual keyboard
        InputMethodManager inputManager = (InputMethodManager)
                mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(mainActivity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);


        //check if word is composed from the given letters
        if (isEnteredWordOk(enteredWordString)) {
            //call the thread to search the word
            new WordSearchThread(enteredWordString, mainActivity).start();
        } else {
            mainActivity.updateUI(false, null, enteredWordString);
        }
        //clear textField
        enteredWordEditText.setText("");
    }
}
