package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import Groupe6.game.Game;
import Groupe6.ui.TextInput;
import Groupe6.utilz.Constants;
import Groupe6.ui.BoutonCreation;
/**
 * État « création » : écran de création de compte pour le joueur.
 * Permet de saisir un pseudo et de créer un nouveau profil.
 */
public class Creation extends Etats implements MethodesEtats{

  
  private static final int LARGEUR_BOUTON = 200;
  private static final int HAUTEUR_BOUTON = 44;
  private static final int LARGEUR_CHAMP = 400;
  private static final int HAUTEUR_CHAMP = 36;
  private final int MAX_PSEUDO = 20;
  private TextInput textInput;
  private FondDegrade fond;
  private BoutonCreation bouton; 
  public Creation(Game game) {
    super(game);
    initClasses();
  }

  private void initClasses() {
    this.fond = FondDegrade.getInstance();
    int cx = (int) (Constants.game_width * Constants.Ratios.RATIO_CENTER_X);
    int cy = (int) (Constants.game_height * Constants.Ratios.Start.RATIO_START_FORM_Y);
    textInput = new TextInput(
                cx / 2,
                cy - HAUTEUR_CHAMP - 20,
                LARGEUR_CHAMP,
                HAUTEUR_CHAMP,
                "Pseudo...",
                MAX_PSEUDO);
   bouton = new BoutonCreation(
       cx - LARGEUR_BOUTON / 2,
       cy + 10,
       LARGEUR_BOUTON,
       HAUTEUR_BOUTON 
       );
  }
  /** Dessine le fond animé, le champ de saisie et le bouton de création. */
  @Override
  public void draw(Graphics g) {
    ensureLayoutUpToDate();
    fond.draw(g);
    textInput.draw(g);
    bouton.draw(g);
  }

  /** Met à jour le fond animé et le champ de saisie. */
  @Override
  public void update() {
    fond.update();
    textInput.update();
  }

  /** Transmet la saisie clavier au champ de texte si celui-ci a le focus. */
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

  /** Gère le focus du champ de texte et le survol du bouton selon la position de la souris. */
  @Override
  public void mouseMoved(MouseEvent e) {
    bouton.setSourisSurvol(isIn(e, bouton));
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

  /** Marque le bouton de création comme enfoncé lors de l'appui. */
  @Override
  public void mousePressed(MouseEvent e) {
    if (isIn(e, bouton)) {
      bouton.setSourisEnfonce(true);
    }
    if (textInput.contains(e.getX(), e.getY())) {
      textInput.setFocused(true);
      return;
    }
    textInput.setFocused(false);
  }

  /** Déclenche l'action du bouton de création si le clic est valide. */
  @Override
  public void mouseReleased(MouseEvent e) {
    if (bouton.isSourisEnfonce() && isIn(e, bouton)) {
      bouton.appliquerAction();
    }
    bouton.setSourisEnfonce(false);
  }

  @Override
  public void updateTexts() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateTexts'");
  }

  /** Recalcule les positions du champ de texte et du bouton selon les nouvelles dimensions. */
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
    bouton.setX(cx - LARGEUR_BOUTON / 2);
    bouton.setY(cy + 10);
  }

  /** Retourne le pseudo saisi (pour la connexion). */
  public String getPseudoSaisi() {
    return textInput.getTextTrimmed();
  }
}
