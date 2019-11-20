/************************************************************
 * Name: Bibhash Mulmi                                      *
 * Project: Project 3, Five Crowns Android                  *
 * Class: OPL Fall 19                                       *
 * Date: 11/20/2019                                         *
 ************************************************************/
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

    /**
     * calculates the depth of the Assembled struct tree
     * @return int value, depth of the tree
     */
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
