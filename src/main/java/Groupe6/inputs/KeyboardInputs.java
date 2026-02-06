package Groupe6.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.BiConsumer;

import Groupe6.etats.EtatJeu;
import Groupe6.etats.MethodesEtats;
import Groupe6.game.GamePanel;

/**
 * Écoute les événements clavier et les transmet à l’état actuel (Start, Menu, etc.).
 */
public class KeyboardInputs implements KeyListener {

    private final GamePanel gamePanel;

    public KeyboardInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    private void handleKeyEvent(KeyEvent e, BiConsumer<MethodesEtats, KeyEvent> action) {
        MethodesEtats state = null;
        switch (EtatJeu.getEtatActuel()) {
            case START:
                state = gamePanel.getGame().getStart();
                break;
            case MENU:
                state = gamePanel.getGame().getMenu();
                break;
            case GRILLE:
                state = gamePanel.getGame().getMenu(); // TODO: getGrille()
                break;
            case PARAMETRES:
                state = gamePanel.getGame().getParametres();
                break;
            default:
                throw new IllegalStateException("État de jeu non géré: " + EtatJeu.getEtatActuel());
        }
        if (state != null) {
            action.accept(state, e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        handleKeyEvent(e, (state, event) -> state.keyPressed(event));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        handleKeyEvent(e, (state, event) -> state.keyReleased(event));
    }

    @Override
    public void keyTyped(KeyEvent e) {
        handleKeyEvent(e, (state, event) -> state.keyTyped(event));
    }
}