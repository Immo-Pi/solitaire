// SolitaireFrame.java
package org.yourcompany.yourproject;

import java.awt.Color;
import javax.swing.JFrame;

public class SolitaireFrame extends JFrame {
    public SolitaireFrame() {
        setTitle("Solitaire");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(new BoardPanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}