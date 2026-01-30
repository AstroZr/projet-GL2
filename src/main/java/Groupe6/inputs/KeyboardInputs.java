package Groupe6.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.BiConsumer;

import Groupe6.etats.EtatJeu;
import Groupe6.etats.MethodesEtats;
import Groupe6.game.GamePanel;

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
    // ========================================
    // === GESTION DES ÉVÉNEMENTS CLAVIER ===
    // ========================================
    /**
     * Méthode générique pour gérer tous les événements clavier.
     * Délègue l'événement à l'état approprié selon le GameState actuel.
     * 
     * @param e Événement clavier à traiter
     * @param action Action à exécuter sur l'état (ex: keyPressed, keyReleased)
     */
    private void handleKeyEvent(KeyEvent e, BiConsumer<MethodesEtats, KeyEvent> action) {
        MethodesEtats state = null;
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
        if (state != null) 
            action.accept(state, e);
    }

    /**
     * Appelé lorsqu'une touche est pressée.
     * Gère les actions continues (déplacement, attaque maintenue, etc.).
     * 
     * @param e Événement contenant les informations de la touche pressée
     */
    @Override
    public void keyPressed(KeyEvent e) {
        handleKeyEvent(e, (state, event) -> state.keyPressed(event));
    }

    /**
     * Appelé lorsqu'une touche est relâchée.
     * Gère les actions de fin (arrêt du déplacement, etc.).
     * 
     * @param e Événement contenant les informations de la touche relâchée
     */
    @Override
    public void keyReleased(KeyEvent e) {
        handleKeyEvent(e, (state, event) -> state.keyReleased(event));
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
        handleKeyEvent(e, (state, event) -> state.keyTyped(event));
    }
}