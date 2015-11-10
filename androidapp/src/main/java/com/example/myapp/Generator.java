package com.example.myapp;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by mihai on 09.11.2015.
 */
public class Generator {
    private class Face {
        private Character letter;

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
        }


        //generate a new face
        public void Roll(int id) {
            int value = this.random.nextInt(6);

            //pentru fiecare zar, in functie de value si id, setez caracterul fetei
            this.face.setLetter(Generator.diceFaces[id][value]);
        }

        //get the current face
        public Face getFace(){
            return this.face;
        }

    }

    final public int DICES = 9;
    private ArrayList<Dice> dices;

    public Generator() {
        this.dices = new ArrayList<Dice>(DICES);
    }

    public void RollDices() {
        for (Dice dice : this.dices) {
            dice.Roll(this.dices.indexOf(dice));
        }
    }

    public ArrayList<Character> getFaces() {
        ArrayList<Character> faces = new ArrayList<Character>();

        for (Dice dice : this.dices) {
            faces.add(dice.getFace().getLetter());
        }

        return faces;
    }

    public static Character[][] diceFaces = {
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
