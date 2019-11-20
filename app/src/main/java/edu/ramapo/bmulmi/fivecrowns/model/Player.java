/************************************************************
 * Name: Bibhash Mulmi                                      *
 * Project: Project 3, Five Crowns Android                  *
 * Class: OPL Fall 19                                       *
 * Date: 11/20/2019                                         *
 ************************************************************/
package edu.ramapo.bmulmi.fivecrowns.model;

import java.util.Vector;

public class Player {
    protected int score;
    protected boolean goneOut;
    protected Deck deck;
    protected Vector<Card> hand;

    /**
     * Constructor for player class
     */
    public Player() {
        this.score = 0;
        this.hand = new Vector<>();
        hand.clear();
        this.goneOut = false;
    }

    /**
     * function to get the type of the player
     * @return string value, holds the player type
     */
    public String getType() {
        return "player";
    }

    /**
     * function to get the score of the player
     * @return int value, holds the player score
     */
    public int getScore() {
        return this.score;
    }

    /**
     * function to get the hand of the player
     * @return vector of card objects, holds the hand cards of the player
     */
    public Vector<Card> getHand() {
        return this.hand;
    }

    /**
     * function to get the current hand's score of player
     * @return int value, holds the score of the hand
     */
    public int getHandScore() {
        if (canGoOut()) {
            return 0;
        }

        Vector<Vector<Card>> possibleCombos = assemblePossibleHand();

        Vector<Card> scoreHand = possibleCombos.lastElement();
        return Validate.calculateScore(scoreHand);
    }

    /**
     * function to get the hand as string object for serialization
     * @return string value, holds the hand cards as string
     */
    public String getSerializableHand() {
        String temp = new String();
        for (Card each : hand) {
            temp += each.serializableString() + " ";
        }
        return temp;
    }

    /**
     * function to set the score of the player
     * @param score int value, score to be set for the player
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * function to add to the current score of the player
     * @param score int value, score to be added
     */
    public void updateScore(int score) {
        this.score += score;
    }

    /**
     * function to set the hand of the player
     * @param hand vector of card objects, holds the vector to be set as hand
     */
    public void setHand(Vector<Card> hand) {
        this.hand = hand;
    }

    /**
     * function to add the card to the player's hand
     * @param card Card object, holds the card to be added to the hand
     */
    public void addToHand(Card card) {
        this.hand.add(card);
    }

    /**
     * function to clone a vector of card objects into a new vector
     * @param a_hand vector of card objects to be copied into a new vector
     * @return vector of card objects
     */
    public static Vector<Card> copyCards(Vector<Card> a_hand) {
        Vector<Card> temp = new Vector<>();
        for (Card each : a_hand) {
            temp.add(each);
        }
        return temp;
    }

    /**
     * function to remove card from the hand
     * @param index int value, holds the index value that is to be removed
     * @return Card object that was removed from the hand
     */
    public Card removeFromHand(int index) {
        return this.hand.remove(index);
    }

    /**
     * function to remove collection of cards from the hand
     * @param a_hand vector of card objects, holds the collection from where
     *              the cards are to be removed
     * @param a_cards vector of card objects, holds the card collection that is
     *                to be removed
     */
    public void removeCards(Vector<Card> a_hand, Vector<Card> a_cards) {
        for (Card each : a_cards) {
            for (int i = 0; i < a_hand.size(); i++) {
                if (each.isEqual(each, a_hand.elementAt(i))) {
                    a_hand.removeElementAt(i);
                    break;
                }
            }
        }
    }

    /**
     * checks if the player can go out
     * @return boolean value, holds true if score of hand is 0, else false
     */
    public boolean canGoOut() {
        Assembled assembledHand = new Assembled(hand);
        int temp = getLowestScore(this.hand, assembledHand);
        return temp == 0;
    }

    /* **************************************************************
    Source code to use computer strategy and provide hint for players
    *************************************************************** */

