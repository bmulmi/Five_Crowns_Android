package edu.ramapo.bmulmi.fivecrowns.model;

import java.util.Vector;

public class Validate {
    /**
     * function to check if the collection of card is a valid run
     * @param a_hand vector of cards, holds the collection to be checked
     * @return boolean value
     */
    public static boolean isRun(Vector<Card> a_hand) {
        if (a_hand.size() < 3) return false;

        Vector<Card> initialHand = Player.copyCards(a_hand);
        Vector<Card> jokerCards = extractJokerCards(initialHand);
        Vector<Card> wildCards = extractWildCards(initialHand);

        if (initialHand.isEmpty()) return true;

        if (hasSameSuite(initialHand)) {
            String suite = initialHand.elementAt(0).getSuite();
            sortCards(initialHand);
            int [] missingCardsCount = {0};
            boolean potentialRun = canBeRun(initialHand, missingCardsCount);
            if (potentialRun && missingCardsCount[0] <= (jokerCards.size() + wildCards.size())) {
                return true;
            }
        }
        return false;
    }

    /**
     * function to check if the collection of card is a valid book
     * @param a_hand vector of cards, holds the collection to be checked
     * @return boolean value
     */
    public static boolean isBook(Vector<Card> a_hand) {
        if (a_hand.size() < 3) return false;

        Deck deck = Deck.getInstanceOfDeck(2);
        String wildCard = deck.getWildCardFace();
        Vector<Card> initialHand = Player.copyCards(a_hand);
        Vector<Card> jokerCards = extractJokerCards(initialHand);
        Vector<Card> wildCards = extractWildCards(initialHand);

        if ((initialHand.isEmpty())) return true;

        for (int i = 0; i < initialHand.size() - 1; i++) {
            if (initialHand.elementAt(i).getFaceValue() != initialHand.elementAt(i+1).getFaceValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * function to check if collection has same suite of cards
     * @param a_hand vector of cards, holds the collection of cards
     * @return boolean value
     */
    public static boolean hasSameSuite(Vector<Card> a_hand) {
        if (a_hand.isEmpty()) return true;
        String a_suite = a_hand.elementAt(0).getSuite();
        for (Card each : a_hand) {
            if (!each.getSuite().equals(a_suite)) {
                return false;
            }
        }
        return true;
    }

    /**
     * function to check if collection of card has potential to be run
     * @param a_cards vector of cards, holds the collection of cards
     * @param missingCardsCount int value, holds the missing card to complete the run
     * @return boolean value
     */
    public static boolean canBeRun(Vector<Card> a_cards, int[] missingCardsCount) {
        for (int i = 0; i < a_cards.size() - 1; i++) {
            if (a_cards.elementAt(i).getFaceValue() == a_cards.elementAt(i+1).getFaceValue()-1)
                continue;
            if (a_cards.elementAt(i).getFaceValue() == a_cards.elementAt(i+1).getFaceValue())
                return false;
            if (a_cards.elementAt(i).getFaceValue() < a_cards.elementAt(i+1).getFaceValue())
                missingCardsCount[0] += a_cards.elementAt(i+1).getFaceValue() - a_cards.elementAt(i).getFaceValue()-1;
        }
        return true;
    }

    /**
     * function to calculate the score of hand
     * @param a_hand vector of cards, holds the collection of card to calculate its score
     * @return int value, score of the hand
     */
    public static int calculateScore(Vector<Card> a_hand) {
        int score = 0;
        Deck deck = Deck.getInstanceOfDeck(2);
        String wildCard = deck.getWildCardFace();

        for (Card each : a_hand) {
            int currCardVal;

            if (each.getFace().equals(wildCard)) {
                currCardVal = 20;
            }
            else if (each.isJoker()) {
                currCardVal = 50;
            }
            else {
                currCardVal = each.getFaceValue();
            }

            score += currCardVal;
        }
        return score;
    }

    /**
     * function to sort in ascending order the collection of card
     * @param a_hand vector of cards, holds the collection of card to be sorted
     */
    public static void sortCards(Vector<Card> a_hand) {
        for (int i = 0; i < a_hand.size(); i++) {
            for (int j = 0; j < a_hand.size() - i - 1; j++) {
                if (a_hand.elementAt(j).getFaceValue() > a_hand.elementAt(j+1).getFaceValue()) {
                    swapCards(a_hand, j, j+1);
                }
            }
        }
    }

    /**
     * function to swap two cards
     * @param a_hand vector of cards, holds the collection in which to sort
     * @param left int value, holds the index of card to swap
     * @param right int value, holds the index of card to swap
     */
    public static void swapCards(Vector<Card> a_hand, int left, int right) {
        Card leftCard = a_hand.elementAt(left);
        Card rightCard = a_hand.elementAt(right);
        a_hand.setElementAt(rightCard, left);
        a_hand.setElementAt(leftCard, right);
    }

    /**
     * function to extract joker cards from the passed hand
     * @param a_hand vector of cards, holds the collection of cards
     * @return vector of cards
     */
    public static Vector<Card> extractJokerCards(Vector<Card> a_hand) {
        Vector<Card> temp = new Vector<>();
        int handSize = a_hand.size();

        for (int i = 0; i < handSize; i++) {
            if (a_hand.elementAt(i).isJoker()) {
                temp.add(a_hand.remove(i));
                i--;
                handSize--;
            }
        }

        return temp;
    }

    /**
     * function to extract wild cards form the passed hand
     * @param a_hand vector of cards, holds the collection of cards
     * @return vector of cards
     */
    public static Vector<Card> extractWildCards(Vector<Card> a_hand) {
        Vector<Card> temp = new Vector<>();
        int handSize = a_hand.size();
        Deck deck = Deck.getInstanceOfDeck(2);
        String wildCard = deck.getWildCardFace();

        for (int i = 0; i < handSize; i++) {
            if (a_hand.elementAt(i).getFace().equals(wildCard)) {
                temp.add(a_hand.remove(i));
                i--;
                handSize--;
            }
        }

        return temp;
    }
}
