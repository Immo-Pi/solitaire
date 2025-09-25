package org.yourcompany.yourproject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Pile {
    private final List<Card> cards = new ArrayList<>();
    private final PileType type;

    public Pile(PileType type) {
        this.type = type;
    }

    public void shuffle() {
    Collections.shuffle(cards);
    }
    
    public List<Card> getSequenceFrom(Card startCard) {
    int index = cards.indexOf(startCard);
    if (index == -1) return new ArrayList<>();
    return new ArrayList<>(cards.subList(index, cards.size()));
    } 
    
    public List<Card> removeSequenceFrom(Card startCard) {
    int index = cards.indexOf(startCard);
    if (index == -1) return new ArrayList<>();
    List<Card> seq = new ArrayList<>(cards.subList(index, cards.size()));
    cards.subList(index, cards.size()).clear();
    return seq;
    }

    public PileType getType() {
        return type;
    }

    public void add(Card c) {
        cards.add(c);
    }

    public Card draw() {
        if (cards.isEmpty()) return null;
        return cards.remove(cards.size() - 1);
    }

    public Card top() {
        if (cards.isEmpty()) return null;
        return cards.get(cards.size() - 1);
    }

    public Card removeTop() {
        return draw();
    }

    public Card get(int i) {
        return cards.get(i);
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public boolean canAccept(Card c) {
        switch (type) {
            case FOUNDATION:
                if (isEmpty()) return c.getRank() == 1;
                Card f = top();
                return f.getSuit() == c.getSuit()
                       && c.getRank() == f.getRank() + 1;
            case TABLEAU:
                if (isEmpty()) return c.getRank() == 13;
                Card t = top();
                return t.getColor() != c.getColor()
                       && c.getRank() == t.getRank() - 1;
            default:
                return false;
        }
    }
}