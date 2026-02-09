package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

import Groupe6.game.Game;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;

import Groupe6.utilz.Constants;
import Groupe6.utilz.HelpMethods;
/**
 * État d’écran de démarrage : fond en dégradé (aurore), champ pseudo et bouton connexion.
 * Premier état affiché au lancement.
 *
 * @author Lounol72
 * @version 1.0
 * @since 2026-01-28
 */
public class Start extends Etats implements MethodesEtats {

    private static final int LARGEUR_BOUTON = 200;
    private static final int HAUTEUR_BOUTON = 44;
    private final int LOGO_X_POS = 0;
    private final int LOGO_Y_POS = 0; 
    private final int LOGO_DEFAULT_SIZE = 512; 
    private final int LOGO_SIZE = (int)(LOGO_DEFAULT_SIZE * 0.5f); 
    private BufferedImage logo;

    private FondDegrade fondDegrade;


    public Start(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        boutons = new ArrayList<>();
        fondDegrade = FondDegrade.getInstance();
        int cx = (int) (Constants.game_width * Constants.Ratios.RATIO_CENTER_X);
        int cy = (int) (Constants.game_height * Constants.Ratios.Start.RATIO_START_FORM_Y);

        boutons.add(new BoutonChangeurEtat(
                cx - LARGEUR_BOUTON / 2,
                cy + 10,
                LARGEUR_BOUTON,
                HAUTEUR_BOUTON,
                EtatJeu.CREATION,
                "Creation"
                ));
        boutons.add( new BoutonChangeurEtat(
                cx / 2,
                cy + 10,
                LARGEUR_BOUTON,
                HAUTEUR_BOUTON,
                EtatJeu.CONNEXION,
                "Connexion"
                ));
        logo = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "Logo_Rect.png");
    }

    /** Met à jour le fond animé (nuages). */
    @Override
    public void update() {
        fondDegrade.update();

    }

    /** Recalcule les positions des boutons selon les nouvelles dimensions. */
    @Override
    public void updateLayout(int gameWidth, int gameHeight) {
        applyLayout(gameWidth, gameHeight);
    }

    @Override
    protected void applyLayout(int w, int h) {
        int cx = (int) (w * Constants.Ratios.RATIO_CENTER_X);
        int cy = (int) (h * Constants.Ratios.Start.RATIO_START_FORM_Y);
        boutons.get(0).setX(cx / 2);
        boutons.get(0).setY(cy + 10);
        boutons.get(1).setX(cx - LARGEUR_BOUTON / 2);
        boutons.get(1).setY(cy + 10);
    }

    /** Dessine le fond animé, le logo et tous les boutons de l'écran de démarrage. */
    @Override
    public void draw(Graphics g) {
        ensureLayoutUpToDate();
        fondDegrade.draw(g);
        g.drawImage(logo, LOGO_X_POS, LOGO_Y_POS, LOGO_SIZE, LOGO_SIZE, null);
        for (Bouton b : boutons) {
            b.draw(g);
        }
    }

    /** Aucune action clavier spécifique pour cet écran. */
    @Override
    public void keyTyped(KeyEvent e) {
      // Rien de spécifique
    }

    /** Aucune action clavier spécifique pour cet écran. */
    @Override
    public void keyReleased(KeyEvent e) {
        // Rien de spécifique
    }

    /** Aucune action clavier spécifique pour cet écran. */
    @Override
    public void keyPressed(KeyEvent e) {
        // Rien de spécifique
    }

    /** Met à jour l'état de survol des boutons selon la position de la souris. */
    @Override
    public void mouseMoved(MouseEvent e) {
        for (Bouton b : boutons) {
            b.setSourisSurvol(isIn(e, b));
        }
    }

    /** Aucune action spécifique lors du drag de la souris. */
    @Override
    public void mouseDragged(MouseEvent e) {
        // Optionnel : garder le focus du champ pendant le drag
    }

    /** Les clics sont gérés via mousePressed et mouseReleased pour une meilleure détection. */
    @Override
    public void mouseClicked(MouseEvent e) {
        // Géré via pressed + released pour le bouton
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

    /** Aucune mise à jour de texte dynamique pour cet écran. */
    @Override
    public void updateTexts() {
        // Optionnel : mise à jour de libellés dynamiques
    }


}
