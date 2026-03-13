package Groupe6.ui;

import java.awt.Font;
import java.awt.Graphics;

import Groupe6.etats.EtatJeu;
import Groupe6.fond.Fond;


/**
 * Bouton permettant de changer l'état du jeu.
 */
public class BoutonChangeurEtat extends Bouton {
    private static final Font FONT_LABEL = new Font("Berlin Sans FB Demi", Font.BOLD, 16);
    private EtatJeu action;
    protected String label;

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

    /** Dessine le bouton avec son libellé centré. */
    @Override
    public void draw(Graphics g, Fond fond) {
        super.draw(g, fond);
        g.setColor(fond.getCouleurTexte());
        g.setFont(FONT_LABEL);
        // Recalculer la position du texte à chaque draw pour suivre setX/setY (resize)
        int lw = g.getFontMetrics().stringWidth(label);
        int lx = x + (largeur - lw) / 2;
        int ly = y + (hauteur + g.getFontMetrics().getAscent()) / 2 - 2;
        g.drawString(label, lx, ly);
    }

    /** Change l'état du jeu vers l'état associé à ce bouton. */
    public void appliquerAction() {
        EtatJeu.setEtatActuel(action);
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
