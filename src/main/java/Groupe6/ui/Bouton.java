package Groupe6.ui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
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
    protected BufferedImage[] img;
    protected boolean sourisSurvol;
    protected boolean sourisEnfonce;

    int arc = 15; // 15px de rayon
    private RoundRectangle2D rect;
    private Color[] backgroundColor;

    public Bouton(int x, int y, int largeur, int hauteur) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
        creationDelimitation();
        backgroundColor = new Color[3]; // 3 états : 0 normal, 1 hover, 2 clicked
        backgroundColor[0] = new Color(200, 200, 200, 180);
        backgroundColor[1] = new Color(220,220,220, 180);
        backgroundColor[2] = new Color(240,240,240,180);
    }

    private void creationDelimitation() {
        delimitation = new Rectangle(x, y, largeur, hauteur);
        rect = new RoundRectangle2D.Float(x, y, largeur, hauteur, arc, arc);
    }

    public void resetBooleens() {
        sourisSurvol = false;
        sourisEnfonce = false;
    }

    /** Action exécutée au clic ; à implémenter par les sous-classes. */
    public abstract void appliquerAction();

    /** Dessine le bouton (image si disponible, sinon rectangle gris avec bordure). */
    public void draw(Graphics g) { 
        drawBackground(g);
    }

    /** Retourne la zone de délimitation du bouton. */
    public Rectangle getDelimitation() {
        return delimitation;
    }

    /** Définit la zone de délimitation du bouton. */
    public void setDelimitation(Rectangle delimitation) {
        this.delimitation = delimitation;
    }

    /** Retourne la position Y du bouton. */
    public int getY() {
        return y;
    }

    /** Définit la position Y du bouton et met à jour la délimitation. */
    public void setY(int y) {
        this.y = y;
        creationDelimitation();
    }

    /** Retourne la position X du bouton. */
    public int getX() {
        return x;
    }

    /** Définit la position X du bouton et met à jour la délimitation. */
    public void setX(int x) {
        this.x = x;
        creationDelimitation();
    }

    /** Retourne la largeur du bouton. */
    public int getLargeur() {
        return largeur;
    }

    /** Définit la largeur du bouton et met à jour la délimitation. */
    public void setLargeur(int largeur) {
        this.largeur = largeur;
        creationDelimitation();
    }

    /** Retourne la hauteur du bouton. */
    public int getHauteur() {
        return hauteur;
    }

    /** Définit la hauteur du bouton et met à jour la délimitation. */
    public void setHauteur(int hauteur) {
        this.hauteur = hauteur;
        creationDelimitation();
    }

    /** Indique si le bouton est actuellement enfoncé. */
    public boolean isSourisEnfonce() {
        return sourisEnfonce;
    }

    /** Définit l'état d'enfoncement du bouton. */
    public void setSourisEnfonce(boolean sourisEnfonce) {
        this.sourisEnfonce = sourisEnfonce;
    }

    /** Indique si la souris survole le bouton. */
    public boolean isSourisSurvol() {
        return sourisSurvol;
    }

    /** Définit l'état de survol du bouton. */
    public void setSourisSurvol(boolean sourisSurvol) {
        this.sourisSurvol = sourisSurvol;
    }
    private void drawBackground(Graphics g) {
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setColor(backgroundColor[sourisSurvol ? (sourisEnfonce ? 2 : 1) : 0]);
      g2d.fill(rect);
      g2d.setColor(Color.GRAY);
      g2d.setStroke(new BasicStroke(2));
      g2d.draw(rect);
    }
}
