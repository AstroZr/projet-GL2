package Groupe6.etats;

// Java standard library imports
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Interface des méthodes des états du jeu.
 * 
 * @author Lounol72
 * @version 1.0
 * @since 2026-01-28
 */
public interface MethodesEtats {

    /**
     * Dessine l'état du jeu.
     * 
     * @param g Contexte graphique
     */
    void draw(Graphics g);

    /**
     * Met à jour l'état du jeu.
     */
    void update();

    /**
     * Gère les entrées clavier.
     * 
     * @param e Événement clavier
     */
    void keyTyped(KeyEvent e);

    /**
     * Gère les entrées clavier.
     * 
     * @param e Événement clavier
     */
    void keyReleased(KeyEvent e);

    /**
     * Gère les entrées clavier.
     * 
     * @param e Événement clavier
     */
    void keyPressed(KeyEvent e);

    /**
     * Gère les entrées souris.
     * 
     * @param e Événement souris
     */
    void mouseMoved(MouseEvent e);

    /**
     * Gère les entrées souris.
     * 
     * @param e Événement souris
     */
    void mouseDragged(MouseEvent e);

    /**
     * Gère les entrées souris.
     * 
     * @param e Événement souris
     */
    void mouseClicked(MouseEvent e);

    /**
     * Gère les entrées souris.
     * 
     * @param e Événement souris
     */
    void mousePressed(MouseEvent e);

    /**
     * Gère les entrées souris.
     * 
     * @param e Événement souris
     */
    void mouseReleased(MouseEvent e);

    /**
     * Met à jour les textes.
     */
    void updateTexts();
}

