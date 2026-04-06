package Groupe6.etats;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import Groupe6.audio.SoundManager;
import Groupe6.game.Game;
import Groupe6.save.SaveManager;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.utilz.Constants;
import Groupe6.utilz.FontCache;
import Groupe6.utilz.LangManager;
import Groupe6.utilz.LayoutScale;

/**
 * État « SELECTION DE NIVEAU » : affiche les niveaux disponibles sous forme de grille de boutons.
 * 
 * Layout:
 * - Titre: "Sélectionner un niveau"
 * - Grille de boutons (par défaut: 3 colonnes × N lignes)
 * - Chaque bouton est un BoutonNiveau custom (chargé de lancer Jeu avec ce niveau)
 * - Bouton RETOUR en bas pour revenir au Menu
 * 
 * Interaction:
 * - Clic sur BoutonNiveau("facile1") → Jeu.chargerNiveau("facile1") → EtatJeu.GRILLE
 * - Les niveaux sont lus depuis SaveManager.chargerLesNiveaux()
 * 
 * Responsivité:
 * - Calcule positions boutons selon game_width/game_height (padding, gap, center)
 * - Réadjuste à chaque resize via updateLayout
 * 
 * Héritage: Etats
 */
public class Selection extends Etats {

    // ====== DIMENSIONS DE RÉFÉRENCE ======
    private static final int LARGEUR_BOUTON = 280;          // Largeur boutons niveaux
    private static final int GAP_COLONNES_REF = 60;          // Espacement horizontal entre colonnes
    private static final int HAUTEUR_BOUTON = 55;           // Hauteur boutons
    private static final int ESPACEMENT_BOUTONS_REF = 64;    // Espacement vertical
    
    // ====== AFFICHAGE ======
    private static final Font FONT_TITRE = FontCache.get("Berlin Sans FB Demi", Font.BOLD, 36);  // Titre

    // ====== SCALING & LAYOUT ======
    private LayoutScale layoutScale;  // Responsable du redimensionnement
    private int nombreNiveaux;        // Nombre de niveaux chargés
    private int titreTy;              // Position Y du titre
    
    // ====== TEXTES LOCALISÉS ======
    private String labelTitre;        // "Sélectionner un niveau"
    private String labelRetour;       // "Retour"

    private static class BoutonNiveau extends BoutonChangeurEtat {
        private final Jeu jeu;
        private final String idNiveau;

        BoutonNiveau(int x, int y, int w, int h, String idNiveau, Jeu jeu) {
            super(x, y, w, h, EtatJeu.GRILLE, idNiveau);
            this.jeu = jeu;
            this.idNiveau = idNiveau;
        }

        static String labelTraduit(String idNiveau){
            String cle = "niveau." + idNiveau.toLowerCase().replace(" ", "");
            String traduit = LangManager.get(cle);
            // Si la clé n'existe pas, LangManager retourne la clé elle-même
            return traduit.equals(cle) ? idNiveau : traduit;
        }

        void updateLabel() {
            setLabel(labelTraduit(idNiveau));
        }

        @Override
        public void appliquerAction() {
            jeu.chargerNiveau(idNiveau);
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
        updateTexts();

        layoutScale.update(Constants.game_width, Constants.game_height);
        int cx = layoutScale.centerX();
        int bw = layoutScale.scaleX(LARGEUR_BOUTON);
        int bh = layoutScale.scaleY(HAUTEUR_BOUTON);
        int gap = layoutScale.scaleUniform(ESPACEMENT_BOUTONS_REF);
        int startY = layoutScale.ratioY(Constants.Ratios.Selection.RATIO_SELECTION_BUTTONS_Y);

        List<String> ids = SaveManager.listerIdsNiveaux();
        nombreNiveaux = ids.size();
        Jeu jeu = game.getJeu();

        int colGap = layoutScale.scaleUniform(GAP_COLONNES_REF);
        int leftX  = cx - colGap / 2 - bw;
        int rightX = cx + colGap / 2;

        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            int bx = (i % 2 == 0) ? leftX : rightX;
            int by = startY + (i / 2) * gap;
            boutons.add(new BoutonNiveau(bx, by, bw, bh, id, jeu));
        }

        int retourY = layoutScale.ratioY(Constants.Ratios.Selection.RATIO_SELECTION_RETOUR_Y);
        boutons.add(new BoutonChangeurEtat(cx - bw / 2, retourY, bw, bh, EtatJeu.MENU, labelRetour));

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

        int colGap = layoutScale.scaleUniform(GAP_COLONNES_REF);
        int leftX  = cx - colGap / 2 - bw;
        int rightX = cx + colGap / 2;

        for (int i = 0; i < nombreNiveaux; i++) {
            Bouton b = boutons.get(i);
            b.setX((i % 2 == 0) ? leftX : rightX);
            b.setY(startY + (i / 2) * gap);
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
        int tw = g.getFontMetrics().stringWidth(labelTitre);
        int tx = (Constants.game_width - tw) / 2;
        g.drawString(labelTitre, tx, titreTy);

        for (Bouton b : boutons) {
            b.draw(g, getFond());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        clearFocusClavier();
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
    public void keyPressed(KeyEvent e) {
        if (boutons == null || boutons.isEmpty()) {
            return;
        }

        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
            if (indiceFocusClavierBouton < 0) {
                setFocusIndex(0);
            } else {
                boutons.get(indiceFocusClavierBouton).appliquerAction();
            }
            return;
        }

        if (indiceFocusClavierBouton < 0) {
            if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_RIGHT
                    || code == KeyEvent.VK_UP || code == KeyEvent.VK_LEFT) {
                setFocusIndex(0);
            }
            return;
        }

        int current = indiceFocusClavierBouton;
        int next = current;
        int retourIndex = nombreNiveaux;

        switch (code) {
            case KeyEvent.VK_RIGHT:
                if (current < nombreNiveaux && current % 2 == 0 && current + 1 < nombreNiveaux) {
                    next = current + 1;
                }
                break;

            case KeyEvent.VK_LEFT:
                if (current < nombreNiveaux && current % 2 == 1) {
                    next = current - 1;
                }
                break;

            case KeyEvent.VK_DOWN:
                if (current < nombreNiveaux) {
                    int below = current + 2;
                    next = (below < nombreNiveaux) ? below : retourIndex;
                }
                break;

            case KeyEvent.VK_UP:
                if (current == retourIndex) {
                    next = Math.max(0, nombreNiveaux - 1);
                } else if (current < nombreNiveaux) {
                    int above = current - 2;
                    next = (above >= 0) ? above : current;
                }
                break;

            default:
                gererNavigationClavier(e);
                return;
        }

        if (next != current) {
            setFocusIndex(next);
        }
    }

    private void setFocusIndex(int index) {
        if (index < 0 || index >= boutons.size()) return;
        for (Bouton b : boutons) {
            b.setFocusClavier(false);
        }
        indiceFocusClavierBouton = index;
        boutons.get(index).setFocusClavier(true);
        SoundManager.getInstance().playClick();
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void updateTexts() {
        labelTitre = LangManager.get("selection.titre");
        labelRetour = LangManager.get("common.retour");

        if (boutons == null || boutons.size() <= nombreNiveaux) {
            return;
        }

        for(int i = 0; i < nombreNiveaux; i++){
            Bouton b = boutons.get(i);
            if (b instanceof BoutonNiveau) {
                ((BoutonNiveau) b).updateLabel();
            }
        }
        ((BoutonChangeurEtat) boutons.get(nombreNiveaux)).setLabel(labelRetour);
    }
}
