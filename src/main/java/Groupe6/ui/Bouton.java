package Groupe6.ui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Base abstraite pour les boutons UI : position, zone de clic, image(s), survol/enfoncement.
 * Les sous-classes implémentent appliquerAction() et peuvent surcharger draw() et chargerImages().
 */
public abstract class Bouton {
    protected int x;
    protected int y;
    protected int largeur;
    protected int hauteur;
    protected Rectangle delimitation;
    protected int index;
    protected BufferedImage[] img;
    protected boolean sourisSurvol;
    protected boolean sourisEnfonce;

    public Bouton(int x, int y, int largeur, int hauteur, int index) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.index = index;
        creationDelimitation();
    }

    private void creationDelimitation() {
        delimitation = new Rectangle(x, y, largeur, hauteur);
    }

    public void resetBooleens() {
        sourisSurvol = false;
        sourisEnfonce = false;
    }

    /** Action exécutée au clic ; à implémenter par les sous-classes. */
    public abstract void appliquerAction();

    /** Dessine le bouton (image si disponible, sinon rectangle gris avec bordure). */
    public void draw(Graphics g) {
        if (img != null && index >= 0 && index < img.length && img[index] != null) {
            g.drawImage(img[index], x, y, largeur, hauteur, null);
        } else {
            g.setColor(sourisSurvol ? java.awt.Color.LIGHT_GRAY : java.awt.Color.GRAY);
            g.fillRect(x, y, largeur, hauteur);
            g.setColor(java.awt.Color.DARK_GRAY);
            g.drawRect(x, y, largeur, hauteur);
        }
    }

    public Rectangle getDelimitation() {
        return delimitation;
    }

    public void setDelimitation(Rectangle delimitation) {
        this.delimitation = delimitation;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        creationDelimitation();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        creationDelimitation();
    }

    public int getLargeur() {
        return largeur;
    }

    public void setLargeur(int largeur) {
        this.largeur = largeur;
        creationDelimitation();
    }

    public int getHauteur() {
        return hauteur;
    }

    public void setHauteur(int hauteur) {
        this.hauteur = hauteur;
        creationDelimitation();
    }

    public boolean isSourisEnfonce() {
        return sourisEnfonce;
    }

    public void setSourisEnfonce(boolean sourisEnfonce) {
        this.sourisEnfonce = sourisEnfonce;
    }

    public boolean isSourisSurvol() {
        return sourisSurvol;
    }

    public void setSourisSurvol(boolean sourisSurvol) {
        this.sourisSurvol = sourisSurvol;
    }

    /** Charge les images du bouton (normal, survol, enfoncé) ; à surcharger par les sous-classes. */
    protected void chargerImages() {
    }
}
