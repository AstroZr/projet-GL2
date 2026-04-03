package Groupe6.ui;

import Groupe6.etats.EtatJeu;
import Groupe6.fond.Fond;
import Groupe6.game.Game;
import java.awt.Graphics;

/** Bouton « Création » : au clic, crée un nouveau profil puis change l'état vers MENU. */
public class BoutonCreation extends BoutonChangeurEtat {
  private volatile String pseudo = "";
  private Game game = null;

  /** Constructeur du bouton de création de profil. */
  public BoutonCreation(int x, int y, int largeur, int hauteur) {
    super(x, y, largeur, hauteur, EtatJeu.MENU, "Création du profil");
  }

  /** Dessine le bouton avec son libellé « Création du profil ». */
  @Override
  public void draw(Graphics g, Fond fond) {
    super.draw(g, fond);
  }

  /** Déclenche la création du profil puis change l'état vers MENU. */
  @Override
  public void appliquerAction() {
    Groupe6.save.ParametresJoueur pjParDefaut =
        new Groupe6.save.ParametresJoueur(pseudo, "Fr", 0, 0, 0);
    Groupe6.save.SaveManager.sauvegarderParametres(pjParDefaut);
    
    // Définir le joueur courant pour éviter la création d'un compte "Invité" à la fermeture
    if (game != null) {
      game.setJoueurCourant(pseudo);
    }
    
    super.appliquerAction();
  }

  /**
   * @param pseudo the pseudo to set
   */
  public void setPseudo(String pseudo) {
    this.pseudo = pseudo;
  }

  /**
   * @param game the game instance to set (needed to setJoueurCourant)
   */
  public void setGame(Game game) {
    this.game = game;
  }
}

