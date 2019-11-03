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
    private String wildCardFace;

    private Deck(int numDecks) {
        Deque<Card> initialDeck = arrangeDeck(numDecks);
        drawPile = initialDeck;
        discardPile = new LinkedList<>();
    }

    private final int numJokers = 3;
    private final String [] suite = new String[]{"S", "C", "D", "H", "T"};
    private final String [] face = new String[]{"3", "4", "5", "6", "7", "8",
            "9", "X", "J", "Q", "K"};

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

    private Deque<Card> createJokers(int num) {
        Deque<Card> temp = new LinkedList<>();
        String a_face = "J";
        for (int i = 1; i <= num; i++) {
            String a_suite = Integer.toString(i);
            temp.push(new Card(a_face, a_suite));
        }
        return temp;
    }

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

    public static Deck getInstanceOfDeck(int num) {
        if (deck == null) {
            deck = new Deck(num);
        }
        return deck;
    }

    public String getWildCardFace() { return this.wildCardFace; }

    public Deque<Card> getDiscardPile() {
        return discardPile;
    }

    public Deque<Card> getDrawPile() {
        return drawPile;
    }

    public void setDrawPile(Deque<Card> cards) {
        drawPile.clear();
        drawPile = cards;
    }

    public void setDiscardPile(Deque<Card> cards) {
        discardPile.clear();
        discardPile = cards;
    }

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

    public Card drawDiscardCard() {
        return discardPile.pop();
    }

    public Card showDiscardCard() {
        return discardPile.peekFirst();
    }

    public Card drawCard() {
        return drawPile.pop();
    }

    public void discard(Card card) {
        discardPile.addFirst(card);
    }

    public void shuffleDeck() {
        drawPile.clear();
        discardPile.clear();
        List<Card> temp = new ArrayList<>(arrangeDeck(2));
        Collections.shuffle(temp);
        for (Card a_card : temp) {
            drawPile.push(a_card);
        }
    }

    public String toString(Deque<Card> cards) {
        String temp = "";
        for (Card a_card : cards) {
            temp += a_card.toString() + ",";
        }
        return temp;
    }
}
