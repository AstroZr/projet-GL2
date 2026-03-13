package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Groupe6.game.Game;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.utilz.Constants;
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

    public Menu(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        layoutScale = LayoutScale.getInstance();
        boutons = new ArrayList<>();

        layoutScale.update(Constants.game_width, Constants.game_height);
        int cx = layoutScale.centerX();
        int cy = layoutScale.ratioY(Constants.Ratios.Menu.RATIO_MENU_BUTTONS_Y);
        int bw = layoutScale.scaleX(LARGEUR_BOUTON);
        int bh = layoutScale.scaleY(HAUTEUR_BOUTON);
        int gap = layoutScale.scaleUniform(ESPACEMENT_BOUTONS_REF);

        boutons.add(new BoutonChangeurEtat(cx - bw / 2, cy, bw, bh, EtatJeu.GRILLE, "Jouer"));
        boutons.add(new BoutonChangeurEtat(cx - bw / 2, cy + gap, bw, bh, EtatJeu.PARAMETRES, "Paramètres"));
        boutons.add(new BoutonChangeurEtat(cx - bw / 2, cy + 2 * gap, bw, bh, EtatJeu.QUITTER, "Quitter"));
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

        boutons.get(0).setX(cx - bw / 2);
        boutons.get(0).setY(cy);
        boutons.get(0).setLargeur(bw);
        boutons.get(0).setHauteur(bh);
        boutons.get(1).setX(cx - bw / 2);
        boutons.get(1).setY(cy + gap);
        boutons.get(1).setLargeur(bw);
        boutons.get(1).setHauteur(bh);
        boutons.get(2).setX(cx - bw / 2);
        boutons.get(2).setY(cy + 2 * gap);
        boutons.get(2).setLargeur(bw);
        boutons.get(2).setHauteur(bh);
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

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'keyPressed'");
    }

    /** Met à jour l'état de survol des boutons selon la position de la souris. */
    @Override
    public void mouseMoved(MouseEvent e) {
        for (Bouton b : boutons) {
            b.setSourisSurvol(isIn(e, b));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseDragged'");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseClicked'");
    }

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateTexts'");
    }
}