    /**
     * function to build suggestion when user asks help to choose a pile
     * to draw a card.
     * @return string value, holds "draw" or "discard" according to the strategy result
     */
    protected String whichPileToChoose() {
        deck = Deck.getInstanceOfDeck(2);

        // stores the discard pile card
        Card pickedCard = deck.showDiscardCard();

        String wildCard = deck.getWildCardFace();
        Vector<Card> copyHand = copyCards(this.hand);
        Assembled assembledHand = new Assembled(copyHand);
        // stores the score of current hand before picking up the card
        int scr = getLowestScore(copyHand, assembledHand);
        boolean chooseDiscard = false;


        if (pickedCard.isJoker() || pickedCard.getFace().equals(wildCard))
            return "discard";

        // pick the discard card
        copyHand.add(pickedCard);

        // remove every card from previous hand AND
        // check if the player will have more books or runs with lower score
        // with the newly picked card
        for (int i = 0; i < hand.size(); i++) {
            Vector<Card> temp = copyCards(copyHand);

            if (temp.elementAt(i).isJoker() || temp.elementAt(i).getFace().equals(wildCard))
                continue;

            temp.remove(i);
            Assembled curr_assembledHand = new Assembled(temp);
            int curr_scr = getLowestScore(temp, curr_assembledHand);
            if (this.hand.size() < 6 && (!curr_assembledHand.bestCombo.isEmpty() && curr_assembledHand.bestCombo.size() > assembledHand.bestCombo.size() && curr_scr < scr)){
                    assembledHand = curr_assembledHand;
                    scr = curr_scr;
                    chooseDiscard = true;
            }
            else if (!curr_assembledHand.bestCombo.isEmpty() && curr_assembledHand.size() > assembledHand.size() && curr_scr < scr) {
                assembledHand = curr_assembledHand;
                scr = curr_scr;
                chooseDiscard = true;
            }
        }

        if (chooseDiscard) {
            return "discard";
        }
        else {
            return "draw";
        }
    }

    /**
     * function to build suggestion when user asks help to choose a card
     * to discard from hand
     * @return int value, holds the index of the card of the hand to discard
     */
    protected int whichCardToDiscard() {
        Vector<Card> currHand = this.hand;
        int card_r = -1;
        int currScore = 99999;

        deck = Deck.getInstanceOfDeck(2);
        String wildCard = deck.getWildCardFace();

        for (int i = 0; i < currHand.size(); i++) {
            Vector<Card> temp = copyCards(currHand);

            if (currHand.elementAt(i).isJoker() || currHand.elementAt(i).getFace().equals(wildCard))
                continue;

            temp.remove(i);

            Assembled assembledHand = new Assembled(temp);

            int tempScr = getLowestScore(temp, assembledHand);
            if (tempScr < currScore) {
                currScore = tempScr;
                card_r = i;
            }
        }
        return card_r;
    }

    /**
     * function to assemble all the runs and books
     * @return vector of vector of cards, holds the collection of runs and books
     * of the hand
     */
    protected Vector<Vector<Card>> assemblePossibleHand() {
        Vector<Card> currHand = this.hand;
        Assembled assembledHand = new Assembled(currHand);

        int scr = getLowestScore(currHand, assembledHand);

        Vector<Card> temp = assembledHand.bestCombo;
        Vector<Vector<Card>> ret = new Vector<>();
        while(!temp.isEmpty()) {
            ret.add(temp);
            assembledHand = assembledHand.bestChild;
            temp = assembledHand.bestCombo;
        }

        boolean isSpecial = true;
        Vector<Card> lastCombo = ret.lastElement();
        deck = Deck.getInstanceOfDeck(2);

        for(Card each : lastCombo) {
            if (!each.isJoker() && !each.getFace().equals(deck.getWildCardFace())) {
                isSpecial = false;
            }
        }

        if (isSpecial) {
            ret.remove(ret.size()-1);
            for (Card each : lastCombo) {
                ret.lastElement().add(each);
            }
        }

        return ret;
    }

    /* *******************************************
    Source code for the main strategy of the game
    ******************************************** */

