package edu.ramapo.bmulmi.fivecrowns.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Vector;

public class Round {
    private Player human;
    private Player computer;
    private Player nextPlayer;
    private Deck deck;
    private int roundNumber;

    public Round(Player human, Player computer, int roundNumber) {
        this.roundNumber = roundNumber;
        this.human = human;
        this.computer = computer;
        this.deck = Deck.getInstanceOfDeck(2);
    }

    public void init() {
        deck = Deck.getInstanceOfDeck(2);
        deck.setWildCardFace(roundNumber + 2);
        deck.shuffleDeck();
        distributeCards(human);
        distributeCards(computer);
        deck.discard(deck.drawCard());
    }

//    private boolean roundEnded() {
//
//    }

    private void distributeCards(Player player){
        Vector<Card> hand = new Vector<>();
        for (int i = 0; i < roundNumber + 2; i++) {
            hand.add(deck.drawCard());
        }
        player.setHand(hand);
    }

    public Vector<Card> getHumanHand(){
        return human.getHand();
    }

    public Vector<Card> getComputerHand() {
        return computer.getHand();
    }

    public int getHumanScore() {
        return human.getScore();
    }

    public int getComputerScore() {
        return computer.getScore();
    }


//    public String getSerializableInfo() {
//
//    }

    public void load(BufferedReader info) {
        deck = Deck.getInstanceOfDeck(2);

        try {
            // Empty Line
            info.readLine();
            // "Computer: "
            info.readLine();

            // Computer score;
            String line = info.readLine();
            String[] data = line.split(":");
            computer.setScore(Integer.parseInt(data[1].trim()));

            // Computer hand;
            line = info.readLine();
            data = line.split(":");
            computer.setHand(loadHand(data[1].trim()));

            // Empty Line
            info.readLine();
            // "Human: "
            info.readLine();

            // Human score;
            line = info.readLine();
            data = line.split(":");
            human.setScore(Integer.parseInt(data[1].trim()));

            // Human hand;
            line = info.readLine();
            data = line.split(":");
            human.setHand(loadHand(data[1].trim()));

            // Empty Line
            info.readLine();

            // Draw pile
            line = info.readLine();
            data = line.split(":");
            deck.setDrawPile(loadDeck(data[1].trim()));

            // Empty Line
            info.readLine();

            // Discard Pile
            line = info.readLine();
            data = line.split(":");
            deck.setDiscardPile(loadDeck(data[1].trim()));

            // Empty Line
            info.readLine();

            // Next Player
            line = info.readLine();
            data = line.split(":");
            data[1].trim();
            if (data[1].toLowerCase().equals("human")) nextPlayer = human;
            else nextPlayer = computer;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Deque<Card> loadDeck(String cards) {
        String[] data = cards.split(" ");
        Deque<Card> temp = new LinkedList<>();
        for(String each : data) {
            String face = each.substring(0,each.length()-1);
            String suite = each.substring(each.length()-1);
            temp.add(new Card(face, suite));
        }
        return temp;
    }

    private Vector<Card> loadHand(String cards) {
        String[] data = cards.split(" ");
        Vector<Card> temp = new Vector<>();
        for (String each : data) {
            String face = each.substring(0,each.length()-1);
            String suite = each.substring(each.length()-1);
            temp.add(new Card(face, suite));
        }
        return temp;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }
}
