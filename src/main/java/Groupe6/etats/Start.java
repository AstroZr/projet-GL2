package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

import Groupe6.game.Game;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonConnexion;
import Groupe6.ui.TextInput;
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

    private static final int LARGEUR_CHAMP = 400;
    private static final int HAUTEUR_CHAMP = 36;
    private static final int LARGEUR_BOUTON = 200;
    private static final int HAUTEUR_BOUTON = 44;
    private static final int MAX_PSEUDO = 20;
    private final int LOGO_X_POS = 0;
    private final int LOGO_Y_POS = 0; 
    private final int LOGO_DEFAULT_SIZE = 512; 
    private final int LOGO_SIZE = (int)(LOGO_DEFAULT_SIZE * 1.0); 
    private BufferedImage logo;

    private FondDegrade fondDegrade;
    private TextInput textInput;

    public Start(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        boutons = new ArrayList<>();
        fondDegrade = FondDegrade.getInstance();
        int cx = 960;
        int cy = 520;
        textInput = new TextInput(
                cx - LARGEUR_CHAMP / 2,
                cy - HAUTEUR_CHAMP - 20,
                LARGEUR_CHAMP,
                HAUTEUR_CHAMP,
                "Pseudo...",
                MAX_PSEUDO);
        boutons.add(new BoutonConnexion(
                cx - LARGEUR_BOUTON / 2,
                cy + 10,
                LARGEUR_BOUTON,
                HAUTEUR_BOUTON,
                EtatJeu.MENU,
                "Connexion"));
        logo = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "Logo_Rect.png");
    }

    @Override
    public void update() {
        fondDegrade.update();
        textInput.update();
    }

    @Override
    public void draw(Graphics g) {
        fondDegrade.draw(g);
        textInput.draw(g);
        g.drawImage(logo, LOGO_X_POS, LOGO_Y_POS, LOGO_SIZE, LOGO_SIZE, null);
        for (Bouton b : boutons) {
            b.draw(g);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        textInput.handleKeyTyped(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Rien de spécifique
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Rien de spécifique
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (Bouton b : boutons) {
            b.setSourisSurvol(isIn(e, b));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Optionnel : garder le focus du champ pendant le drag
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Géré via pressed + released pour le bouton
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (textInput.contains(e.getX(), e.getY())) {
            textInput.setFocused(true);
            return;
        }
        textInput.setFocused(false);
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
        // Optionnel : mise à jour de libellés dynamiques
    }

    /** Retourne le pseudo saisi (pour la connexion). */
    public String getPseudoSaisi() {
        return textInput.getTextTrimmed();
    }
}
