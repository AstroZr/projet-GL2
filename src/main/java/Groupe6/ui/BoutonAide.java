package Groupe6.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import Groupe6.aide.Aide;
import Groupe6.aide.AideManager;
import Groupe6.aide.AideTextuel;
import Groupe6.aide.AideVisuel;
import Groupe6.aide.EffetVisuel;
import Groupe6.aide.TypeEffect;
import Groupe6.etats.Jeu;
import Groupe6.fond.Fond;
import Groupe6.models.Grille;

/**
 * Bouton « Aide » : ouvre l’écran ou la fenêtre d’aide (implémentation à venir).
 */
public class BoutonAide extends Bouton {

    private static final Font FONT_LABEL = new Font("Berlin Sans FB Demi", Font.BOLD, 16);

    private final Jeu jeu;
    private AideManager aideManager;
    private String label;
    private final List<int[]> historiqueAides;
    private int indexAide;

    /**
     * Constructeur du bouton d'aide.
     */
    public BoutonAide(int x, int y, int largeur, int hauteur, String label, Jeu jeu) {
        super(x, y, largeur, hauteur);
        this.label = label;
        this.jeu = jeu;
        this.historiqueAides = new ArrayList<>();
        resetProgression();
    }

    @Override
    public void draw(Graphics g, Fond fond) {
        super.draw(g, fond);
        g.setColor(fond.getCouleurTexte());
        g.setFont(FONT_LABEL);
        int lw = g.getFontMetrics().stringWidth(label);
        int lx = x + (largeur - lw) / 2;
        int ly = y + (hauteur + g.getFontMetrics().getAscent()) / 2 - 2;
        g.drawString(label, lx, ly);
    }

    @Override
    public void appliquerAction() {
        Grille grille = jeu.getGrille();
        if (grille == null) {
            return;
        }

        boolean aideTrouvee = aideManager.call(grille);
        if (!aideTrouvee) {
            jeu.showAideOverlay("Aide", "Aucune aide disponible pour l'instant.");
            return;
        }

        if (indexAide < historiqueAides.size() - 1) {
            historiqueAides.subList(indexAide + 1, historiqueAides.size()).clear();
        }

        Aide aide = aideManager.getAide();
        if (aide != null) {
            appliquerAideVisuelle(grille, aide.getAideVisuel());
            afficherAideTextuelle(aide.getAideTextuel());
            historiqueAides.add(copierVecteur(aideManager.saveVector()));
            indexAide++;
            grille.enregistrerUsageAide();
        }

        grille.notifierObservateurs();
    }

    private void afficherAideTextuelle(AideTextuel aideTextuel) {
        if (aideTextuel == null) {
            return;
        }
        jeu.showAideOverlay(aideTextuel.getTitre(), aideTextuel.getTexte());
    }

    private void appliquerAideVisuelle(Grille grille, AideVisuel aideVisuel) {
        if (aideVisuel == null) {
            return;
        }

        for (EffetVisuel effet : aideVisuel) {
            if (effet == null) {
                continue;
            }

            int ligne = effet.getX();
            int colonne = effet.getY();
            if (grille.getCellule(ligne, colonne) == null) {
                continue;
            }

            if (effet.getType() == TypeEffect.CASE_NEGATIVE || effet.getType() == TypeEffect.NUMERO_NEGATIF) {
                grille.getCellule(ligne, colonne).setEstErreurDuplique(true);
            }
            if (effet.getType() == TypeEffect.CASE_POSITIVE || effet.getType() == TypeEffect.NUMERO_POSITIF) {
                grille.selectionnerCellule(ligne, colonne);
            }
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void resetProgression() {
        this.aideManager = AideManager.getInstance();
        this.historiqueAides.clear();
        this.historiqueAides.add(copierVecteur(this.aideManager.saveVector()));
        this.indexAide = 0;
    }

    public void undoAide() {
        if (indexAide <= 0) {
            return;
        }
        indexAide--;
        aideManager.restoreVector(historiqueAides.get(indexAide));
    }

    public void redoAide() {
        if (indexAide >= historiqueAides.size() - 1) {
            return;
        }
        indexAide++;
        aideManager.restoreVector(historiqueAides.get(indexAide));
    }

    private int[] copierVecteur(int[] source) {
        if (source == null) {
            return new int[0];
        }
        int[] copie = new int[source.length];
        System.arraycopy(source, 0, copie, 0, source.length);
        return copie;
    }
}
