package Groupe6.etats;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import Groupe6.game.Game;
import Groupe6.save.SaveManager;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.utilz.Constants;
import Groupe6.utilz.LayoutScale;

public class Selection extends Etats {

    private static final int LARGEUR_BOUTON = 400;
    private static final int HAUTEUR_BOUTON = 55;
    private static final int ESPACEMENT_BOUTONS_REF = 64;
    private static final Font FONT_TITRE = new Font("Berlin Sans FB Demi", Font.BOLD, 36);

    private LayoutScale layoutScale;
    private int nombreNiveaux;
    private int titreTy;

    private static class BoutonNiveau extends BoutonChangeurEtat {
        private final Jeu jeu;

        BoutonNiveau(int x, int y, int w, int h, String idNiveau, Jeu jeu) {
            super(x, y, w, h, EtatJeu.GRILLE, idNiveau);
            this.jeu = jeu;
        }

        @Override
        public void appliquerAction() {
            jeu.chargerNiveau(label);
            super.appliquerAction();
        }
    }

    public Selection(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        layoutScale = LayoutScale.getInstance();
        boutons = new ArrayList<>();

        layoutScale.update(Constants.game_width, Constants.game_height);
        int cx = layoutScale.centerX();
        int bw = layoutScale.scaleX(LARGEUR_BOUTON);
        int bh = layoutScale.scaleY(HAUTEUR_BOUTON);
        int gap = layoutScale.scaleUniform(ESPACEMENT_BOUTONS_REF);
        int startY = layoutScale.ratioY(Constants.Ratios.Selection.RATIO_SELECTION_BUTTONS_Y);

        List<String> ids = SaveManager.listerIdsNiveaux();
        nombreNiveaux = ids.size();
        Jeu jeu = game.getJeu();

        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            boutons.add(new BoutonNiveau(cx - bw / 2, startY + i * gap, bw, bh, id, jeu));
        }

        int retourY = layoutScale.ratioY(Constants.Ratios.Selection.RATIO_SELECTION_RETOUR_Y);
        boutons.add(new BoutonChangeurEtat(cx - bw / 2, retourY, bw, bh, EtatJeu.MENU, "Retour"));

        titreTy = startY - 40;
    }

    @Override
    public void update() {
        getFond().update();
    }

    @Override
    protected void applyLayout(int w, int h) {
        int cx = layoutScale.centerX();
        int bw = layoutScale.scaleX(LARGEUR_BOUTON);
        int bh = layoutScale.scaleY(HAUTEUR_BOUTON);
        int gap = layoutScale.scaleUniform(ESPACEMENT_BOUTONS_REF);
        int startY = layoutScale.ratioY(Constants.Ratios.Selection.RATIO_SELECTION_BUTTONS_Y);

        for (int i = 0; i < nombreNiveaux; i++) {
            Bouton b = boutons.get(i);
            b.setX(cx - bw / 2);
            b.setY(startY + i * gap);
            b.setLargeur(bw);
            b.setHauteur(bh);
        }

        int retourY = layoutScale.ratioY(Constants.Ratios.Selection.RATIO_SELECTION_RETOUR_Y);
        Bouton retour = boutons.get(nombreNiveaux);
        retour.setX(cx - bw / 2);
        retour.setY(retourY);
        retour.setLargeur(bw);
        retour.setHauteur(bh);

        titreTy = startY - 40;
    }

    @Override
    public void draw(Graphics g) {
        ensureLayoutUpToDate();
        getFond().draw(g);

        g.setColor(getFond().getCouleurTexte());
        g.setFont(FONT_TITRE);
        String titre = "Sélection du niveau";
        int tw = g.getFontMetrics().stringWidth(titre);
        int tx = (Constants.game_width - tw) / 2;
        g.drawString(titre, tx, titreTy);

        for (Bouton b : boutons) {
            b.draw(g, getFond());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (Bouton b : boutons) {
            b.setSourisSurvol(isIn(e, b));
        }
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
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void updateTexts() {}
}
