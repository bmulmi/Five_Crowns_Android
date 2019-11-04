package edu.ramapo.bmulmi.fivecrowns.model;

import java.util.Vector;

public class Validate {
    public static boolean isRun(Vector<Card> a_hand) {
        if (a_hand.size() < 3) return false;

        Vector<Card> initialHand = a_hand;
        Vector<Card> jokerCards = extractJokerCards(initialHand);
        Vector<Card> wildCards = extractWildCards(initialHand);

        if (initialHand.isEmpty()) return true;

        if (hasSameSuite(initialHand)) {
            String suite = initialHand.elementAt(0).getSuite();
            sortCards(initialHand);
            int missingCardsCount = 0;
            boolean potentialRun = canBeRun(initialHand, missingCardsCount);
            if (potentialRun && missingCardsCount <= (jokerCards.size() + wildCards.size())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBook(Vector<Card> a_hand) {
        if (a_hand.size() < 3) return false;
        Deck deck = Deck.getInstanceOfDeck(2);
        String wildCard = deck.getWildCardFace();
        Vector<Card> initialHand = a_hand;
        Vector<Card> jokerCards = extractJokerCards(initialHand);
        Vector<Card> wildCards = extractWildCards(initialHand);

        if ((initialHand.isEmpty())) return true;

        for (int i = 0; i < initialHand.size(); i++) {
            if (initialHand.elementAt(i).getFaceValue() != initialHand.elementAt(i+1).getFaceValue()) {
                return false;
            }
        }
        return true;
    }

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

    public static boolean canBeRun(Vector<Card> a_cards, int missingCardsCount) {
        for (int i = 0; i < a_cards.size() - 1; i++) {
            if (a_cards.elementAt(i).getFaceValue() == a_cards.elementAt(i+1).getFaceValue()-1)
                continue;
            if (a_cards.elementAt(i).getFaceValue() == a_cards.elementAt(i+1).getFaceValue())
                return false;
            if (a_cards.elementAt(i).getFaceValue() < a_cards.elementAt(i+1).getFaceValue())
                missingCardsCount += a_cards.elementAt(i+1).getFaceValue() - a_cards.elementAt(i).getFaceValue()-1;
        }
        return true;
    }

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

    public static void sortCards(Vector<Card> a_hand) {
        for (int i = 0; i < a_hand.size(); i++) {
            for (int j = 0; j < a_hand.size() - i - 1; j++) {
                if (a_hand.elementAt(j).getFaceValue() > a_hand.elementAt(j+1).getFaceValue()) {
                    swapCards(a_hand, j, j+1);
                }
            }
        }
    }

    public static void swapCards(Vector<Card> a_hand, int left, int right) {
        Card leftCard = a_hand.elementAt(left);
        Card rightCard = a_hand.elementAt(right);
        a_hand.setElementAt(rightCard, left);
        a_hand.setElementAt(leftCard, right);
    }

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
