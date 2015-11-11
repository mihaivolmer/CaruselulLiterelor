package com.example.myapp.androidutils;

import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    private int listSize;

    public LetterGraphicSetup(MainActivity mainActivity, List<Character> letterList, int listSize) {
        this.mainActivity = mainActivity;
        this.letterList = letterList;
        this.listSize = listSize;
        setLetterListView();
    }

    private void setLetterListView() {
        LinearLayout myLayout = (LinearLayout) mainActivity.findViewById(R.id.lettersList);
        TextView[] pairs=new TextView[listSize];

        boolean isBetweenLimit = true;
        boolean isNextRowStarted = false;

        for (int l = 0; l < listSize; l++) {
            pairs[l] = new TextView(mainActivity);
            pairs[l].setWidth(SIZE);
            pairs[l].setHeight(SIZE);

            //set coordinates
            if (l >= 1) {
                if (pairs[l - 1].getX() >= FIRST_ROW_LIMIT) {
                    isBetweenLimit = false;
                }
                if (isBetweenLimit) {
                    float x = pairs[l - 1].getX() + DISTANCE_BETWEEN_LETTERS;
                    pairs[l].setX(x);
                } else {
                    if (!isNextRowStarted) {
                        pairs[l].setX(X_START_SECOND_ROW);
                        pairs[l].setY(Y_START_SECOND_ROW);
                        isNextRowStarted = true;
                    } else {
                        float x = pairs[l - 1].getX() + DISTANCE_BETWEEN_LETTERS;
                        pairs[l].setX(x);
                        pairs[l].setY(Y_START_SECOND_ROW);
                    }
                }
            } else {
                pairs[l].setX(X_START_FIRST_ROW);
            }

            Log.i("getX:", String.valueOf(pairs[l].getX()));
            Log.i("getY:", String.valueOf(pairs[l].getY()));

            //set text size, gravity, id, background
            pairs[l].setTextSize(TEXT_SIZE);
            pairs[l].setGravity(Gravity.CENTER);
            pairs[l].setId(l);
            pairs[l].setBackground(mainActivity.getResources().getDrawable(R.drawable.circle));

            //set the corresponding letter to each list component
            pairs[l].setText(letterList.get(l).toString());

            //add letter to the array of textviews
            myLayout.addView(pairs[l]);
        }
    }
}