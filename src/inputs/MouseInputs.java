package inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import game.GamePanel;

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
     * Appelé lorsque la souris est déplacée avec un bouton enfoncé.
     * Utile pour les actions de drag (déplacement de caméra, sélection, etc.).
     * 
     * @param e Événement contenant la position de la souris
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO: Implémenter la gestion du drag
        // Exemple: déplacement de caméra, sélection de zone
        throw new UnsupportedOperationException("Unimplemented method 'mouseDragged'");
    }

    /**
     * Appelé lorsque la souris se déplace sans bouton enfoncé.
     * Utile pour le survol d'éléments UI (highlight, tooltips, etc.).
     * 
     * @param e Événement contenant la position de la souris
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO: Implémenter la gestion du mouvement
        // Exemple: highlight de boutons, affichage de tooltips
        throw new UnsupportedOperationException("Unimplemented method 'mouseMoved'");
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
        // TODO: Implémenter la gestion des clics
        // Exemple: interaction avec boutons, sélection d'objets
        throw new UnsupportedOperationException("Unimplemented method 'mouseClicked'");
    }

    /**
     * Appelé lorsque le curseur entre dans la zone du composant.
     * Peut être utilisé pour activer certains comportements.
     * 
     * @param e Événement d'entrée de souris
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO: Implémenter si nécessaire
        // Exemple: activation de certains contrôles
        throw new UnsupportedOperationException("Unimplemented method 'mouseEntered'");
    }

    /**
     * Appelé lorsque le curseur sort de la zone du composant.
     * Peut être utilisé pour désactiver certains comportements.
     * 
     * @param e Événement de sortie de souris
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // TODO: Implémenter si nécessaire
        // Exemple: désactivation de certains contrôles
        throw new UnsupportedOperationException("Unimplemented method 'mouseExited'");
    }

    /**
     * Appelé lorsqu'un bouton de la souris est pressé.
     * Marque le début d'une interaction (avant le relâchement).
     * 
     * @param e Événement contenant le bouton pressé et la position
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // TODO: Implémenter la gestion des pressions
        // Exemple: début d'un drag, début d'attaque
        throw new UnsupportedOperationException("Unimplemented method 'mousePressed'");
    }

    /**
     * Appelé lorsqu'un bouton de la souris est relâché.
     * Marque la fin d'une interaction.
     * 
     * @param e Événement contenant le bouton relâché et la position
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO: Implémenter la gestion des relâchements
        // Exemple: fin d'un drag, fin d'attaque
        throw new UnsupportedOperationException("Unimplemented method 'mouseReleased'");
    }
}
