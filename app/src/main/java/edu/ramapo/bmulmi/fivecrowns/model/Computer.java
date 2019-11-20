/************************************************************
 * Name: Bibhash Mulmi                                      *
 * Project: Project 3, Five Crowns Android                  *
 * Class: OPL Fall 19                                       *
 * Date: 11/20/2019                                         *
 ************************************************************/
package edu.ramapo.bmulmi.fivecrowns.model;

public class Computer extends Player{
    /**
     * selector to get the type of the player
     * @return string value "computer"
     */
    public String getType() {
        return "computer";
    }

    /**
     * uses the player's strategy to make its moves
     * @return string value, the strategy and reasoning used by computer
     */
    public String play() {
        String strategy = "";
        deck = Deck.getInstanceOfDeck(2);

        String chosenPile = whichPileToChoose();

        Card cardPicked;
        if (chosenPile.equals("discard")) {
            strategy += "Computer chose discard pile because it helped in making a run or a book.\n";
            cardPicked = deck.drawDiscardCard();
        }
        else {
            strategy += "Computer chose draw pile because discard pile card did not help in making more " +
                    "numbers of complete runs or complete books.\n";
            cardPicked = deck.drawCard();
        }
        this.hand.add(cardPicked);

        int discardIndex = whichCardToDiscard();
        strategy += "Computer discarded " + this.hand.elementAt(discardIndex).serializableString() +
                " because it made the score lower.\n";

        deck.discard(this.hand.remove(discardIndex));

        return strategy;
    }
}
