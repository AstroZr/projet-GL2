package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import game.GamePanel;

/**
 * Gestionnaire des entrées clavier - Capture et traite les événements clavier.
 * 
 * FONCTIONNALITÉS :
 * - Détecte et traite les touches pressées/relâchées
 * - Transmet les commandes au jeu via GamePanel
 * - Gère les raccourcis clavier et les contrôles du joueur
 * 
 * ARCHITECTURE :
 * - Implémente l'interface KeyListener de Java AWT
 * - Service d'entrée utilisé par GamePanel
 * - Découple les entrées système de la logique métier du jeu
 */
public class KeyboardInputs implements KeyListener {

    // === RÉFÉRENCE AU PANNEAU ===
    private GamePanel gamePanel;  // Référence pour accéder au jeu et transmettre les événements

    /**
     * Constructeur du gestionnaire d'entrées clavier.
     * 
     * @param gamePanel Le panneau de jeu associé
     */
    public KeyboardInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    /**
     * Appelé lorsqu'une touche est pressée.
     * Gère les actions continues (déplacement, attaque maintenue, etc.).
     * 
     * @param e Événement contenant les informations de la touche pressée
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // TODO: Implémenter la gestion des touches pressées
        // Exemple: déplacement du joueur, saut, attaque
        throw new UnsupportedOperationException("Unimplemented method 'keyPressed'");
    }

    /**
     * Appelé lorsqu'une touche est relâchée.
     * Gère les actions de fin (arrêt du déplacement, etc.).
     * 
     * @param e Événement contenant les informations de la touche relâchée
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // TODO: Implémenter la gestion des touches relâchées
        // Exemple: arrêt du déplacement, fin d'attaque
        throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
    }

    /**
     * Appelé lorsqu'une touche génère un caractère Unicode.
     * Généralement non utilisé pour les contrôles de jeu.
     * Utile pour la saisie de texte (chat, nom de joueur, etc.).
     * 
     * @param e Événement contenant le caractère généré
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // TODO: Implémenter si nécessaire pour la saisie de texte
        throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }
}
