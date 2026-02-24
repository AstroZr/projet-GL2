package Groupe6.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import Groupe6.models.Cellule;
import Groupe6.models.Grille;
import Groupe6.models.ZoneCalcul;

/**
 * Vue responsable de l'affichage de la grille de jeu.
 */
public class VueGrille {
    
    private Grille grille;
    private static final int TAILLE_GRILLE = (int)(1080 / 1.5f);
    private final int TAILLE_CELLULE ;
    private static final int OFFSET_X = 40;
    private static final int OFFSET_Y = (1080 - TAILLE_GRILLE) / 2;
    
    public VueGrille(Grille grille) {
        this.grille = grille;
        TAILLE_CELLULE = TAILLE_GRILLE / grille.getTaille();
        System.out.println("======================================");
        System.out.println("Taille grille = " + TAILLE_GRILLE);
        System.out.println("Taille cellule = " +TAILLE_CELLULE);
        System.out.println("======================================");
    }
    
    /**
     * Dessine la grille à l'écran.
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Dessiner la grille
        dessinerGrille(g2d);
        dessinerZones(g2d);
    }
    
    private void dessinerGrille(Graphics2D g2d) {
        int taille = grille.getTaille();
        
        for (int ligne = 0; ligne < taille; ligne++) {
            for (int col = 0; col < taille; col++) {
                int x = OFFSET_X + col * TAILLE_CELLULE;
                int y = OFFSET_Y + ligne * TAILLE_CELLULE;
                
                Cellule cellule = grille.getCellule(ligne, col);
                
                // Fond de la cellule
                if (cellule.estSelectionnee()) {
                    g2d.setColor(new Color(100, 150, 255, 150));
                } else {
                    g2d.setColor(Color.WHITE);
                }
                g2d.fillRect(x, y, TAILLE_CELLULE, TAILLE_CELLULE);
                
                // Bordure
                if (cellule.estErreurDuplique() || !cellule.estValide()) {
                    g2d.setColor(Color.RED);
                    g2d.setStroke(new java.awt.BasicStroke(3));
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new java.awt.BasicStroke(1));
                }
                g2d.drawRect(x, y, TAILLE_CELLULE, TAILLE_CELLULE);
                
                // Afficher la valeur si présente
                int valeur = cellule.getValeur();
                if (valeur != 0) {
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Arial", Font.BOLD, 32));
                    String valeurStr = String.valueOf(valeur);
                    int strWidth = g2d.getFontMetrics().stringWidth(valeurStr);
                    int strHeight = g2d.getFontMetrics().getAscent();
                    g2d.drawString(
                        valeurStr,
                        x + (TAILLE_CELLULE - strWidth) / 2,
                        y + (TAILLE_CELLULE + strHeight) / 2 - 5
                    );
                }
            }
        }
    }
    
    private void dessinerZones(Graphics2D g2d) {
        g2d.setStroke(new java.awt.BasicStroke(3));
        g2d.setColor(new Color(0, 100, 200));
        for (ZoneCalcul zone : grille.getListeZones()) {
            Cellule premiere = zone.getListeCellules().get(0);
            int x = OFFSET_X + premiere.getColonne() * TAILLE_CELLULE;
            int y = OFFSET_Y + premiere.getLigne() * TAILLE_CELLULE;
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString(zone.getValeurCible() + zone.getTypeOperation().getSymbole(), x + 3, y + 15);
        }
    }
    
    /**
     * Gère le clic de souris sur la grille.
     */
    public void mouseClicked(MouseEvent e) {
        int col = (e.getX() - OFFSET_X) / TAILLE_CELLULE;
        int ligne = (e.getY() - OFFSET_Y) / TAILLE_CELLULE;
        
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
}
