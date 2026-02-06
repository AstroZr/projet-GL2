package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Groupe6.game.Game;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.utilz.Constants;

/**
 * État « paramètres » : écran de configuration du jeu.
 */
public class Parametres extends Etats implements MethodesEtats {

    private static final int LARGEUR_BOUTON = 200;
    private static final int HAUTEUR_BOUTON = 44;

    private FondDegrade fond;

    public Parametres(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        boutons = new ArrayList<>();
        this.fond = FondDegrade.getInstance();
        int cx = (int) (Constants.game_width * Constants.Ratios.RATIO_CENTER_X);
        int cy = (int) (Constants.game_height * Constants.Ratios.Parametres.RATIO_PARAMETRES_BUTTON_Y);
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
        fond.update();
    }

    @Override
    public void updateLayout(int gameWidth, int gameHeight) {
        applyLayout(gameWidth, gameHeight);
    }

    @Override
    protected void applyLayout(int w, int h) {
        int cx = (int) (w * Constants.Ratios.RATIO_CENTER_X);
        int cy = (int) (h * Constants.Ratios.Parametres.RATIO_PARAMETRES_BUTTON_Y);
        boutons.get(0).setX(cx - LARGEUR_BOUTON / 2);
        boutons.get(0).setY(cy);
    }

    @Override
    public void draw(Graphics g) {
        ensureLayoutUpToDate();
        fond.draw(g);
        for (Bouton b : boutons) {
            b.draw(g);
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
    public void updateTexts() {
        // Non implémenté
    }
}
