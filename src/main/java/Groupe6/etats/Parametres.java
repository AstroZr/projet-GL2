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
 * État « paramètres » : écran de configuration du jeu.
 */
public class Parametres extends Etats {

    private static final int LARGEUR_BOUTON = 200;
    private static final int HAUTEUR_BOUTON = 44;

    private LayoutScale layoutScale;

    public Parametres(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        layoutScale = LayoutScale.getInstance();
        boutons = new ArrayList<>();

        layoutScale.update(Constants.game_width, Constants.game_height);
        int cx = layoutScale.centerX();
        int cy = layoutScale.ratioY(Constants.Ratios.Parametres.RATIO_PARAMETRES_BUTTON_Y);
        int bw = layoutScale.scaleX(LARGEUR_BOUTON);
        int bh = layoutScale.scaleY(HAUTEUR_BOUTON);

        boutons.add(new BoutonChangeurEtat(cx - bw / 2, cy, bw, bh, EtatJeu.MENU, "Retour"));
    }

    /** Met à jour le fond animé (nuages). */
    @Override
    public void update() {
        getFond().update();
    }

    @Override
    protected void applyLayout(int w, int h) {
        int cx = layoutScale.centerX();
        int cy = layoutScale.ratioY(Constants.Ratios.Parametres.RATIO_PARAMETRES_BUTTON_Y);
        int bw = layoutScale.scaleX(LARGEUR_BOUTON);
        int bh = layoutScale.scaleY(HAUTEUR_BOUTON);

        boutons.get(0).setX(cx - bw / 2);
        boutons.get(0).setY(cy);
        boutons.get(0).setLargeur(bw);
        boutons.get(0).setHauteur(bh);
    }

    /** Dessine le fond animé et le bouton Retour. */
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
        // Non implémenté
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Non implémenté
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Non implémenté
    }

    /** Met à jour l'état de survol du bouton Retour selon la position de la souris. */
    @Override
    public void mouseMoved(MouseEvent e) {
        for (Bouton b : boutons) {
            b.setSourisSurvol(isIn(e, b));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Non implémenté
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Non implémenté
    }

    /** Marque le bouton Retour comme enfoncé lors de l'appui. */
    @Override
    public void mousePressed(MouseEvent e) {
        for (Bouton b : boutons) {
            if (isIn(e, b)) {
                b.setSourisEnfonce(true);
            }
        }
    }

    /** Déclenche l'action du bouton Retour si le clic est valide. */
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
        // Non implémenté
    }
}
