package Groupe6.etats;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

import Groupe6.game.Game;
import Groupe6.save.SaveManager;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.utilz.Constants;
import Groupe6.utilz.HelpMethods;
import Groupe6.utilz.LangManager;
import Groupe6.utilz.LayoutScale;

/**
 * État d'écran de démarrage : fond en dégradé, logo animé, titre, boutons Création / Connexion.
 *
 * @author Lounol72
 * @version 1.0
 * @since 2026-01-28
 */
public class Start extends Etats {

    private static final int LARGEUR_BOUTON = 250;
    private static final int HAUTEUR_BOUTON = 55;
    private static final int LOGO_DEFAULT_SIZE = 320;

    private int logoX;
    private int logoY;
    private int logoSize;
    private BufferedImage logo;

    private static final float LOGO_FLOAT_AMPLITUDE = 0.06f;
    private static final float LOGO_FLOAT_PERIOD_SEC = 2.2f;
    private static final float LOGO_DISPLAY_LERP = 0.25f;
    private final long logoAnimStartNanos = System.nanoTime();
    private float logoFloatScale = 1f;
    private float displayedLogoScale = 1f;

    private BufferedImage nameAppImage;
    private int nameAppX;
    private int nameAppY;
    private int nameAppWidth;
    private int nameAppHeight;

    private LayoutScale layoutScale;
    private String labelCreation;
    private String labelConnexion;
    private boolean synchroniseDepuisEntree = false;
    private boolean hasProfiles = false;

    public Start(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        layoutScale = LayoutScale.getInstance();
        boutons = new ArrayList<>();
        updateTexts();
        logo = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "Logo_Rect.png");
        nameAppImage = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "NameApp.png");
    }

    private void rechargerProfils() {
        boolean ancien = hasProfiles;
        hasProfiles = !SaveManager.listerJoueurs().isEmpty();
        if (hasProfiles != ancien) lastLayoutWidth = -1; // force recalcul layout
    }

    @Override
    public void update() {
        if (!synchroniseDepuisEntree) {
            rechargerProfils();
            synchroniseDepuisEntree = true;
        }
        getFond().update();
        logoFloatScale = computeLogoFloatScale();
    }

    private float computeLogoFloatScale() {
        double elapsedSec = (System.nanoTime() - logoAnimStartNanos) / 1e9;
        double phase = (elapsedSec % LOGO_FLOAT_PERIOD_SEC) / LOGO_FLOAT_PERIOD_SEC * 2.0 * Math.PI;
        float ease = (float) (0.5 + 0.5 * Math.sin(phase));
        return 1f + LOGO_FLOAT_AMPLITUDE * ease;
    }

    /** Bloc logo → nom app → boutons, centré. Boutons conditionnels selon présence de profils. */
    @Override
    protected void applyLayout(int w, int h) {
        layoutScale.update(w, h);
        int cx = layoutScale.centerX();

        logoSize = layoutScale.scaleUniform(LOGO_DEFAULT_SIZE);
        logoX = cx - logoSize / 2;
        logoY = layoutScale.ratioY(Constants.Ratios.Start.RATIO_LOGO_Y);

        int gapLogoName = layoutScale.scaleUniform(Constants.Ratios.Start.ESPACEMENT_LOGO_NAME_REF);
        nameAppHeight = layoutScale.scaleUniform(Constants.Ratios.Start.NAME_APP_REF_HEIGHT);
        nameAppWidth = (nameAppImage != null && nameAppImage.getHeight() > 0)
                ? nameAppHeight * nameAppImage.getWidth() / nameAppImage.getHeight()
                : nameAppHeight;
        nameAppX = cx - nameAppWidth / 2;
        nameAppY = logoY + logoSize + gapLogoName;

        int gapNameButtons = layoutScale.scaleUniform(Constants.Ratios.Start.ESPACEMENT_NAME_BOUTONS_REF);
        int buttonY = nameAppY + nameAppHeight + gapNameButtons;
        int gap = layoutScale.scaleX(Constants.Ratios.Start.ESPACEMENT_BOUTONS_REF);
        int bw = layoutScale.scaleX(LARGEUR_BOUTON);
        int bh = layoutScale.scaleY(HAUTEUR_BOUTON);

        boutons.clear();
        if (hasProfiles) {
            boutons.add(new BoutonChangeurEtat(cx - gap - bw, buttonY, bw, bh, EtatJeu.CREATION, labelCreation));
            boutons.add(new BoutonChangeurEtat(cx + gap, buttonY, bw, bh, EtatJeu.CONNEXION, labelConnexion));
        } else {
            boutons.add(new BoutonChangeurEtat(cx - bw / 2, buttonY, bw, bh, EtatJeu.CREATION, labelCreation));
        }
    }

    /** Dessine le fond animé, le logo et tous les boutons de l'écran de démarrage. */
    @Override
    public void draw(Graphics g) {
        ensureLayoutUpToDate();
        getFond().draw(g);

        displayedLogoScale += (logoFloatScale - displayedLogoScale) * LOGO_DISPLAY_LERP;
        int drawSize = (int) (logoSize * displayedLogoScale);
        int offset = (logoSize - drawSize) / 2;
        g.drawImage(logo, logoX + offset, logoY + offset, drawSize, drawSize, null);
        g.drawImage(nameAppImage, nameAppX, nameAppY, nameAppWidth, nameAppHeight, null);
        for (Bouton b : boutons) {
            b.draw(g, getFond());
        }
        // super.drawGrid(g);
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
                synchroniseDepuisEntree = false; // prêt pour la prochaine entrée
                b.appliquerAction();
            }
            b.setSourisEnfonce(false);
        }
    }

    @Override
    public void updateTexts() {
        labelCreation = LangManager.get("start.creation");
        labelConnexion = LangManager.get("start.connexion");

        if (boutons == null || boutons.isEmpty()) return;
        ((BoutonChangeurEtat) boutons.get(0)).setLabel(labelCreation);
        if (boutons.size() >= 2) ((BoutonChangeurEtat) boutons.get(1)).setLabel(labelConnexion);
    }


}
