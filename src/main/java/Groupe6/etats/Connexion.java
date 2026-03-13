package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import Groupe6.game.Game;

/**
 * État « connexion » : écran de connexion au jeu avec fond animé.
 * Permet au joueur de se connecter avec son compte.
 */
public class Connexion extends Etats {


  /**
   * Constructeur de l'état de connexion.
   */
  public Connexion (Game game) {
    super(game);
    initClasses();
  }

  private void initClasses() {
  }

  /** Dessine le fond animé de l'écran de connexion. */
  @Override
  public void draw(Graphics g) {
    ensureLayoutUpToDate();
    getFond().draw(g);
  }

  /** Met à jour le fond animé (nuages). */
  @Override
  public void update() {
    getFond().update();
  }

  @Override
  protected void applyLayout(int w, int h) {
  }

  @Override
  public void keyTyped(KeyEvent e) {
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
    if(bouton.isSourisEnfonce() && isIn(e, bouton)){
      String pseudo = getPseudoSaisi();

      // Même chose que pour la méthode mouseReleased dans Creation.java
      // On bloque la connexion avec un pseudo vide
      if(!pseudo.trim().isEmpty()){
        bouton.appliquerAction;
      }
    }
    bouton.setSourisEnfonce(false);
  }

  @Override
  public void updateTexts() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateTexts'");
  }
  

}
