package Groupe6.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import Groupe6.fond.Fond;
import Groupe6.models.Cellule;
import Groupe6.models.Grille;
import Groupe6.models.ZoneCalcul;

/**
 * Vue responsable de l'affichage de la grille de jeu.
 */
public class VueGrille {

    // Pre-allocated immutable graphical objects to avoid per-frame allocations
    private static final Color COULEUR_SELECTION = new Color(100, 150, 255, 150);
    private static final Color COULEUR_ERREUR = new Color(255, 100, 100, 180);
    private static final Font FONT_VALEUR = new Font("Arial", Font.BOLD, 32);
    private static final Font FONT_ZONE = new Font("Arial", Font.BOLD, 14);
    private static final BasicStroke STROKE_EPAISSE = new BasicStroke(3);
    private static final BasicStroke STROKE_FINE = new BasicStroke(1);

    private static final int TAILLE_GRILLE_DEFAULT = (int)(1080 / 1.5f);
    private static final int OFFSET_X_DEFAULT = 40;
    private static final int OFFSET_Y_DEFAULT = (1080 - TAILLE_GRILLE_DEFAULT) / 2;

    private final Grille grille;
    private int tailleCellule;
    private int tailleGrille;
    private int offsetX;
    private int offsetY;

    public VueGrille(Grille grille) {
        this.grille = grille;
        tailleCellule = TAILLE_GRILLE_DEFAULT / grille.getTaille();
        offsetX = OFFSET_X_DEFAULT;
        offsetY = OFFSET_Y_DEFAULT;
        tailleGrille = TAILLE_GRILLE_DEFAULT;
    }

