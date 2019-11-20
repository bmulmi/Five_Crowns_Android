/************************************************************
 * Name: Bibhash Mulmi                                      *
 * Project: Project 3, Five Crowns Android                  *
 * Class: OPL Fall 19                                       *
 * Date: 11/20/2019                                         *
 ************************************************************/
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

    /**
     * Constructor for Round class
     * @param human player object, holds the human player object
     * @param computer player object, holds the computer player object
     * @param roundNumber int value, holds the round number
     */
    public Round(Player human, Player computer, int roundNumber) {
        this.roundNumber = roundNumber;
        this.human = (Human) human;
        this.computer = (Computer) computer;
        this.deck = Deck.getInstanceOfDeck(2);
    }

    /**
     * initializes the round,
     * sets wild cards,
     * distributes cards,
     * sets discard pile card
     */
    public void init() {
        deck = Deck.getInstanceOfDeck(2);
        deck.setWildCardFace(roundNumber + 2);
        deck.shuffleDeck();
        distributeCards(human);
        distributeCards(computer);
        deck.discard(deck.drawCard());
    }

    /**
     * function to get the human hand
     * @return vector of cards that holds the human hand cards
     */
    public Vector<Card> getHumanHand(){
        return human.getHand();
    }

    /**
     * function to get the computer hand
     * @return vector of cards that holds the computer hand cards
     */
    public Vector<Card> getComputerHand() {
        return computer.getHand();
    }

    /**
     * function to get the human score
     * @return int value that holds the human score
     */
    public int getHumanScore() {
        return human.getScore();
    }

    /**
     * function to get the computer score
     * @return int value that holds the computer score
     */
    public int getComputerScore() {
        return computer.getScore();
    }

    /**
     * function to get the hint from which pile to choose
     * @return string value that holds the suggestion generated
     * by the computer's strategy
     */
    public String getPileHint() {
        return human.whichPileToChoose();
    }

    /**
     * function to get the hint to discard a card
     * @return int value that holds the index of the card to discard
     */
    public int getDiscardHint() {
        return human.whichCardToDiscard();
    }

    /**
     * function to get the next player as string
     * @return string value that holds "computer" or "human"
     */
    public String getNextPlayer() {
        return nextPlayer.getClass() == computer.getClass() ? "computer" : "human";
    }

    /**
     * function to let the computer play its turn
     * @return string value that holds the computer's move description
     */
    public String playComputer() {
        return computer.play();
    }

    /**
     * function to check if the current player can go out
     * @return boolean value, holds true if nextPlayer can go out
     */
    public boolean canCurrPlayerGoOut() {
        return nextPlayer.canGoOut();
    }

    /**
     * function to set the next player to play its turn
     * @param player string value that holds the type of player
     */
    public void setNextPlayer(String player) {
        if (player.equals("human")) nextPlayer = human;
        else nextPlayer = computer;
    }

    /**
     * function to distribute cards and set player's hand
     * @param player player object to distribute the card for
     */
    private void distributeCards(Player player){
        Vector<Card> hand = new Vector<>();
        for (int i = 0; i < roundNumber + 2; i++) {
            hand.add(deck.drawCard());
        }
        player.setHand(hand);
    }

    /**
     * function to arrange the hand of the player
     * @param type string value, holds the type of player to arrange hand for
     * @return string value, holds the arranged hand as string and the hand score
     */
    public String arrangeHand(String type) {
        Vector<Vector<Card>> arrangedHand = type.equals("human") ? human.assemblePossibleHand() : computer.assemblePossibleHand();

        String temp = type + " ";
        Vector<Card> tempHand = new Vector<>();
        for (Vector<Card> eachHand : arrangedHand) {
            for (Card each : eachHand) {
                tempHand.add(each);
                temp += each.serializableString() + " ";
            }
            temp += " | ";
        }

        if (type.equals("human")) {
            human.setHand(tempHand);
            temp += "Hand score: " + human.getHandScore();
        }
        else {
            computer.setHand(tempHand);
            temp += "Hand score: " + computer.getHandScore();
        }
        return temp;
    }

    /**
     * function to end the round and get the round stats
     * @return string builder object, holds the round info
     */
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

    /**
     * function to let human draw a card
     * @param pile string value, holds the pile type to draw card from
     */
    public void draw(String pile) {
        deck = Deck.getInstanceOfDeck(2);
        if (pile.equals("discard")) {
            human.addToHand(deck.drawDiscardCard());
        }
        else if (pile.equals("draw")) {
            human.addToHand(deck.drawCard());
        }
    }

    /**
     * function to let human discard a card
     * @param index int value, holds the index of the card of hand to discard
     */
    public void discard(int index) {
        deck = Deck.getInstanceOfDeck(2);
        deck.discard(human.removeFromHand(index));
    }

    /**
     * function to load round from serialized file
     * @param info bufferred reader object, holds the information of the round
     */
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
            if (data[1].trim().toLowerCase().equals("human")) nextPlayer = human;
            else nextPlayer = computer;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * function to change the player's turn
     */
    public void changePlayer(){
        if (nextPlayer.getType().equals("human")) {
            nextPlayer = computer;
        }
        else {
            nextPlayer = human;
        }
    }

    /**
     * function to get the serialized string of the round
     * @return string object, holds the serialized information
     */
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

    /**
     * function to load string of cards into a deque structure
     * @param cards string value, holds the cards
     * @return deque of card objects
     */
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

    /**
     * function to load string of cards into a vector structure
     * @param cards string value, holds the cards
     * @return vector of card objects
     */
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
}
