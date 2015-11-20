package com.example.myapp.wordutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by mihai on 09.11.2015.
 */
public class Generator {
    private class Face {
        private Character letter;

        public Face() {

        }

        public Character getLetter() {
            return letter;
        }

        public void setLetter(Character letter) {
            this.letter = letter;
        }
    }

    private class Dice {
        private Face face;
        private Random random;

        public Dice() {
            this.random = new Random();
            this.face = new Face();
        }


        //generate a new face
        public void Roll(int id) {
            int value = this.random.nextInt(6);

            //pentru fiecare zar, in functie de value si id, setez caracterul fetei
            this.face.setLetter(Generator.diceFaces[id][value]);

        }

        //get the current face
        public Face getFace() {
            return this.face;
        }

    }

    private final int DICES = 9;
    private ArrayList<Dice> dices;

    public Generator() {
        createDices();
    }

    private void createDices() {
        this.dices = new ArrayList<Dice>(DICES);
        for (int i = 0; i < DICES; i++) {
            this.dices.add(new Dice());
        }
    }

    private void RollDices() {
        for (Dice dice : this.dices) {
            dice.Roll(this.dices.indexOf(dice));
        }
    }

    private ArrayList<Character> getFaces() {
        ArrayList<Character> faces = new ArrayList<Character>();

        for (Dice dice : this.dices) {
            faces.add(dice.getFace().getLetter());
        }

        return faces;
    }

    //function to roll the dice and get letter_layout
    public List getLetters() {
        RollDices();
        ArrayList<Character> diceFaces = getFaces();

        return diceFaces;
    }


    private static final Character[][] diceFaces = {
            {'A', 'A', 'U', 'I', 'H', 'J'},
            {'T', 'R', 'N', 'S', 'M', 'B'},
            {'A', 'A', 'R', 'C', 'D', 'M'},
            {'E', 'E', 'I', 'O', 'D', 'F'},
            {'A', 'E', 'U', 'S', 'F', 'V'},
            {'T', 'L', 'N', 'P', 'G', 'C'},
            {'A', 'I', 'O', 'E', 'X', 'Z'},
            {'N', 'S', 'T', 'R', 'G', 'B'},
            {'I', 'I', 'U', 'E', 'L', 'P'},
    };
}
