package Groupe6.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import Groupe6.etats.EtatJeu;

/**
 * Bouton permettant de changer l'état du jeu.
 */
public class BoutonChangeurEtat extends Bouton {
    private EtatJeu action;
    protected String label;
    private int lw = -1;
    private int lx = -1;
    private int ly = -1;
    
    /**
     * Constructeur du bouton changeur d'état.
     * 
     * @param x Position X du bouton
     * @param y Position Y du bouton
     * @param largeur Largeur du bouton
     * @param hauteur Hauteur du bouton
     * @param action État du jeu à appliquer lors du clic
     * @param label Texte affiché sur le bouton 
     */
    public BoutonChangeurEtat(int x, int y, int largeur, int hauteur, EtatJeu action, String label) {
        super(x, y, largeur, hauteur);
        this.action = action;
        this.label = label;
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, 16));
        if (lw == -1 || lx == -1 || ly == -1) {
            this.lw = g.getFontMetrics().stringWidth(label);
            this.lx = x + (largeur - lw) / 2;
            this.ly = y + (hauteur + g.getFontMetrics().getAscent()) / 2 - 2;
        }
        g.drawString(label, this.lx, this.ly);
    }

    public void appliquerAction() {
        EtatJeu.setEtatActuel(action);
    }
}
