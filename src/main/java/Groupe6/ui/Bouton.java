package Groupe6.ui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
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
    protected int index;
    protected BufferedImage[] img;
    protected boolean sourisSurvol;
    protected boolean sourisEnfonce;

    int arc = 15; // 15px de rayon
    private RoundRectangle2D rect;
    private Color[] backgroundColor;

    public Bouton(int x, int y, int largeur, int hauteur, int index) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.index = index;
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
        if (img != null && index >= 0 && index < img.length && img[index] != null) {
            g.drawImage(img[index], x, y, largeur, hauteur, null);
        } else {
          drawBackground(g);
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
      // TODO:Charger les images des boutons
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
