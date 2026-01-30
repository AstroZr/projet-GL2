package Groupe6.inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.function.BiConsumer;

import Groupe6.game.GamePanel;
import Groupe6.etats.MethodesEtats;
import Groupe6.etats.EtatJeu;

/**
 * Gestionnaire des entrées souris - Capture et traite les événements de souris.
 * 
 * FONCTIONNALITÉS :
 * - Détecte les clics, mouvements et drags de la souris
 * - Gère les interactions avec l'interface utilisateur (boutons, menus)
 * - Transmet les événements au jeu via GamePanel
 * 
 * ARCHITECTURE :
 * - Implémente MouseListener et MouseMotionListener de Java AWT
 * - Service d'entrée utilisé par GamePanel
 * - Découple les événements souris de la logique métier du jeu
 */
public class MouseInputs implements MouseListener, MouseMotionListener {

    // === RÉFÉRENCE AU PANNEAU ===
    private GamePanel gamePanel;  // Référence pour accéder au jeu et transmettre les événements
    
    /**
     * Constructeur du gestionnaire d'entrées souris.
     * 
     * @param gamePanel Le panneau de jeu associé
     */
    public MouseInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    // ========================================
    // === GESTION DES MOUVEMENTS SOURIS ===
    // ========================================

    /**
     * Méthode générique pour gérer tous les événements souris.
     * Délègue l'événement à l'état approprié selon le GameState actuel.
     * 
     * @param e Événement souris à traiter
     * @param action Action à exécuter sur l'état (ex: mouseMoved, mousePressed)
     */
    private void handleMouseEvent(MouseEvent e, BiConsumer<MethodesEtats, MouseEvent> action) {
        MethodesEtats state = null;

        // Sélectionner l'état approprié selon le GameState actuel
        switch (EtatJeu.getEtatActuel()) {
            case START:
                state = gamePanel.getGame().getStart();
                break;
            case MENU:
                state = gamePanel.getGame().getMenu();
                break;
            default:
                throw new IllegalStateException("État de jeu non géré: " + EtatJeu.getEtatActuel());
        }
        if (state != null) {
            action.accept(state, e);
        }
    }

    /**
     * Appelé lorsque la souris est déplacée avec un bouton enfoncé.
     * Utile pour les actions de drag (déplacement de caméra, sélection, etc.).
     * 
     * @param e Événement contenant la position de la souris
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mouseDragged(event));
    }

    /**
     * Appelé lorsque la souris se déplace sans bouton enfoncé.
     * Utile pour le survol d'éléments UI (highlight, tooltips, etc.).
     * 
     * @param e Événement contenant la position de la souris
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mouseMoved(event));
    }

    // ========================================
    // === GESTION DES CLICS SOURIS ===
    // ========================================

    /**
     * Appelé lors d'un clic complet (pression + relâchement).
     * Utilisé pour les interactions simples (boutons, sélection, etc.).
     * 
     * @param e Événement contenant les informations du clic
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mouseClicked(event));
    }

    /**
     * Appelé lorsque le curseur entre dans la zone du composant.
     * Peut être utilisé pour activer certains comportements.
     * 
     * @param e Événement d'entrée de souris
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Appelé lorsque le curseur sort de la zone du composant.
     * Peut être utilisé pour désactiver certains comportements.
     * 
     * @param e Événement de sortie de souris
     */
    @Override
    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Appelé lorsqu'un bouton de la souris est pressé.
     * Marque le début d'une interaction (avant le relâchement).
     * 
     * @param e Événement contenant le bouton pressé et la position
     */
    @Override
    public void mousePressed(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mousePressed(event));
    }

    /**
     * Appelé lorsqu'un bouton de la souris est relâché.
     * Marque la fin d'une interaction.
     * 
     * @param e Événement contenant le bouton relâché et la position
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mouseReleased(event));
    }
}
