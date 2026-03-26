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
import java.util.ArrayList;
import java.util.List;

import Groupe6.fond.Fond;
import Groupe6.models.Cellule;
import Groupe6.models.Grille;
import Groupe6.models.ZoneCalcul;
import Groupe6.utilz.FontCache;

/**
 * Vue (Renderer) responsable du rendu graphique de la grille de jeu CalcuDoku.
 * 
 * Responsabilités:
 * - Dessiner la grille (cellules, valeurs, candidats)
 * - Afficher les bordures des zones (couleurs différentes par zone)
 * - Colorer les cellules selon l'état (sélection, erreur, validité)
 * - Appliquer les thèmes de couleur (via Fond)
 * - Gérer le positionnement responsive et taille des cellules
 * 
 * Optimisations:
 * - Fonts et Colors pré-alloués (pas de création per-frame)
 * - BasicStrokes immuables réutilisés
 * - Rendu en deux passes: fond des cellules → bordures → texte
 * 
 * Observer Pattern:
 * - Enregistrée comme GrilleObserver sur Grille
 * - Appelée à chaque changement (redessine l'affichage)
 */
public class VueGrille {

    // ====== COULEURS PRÉ-ALLOUÉES ======
    private static final Color COULEUR_SELECTION = new Color(100, 150, 255, 150);  // Bleu semi-transparent pour cellule sélectionnée
    private static final Color COULEUR_ERREUR = new Color(255, 100, 100, 180);     // Rouge semi-transparent pour erreur
    
    // ====== FONTS PRÉ-ALLOUÉES ======
    private static final Font FONT_VALEUR = FontCache.get("Arial", Font.BOLD, 32);      // Chiffres dans les cellules
    private static final Font FONT_ZONE = FontCache.get("Arial", Font.BOLD, 14);        // Label zone (target + opération)
    private static final Font FONT_CANDIDAT = FontCache.get("Arial", Font.BOLD, 16);   // Candidats (petits chiffres)
    
    // ====== TRAITS/STROKES PRÉ-ALLOUÉS ======
    private static final BasicStroke STROKE_CONTOUR_GRILLE = new BasicStroke(4);        // Bordure externe grille
    private static final BasicStroke STROKE_ZONE_SOUS_COUCHE = new BasicStroke(5);      // Sous-couche zones
    private static final BasicStroke STROKE_EPAISSE = new BasicStroke(3);               // Bordures épaisses
    private static final BasicStroke STROKE_FINE = new BasicStroke(1);                  // Lignes fines
    
    // ====== DIMENSIONS DE RÉFÉRENCE ======
    private static final int MARGE_CASE = 2;                                           // Marge entre cellules (pixels)
    private static final int TAILLE_GRILLE_DEFAULT = (int)(1080 / 1.5f);              // Taille grille par défaut
    private static final int OFFSET_X_DEFAULT = (1920 - TAILLE_GRILLE_DEFAULT) / 2;    // Centrage horizontal
    private static final int OFFSET_Y_DEFAULT = (1080 - TAILLE_GRILLE_DEFAULT) / 2;    // Centrage vertical

    // ====== INSTANCE ======
    private final Grille grille;       // Référence au modèle (lecture seule)
    
    // ====== LAYOUT COURANT ======
    private int tailleCellule;         // Taille d'une cellule (pixels)
    private int tailleGrille;          // Taille totale grille (N×N cellules)
    private int offsetX;               // Position X de la grille sur l'écran
    private int offsetY;               // Position Y de la grille sur l'écran
    private boolean modeCandidat;      // Mode candidat actif (affiche petits chiffres)
    private Color contourGrilleCache;
    private Fond contourFondCache;

    public VueGrille(Grille grille) {
        this.grille = grille;
        tailleCellule = TAILLE_GRILLE_DEFAULT / grille.getTaille();
        offsetX = OFFSET_X_DEFAULT;
        offsetY = OFFSET_Y_DEFAULT;
        tailleGrille = TAILLE_GRILLE_DEFAULT;
        modeCandidat = false;
    }