    /**
     * function to get the score of the current hand and compute best runs and books
     * @param a_hand vector of card objects, usually holds the hand card of the player
     * @param assembled_hands Assembled struct, holds the assembled struct of the hand
     * @return int value, holds the score of the hand after arranging all possible runs
     * and books
     */
    private int getLowestScore(Vector<Card> a_hand, Assembled assembled_hands) {
        int minScore = 99999;

//        Vector<Card> bestCombo;
        Vector<Card> t_hand = copyCards(a_hand);
        Validate.sortCards(t_hand);
        Vector<Vector<Card>> booksAndRuns = getBooksAndRuns(t_hand);

        if (booksAndRuns.isEmpty()) {
            assembled_hands.bestChild = new Assembled(t_hand);
            assembled_hands.bestCombo = a_hand;
            return Validate.calculateScore(a_hand);
        }
        else {
            for (Vector<Card> each : booksAndRuns) {
                Vector<Card> temp_hand = copyCards(a_hand);
                removeCards(temp_hand, each);
                Assembled temp_assembled = new Assembled(temp_hand);
                int scr = getLowestScore(temp_hand, temp_assembled);
                if (scr < minScore) {
                    minScore = scr;
                    assembled_hands.bestChild = temp_assembled;
                    assembled_hands.bestCombo = each;
//                    bestCombo = each;
                }
            }
        }
        return minScore;
    }

    /**
     * function to get collection of books and runs of the hand
     * @param a_hand vector of card objects, holds the collection of cards
     * @return vector of collection of runs and books
     */
    private Vector<Vector<Card>> getBooksAndRuns(Vector<Card> a_hand) {
        Vector<Vector<Card>> temp = new Vector<>(new Vector());
        getBooksOrRuns(a_hand, temp, 0);

        Vector<Vector<Card>> temp_sameSuiteHands = getSameSuiteHands(a_hand);
        for (Vector<Card> each : temp_sameSuiteHands) {
            getBooksOrRuns(each, temp, 1);
        }
        return temp;
    }


    /**
     * function that complements the getBooksAndRuns function
     * @param a_hand vector of card objects, holds the collection from which
     *               to generate books and runs
     * @param a_collection vector of collection of card objects, stores books
     *                     and runs found by the function
     * @param check_type int value, holds 0 to check for books, 1 for runs
     */
    private void getBooksOrRuns(Vector<Card> a_hand, Vector<Vector<Card>> a_collection, int check_type) {
        Vector<Card> temp_hand = copyCards(a_hand);
        Vector<Card> temp_jokers = Validate.extractJokerCards(temp_hand);
        Vector<Card> temp_wilds = Validate.extractWildCards(temp_hand);
        int totalJnW = temp_jokers.size() + temp_wilds.size();
        boolean jokerAdnWildExist = totalJnW != 0;

        if (temp_hand.isEmpty() && jokerAdnWildExist) {
            Vector<Card> t_hand = copyCards(a_hand);
            a_collection.add(t_hand);
            return;
        }

        for (int i = 0; i < temp_hand.size(); i++) {
            for (int j = 1; j < temp_hand.size() + 1 - i; j++) {
                Vector<Card> curr = new Vector<>();
                copyHand(temp_hand, curr, i, i+j);
                if (check_type == 0) {
                    if (Validate.isBook(curr)) {
                        a_collection.add(curr);
                    }
                }
                else {
                    if (Validate.isRun(curr)) {
                        a_collection.add(curr);
                    }
                }
            }
        }

        // when there are no jokers or wildcards in the hand
        // no need to go further than this command
        if (!jokerAdnWildExist) {
            return;
        }

        // ------------------------------------------------
        // STEP 2: check with Jokers in each combination
        if (!temp_jokers.isEmpty()) {
            combineAndCheck(temp_hand, temp_jokers, a_collection, check_type);
        }

        // STEP 3: check with Wild Cards in each combination
        if (!temp_wilds.isEmpty()) {
            combineAndCheck(temp_hand, temp_wilds, a_collection, check_type);
        }

        // STEP 4: check with Jokers AND WildCards in each combination
        // reset the variables
        temp_hand = copyCards(a_hand);
        temp_jokers = Validate.extractJokerCards(temp_hand);
        temp_wilds = Validate.extractWildCards(temp_hand);
        if (!temp_jokers.isEmpty() && !temp_wilds.isEmpty()) {
            combineTwoAndCheck(temp_hand, temp_wilds, temp_jokers, a_collection, check_type);
        }
    }

