package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Groupe6.game.Game;
import Groupe6.models.Grille;
import Groupe6.models.LevelData;
import Groupe6.models.SaveData;
import Groupe6.models.TypeOperation;
import Groupe6.models.ZoneCalcul;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.utilz.Constants;
import Groupe6.view.VueGrille;

public class Jeu extends Etats implements MethodesEtats {

    private static final int LARGEUR_BOUTON = 200;
    private static final int HAUTEUR_BOUTON = 44;

    private Grille grille;
    private VueGrille vueGrille;
    private static final int TAILLE_GRILLE = 4;
    
    private LevelData niveauActuel; 
    private SaveData profilActif = null; // Pour savoir qui joue actuellement

    public Jeu(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        boutons = new ArrayList<>();
        
        // On crée une grille vide par défaut pour éviter que le jeu plante au démarrage
        grille = new Grille(TAILLE_GRILLE, null);
        vueGrille = new VueGrille(grille);
        
        int cx = 50 + LARGEUR_BOUTON / 2;
        int cy = 950;
        boutons.add(new BoutonChangeurEtat(
                cx - LARGEUR_BOUTON / 2, cy, LARGEUR_BOUTON, HAUTEUR_BOUTON,
                EtatJeu.MENU, "Retour") {
            @Override
            public void appliquerAction() {
                sauvegarderProgression(); // On sauvegarde avant de retourner au menu !
                super.appliquerAction();
            }
        });
    }

    // --- LE CŒUR DU FIX : On charge la partie seulement quand on arrive sur l'écran ---
    private void chargerPartieSiBesoin() {
        SaveData profilCourant = game.getCurrentSave();
        
        // Si un joueur est connecté et que ce n'est pas le même qu'avant
        if (profilCourant != null && profilCourant != profilActif) {
            profilActif = profilCourant;
            
            // 1. On recrée une grille toute propre pour lui
            grille = new Grille(TAILLE_GRILLE, null);
            
            // On remet la zone d'exemple de tes potes
            ZoneCalcul zone1 = new ZoneCalcul(5, TypeOperation.ADDITION);
            zone1.ajouterCellule(grille.getCellule(0, 0));
            zone1.ajouterCellule(grille.getCellule(0, 1));
            grille.ajouterZone(zone1);
            
            vueGrille = new VueGrille(grille);
            
            // 2. On charge ses données depuis le JSON
            if (profilActif.historiqueNiveau.isEmpty()) {
                niveauActuel = new LevelData(1, TAILLE_GRILLE);
                profilActif.addLevelResult(niveauActuel);
            } else {
                niveauActuel = profilActif.historiqueNiveau.get(0);
                if (niveauActuel.contenuGrille != null) {
                    grille.importerDepuisTableau(niveauActuel.contenuGrille);
                }
            }
        }
    }

    public void sauvegarderProgression() {
        if (niveauActuel != null) {
            niveauActuel.contenuGrille = grille.exporterVersTableau();
        }
    }

    @Override
    public void update() {
        chargerPartieSiBesoin(); // Vérifie en continu si un nouveau joueur est arrivé
    }

    @Override
    public void updateLayout(int gameWidth, int gameHeight) {
        applyLayout(gameWidth, gameHeight);
    }

    @Override
    protected void applyLayout(int w, int h) {
        int cx = 50 + LARGEUR_BOUTON / 2;
        int cy = h - 130;
        if (!boutons.isEmpty()) {
            boutons.get(0).setX(cx - LARGEUR_BOUTON / 2);
            boutons.get(0).setY(cy);
        }
    }

    @Override
    public void draw(Graphics g) {
        ensureLayoutUpToDate();
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, Constants.game_width, Constants.game_height);
        vueGrille.draw(g);
        for (Bouton b : boutons) {
            b.draw(g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        vueGrille.mouseClicked(e);
        sauvegarderProgression();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (Bouton b : boutons) {
            if (isIn(e, b)) b.setSourisEnfonce(true);
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
        sauvegarderProgression();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        vueGrille.keyPressed(e);
        sauvegarderProgression();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void updateTexts() {}
}
