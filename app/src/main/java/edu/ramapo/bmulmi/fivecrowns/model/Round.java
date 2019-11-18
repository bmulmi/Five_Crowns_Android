package edu.ramapo.bmulmi.fivecrowns.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Vector;

public class Round {
    private Human human;
    private Computer computer;
    private Player nextPlayer;
    private Deck deck;
    private int roundNumber;

    public Round(Player human, Player computer, int roundNumber) {
        this.roundNumber = roundNumber;
        this.human = (Human) human;
        this.computer = (Computer) computer;
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

    public boolean canCurrPlayerGoOut() {
        return nextPlayer.canGoOut();
    }

    private void distributeCards(Player player){
        Vector<Card> hand = new Vector<>();
        for (int i = 0; i < roundNumber + 2; i++) {
            hand.add(deck.drawCard());
        }
        player.setHand(hand);
    }

    public void setNextPlayer(String player) {
        if (player.equals("human")) nextPlayer = human;
        else nextPlayer = computer;
    }

    public void changePlayer(){
        if (nextPlayer.getType().equals("human")) {
            nextPlayer = computer;
        }
        else {
            nextPlayer = human;
        }
    }

    public StringBuilder endRound(){
        // the current player lost the round
        // update the score of that player only
        StringBuilder info = new StringBuilder();
        if (nextPlayer.getType().equals("human")) {
            int human_scr = human.getHandScore();
            info.append("Computer went out first.\n");
            info.append("Human Score: ").append(human_scr);
            info.append("\nComputer Score: ").append(0);
            human.updateScore(human_scr);
            computer.updateScore(0);
        }
        else {
            int comp_scr = computer.getHandScore();
            info.append("Human went out first.\n");
            info.append("Human: ").append(0);
            info.append("\nComputer: ").append(comp_scr);
            computer.updateScore(comp_scr);
            human.updateScore(0);
        }
        return info;
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

    public void draw(String pile) {
        deck = Deck.getInstanceOfDeck(2);
        if (pile.equals("discard")) {
            human.addToHand(deck.drawDiscardCard());
        }
        else if (pile.equals("draw")) {
            human.addToHand(deck.drawCard());
        }
    }

    public void discard(int index) {
        deck = Deck.getInstanceOfDeck(2);
        deck.discard(human.removeFromHand(index));
    }

    public void load(BufferedReader info) {
        deck = Deck.getInstanceOfDeck(2);
        deck.setWildCardFace(roundNumber + 2);

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

    public String getNextPlayer() {
        return nextPlayer.getClass() == computer.getClass() ? "computer" : "human";
    }

    public String playComputer() {
        return computer.play();
    }

    public String getPileHint() {
        return human.whichPileToChoose();
    }

    public int getDiscardHint() {
        return human.whichCardToDiscard();
    }

    public String arrangeHand() {
        Vector<Vector<Card>> arrangedHand = human.assemblePossibleHand();
        String temp = "";
        Vector<Card> tempHand = new Vector<>();
        for (Vector<Card> eachHand : arrangedHand) {
            for (Card each : eachHand) {
                tempHand.add(each);
                temp += each.serializableString() + " ";
            }
            temp += " | ";
        }

        human.setHand(tempHand);

        temp += "Hand score: " + human.getHandScore();

        return temp;
    }

    public String serialize() {
        deck = Deck.getInstanceOfDeck(2);
        StringBuilder info = new StringBuilder();
        info.append("Round: ").append(Integer.toString(roundNumber)).append("\n");
        info.append("\nComputer:\n");
        info.append("\tScore: ").append(computer.getScore()).append("\n");
        info.append("\tHand: ").append(computer.getSerializableHand()).append("\n");
        info.append("\nHuman:\n");
        info.append("\tScore: ").append(human.getScore()).append("\n");
        info.append("\tHand: ").append(human.getSerializableHand()).append("\n");
        info.append("\nDraw Pile: ").append(deck.toString(deck.getDrawPile())).append("\n");
        info.append("\nDiscard Pile: ").append(deck.toString(deck.getDiscardPile())).append("\n");
        info.append("\nNext Player: ").append(nextPlayer.getType()).append("\n");
        return info.toString();
    }
}
