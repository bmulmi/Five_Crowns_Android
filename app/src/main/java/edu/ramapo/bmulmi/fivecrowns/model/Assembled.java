package edu.ramapo.bmulmi.fivecrowns.model;

import java.util.Vector;

public class Assembled {
    public Vector<Card> parentHand;
    public Vector<Card> bestCombo;
    public Assembled bestChild;

    public Assembled(Vector<Card> hand) {
        parentHand = hand;
        bestCombo = new Vector<>();
        bestChild = null;
    }

    public int size() {
        int size = 0;
        Assembled temp = this;
        while (temp.bestChild != null) {
            size++;
            temp = temp.bestChild;
        }
        return size;
    }
}
