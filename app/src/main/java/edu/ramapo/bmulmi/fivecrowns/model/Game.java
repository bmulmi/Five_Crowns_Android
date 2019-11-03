package edu.ramapo.bmulmi.fivecrowns.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class Game {
    private int roundNumber;
    private Player human;
    private Player computer;
    private Round round;

    public Game() {
        this(1);
    }

    public Game(int roundNumber){
        this.roundNumber = roundNumber;
        this.human = new Human();
        this.computer = new Computer();
    }

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

    public Round generateNewRound() {
        round = new Round(human, computer, this.roundNumber++);
        return round;
    }

    public int getRoundNumber() {
        return roundNumber - 1;
    }
    public boolean gameEnded() {
        return roundNumber > 11;
    }


}
