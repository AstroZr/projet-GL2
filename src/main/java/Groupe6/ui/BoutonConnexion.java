package Groupe6.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import Groupe6.etats.EtatJeu;

/**
 * Bouton « Connexion » : au clic, change l’état vers MENU (ou déclenche la connexion).
 */
public class BoutonConnexion extends Bouton {
    
  private final String label = "Connexion";
  private int lw = -1;
  private int lx = -1;
  private int ly = -1;
    public BoutonConnexion(int x, int y, int largeur, int hauteur, int index) {
        super(x, y, largeur, hauteur, index);
        chargerImages();
    }

    /** Dessine l’image ou, à défaut, un rectangle avec le libellé « Connexion ». */
    @Override
    public void draw(Graphics g) {
        super.draw(g);      
        g.setColor(Color.BLACK);
        g.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 16));
        if (lw == -1 || lx == -1|| ly == -1) {
          this.lw = g.getFontMetrics().stringWidth(label);
          this.lx = x + (largeur - lw) / 2;
          this.ly = y + (hauteur + g.getFontMetrics().getAscent()) / 2 - 2;
        }
        g.drawString(label, this.lx, this.ly);
    }

    @Override
    public void appliquerAction() {
        // TODO: Charger les informations du joueur selon le pseudo reçu (pseudo.json)
        EtatJeu.setEtatActuel(EtatJeu.MENU);
    }
}
