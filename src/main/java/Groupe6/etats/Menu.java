package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import Groupe6.game.Game;

/**
 * État « menu principal » : écran d’accueil avec boutons (jouer, paramètres, etc.).
 * Implémentation en cours ; les méthodes déléguent encore à UnsupportedOperationException.
 */
public class Menu extends Etats implements MethodesEtats {
    private FondDegrade fond;
    public Menu(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
      this.fond = FondDegrade.getInstance();
    }

    @Override
    public void update() {
      fond.update();
    }

    @Override
    public void draw(Graphics g) {
      fond.draw(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyPressed'");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseMoved'");
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseDragged'");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseClicked'");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mousePressed'");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseReleased'");
    }

    @Override
    public void updateTexts() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateTexts'");
    }
}
