package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import Groupe6.game.Game;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.utilz.Constants;
import Groupe6.utilz.LangManager;
import Groupe6.utilz.LayoutScale;

/**
 * État « menu principal » : écran d’accueil avec boutons (jouer, paramètres, etc.).
 * Implémentation en cours ; les méthodes déléguent encore à UnsupportedOperationException.
 */
public class Menu extends Etats {

    private static final int LARGEUR_BOUTON = 400;
    private static final int HAUTEUR_BOUTON = 55;
    private static final int ESPACEMENT_BOUTONS_REF = 64;

    private LayoutScale layoutScale;
    private String labelJouer;
    private String labelParametres;
    private String labelRecords;
    private String labelQuitter;

    public Menu(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        layoutScale = LayoutScale.getInstance();
        boutons = new ArrayList<>();
        updateTexts();

        layoutScale.update(Constants.game_width, Constants.game_height);
        int cx = layoutScale.centerX();
        int cy = layoutScale.ratioY(Constants.Ratios.Menu.RATIO_MENU_BUTTONS_Y);
        int bw = layoutScale.scaleX(LARGEUR_BOUTON);
        int bh = layoutScale.scaleY(HAUTEUR_BOUTON);
        int gap = layoutScale.scaleUniform(ESPACEMENT_BOUTONS_REF);

        boutons.add(new BoutonChangeurEtat(cx - bw / 2, cy,            bw, bh, EtatJeu.SELECTION,  labelJouer));
        boutons.add(new BoutonChangeurEtat(cx - bw / 2, cy + gap,      bw, bh, EtatJeu.PARAMETRES, labelParametres));
        boutons.add(new BoutonChangeurEtat(cx - bw / 2, cy + 2 * gap,  bw, bh, EtatJeu.RECORDS,    labelRecords));
        boutons.add(new BoutonChangeurEtat(cx - bw / 2, cy + 3 * gap,  bw, bh, EtatJeu.QUITTER,    labelQuitter));
    }

    /** Met à jour le fond animé (nuages). */
    @Override
    public void update() {
      getFond().update();
    }

    @Override
    protected void applyLayout(int w, int h) {
        int cx = layoutScale.centerX();
        int cy = layoutScale.ratioY(Constants.Ratios.Menu.RATIO_MENU_BUTTONS_Y);
        int bw = layoutScale.scaleX(LARGEUR_BOUTON);
        int bh = layoutScale.scaleY(HAUTEUR_BOUTON);
        int gap = layoutScale.scaleUniform(ESPACEMENT_BOUTONS_REF);

        for (int i = 0; i < boutons.size(); i++) {
            boutons.get(i).setX(cx - bw / 2);
            boutons.get(i).setY(cy + i * gap);
            boutons.get(i).setLargeur(bw);
            boutons.get(i).setHauteur(bh);
        }
    }

    /** Dessine le fond animé et tous les boutons du menu. */
    @Override
    public void draw(Graphics g) {
        ensureLayoutUpToDate();
        getFond().draw(g);
        for (Bouton b : boutons) {
            b.draw(g, getFond());
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        gererNavigationClavier(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        clearFocusClavier();
        for (Bouton b : boutons) b.setSourisSurvol(isIn(e, b));
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    /** Marque le bouton sous la souris comme enfoncé lors de l'appui. */
    @Override
    public void mousePressed(MouseEvent e) {
        for (Bouton b : boutons) {
            if (isIn(e, b)) {
                b.setSourisEnfonce(true);
            }
        }
    }

    /** Déclenche l'action du bouton si le clic est valide (appui puis relâchement sur le même bouton). */
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
    public void updateTexts() {
        labelJouer      = LangManager.get("menu.jouer");
        labelParametres = LangManager.get("menu.parametres");
        labelRecords    = LangManager.get("menu.records");
        labelQuitter    = LangManager.get("menu.quitter");

        if (boutons == null || boutons.size() < 4) return;

        ((BoutonChangeurEtat) boutons.get(0)).setLabel(labelJouer);
        ((BoutonChangeurEtat) boutons.get(1)).setLabel(labelParametres);
        ((BoutonChangeurEtat) boutons.get(2)).setLabel(labelRecords);
        ((BoutonChangeurEtat) boutons.get(3)).setLabel(labelQuitter);
    }
}
