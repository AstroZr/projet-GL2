package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Groupe6.game.Game;


/**
 * Classe représentant l'état de démarrage du jeu.
 * 
 * @author Lounol72
 * @version 1.0
 * @since 2026-01-28
 */
public class Start extends Etats implements MethodesEtats {

    private FondDegrade fondDegrade;

    /**
     * Constructeur de l'état de démarrage du jeu.
     * 
     * @param game Instance du jeu
     */
    public Start(Game game) {
        super(game);
        initClasses();
    }

    /**
     * Initialise les classes de l'état de démarrage du jeu.
     */
    private void initClasses() {
        boutons = new ArrayList<>();
        fondDegrade = new FondDegrade();
    }

    /**
     * Met à jour l'état de démarrage du jeu.
     */
    @Override
    public void update() {
        // Mise à jour de l'état de démarrage du jeu
        fondDegrade.update();
    }

    /**
     * Dessine l'état de démarrage du jeu.
     * Fond en dégradé type soleil levant : ciel nocturne en haut, aurore orange/corail en bas.
     */
    @Override
    public void draw(Graphics g) {
        // Mise en cache des dimensions au premier draw (évite recalcul à chaque frame)
        fondDegrade.draw(g);
    }

    /**
     * Gère les entrées clavier.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gère les entrées clavier.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gère les entrées clavier.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gère les entrées souris.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gère les entrées souris.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gère les entrées souris.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gère les entrées souris.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gère les entrées souris.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Met à jour les textes.
     */
    @Override
    public void updateTexts() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
