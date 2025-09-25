package org.yourcompany.yourproject;
import java.awt.Color;

public class Card {
    public enum Suit { HEARTS("♥"), DIAMONDS("♦"), CLUBS("♣"), SPADES("♠");
        private final String symbol;
        Suit(String s) { symbol = s; }
        public String symbol() { return symbol; }
    }

    private final Suit suit;
    private final int rank;
    private final Color color;

    public Card(Suit suit, int rank) {
        this.suit = suit;
        this.rank = rank;
        this.color = (suit == Suit.HEARTS || suit == Suit.DIAMONDS)
                     ? Color.RED : Color.BLACK;
    }

    public Suit getSuit()  { return suit; }
    public int getRank()   { return rank; }
    public Color getColor(){ return color; }

    @Override
    public String toString() {
        String r;
        switch (rank) {
            case 1:  r = "A"; break;
            case 11: r = "J"; break;
            case 12: r = "Q"; break;
            case 13: r = "K"; break;
            default: r = String.valueOf(rank);
        }
        return r + suit.symbol();
    }
}
