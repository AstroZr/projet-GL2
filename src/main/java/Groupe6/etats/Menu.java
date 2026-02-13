package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Groupe6.game.Game;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.ui.BoutonConnexion;
import Groupe6.ui.TextInput;
import Groupe6.utilz.Constants;
import Groupe6.utilz.HelpMethods;

/**
 * État « menu principal » : écran d’accueil avec boutons (jouer, paramètres, etc.).
 * Implémentation en cours ; les méthodes déléguent encore à UnsupportedOperationException.
 */
public class Menu extends Etats implements MethodesEtats {

    private static final int LARGEUR_BOUTON = 400;
    private static final int HAUTEUR_BOUTON = 55;


    private FondDegrade fond;
    public Menu(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        boutons = new ArrayList<>();
        this.fond = FondDegrade.getInstance();
        int cx = (int) (Constants.game_width * Constants.Ratios.RATIO_CENTER_X);
        int cy = (int) (Constants.game_height * Constants.Ratios.Menu.RATIO_MENU_BUTTONS_Y);
        boutons.add(new BoutonChangeurEtat(
                cx - LARGEUR_BOUTON / 2,
                cy,
                LARGEUR_BOUTON,
                HAUTEUR_BOUTON,
                EtatJeu.GRILLE,
                "Jouer"));

        boutons.add(new BoutonChangeurEtat(
                cx - LARGEUR_BOUTON / 2,
                cy + 64,
                LARGEUR_BOUTON,
                HAUTEUR_BOUTON,
                EtatJeu.PARAMETRES,
                "Paramètres"));

        boutons.add(new BoutonChangeurEtat(
                cx - LARGEUR_BOUTON / 2,
                cy + 128,
                LARGEUR_BOUTON,
                HAUTEUR_BOUTON,
                EtatJeu.QUITTER,
                "Quitter"));

    }

    /** Met à jour le fond animé (nuages). */
    @Override
    public void update() {
      fond.update();
    }

    /** Recalcule les positions des boutons selon les nouvelles dimensions. */
    @Override
    public void updateLayout(int gameWidth, int gameHeight) {
        applyLayout(gameWidth, gameHeight);
    }

    @Override
    protected void applyLayout(int w, int h) {
        int cx = (int) (w * Constants.Ratios.RATIO_CENTER_X);
        int cy = (int) (h * Constants.Ratios.Menu.RATIO_MENU_BUTTONS_Y);
        boutons.get(0).setX(cx - LARGEUR_BOUTON / 2);
        boutons.get(0).setY(cy);
        boutons.get(1).setX(cx - LARGEUR_BOUTON / 2);
        boutons.get(1).setY(cy + 64);
        boutons.get(2).setX(cx - LARGEUR_BOUTON / 2);
        boutons.get(2).setY(cy + 128);
    }

    /** Dessine le fond animé et tous les boutons du menu. */
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
