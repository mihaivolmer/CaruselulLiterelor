package com.example.myapp.androidutils;

import android.widget.ArrayAdapter;
import android.widget.GridView;
import com.example.myapp.MainActivity;
import com.example.myapp.R;

import java.util.List;

public class LetterGraphicSetup {
    private static final float X_START_FIRST_ROW = 50;
    private static final int SIZE = 100;
    private static final float X_START_SECOND_ROW = -400;
    private static final float Y_START_SECOND_ROW = 150;
    private static final float FIRST_ROW_LIMIT = 250;
    private static final float DISTANCE_BETWEEN_LETTERS = 50;
    private static final float TEXT_SIZE = 20;

    private MainActivity mainActivity;
    private List<Character> letterList;

    public LetterGraphicSetup(MainActivity mainActivity, List<Character> letterList) {
        this.mainActivity = mainActivity;
        this.letterList = letterList;
        setLetterListView();
    }

    private void setLetterListView() {
        GridView gridView = (GridView) mainActivity.findViewById(R.id.lettersGridView);

        ArrayAdapter arrayAdapter = new ArrayAdapter(mainActivity, R.layout.letter_layout,
                R.id.letterTextView, letterList);


        gridView.setAdapter(arrayAdapter);
    }

}