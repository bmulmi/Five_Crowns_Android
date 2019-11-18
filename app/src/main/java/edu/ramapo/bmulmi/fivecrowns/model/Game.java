package edu.ramapo.bmulmi.fivecrowns.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;

public class Game {
    private int roundNumber;
    private Player human;
    private Player computer;
    private Round round;

    /**
     * default constructor
     */
    public Game() {
        this(1);
    }

    public Game(int roundNumber){
        this.roundNumber = roundNumber;
        this.human = new Human();
        this.computer = new Computer();
    }

    /**
     * loads the game from file
     * @param is InputStream object, holds the file stream for the serialized game to load
     * @return Round Object, round loaded with the information from the file
     */
    public Round load(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line = reader.readLine();
            String[] data = line.split(":");
            this.roundNumber = Integer.parseInt(data[1].trim());

            round = new Round(human, computer, roundNumber);
            round.load(reader);
            roundNumber++;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return round;
    }

    /**
     * function to generate a new fresh round
     * @return Round object
     */
    public Round generateNewRound() {
        round = new Round(human, computer, this.roundNumber);
        roundNumber++;
        return round;
    }

    /**
     * function to get the current round number of the game
     * @return int value, round number of the game
     */
    public int getRoundNumber() {
        return roundNumber - 1;
    }

    /**
     * checks if 11 rounds have been played or not
     * @return boolean value, true if round number is greater than 11
     */
    public boolean gameEnded() {
        return roundNumber > 11;
    }
}
