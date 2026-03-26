package Groupe6.ui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import Groupe6.audio.SoundManager;
import Groupe6.fond.Fond;

/**
 * Base abstraite pour les boutons UI : position, zone de clic, images, survol/enfoncement.
 * 
 * Responsabilités:
 * - Gérer la positionning et la zone de délimitation du bouton
 * - Détecter survolage souris et état enfoncé
 * - Render du bouton avec arrondi et bordure
 * - Support du focus clavier (pour accessibilité)
 * - Callback abstrait appliquerAction() pour chaque sous-classe
 * 
 * Sous-classes: BoutonChangeurEtat, BoutonAide, BoutonCreation, BoutonParametre, BoutonConnexion, etc.
 */
public abstract class Bouton {
    // ====== POSITIONNEMENT ======
    protected int x;               // Coordonnée X (pixels)
    protected int y;               // Coordonnée Y (pixels)
    protected int largeur;         // Largeur du bouton (pixels)
    protected int hauteur;         // Hauteur du bouton (pixels)
    protected Rectangle delimitation;  // Zone de clic (Rectangle de hit-test)
    
    // ====== APPARENCE ======
    protected BufferedImage[] img;  // Images du bouton (états différents)
    protected boolean sourisSurvol; // true si souris passe sur le bouton
    protected boolean sourisEnfonce; // true si bouton actuellement enfoncé
    
    // ====== AFFICHAGE ARRONDI ======
    int arc = 15;  // Rayon d'arrondi des coins (15px)
    private RoundRectangle2D rect;  // Forme avec coins arrondis pour affichage
    private static final BasicStroke STROKE_BORDURE = new BasicStroke(2);  // Bordure normal
    private static final BasicStroke STROKE_FOCUS = new BasicStroke(3);   // Bordure focus clavier
    
    // ====== CLAVIER ======
    private boolean focusClavier = false;  // true si bouton a le focus clavier

    public Bouton(int x, int y, int largeur, int hauteur) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
        creationDelimitation();
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

    /** Dessine le bouton (rectangle arrondi coloré selon le thème). */
    public void draw(Graphics g, Fond fond) {
        drawBackground(g, fond);
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

    /** Définit l'état d'enfoncement ; joue le son de clic sur le front montant. */
    public void setSourisEnfonce(boolean sourisEnfonce) {
        if (sourisEnfonce && !this.sourisEnfonce) {
            SoundManager.getInstance().playClick();
        }
        this.sourisEnfonce = sourisEnfonce;
    }

    /** Indique si la souris survole le bouton. */
    public boolean isSourisSurvol() {
        return sourisSurvol;
    }

    /** Définit l'état de survol ; joue le son de survol sur le front montant. */
    public void setSourisSurvol(boolean sourisSurvol) {
        if (sourisSurvol && !this.sourisSurvol) {
            SoundManager.getInstance().playClick();
        }
        this.sourisSurvol = sourisSurvol;
    }

    public boolean isFocusClavier() { return focusClavier; }

    public void setFocusClavier(boolean focusClavier) {
        this.focusClavier = focusClavier;
    }
    private void drawBackground(Graphics g, Fond fond) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bg;
        if (sourisSurvol || focusClavier) {
            bg = sourisEnfonce ? fond.getCouleurFondBoutonClic() : fond.getCouleurFondBoutonSurvol();
        } else {
            bg = fond.getCouleurFondBouton();
        }
        g2d.setColor(bg);
        g2d.fill(rect);
        g2d.setColor(fond.getCouleurBordreBouton());
        g2d.setStroke(focusClavier ? STROKE_FOCUS : STROKE_BORDURE);
        g2d.draw(rect);
    }
}
