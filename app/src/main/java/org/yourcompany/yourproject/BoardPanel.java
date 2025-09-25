package org.yourcompany.yourproject;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BoardPanel extends JPanel implements MouseListener {
    private static final int CARD_W = 60;
    private static final int CARD_H = 90;
    private static final int DX = 80;
    private static final int DY = 20;

    private Deck deck;
    private Pile stock, waste;
    private List<Pile> foundations, tableaus;
    private Card selectedCard;
    private Pile selectedPile;
    private List<Card> selectedSequence;

    public BoardPanel() {
        initGame();
        addMouseListener(this);
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.GREEN.darker().darker().darker());
    }
    //Startet das Spiel und erstellt die Karten plus Stapel und teilt die Karten aus
    private void initGame() {

        //Erstellt und mischt das Kartendeck
        deck = new Deck();
        deck.shuffle();

        // Initialisiert die Kartenstapel
        stock = new Pile(PileType.STOCK);
        waste = new Pile(PileType.WASTE);
        foundations = new ArrayList<>();
        tableaus = new ArrayList<>();

        // Erstellt die 4 Ablagestapel
        for (int i = 0; i < 4; i++) {
            foundations.add(new Pile(PileType.FOUNDATION));
        }
        // Erstellt die 7 Tableau-Stapel und teilt die Karten aus
        for (int i = 0; i < 7; i++) {
            Pile t = new Pile(PileType.TABLEAU);
            for (int j = 0; j <= i; j++) {
                t.add(deck.draw());
            }
            tableaus.add(t);
        }
        // Restliche Karten in den Stock legen
        while (!deck.isEmpty()) {
            stock.add(deck.draw());
        }
    }
    
    //Überprüft ob Ablagestappel voll sind um zu gewinnen
    private boolean checkWinCondition() {
    for (Pile foundation : foundations) {
        if (foundation.size() != 13) {
            return false;
        }
    }
    return true;
}
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Zeichne Stock
        drawPile(g, stock, 20, 20, false);
        // Zeichne Waste
        drawPile(g, waste, 120, 20, true);
        // Zeichne Foundations
        for (int i = 0; i < 4; i++) {
            drawPile(g, foundations.get(i),
                     240 + i * DX, 20, true);
        }
        // Zeichne Tableaus
        for (int i = 0; i < 7; i++) {
            drawPileCascade(g, tableaus.get(i),
                            20 + i * DX, 150);
        }
        // Auswahlrahmen
        if (selectedCard != null && selectedPile != null) {
            g.setColor(Color.BLUE);
            int x = getPileX(selectedPile);
            int y = getPileY(selectedPile);
            g.drawRect(x, y, CARD_W, CARD_H);
        }
    }

    private void drawPile(Graphics g, Pile pile,
                          int x, int y, boolean faceUp) {
        if (pile.isEmpty()) {
            g.setColor(Color.BLACK);
            g.drawRect(x, y, CARD_W, CARD_H); 
            }

        else {
            Card top = pile.top();
            if (faceUp ||
               pile.getType() == PileType.WASTE ||
               pile.getType() == PileType.FOUNDATION) {
                drawCard(g, top, x, y);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x, y, CARD_W, CARD_H);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, CARD_W, CARD_H);
            }
        }
    }

    private void drawPileCascade(Graphics g, Pile pile,
                                 int x, int y) {
        for (int i = 0; i < pile.size(); i++) {
            Card c = pile.get(i);
            int yy = y + i * DY;
            drawCard(g, c, x, yy);
        }
    }

    private void drawCard(Graphics g, Card c, int x, int y) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, CARD_W, CARD_H);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, CARD_W, CARD_H);
        g.setColor(c.getColor());
        g.drawString(c.toString(), x + 5, y + 15);
    }

    private int getPileX(Pile p) {
        if (p == stock) return 20;
        if (p == waste) return 120;
        int fi = foundations.indexOf(p);
        if (fi >= 0) return 240 + fi * DX;
        int ti = tableaus.indexOf(p);
        if (ti >= 0) return 20 + ti * DX;
        return 0;
    }

    private int getPileY(Pile p) {
        if (p == stock || p == waste
            || foundations.contains(p)) return 20;
        return 150 + (p.size() - 1) * DY;
    }

    private Pile findClickedPile(Point pt) {
        Rectangle r;
        // Stock
        r = new Rectangle(20, 20, CARD_W, CARD_H);
        if (r.contains(pt)) return stock;
        // Waste
        r = new Rectangle(120, 20, CARD_W, CARD_H);
        if (r.contains(pt)) return waste;
        // Foundations
        for (int i = 0; i < 4; i++) {
            r = new Rectangle(240 + i*DX, 20, CARD_W, CARD_H);
            if (r.contains(pt)) return foundations.get(i);
        }
        // Tableaus
        for (int i = 0; i < 7; i++) {
            int x = 20 + i*DX;
            int h = CARD_H + (tableaus.get(i).size()-1)*DY;
            r = new Rectangle(x, 150, CARD_W, h);
            if (r.contains(pt)) return tableaus.get(i);
        }
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Pile clicked = findClickedPile(e.getPoint());
        if (clicked == null) return;

        // Stock ziehen
        if (clicked == stock) {
            if (!stock.isEmpty()) {
                waste.add(stock.draw());
                repaint();
            }
            else if (!waste.isEmpty()) {
                while (!waste.isEmpty()) {
                stock.add(waste.draw());
                }
                stock.shuffle();
                repaint();
            }
            return;
        }

        // Erstes Klicken: Karte auswählen
        if (selectedCard == null) {
            if (clicked.isEmpty()) return;
            selectedCard = clicked.top();
            selectedPile = clicked;
            repaint();
        } else {
            // Zweites Klicken: Verschieben versuchen
            if (clicked.canAccept(selectedCard)) {
                selectedPile.removeTop();
                clicked.add(selectedCard);
            }
            selectedCard = null;
            selectedPile = null;
            repaint();
        }
        
        if (checkWinCondition()) {
        JOptionPane.showMessageDialog(this, "Looser! ChatGPT hat gewonnen!");
}
    }

    // Unbenutzte Listener-Methoden
    public void mousePressed(MouseEvent e)  {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e)  {}
    public void mouseExited(MouseEvent e)   {}
}

