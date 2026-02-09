package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import Groupe6.game.Game;
import Groupe6.utilz.Constants;

/**
 * État « connexion » : écran de connexion au jeu avec fond animé.
 * Permet au joueur de se connecter avec son compte.
 */
public class Connexion extends Etats implements MethodesEtats {


  private FondDegrade fond;

  /**
   * Constructeur de l'état de connexion.
   */
  public Connexion (Game game) {
    super(game);
    initClasses();
  }

  private void initClasses() {
    this.fond = FondDegrade.getInstance();
    // int cx = (int) (Constants.game_width * Constants.Ratios.RATIO_CENTER_X);
    // int cy = (int) (Constants.game_height * Constants.Ratios.Start.RATIO_START_FORM_Y);
    
  }

  /** Dessine le fond animé de l'écran de connexion. */
  @Override
  public void draw(Graphics g) {
    ensureLayoutUpToDate();
    fond.draw(g);
  }

  /** Met à jour le fond animé (nuages). */
  @Override
  public void update() {
    fond.update();
  }

  /** Recalcule le layout selon les nouvelles dimensions (vide pour l'instant). */
  @Override
  public void updateLayout(int gameWidth, int gameHeight) {
    applyLayout(gameWidth, gameHeight);
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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'mouseReleased'");
  }

  @Override
  public void updateTexts() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateTexts'");
  }
  

}