    /**
     * Dessine la grille à l'écran en appliquant les couleurs du thème actif.
     */
    public void draw(Graphics g, Fond fond) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        dessinerGrille(g2d, fond);
        dessinerBorduresZones(g2d, fond);
        dessinerZones(g2d, fond);
    }

    private void dessinerGrille(Graphics2D g2d, Fond fond) {
        int taille = grille.getTaille();

        g2d.setFont(FONT_VALEUR);
        FontMetrics fm = g2d.getFontMetrics();
        int fontAscent = fm.getAscent();

        for (int ligne = 0; ligne < taille; ligne++) {
            for (int col = 0; col < taille; col++) {
                int x = offsetX + col * tailleCellule;
                int y = offsetY + ligne * tailleCellule;
                Cellule cellule = grille.getCellule(ligne, col);

                dessinerFondCellule(g2d, cellule, x, y, fond);
                dessinerBordureCellule(g2d, cellule, x, y, fond);
                dessinerValeurCellule(g2d, cellule, x, y, fm, fontAscent, fond);
            }
        }
    }

    /**
     * Remplit le fond de la cellule selon son état (erreur, sélection, normal).
     */
    private void dessinerFondCellule(Graphics2D g2d, Cellule cellule, int x, int y, Fond fond) {
        if (cellule.estErreurDuplique() || !cellule.estValide()) {
            g2d.setColor(COULEUR_ERREUR);
        } else if (cellule.estSelectionnee()) {
            g2d.setColor(COULEUR_SELECTION);
        } else {
            g2d.setColor(fond.getCouleurFondCellule());
        }
        g2d.fillRect(x, y, tailleCellule, tailleCellule);
    }

    /**
     * Dessine la bordure fine de la cellule.
     */
    private void dessinerBordureCellule(Graphics2D g2d, Cellule cellule, int x, int y, Fond fond) {
        g2d.setColor(fond.getCouleurTexte());
        g2d.setStroke(STROKE_FINE);
        g2d.drawRect(x, y, tailleCellule, tailleCellule);
    }

    /**
     * Affiche la valeur numérique centrée dans la cellule, si présente.
     */
    private void dessinerValeurCellule(Graphics2D g2d, Cellule cellule, int x, int y,
                                       FontMetrics fm, int fontAscent, Fond fond) {
        int valeur = cellule.getValeur();
        if (valeur == 0) return;

        g2d.setColor(fond.getCouleurTexte());
        g2d.setFont(FONT_VALEUR);
        String valeurStr = String.valueOf(valeur);
        int strWidth = fm.stringWidth(valeurStr);
        g2d.drawString(
            valeurStr,
            x + (tailleCellule - strWidth) / 2,
            y + (tailleCellule + fontAscent) / 2 - 5
        );
    }

    /**
     * Dessine les bordures épaisses délimitant chaque zone de calcul.
     * Pour chaque côté d'une cellule, un trait épais est tracé si la cellule
     * voisine appartient à une zone différente (ou est hors grille).
     */
    private void dessinerBorduresZones(Graphics2D g2d, Fond fond) {
        int taille = grille.getTaille();
        g2d.setStroke(STROKE_EPAISSE);
        g2d.setColor(fond.getCouleurBordureZone());

        for (int ligne = 0; ligne < taille; ligne++) {
            for (int col = 0; col < taille; col++) {
                Cellule cellule = grille.getCellule(ligne, col);
                ZoneCalcul zone = cellule.getZoneCalcul();
                int x = offsetX + col * tailleCellule;
                int y = offsetY + ligne * tailleCellule;

                // Bord du haut
                if (ligne == 0 || grille.getCellule(ligne - 1, col).getZoneCalcul() != zone) {
                    g2d.drawLine(x, y, x + tailleCellule, y);
                }
                // Bord du bas
                if (ligne == taille - 1 || grille.getCellule(ligne + 1, col).getZoneCalcul() != zone) {
                    g2d.drawLine(x, y + tailleCellule, x + tailleCellule, y + tailleCellule);
                }
                // Bord gauche
                if (col == 0 || grille.getCellule(ligne, col - 1).getZoneCalcul() != zone) {
                    g2d.drawLine(x, y, x, y + tailleCellule);
                }
                // Bord droit
                if (col == taille - 1 || grille.getCellule(ligne, col + 1).getZoneCalcul() != zone) {
                    g2d.drawLine(x + tailleCellule, y, x + tailleCellule, y + tailleCellule);
                }
            }
        }
    }

    private void dessinerZones(Graphics2D g2d, Fond fond) {
        g2d.setStroke(STROKE_EPAISSE);
        g2d.setColor(fond.getCouleurEtiquetteZone());
        g2d.setFont(FONT_ZONE);
        for (ZoneCalcul zone : grille.getListeZones()) {
            Cellule premiere = zone.getListeCellules().get(0);
            int x = offsetX + premiere.getColonne() * tailleCellule;
            int y = offsetY + premiere.getLigne() * tailleCellule;
            g2d.drawString(zone.getValeurCible() + zone.getTypeOperation().getSymbole(), x + 3, y + 15);
        }
    }

    /**
     * Gère le clic de souris sur la grille.
     */
    public void mouseClicked(MouseEvent e) {
        int col = (e.getX() - offsetX) / tailleCellule;
        int ligne = (e.getY() - offsetY) / tailleCellule;

        int taille = grille.getTaille();
        if (ligne >= 0 && ligne < taille && col >= 0 && col < taille) {
            grille.selectionnerCellule(ligne, col);
        }
    }

    /**
     * Gère la saisie de caractères (chiffres).
     */
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (Character.isDigit(c)) {
            int valeur = Character.getNumericValue(c);
            if (valeur >= 1 && valeur <= grille.getTaille()) {
                Cellule cellule = grille.getCelluleSelectionnee();
                if (cellule != null) {
                    grille.ajouterChiffre(cellule.getLigne(), cellule.getColonne(), valeur);
                }
            }
        }
    }

    /**
     * Gère les touches pressées (suppression).
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            Cellule cellule = grille.getCelluleSelectionnee();
            if (cellule != null) {
                grille.supprimerChiffre(cellule.getLigne(), cellule.getColonne());
            }
        }
    }
    public void applyLayout(int w, int h) {
        tailleGrille = (int)(h / 1.5f);
        tailleCellule = tailleGrille / grille.getTaille();
        offsetX = (int)(w / 48);
        offsetY = (h - tailleGrille) / 2;
    }
}
