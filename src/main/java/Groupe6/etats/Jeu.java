package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Groupe6.game.Game;
import Groupe6.models.Grille;
import Groupe6.models.TypeOperation;
import Groupe6.models.ZoneCalcul;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.view.VueGrille;

/**
 * État du jeu en cours : affiche et gère la grille Mathdoku.
 */
public class Jeu extends Etats implements MethodesEtats {

    private static final int LARGEUR_BOUTON = 200;
    private static final int HAUTEUR_BOUTON = 44;

    private Grille grille;
    private VueGrille vueGrille;
    private static final int TAILLE_GRILLE = 4; // Grille 4x4 par défaut

    public Jeu(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        boutons = new ArrayList<>();
        
        // Créer la grille (modèle)
        grille = new Grille(TAILLE_GRILLE, null);
        
        // Exemple : ajouter des zones de calcul
        ZoneCalcul zone1 = new ZoneCalcul(5, TypeOperation.ADDITION);
        zone1.ajouterCellule(grille.getCellule(0, 0));
        zone1.ajouterCellule(grille.getCellule(0, 1));
        grille.ajouterZone(zone1);
        
        // Créer la vue
        vueGrille = new VueGrille(grille);
        
        // Bouton retour au menu
        int cx = 50 + LARGEUR_BOUTON / 2;
        int cy = 950;
        boutons.add(new BoutonChangeurEtat(
                cx - LARGEUR_BOUTON / 2,
                cy,
                LARGEUR_BOUTON,
                HAUTEUR_BOUTON,
                EtatJeu.MENU,
                "Retour"));
    }

    @Override
    public void update() {
        // Mettre à jour la logique du jeu si nécessaire
    }

    @Override
    public void draw(Graphics g) {
        // Déléguer l'affichage à la vue
        vueGrille.draw(g);
        
        // Dessiner les boutons
        for (Bouton b : boutons) {
            b.draw(g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Géré via pressed + released pour le bouton
        // Déléguer le clic à la vue grille
        vueGrille.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (Bouton b : boutons) {
            if (isIn(e, b)) {
                b.setSourisEnfonce(true);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (Bouton b : boutons) {
            if (b.isSourisEnfonce() && isIn(e, b)) {
                b.appliquerAction();
            }
            b.setSourisEnfonce(false);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (Bouton b : boutons) {
            b.setSourisSurvol(isIn(e, b));
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        vueGrille.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        vueGrille.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Rien pour l'instant
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Rien pour l'instant
    }

    @Override
    public void updateTexts() {
        // Rien pour l'instant
    }
}
