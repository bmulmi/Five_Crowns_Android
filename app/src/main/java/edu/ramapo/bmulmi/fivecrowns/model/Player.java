package edu.ramapo.bmulmi.fivecrowns.model;

import android.view.ActionProvider;

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
    public String getType() {
        return "player";
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

    public void addToHand(Card card) {
        this.hand.add(card);
    }

    public void clearHand() {
        this.hand.clear();
    }

    public Card removeFromHand(int index) {
        return this.hand.remove(index);
    }

    public String getSerializableHand() {
        String temp = new String();
        for (Card each : hand) {
            temp += each.serializableString() + " ";
        }
        return temp;
    }

    public int getHandScore() {
        if (canGoOut(hand)) {
            return 0;
        }

        Vector<Vector<Card>> possibleCombos = assemblePossibleHand();

        Vector<Card> scoreHand = possibleCombos.lastElement();
        int currScore = Validate.calculateScore(scoreHand);
        return currScore;
    }

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

    public boolean canGoOut(Vector<Card> a_hand) {
        Assembled assembledHand = new Assembled(hand);
        int temp = getLowestScore(a_hand, assembledHand);
        return temp == 0;
    }

    private String whichPileToChoose() {
        deck = Deck.getInstanceOfDeck(2);

        // stores the discard pile card
        Card pickedCard = deck.showDiscardCard();

        String wildCard = deck.getWildCardFace();
        Vector<Card> copyHand = this.hand;
        Assembled assembledHand = new Assembled(copyHand);
        int scr = getLowestScore(copyHand, assembledHand);
        boolean chooseDiscard = false;

        // pick the discard card
        copyHand.add(pickedCard);

        // remove every card from previous hand AND
        // check if the player will have more books or runs with lower score
        // with the newly picked card
        for (int i = 0; i < hand.size(); i++) {
            Vector<Card> temp = copyHand;
            temp.remove(i);
            Assembled curr_assembledHand = new Assembled(temp);
            int curr_scr = getLowestScore(temp, curr_assembledHand);
            if (this.hand.size() < 6 && (!curr_assembledHand.bestCombo.isEmpty() && curr_assembledHand.size() >= assembledHand.size() && curr_scr < scr)){
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

    private Card whichCardToDiscard() {
        Vector<Card> currHand = hand;
        Card card_r = new Card();
        int currScore = 99999;

        deck = Deck.getInstanceOfDeck(2);
        String wildCard = deck.getWildCardFace();

        for (int i = 0; i < currHand.size(); i++) {
            Vector<Card> temp = currHand;

            if (currHand.elementAt(i).isJoker() || currHand.elementAt(i).getFace().equals(wildCard))
                continue;

            temp.remove(i);

            Assembled assembledHand = new Assembled(temp);

            int tempScr = getLowestScore(temp, assembledHand);
            if (tempScr < currScore) {
                currScore = tempScr;
                card_r = currHand.elementAt(i);
            }
        }
        return card_r;
    }

    private Vector<Vector<Card>> assemblePossibleHand() {
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

    private int getLowestScore(Vector<Card> a_hand, Assembled assembled_hands) {
        int minScore = 99999;

//        Vector<Card> bestCombo;
        Vector<Card> t_hand = a_hand;
        Validate.sortCards(t_hand);
        Vector<Vector<Card>> booksAndRuns = getBooksAndRuns(t_hand);

        if (booksAndRuns.isEmpty()) {
            Assembled temp_assembled = new Assembled(t_hand);
            assembled_hands.bestChild = temp_assembled;
            assembled_hands.bestCombo = a_hand;
            return Validate.calculateScore(a_hand);
        }
        else {
            for (Vector<Card> each : booksAndRuns) {
                Vector<Card> temp_hand = a_hand;
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

    private Vector<Vector<Card>> getBooksAndRuns(Vector<Card> a_hand) {
        Vector<Vector<Card>> temp = new Vector<>();
        Vector<Card> temp_hand = a_hand;
        getBooksOrRuns(temp_hand, temp, 0);

        Vector<Vector<Card>> temp_sameSuiteHands = getSameSuiteHands(temp_hand);
        for (Vector<Card> each : temp_sameSuiteHands) {
            getBooksOrRuns(each, temp, 1);
        }
        return temp;
    }

    private void copyHand(Vector<Card> from, Vector<Card> to, int start, int end){
        for (int i = start; i < end; i++) {
            to.add(from.elementAt(i));
        }
    }

    private void getBooksOrRuns(Vector<Card> a_hand, Vector<Vector<Card>> a_collection, int check_type) {
        Vector<Card> temp_hand = a_hand;
        Vector<Card> temp_jokers = Validate.extractJokerCards(temp_hand);
        Vector<Card> temp_wilds = Validate.extractWildCards(temp_hand);
        int totalJnW = temp_jokers.size() + temp_wilds.size();
        boolean jokerAdnWildExist = totalJnW != 0;

        if (temp_hand.isEmpty() && jokerAdnWildExist) {
            a_collection.add(a_hand);
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
        temp_hand = a_hand;
        temp_jokers = Validate.extractJokerCards(temp_hand);
        temp_wilds = Validate.extractWildCards(temp_hand);
        if (!temp_jokers.isEmpty() && !temp_wilds.isEmpty()) {
            combineTwoAndCheck(temp_hand, temp_wilds, temp_jokers, a_collection, check_type);
        }
    }

    private void combineAndCheck(Vector<Card> a_hand, Vector<Card> a_cards, Vector<Vector<Card>> a_collection, int check_type) {
        for (int i = 0; i < a_hand.size(); i++) {
            for (int j = 0; j < a_hand.size() + 1 - i; j++) {
                Vector<Card> curr = new Vector<>();
                copyHand(a_hand, curr, i, i+j);
                Vector<Card> cardsToCombine = a_cards;

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

    private void combineTwoAndCheck(Vector<Card> a_hand, Vector<Card> a_cards1, Vector<Card> a_cards2,
                                    Vector<Vector<Card>> a_collection, int check_type) {
        for (int i = 0; i < a_hand.size(); i++) {
            for (int j = 0; j < a_hand.size() + 1 - i; j++) {
                Vector<Card> curr = new Vector<>();
                Vector<Card> temp_cards1 = a_cards1;

                // check this group of cards with wild cards for runs or books
                while(!temp_cards1.isEmpty()) {
                    Card currWilds = temp_cards1.elementAt(0);
                    temp_cards1.remove(0);
                    curr.add(currWilds);

                    Vector<Card> copy_curr = curr;
                    Vector<Card> temp_cards2 = a_cards2;

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
}
