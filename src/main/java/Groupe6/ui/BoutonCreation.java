package Groupe6.ui;

import java.awt.Graphics;

import Groupe6.etats.EtatJeu;
import Groupe6.fond.Fond;

/**
 * Bouton « Création » : au clic, crée un nouveau profil puis change l'état vers MENU.
 */
public class BoutonCreation extends BoutonChangeurEtat {

  /**
   * Constructeur du bouton de création de profil.
   */
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
    // TODO: Ajouter la foncitonnalité de création de profil
    super.appliquerAction();
  }
} 
