package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Groupe6.game.Game;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonConnexion;
import Groupe6.ui.TextInput;

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
                0));
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
