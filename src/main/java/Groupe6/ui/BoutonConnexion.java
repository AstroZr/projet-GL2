package Groupe6.ui;

import java.awt.Graphics;

import Groupe6.etats.EtatJeu;
import Groupe6.fond.Fond;

/**
 * Bouton « Connexion » : au clic, change l’état vers MENU (ou déclenche la connexion).
 */
public class BoutonConnexion extends BoutonChangeurEtat {

    /**
     * Constructeur du bouton de connexion.
     */
    public BoutonConnexion(int x, int y, int largeur, int hauteur) {
        super(x, y, largeur, hauteur, EtatJeu.CONNEXION, "Connexion");
    }

    /** Dessine l’image ou, à défaut, un rectangle avec le libellé « Connexion ». */
    @Override
    public void draw(Graphics g, Fond fond) {
        super.draw(g, fond);
    }

    /** Déclenche la connexion du joueur en changeant d'état vers CONNEXION. */
    @Override
    public void appliquerAction() {
        // Change vers l'écran de sélection des profils
        super.appliquerAction();
    }
}