    /**
     * function to get collection of same suite cards of the hand
     * @param a_hand vector of card objects, holds the collection of cards
     * @return vector of collection of same suite cards
     */
    private Vector<Vector<Card>> getSameSuiteHands(Vector<Card> a_hand) {
        String [] suite = new String[] {"S", "C", "D", "H", "T"};
        Vector<Card> jokers = Validate.extractJokerCards(a_hand);
        Vector<Card> wilds = Validate.extractWildCards(a_hand);
        Vector<Vector<Card>> temp = new Vector<>();

        for (String e_suite : suite) {
            Vector<Card> curr = new Vector<>();
            for (Card e_card : a_hand) {
                if (e_card.getSuite().equals(e_suite)) {
                    curr.add(e_card);
                }
            }
            // add the jokers or wild cards only when there are 2 or more cards
            if (curr.size() > 1) {
                for (Card each : jokers) {
                    curr.add(each);
                }
                for (Card each : wilds) {
                    curr.add(each);
                }
                temp.add(curr);
            }
        }
        return temp;
    }

    /**
     * function that combines wild cards or jokers to the passed hand to
     * generate possible possible books and runs
     * @param a_hand vector of card objects, holds the collection of cards
     * @param a_cards vector of card objects, holds the special cards
     * @param a_collection vector of collection of card objects, stores books
     *                     and runs found by the function
     * @param check_type int value, holds 0 to check for books, 1 for runs
     */
    private void combineAndCheck(Vector<Card> a_hand, Vector<Card> a_cards, Vector<Vector<Card>> a_collection, int check_type) {
        for (int i = 0; i < a_hand.size(); i++) {
            for (int j = 0; j < a_hand.size() + 1 - i; j++) {
                Vector<Card> curr = new Vector<>();
                copyHand(a_hand, curr, i, i+j);
                Vector<Card> cardsToCombine = copyCards(a_cards);

                while(!cardsToCombine.isEmpty()) {
                    Card currCard = cardsToCombine.elementAt(0);
                    cardsToCombine.remove(0);

                    curr.add(currCard);
                    if(check_type == 0){
                        if(Validate.isBook(curr)) {
                            a_collection.add(curr);
                        }
                    }
                    else {
                        if (Validate.isRun(curr)) {
                            a_collection.add(curr);
                        }
                    }
                }
            }
        }
    }

    /**
     * function that combines both wild cards and jokers to the passed hand to
     * generate possible possible books and runs
     * @param a_hand vector of card objects, holds the collection of cards
     * @param a_cards1 vector of card objects, holds wild cards
     * @param a_cards2 vector of card objects, holds joker cards
     * @param a_collection vector of collection of card objects, stores books
     *                      and runs found by the function
     * @param check_type int value, holds 0 to check for books, 1 for runn
     */
    private void combineTwoAndCheck(Vector<Card> a_hand, Vector<Card> a_cards1, Vector<Card> a_cards2,
                                    Vector<Vector<Card>> a_collection, int check_type) {
        for (int i = 0; i < a_hand.size(); i++) {
            for (int j = 0; j < a_hand.size() + 1 - i; j++) {
                Vector<Card> curr = new Vector<>();
                Vector<Card> temp_cards1 = copyCards(a_cards1);

                // check this group of cards with wild cards for runs or books
                while(!temp_cards1.isEmpty()) {
                    Card currWilds = temp_cards1.elementAt(0);
                    temp_cards1.remove(0);
                    curr.add(currWilds);

                    Vector<Card> copy_curr = copyCards(curr);
                    Vector<Card> temp_cards2 = copyCards(a_cards2);

                    while(!temp_cards2.isEmpty()) {
                        Card currJoker = temp_cards2.elementAt(0);
                        temp_cards2.remove(0);
                        copy_curr.add(currJoker);
                        if (check_type == 0) {
                            if (Validate.isBook(copy_curr)) {
                                a_collection.add(copy_curr);
                            }
                        }
                        else {
                            if (Validate.isRun(copy_curr)) {
                                a_collection.add(copy_curr);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * function that copies certain part of a vector of cards to another vector
     * @param from vector of card objects, holds cards to be copied from
     * @param to vector of card objects, holds cards to copy into
     * @param start int value, holds starting index to copy from
     * @param end int value, holds last index to copy from
     */
    private void copyHand(Vector<Card> from, Vector<Card> to, int start, int end){
        for (int i = start; i < end; i++) {
            to.add(from.elementAt(i));
        }
    }

}
