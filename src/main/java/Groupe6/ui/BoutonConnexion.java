package Groupe6.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import Groupe6.etats.EtatJeu;

/**
 * Bouton « Connexion » : au clic, change l’état vers MENU (ou déclenche la connexion).
 */
public class BoutonConnexion extends Bouton {

    public BoutonConnexion(int x, int y, int largeur, int hauteur, int index) {
        super(x, y, largeur, hauteur, index);
        chargerImages();
    }

    /** Dessine l’image ou, à défaut, un rectangle avec le libellé « Connexion ». */
    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (img == null || index < 0 || index >= img.length || img[index] == null) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 16));
            String libelle = "Connexion";
            int lw = g.getFontMetrics().stringWidth(libelle);
            int lx = x + (largeur - lw) / 2;
            int ly = y + (hauteur + g.getFontMetrics().getAscent()) / 2 - 2;
            g.drawString(libelle, lx, ly);
        }
    }

    @Override
    public void appliquerAction() {
        EtatJeu.setEtatActuel(EtatJeu.MENU);
    }
}
