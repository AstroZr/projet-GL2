package Groupe6.inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.function.BiConsumer;

import Groupe6.etats.EtatJeu;
import Groupe6.etats.MethodesEtats;
import Groupe6.game.GamePanel;

/**
 * Écoute les événements souris (clic, mouvement, drag) et les transmet à l’état actuel.
 */
public class MouseInputs implements MouseListener, MouseMotionListener {

    private final GamePanel gamePanel;

    public MouseInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    private void handleMouseEvent(MouseEvent e, BiConsumer<MethodesEtats, MouseEvent> action) {
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
        if (state != null) {
            action.accept(state, e);
        }
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
}
