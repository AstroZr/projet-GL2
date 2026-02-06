package Groupe6.ui;

import java.awt.Graphics;

import Groupe6.etats.EtatJeu;

/**
 * Bouton « Connexion » : au clic, change l’état vers MENU (ou déclenche la connexion).
 */
public class BoutonConnexion extends BoutonChangeurEtat {

    public BoutonConnexion(int x, int y, int largeur, int hauteur, EtatJeu action, String label) {
        super(x, y, largeur, hauteur, action, label);
    }

    /** Dessine l’image ou, à défaut, un rectangle avec le libellé « Connexion ». */
    @Override
    public void draw(Graphics g) {
        super.draw(g);
    }

    public void appliquerAction() {
        super.appliquerAction();
    }
}
