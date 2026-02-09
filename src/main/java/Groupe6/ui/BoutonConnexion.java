package Groupe6.ui;

import java.awt.Graphics;

import Groupe6.etats.EtatJeu;

/**
 * Bouton « Connexion » : au clic, change l’état vers MENU (ou déclenche la connexion).
 */
public class BoutonConnexion extends BoutonChangeurEtat {

    /**
     * Constructeur du bouton de connexion.
     */
    public BoutonConnexion(int x, int y, int largeur, int hauteur) {
        super(x, y, largeur, hauteur, EtatJeu.MENU, "Connexion");
    }

    /** Dessine l’image ou, à défaut, un rectangle avec le libellé « Connexion ». */
    @Override
    public void draw(Graphics g) {
        super.draw(g);
    }

    /** Déclenche la connexion du joueur puis change l'état vers MENU. */
    public void appliquerAction() {
      // TODO: Ajouter la fonctionnalité de connexion du joueur
        super.appliquerAction();
    }
}
