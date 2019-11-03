package edu.ramapo.bmulmi.fivecrowns.model;

public class Card {
    private String face;
    private String suite;
    public Card(String face, String suite) {
        this.face = face;
        this.suite = suite;
    }
    public boolean isEqual(Card left, Card right) {
        return (left.face.equals(right.face) && left.suite.equals(right.suite));
    }
    public int getFaceValue () {
        String temp = this.face;

        if (this.isJoker()) return 50;

        if (temp.equals("X")) return 10;
        else if (temp.equals("J")) return 11;
        else if (temp.equals("Q")) return 12;
        else if (temp.equals("K")) return 13;
        else return Integer.parseInt(temp);
    }
    public String getSuite() {
        String temp = this.suite;
        return temp;
    }

    public String getFace() {
        String temp = this.face;
        return temp;
    }

    public String toString () {
        String temp;
        if (this.isJoker()) { temp = face.toLowerCase() + suite; }
        else { temp = suite.toLowerCase() + face.toLowerCase(); }
        return temp;
    }

    public boolean isJoker() {
        return (this.face.equals("J") && (this.suite.equals("1") || this.suite.equals("2") || this.suite.equals("3")));
    }
}
