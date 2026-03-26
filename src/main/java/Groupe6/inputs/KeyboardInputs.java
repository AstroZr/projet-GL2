package Groupe6.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.BiConsumer;

import Groupe6.etats.MethodesEtats;
import Groupe6.game.GamePanel;

/**
 * Listener KeyListener qui intercepte les événements clavier et les délègue à l'état actuel.
 * 
 * Architecture:
 * - Enregistré sur GamePanel.addKeyListener(this)
 * - À chaque événement (press, release, type), récupère l'état actuel via Game.getCurrentState()
 * - Appelle la méthode appropriée (keyPressed, keyReleased, keyTyped) sur cet état
 * - Chaque état décide comment traiter l'événement (ex: Menu répond aux UP/DOWN pour navigation)
 * 
 * Thread: Event Dispatch Thread (EDT) de Swing
 */
public class KeyboardInputs implements KeyListener {

    private final GamePanel gamePanel;  // Référence au panel Swing (pour accéder à Game)

    public KeyboardInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    /**
     * Exécute une action sur l'état actuel avec cet événement clavier.
     * 
     * @param e L'événement KeyEvent
     * @param action BiConsumer qui exécute une méthode (ex: state::keyPressed)
     */
    private void handleKeyEvent(KeyEvent e, BiConsumer<MethodesEtats, KeyEvent> action) {
        MethodesEtats state = gamePanel.getGame().getCurrentState();  // État actuel
        action.accept(state, e);  // Délègue à l'état
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