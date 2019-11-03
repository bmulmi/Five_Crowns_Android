package edu.ramapo.bmulmi.fivecrowns.model;

import java.util.Vector;

public class Player {
    protected int score;
    protected boolean goneOut;
    protected Deck deck;
    protected Vector<Card> hand;

    public Player() {
        this.score = 0;
        this.hand = new Vector<>();
        hand.clear();
        this.goneOut = false;
    }

    public int getScore() {
        return score;
    }

    public Vector<Card> getHand() {
        return hand;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void updateScore(int score) {
        this.score += score;
    }

    public void setHand(Vector<Card> hand) {
        this.hand = hand;
    }

    public void clearHand() {
        this.hand.clear();
    }

    public void removeFromHand(int index) {
        this.hand.remove(index);
    }

}
