package Groupe6.inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.function.BiConsumer;

import Groupe6.etats.MethodesEtats;
import Groupe6.game.GamePanel;

/**
 * Listener MouseListener, MouseMotionListener, MouseWheelListener qui intercepte les événements souris.
 * 
 * Événements gérés:
 * - Clics: mouseClicked, mousePressed, mouseReleased
 * - Mouvement: mouseMoved, mouseDragged
 * - Molette: mouseWheelMoved
 * 
 * Architecture:
 * - Chaque événement est délégué à l'état actuel (via Game.getCurrentState())
 * - Chaque état gère les clics sur ses boutons et change le layout si souris passe dessus
 * - Ex: Menu détecte clic sur "Jouer" -> appelle BoutonJouer.appliquerAction() -> change état
 * 
 * Thread: Event Dispatch Thread (EDT) de Swing
 */
public class MouseInputs implements MouseListener, MouseMotionListener, MouseWheelListener {

    private final GamePanel gamePanel;  // Référence au panel Swing (pour accéder à Game)

    public MouseInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    /**
     * Exécute une action sur l'état actuel avec cet événement souris.
     * 
     * @param e L'événement MouseEvent
     * @param action BiConsumer qui exécute une méthode (ex: state::mouseClicked)
     */
    private void handleMouseEvent(MouseEvent e, BiConsumer<MethodesEtats, MouseEvent> action) {
        MethodesEtats state = gamePanel.getGame().getCurrentState();  // État actuel
        action.accept(state, e);  // Délègue à l'état
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mouseDragged(event));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mouseMoved(event));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mouseClicked(event));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mousePressed(event));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        handleMouseEvent(e, (state, event) -> state.mouseReleased(event));
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        handleMouseEvent(e, (state, event) -> state.mouseWheelMoved((MouseWheelEvent) event));
    }
}
