package edu.ramapo.bmulmi.fivecrowns.model;

public class Computer extends Player{
    public String getType() {
        return "computer";
    }

    public String play() {
        String strategy = "";
        deck = Deck.getInstanceOfDeck(2);

        String chosenPile = whichPileToChoose();

        Card cardPicked = new Card();
        if (chosenPile.equals("discard")) {
            strategy += "Computer chose discard pile because it helped in making a run or a book.\n";
            cardPicked = deck.drawDiscardCard();
        }
        else {
            strategy += "Computer chose draw pile because discard pile card did not help in making more" +
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