    /**
     * Dessine la grille à l'écran en appliquant les couleurs du thème actif.
     */
    public void draw(Graphics g, Fond fond) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        dessinerGrille(g2d, fond);
        dessinerBorduresZones(g2d, fond);
        dessinerContourGrille(g2d, fond);
        dessinerZones(g2d, fond);
    }

    private void dessinerContourGrille(Graphics2D g2d, Fond fond) {
        g2d.setStroke(STROKE_CONTOUR_GRILLE);
        if (contourGrilleCache == null || contourFondCache != fond) {
            Color texte = fond.getCouleurTexte();
            contourFondCache = fond;
            contourGrilleCache = new Color(texte.getRed(), texte.getGreen(), texte.getBlue(), 220);
        }
        g2d.setColor(contourGrilleCache);
        g2d.drawRect(offsetX - 1, offsetY - 1, tailleGrille + 2, tailleGrille + 2);
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
                int xCase = x + MARGE_CASE / 2;
                int yCase = y + MARGE_CASE / 2;
                int tailleCase = Math.max(1, tailleCellule - MARGE_CASE);
                Cellule cellule = grille.getCellule(ligne, col);

                dessinerFondCellule(g2d, cellule, xCase, yCase, tailleCase, fond);
                dessinerBordureCellule(g2d, cellule, xCase, yCase, tailleCase, fond);
                dessinerValeurCellule(g2d, cellule, xCase, yCase, tailleCase, fm, fontAscent, fond);
            }
        }
    }

    /**
     * Remplit le fond de la cellule selon son état (erreur, sélection, normal).
     */
    private void dessinerFondCellule(Graphics2D g2d, Cellule cellule, int x, int y, int tailleCase, Fond fond) {
        if (cellule.estErreurDuplique() || !cellule.estValide()) {
            g2d.setColor(COULEUR_ERREUR);
        } else if (cellule.estSelectionnee()) {
            g2d.setColor(COULEUR_SELECTION);
        } else {
            g2d.setColor(fond.getCouleurFondCellule());
        }
        g2d.fillRect(x, y, tailleCase, tailleCase);
    }

    /**
     * Dessine la bordure fine de la cellule.
     */
    private void dessinerBordureCellule(Graphics2D g2d, Cellule cellule, int x, int y, int tailleCase, Fond fond) {
        g2d.setColor(fond.getCouleurTexte());
        g2d.setStroke(STROKE_FINE);
        g2d.drawRect(x, y, tailleCase, tailleCase);
    }

    /**
     * Affiche la valeur numérique centrée dans la cellule, si présente.
     */
    private void dessinerValeurCellule(Graphics2D g2d, Cellule cellule, int x, int y, int tailleCase,
                                       FontMetrics fm, int fontAscent, Fond fond) {
        int valeur = cellule.getValeur();
        if (valeur == 0) {
            dessinerCandidats(g2d, cellule, x, y, tailleCase, fond);
            return;
        }

        g2d.setColor(fond.getCouleurTexte());
        g2d.setFont(FONT_VALEUR);
        String valeurStr = String.valueOf(valeur);
        int strWidth = fm.stringWidth(valeurStr);
        g2d.drawString(
            valeurStr,
            x + (tailleCase - strWidth) / 2,
            y + (tailleCase + fontAscent) / 2 - 5
        );
    }

    private void dessinerCandidats(Graphics2D g2d, Cellule cellule, int x, int y, int tailleCase, Fond fond) {
        List<Integer> candidats = cellule.getListeCandidat();
        if (candidats == null || candidats.isEmpty()) {
            return;
        }

        g2d.setColor(fond.getCouleurTexte());
        g2d.setFont(FONT_CANDIDAT);
        int nbMaxValeurs = grille.getTaille();
        int colonnes = (int) Math.ceil(Math.sqrt(nbMaxValeurs));
        int lignes = (int) Math.ceil((double) nbMaxValeurs / colonnes);
        int marge = Math.max(6, tailleCase / 12);
        int zoneLargeur = tailleCase - 2 * marge;
        int zoneHauteur = tailleCase - 2 * marge;
        int pasX = Math.max(14, zoneLargeur / Math.max(1, colonnes));
        int pasY = Math.max(14, zoneHauteur / Math.max(1, lignes));
        FontMetrics fm = g2d.getFontMetrics();

        for (Integer candidat : candidats) {
            if (candidat == null || candidat < 1 || candidat > nbMaxValeurs) {
                continue;
            }
            int index = candidat - 1;
            int col = index % colonnes;
            int row = index / colonnes;
            String texte = String.valueOf(candidat);
            int tx = x + marge + col * pasX + (pasX - fm.stringWidth(texte)) / 2;
            int ty = y + marge + row * pasY + fm.getAscent();
            g2d.drawString(texte, tx, ty);
        }
    }

    /**
     * Dessine les bordures épaisses délimitant chaque zone de calcul.
     * Pour chaque côté d'une cellule, un trait épais est tracé si la cellule
     * voisine appartient à une zone différente (ou est hors grille).
     */
    private void dessinerBorduresZones(Graphics2D g2d, Fond fond) {
        int taille = grille.getTaille();

        for (int ligne = 0; ligne < taille; ligne++) {
            for (int col = 0; col < taille; col++) {
                Cellule cellule = grille.getCellule(ligne, col);
                ZoneCalcul zone = cellule.getZoneCalcul();
                int x = offsetX + col * tailleCellule;
                int y = offsetY + ligne * tailleCellule;

                // Bord du haut
                if (ligne == 0 || grille.getCellule(ligne - 1, col).getZoneCalcul() != zone) {
                    dessinerTraitZone(g2d, fond, x, y, x + tailleCellule, y);
                }
                // Bord du bas
                if (ligne == taille - 1 || grille.getCellule(ligne + 1, col).getZoneCalcul() != zone) {
                    dessinerTraitZone(g2d, fond, x, y + tailleCellule, x + tailleCellule, y + tailleCellule);
                }
                // Bord gauche
                if (col == 0 || grille.getCellule(ligne, col - 1).getZoneCalcul() != zone) {
                    dessinerTraitZone(g2d, fond, x, y, x, y + tailleCellule);
                }
                // Bord droit
                if (col == taille - 1 || grille.getCellule(ligne, col + 1).getZoneCalcul() != zone) {
                    dessinerTraitZone(g2d, fond, x + tailleCellule, y, x + tailleCellule, y + tailleCellule);
                }
            }
        }
    }

    private void dessinerTraitZone(Graphics2D g2d, Fond fond, int x1, int y1, int x2, int y2) {
        g2d.setStroke(STROKE_ZONE_SOUS_COUCHE);
        g2d.setColor(fond.getCouleurTexte());
        g2d.drawLine(x1, y1, x2, y2);

        g2d.setStroke(STROKE_EPAISSE);
        g2d.setColor(fond.getCouleurBordureZone());
        g2d.drawLine(x1, y1, x2, y2);
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
            saisirValeur(valeur);
        }
    }

    /**
     * Gère les touches pressées (suppression).
     */
    public void keyPressed(KeyEvent e) {
        int valeur = extraireValeurNumerique(e);
        if (valeur >= 1 && valeur <= 9) {
            saisirValeur(valeur);
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            Cellule cellule = grille.getCelluleSelectionnee();
            if (cellule != null) {
                if (modeCandidat) {
                    supprimerTousCandidats(cellule);
                } else {
                    grille.supprimerChiffre(cellule.getLigne(), cellule.getColonne());
                }
            }
        }

        // Support du minus du pavé numérique pour supprimer
        if (e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
            Cellule cellule = grille.getCelluleSelectionnee();
            if (cellule != null) {
                if (modeCandidat) {
                    supprimerTousCandidats(cellule);
                } else {
                    grille.supprimerChiffre(cellule.getLigne(), cellule.getColonne());
                }
            }
        }
    }

    private int extraireValeurNumerique(KeyEvent e) {
        int code = e.getKeyCode();

        if (code >= KeyEvent.VK_1 && code <= KeyEvent.VK_9) {
            return code - KeyEvent.VK_0;
        }

        if (code >= KeyEvent.VK_NUMPAD1 && code <= KeyEvent.VK_NUMPAD9) {
            return code - KeyEvent.VK_NUMPAD0;
        }

        if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
            switch (code) {
                case KeyEvent.VK_END: return 1;
                case KeyEvent.VK_DOWN: return 2;
                case KeyEvent.VK_PAGE_DOWN: return 3;
                case KeyEvent.VK_LEFT: return 4;
                case KeyEvent.VK_CLEAR: return 5;
                case KeyEvent.VK_RIGHT: return 6;
                case KeyEvent.VK_HOME: return 7;
                case KeyEvent.VK_UP: return 8;
                case KeyEvent.VK_PAGE_UP: return 9;
                default: break;
            }
        }

        char c = e.getKeyChar();
        if (Character.isDigit(c)) {
            return Character.getNumericValue(c);
        }

        return -1;
    }

    private void supprimerTousCandidats(Cellule cellule) {
        List<Integer> candidats = new ArrayList<>(cellule.getListeCandidat());
        for (Integer candidat : candidats) {
            grille.supprimerCandidat(cellule.getLigne(), cellule.getColonne(), candidat);
        }
    }

    public void saisirValeur(int valeur) {
        if (valeur < 1 || valeur > grille.getTaille()) {
            return;
        }

        Cellule cellule = grille.getCelluleSelectionnee();
        if (cellule == null) {
            return;
        }

        if (modeCandidat) {
            if (cellule.getListeCandidat().contains(valeur)) {
                grille.supprimerCandidat(cellule.getLigne(), cellule.getColonne(), valeur);
            } else {
                grille.ajouterCandidat(cellule.getLigne(), cellule.getColonne(), valeur);
            }
        } else {
            grille.ajouterChiffre(cellule.getLigne(), cellule.getColonne(), valeur);
        }
    }

    public void toggleModeCandidat() {
        modeCandidat = !modeCandidat;
    }

    public boolean isModeCandidat() {
        return modeCandidat;
    }

    public void applyLayout(int w, int h) {
        tailleGrille = (int)(h / 1.5f);
        tailleCellule = tailleGrille / grille.getTaille();
        offsetX = (w - tailleGrille) / 2;
        offsetY = (h - tailleGrille) / 2;
    }
}
