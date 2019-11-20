/************************************************************
 * Name: Bibhash Mulmi                                      *
 * Project: Project 3, Five Crowns Android                  *
 * Class: OPL Fall 19                                       *
 * Date: 11/20/2019                                         *
 ************************************************************/
package edu.ramapo.bmulmi.fivecrowns.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Deck {
    private static Deck deck = null;
    private static Deque<Card> drawPile = null;
    private static Deque<Card> discardPile = null;

    private final int numJokers = 3;
    private final String [] suite = new String[]{"S", "C", "D", "H", "T"};
    private final String [] face = new String[]{"3", "4", "5", "6", "7", "8",
                                                "9", "X", "J", "Q", "K"};

    private String wildCardFace;

    /**
     * Constructor for the Deck class
     * @param numDecks int value, holds the number of decks to create
     */
    private Deck(int numDecks) {
        Deque<Card> initialDeck = arrangeDeck(numDecks);
        drawPile = initialDeck;
        discardPile = new LinkedList<>();
    }

    /**
     * selector function to get the single instance of the deck
     * @param num int value, number of decks to be created
     * @return Deck object, the single instance of deck
     */
    public static Deck getInstanceOfDeck(int num) {
        if (deck == null) {
            deck = new Deck(num);
        }
        return deck;
    }

    /**
     * function to get the current wild card face
     * @return string value, wild card face
     */
    public String getWildCardFace() { return this.wildCardFace; }

    /**
     * function to get the discard pile
     * @return Deque of Card objects, it is the discard pile
     */
    public Deque<Card> getDiscardPile() {
        return discardPile;
    }

    /**
     * function to get the draw pile
     * @return Deque of Card objects, it is the draw pile
     */
    public Deque<Card> getDrawPile() {
        return drawPile;
    }

    /**
     * function to set the draw pile of the deck
     * @param cards Deque of Card objects, holds the draw pile cards
     */
    public void setDrawPile(Deque<Card> cards) {
        drawPile.clear();
        drawPile = cards;
    }

    /**
     * function to set the discard pile of the deck
     * @param cards Deque of Card objects, holds the discard pile cards
     */
    public void setDiscardPile(Deque<Card> cards) {
        discardPile.clear();
        discardPile = cards;
    }

    /**
     * function to set the wild card of the current deck
     * @param faceValue int value, holds the face value of the wild card
     */
    public void setWildCardFace(int faceValue) {
        if (faceValue > 9) {
            switch (faceValue) {
                case 10:
                    wildCardFace = "X";
                    break;

                case 11:
                    wildCardFace = "J";
                    break;

                case 12:
                    wildCardFace = "Q";
                    break;

                case 13:
                    wildCardFace = "K";
                    break;

                default:
                    break;
            }
        }
        else {
            wildCardFace = Integer.toString(faceValue);
        }
    }

    /**
     * function to access the top card of discard pile and remove it from the pile
     * @return Card object, top card of discard pile
     */
    public Card drawDiscardCard() {
        return discardPile.pop();
    }

    /**
     * function to get the instance of the top card of discard pile
     * @return Card object, top card of discard pile
     */
    public Card showDiscardCard() {
        return discardPile.peekFirst();
    }

    /**
     * function to draw the top card of the draw pile
     * @return Card object, top card of the draw pile
     */
    public Card drawCard() {
        return drawPile.pop();
    }

    /**
     * function to push Card into top of the discard pile
     * @param card Card object, will be added to top of discard pile
     */
    public void discard(Card card) {
        discardPile.addFirst(card);
    }

    /**
     * function to shuffle the deck of cards
     * generates new set of cards and adds them to the draw pile
     */
    public void shuffleDeck() {
        drawPile.clear();
        discardPile.clear();
        List<Card> temp = new ArrayList<>(arrangeDeck(2));
        Collections.shuffle(temp);
        for (Card a_card : temp) {
            drawPile.push(a_card);
        }
    }

    /**
     * function that converts the Cards collection to serializable string
     * @param cards Deque of Card object
     * @return string value, the sequence of cards
     */
    public String toString(Deque<Card> cards) {
        String temp = new String();
        for (Card a_card : cards) {
            temp += a_card.serializableString() + " ";
        }
        return temp;
    }

    /**
     * function that creates a single fresh deck of cards
     * @return Deque of Card objects
     */
    private Deque<Card> createDeck() {
        Deque<Card> temp = new LinkedList<>();

        for (String a_suite : suite) {
            for (String a_face : face) {
                temp.push(new Card(a_face, a_suite));
            }
        }

        for (Card a_joker : createJokers(numJokers)) {
            temp.push(a_joker);
        }

        return temp;
    }

    /**
     * function to create joker cards for the deck
     * @param num int value, holds number of jokers to create
     * @return Deque of Card object
     */
    private Deque<Card> createJokers(int num) {
        Deque<Card> temp = new LinkedList<>();
        String a_face = "J";
        for (int i = 1; i <= num; i++) {
            String a_suite = Integer.toString(i);
            temp.push(new Card(a_face, a_suite));
        }
        return temp;
    }

    /**
     * function to create and arrange deck of cards for playing the game
     * @param num int value, holds number of decks to create
     * @return Deque of Card objects
     */
    private Deque<Card> arrangeDeck(int num) {
        Deque<Card> initial = new LinkedList<>();

        for (int i = 0; i < num; i++) {
            Deque<Card> secDeck = createDeck();
            for (Card a_card : secDeck) {
                initial.push(a_card);
            }
        }
        return initial;
    }
}
