package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Contrat commun à tous les états du jeu : rendu, boucle de mise à jour, entrées clavier/souris.
 * Chaque état (Start, Menu, Grille, etc.) implémente ce qu’il utilise et peut ignorer le reste.
 *
 * @author Lounol72
 * @version 1.0
 * @since 2026-01-28
 */
public interface MethodesEtats {

    void draw(Graphics g);
    
    void update();

    void keyTyped(KeyEvent e);
    
    void keyReleased(KeyEvent e);
    
    void keyPressed(KeyEvent e);

    void mouseMoved(MouseEvent e);
    
    void mouseDragged(MouseEvent e);
    
    void mouseClicked(MouseEvent e);
    
    void mousePressed(MouseEvent e);
    
    void mouseReleased(MouseEvent e);

    void updateTexts();
}

