package Groupe6.ui;

import Groupe6.etats.EtatJeu;
import Groupe6.fond.Fond;
import java.awt.Graphics;

/** Bouton « Création » : au clic, crée un nouveau profil puis change l'état vers MENU. */
public class BoutonCreation extends BoutonChangeurEtat {
  private volatile String pseudo = "";

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
    super.appliquerAction();
  }

  /**
   * @param pseudo the pseudo to set
   */
  public void setPseudo(String pseudo) {
    this.pseudo = pseudo;
  }
}
