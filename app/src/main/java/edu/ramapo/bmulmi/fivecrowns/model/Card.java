package edu.ramapo.bmulmi.fivecrowns.model;

public class Card {
    private String face;
    private String suite;
    public Card() {
        this("","");
    }

    public Card(String face, String suite) {
        this.face = face;
        this.suite = suite;
    }

    /**
     * checks if cards are equal
     * @param left Card object
     * @param right Card object
     * @return boolean value, true if cards have same face and suite, else false
     */
    public boolean isEqual(Card left, Card right) {
        return (left.face.equals(right.face) && left.suite.equals(right.suite));
    }

    /**
     * calculates the face value of the card
     * @return int value, the face value of card
     */
    public int getFaceValue () {
        String temp = this.face;

        if (this.isJoker()) return 50;

        if (temp.equals("X")) return 10;
        else if (temp.equals("J")) return 11;
        else if (temp.equals("Q")) return 12;
        else if (temp.equals("K")) return 13;
        else return Integer.parseInt(temp);
    }

    /**
     * selector for suite of the card
     * @return string value, the suite of the card
     */
    public String getSuite() {
        String temp = this.suite;
        return temp;
    }

    /**
     * selector for face of the card
     * @return string value, the face of the card
     */
    public String getFace() {
        String temp = this.face;
        return temp;
    }

    /**
     * checks if the card is a joker card
     * @return boolean value, true if joker, else false
     */
    public boolean isJoker() {
        return (this.face.equals("J") && (this.suite.equals("1") || this.suite.equals("2") || this.suite.equals("3")));
    }

    /**
     * selector to get card values as face+suite
     * @return string value, face+suite of the card
     */
    public String serializableString() {
        return face + suite;
    }

    /**
     * utility function to convert the card to string object
     * @return string value, suite+face of the card
     */
    public String toString () {
        String temp;
        if (this.isJoker()) { temp = face.toLowerCase() + suite; }
        else { temp = suite.toLowerCase() + face.toLowerCase(); }
        return temp;
    }
}
