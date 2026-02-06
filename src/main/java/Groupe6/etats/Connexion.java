package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import Groupe6.game.Game;
import Groupe6.ui.TextInput;
import Groupe6.utilz.Constants;


public class Connexion extends Etats implements MethodesEtats {

  private static final int LARGEUR_CHAMP = 400;
  private static final int HAUTEUR_CHAMP = 36;
  private final int MAX_PSEUDO = 20;
  private TextInput textInput;
  private FondDegrade fond;
  public Connexion (Game game) {
    super(game);
    initClasses();
  }

  private void initClasses() {
    this.fond = FondDegrade.getInstance();
    int cx = (int) (Constants.game_width * Constants.Ratios.RATIO_CENTER_X);
    int cy = (int) (Constants.game_height * Constants.Ratios.Start.RATIO_START_FORM_Y);
    textInput = new TextInput(
                cx - LARGEUR_CHAMP / 2,
                cy - HAUTEUR_CHAMP - 20,
                LARGEUR_CHAMP,
                HAUTEUR_CHAMP,
                "Pseudo...",
                MAX_PSEUDO);


  }

  @Override
  public void draw(Graphics g) {
    ensureLayoutUpToDate();
    fond.draw(g);
    textInput.draw(g);
  }

  @Override
  public void update() {
    fond.update();
    textInput.update();
  }
  @Override
  public void updateLayout(int gameWidth, int gameHeight) {
    applyLayout(gameWidth, gameHeight);
  }

  @Override
  protected void applyLayout(int w, int h) {
    int cx = (int) (w * Constants.Ratios.RATIO_CENTER_X);
    int cy = (int) (h * Constants.Ratios.Start.RATIO_START_FORM_Y);
    textInput.setBounds(
        cx - LARGEUR_CHAMP / 2,
        cy - HAUTEUR_CHAMP - 20,
        LARGEUR_CHAMP,
        HAUTEUR_CHAMP);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    textInput.handleKeyTyped(e);
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
    if (textInput.contains(e.getX(), e.getY())) {
      textInput.setFocused(true);
      return;
    }
    textInput.setFocused(false);
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
  
  /** Retourne le pseudo saisi (pour la connexion). */
  public String getPseudoSaisi() {
    return textInput.getTextTrimmed();
  }
}
