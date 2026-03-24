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
import Groupe6.aide.AideManager;

/**
 * État du jeu en cours : affiche et gère la grille Mathdoku.
 */
public class Jeu extends Etats {

  private static final int LARGEUR_BOUTON = 200;
  private static final int HAUTEUR_BOUTON = 44;

  private Grille grille;
  private VueGrille vueGrille;
  private static final int TAILLE_GRILLE = 4; // Grille 4x4 par défaut
  private String labelRetour;

  public Jeu(Game game) {
    super(game);
    initClasses();
  }

  private void initClasses() {
    boutons = new ArrayList<>();
    updateTexts();
    
    AideManager aideManager = new AideManager();

    String joueurActuel = game.getJoueurCourant();
    if (joueurActuel == null)
      joueurActuel = "testUser";
    grille = new Grille(joueurActuel, "test");

    // Créer la vue
    vueGrille = new VueGrille(grille);

    // Bouton retour au menu - position initiale
    int cx = 50 + LARGEUR_BOUTON / 2;
    int cy = 950;
    boutons.add(new BoutonChangeurEtat(
        cx - LARGEUR_BOUTON / 2,
        cy,
        LARGEUR_BOUTON,
        HAUTEUR_BOUTON,
        EtatJeu.MENU,
        labelRetour){
        // sauvegarde grille
          @Override
          public void appliquerAction(){
            if (grille != null) {
              grille.saveGrille();
            }
            super.appliquerAction(); // Retourne au menu
          }
        });
  }

  public void chargerNiveau(String idNiveau) {
    System.out.println("Chargement du niveau : " + idNiveau);
    String joueurActuel = game.getJoueurCourant();
    if (joueurActuel == null)
      joueurActuel = "testUser";
    grille = new Grille(joueurActuel, idNiveau);
    vueGrille = new VueGrille(grille);
    lastLayoutWidth = -1;
    lastLayoutHeight = -1;
  }

  // getters pour la save
  public Grille getGrille() {
    return grille;
  }

  @Override
  public void update() {
    // Mettre à jour la logique du jeu si nécessaire
    getFond().update();
  }

  @Override
  public void updateLayout(int gameWidth, int gameHeight) {
    applyLayout(gameWidth, gameHeight);
  }

  @Override
  protected void applyLayout(int w, int h) {
    // Repositionner le bouton retour en fonction de la taille de l'écran
    int cx = 50 + LARGEUR_BOUTON / 2;
    int cy = h - 130; // 130px du bas

    if (!boutons.isEmpty()) {
      boutons.get(0).setX(cx - LARGEUR_BOUTON / 2);
      boutons.get(0).setY(cy);
    }
    vueGrille.applyLayout(w, h);
  }

  @Override
  public void draw(Graphics g) {
    ensureLayoutUpToDate();

    getFond().draw(g);

    // Déléguer l'affichage à la vue grille
    vueGrille.draw(g, getFond());

    // Dessiner les boutons
    for (Bouton b : boutons) {
      b.draw(g, getFond());
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
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
    if (vueGrille != null) {
      vueGrille.keyPressed(e);
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    // Pas d'action spécifique pour le moment
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    // Pas d'action spécifique pour le moment
  }

  @Override
  public void updateTexts() {
    boolean en = game != null && game.isEnglish();
    labelRetour = en ? "Back" : "Retour";

    if (boutons == null || boutons.isEmpty()) {
      return;
    }
    ((BoutonChangeurEtat) boutons.get(0)).setLabel(labelRetour);
  }
}
