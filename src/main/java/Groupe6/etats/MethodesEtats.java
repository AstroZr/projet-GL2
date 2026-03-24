package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

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

    default void mouseWheelMoved(MouseWheelEvent e) {}

    /** Appelé une fois à chaque activation de cet état. */
    default void onEnter() {}

    /**
     * Recalcule les positions des éléments UI (boutons, champs) après un resize.
     * Implémentation par défaut vide pour les états sans UI ou sans réaction au resize.
     */
    default void updateLayout(int gameWidth, int gameHeight) {
        // vide par défaut
    }
}

