package Groupe6.aide.techniques;

import Groupe6.aide.AideAbstract;
import Groupe6.aide.AideTextuel;
import Groupe6.aide.AideVisuel;
import Groupe6.aide.EffetVisuel;
import Groupe6.aide.TypeEffect;
import Groupe6.models.Grille;
import Groupe6.save.Niveau;
import Groupe6.save.SaveManager;
import Groupe6.utilz.LangManager;

/**
 * Technique "Reste" : permet d'identifier une ligne ou une colonne
 * contenant exactement une seule case vide.
 */
public class Reste extends AideAbstract {

    /** Indique si la cible est une ligne (true) ou une colonne (false) */
    private boolean isLigne;

    /** Index de la ligne ou de la colonne cible */
    private int index = -1;

    /**
     * Constructeur de l'aide Reste.
     */
    public Reste() {
        super(1);
        refreshTexts();
    }

    /**
     * Met à jour les textes en fonction de la langue courante.
     */
    @Override
    public void refreshTexts() {
        this.titre = LangManager.get("aide.reste.titre");
        this.description = LangManager.get("aide.reste.description");
        this.explication = LangManager.get("aide.reste.explication");
    }

    /**
     * Vérifie si l'aide a été utilisée trop de fois.
     *
     * @return true si le nombre d'utilisations est supérieur ou égal à 3
     */
    @Override
    public boolean isOverUsed() {
        return (this.nbUtilisation >= 3);
    }

    /**
     * Vérifie s'il existe une ligne ou une colonne contenant exactement
     * une seule case vide.
     *
     * @param grille la grille à analyser
     * @return true si une telle ligne ou colonne est trouvée
     */
    @Override
    public boolean check(Grille grille) {
        this.index = -1;

        int taille = grille.getTaille();

        int ligne = 0;
        int colonne = 0;

        // Recherche dans les lignes
        while (ligne < taille && this.index == -1) {
            int count = 0;

            for (colonne = 0; colonne < taille; colonne++) {
                if (!grille.getCellule(ligne, colonne).estVide()) {
                    count++;
                }
            }

            // Une seule case vide dans la ligne
            if (count == taille - 1) {
                this.isLigne = true;
                this.index = ligne;
            }

            ligne++;
        }

        if (this.index != -1) {
            return true;
        }

        ligne = 0;
        colonne = 0;

        // Recherche dans les colonnes
        while (colonne < taille && this.index == -1) {
            int count = 0;

            for (ligne = 0; ligne < taille; ligne++) {
                if (!grille.getCellule(ligne, colonne).estVide()) {
                    count++;
                }
            }

            // Une seule case vide dans la colonne
            if (count == taille - 1) {
                this.isLigne = false;
                this.index = colonne;
            }

            colonne++;
        }

        return this.index != -1;
    }

    /**
     * Applique l'aide sur la grille.
     *
     * @param grille la grille à analyser
     * @param nbAides nombre total d'aides
     * @return le coût de l'aide
     */
    @Override
    public int load(Grille grille, int nbAides) {
        refreshTexts();

        int taille = grille.getTaille();

        this.aideVisuel = new AideVisuel();

        switch (this.nbUtilisation) {

            case 0:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);
                this.nbUtilisation++;
                return this.getCost(nbAides);

            case 1:
                this.aideTextuel = new AideTextuel(
                        this.titre,
                        this.explication + "\n"
                                + LangManager.get("aide.reste.position") + " "
                                + LangManager.get(this.isLigne ? "aide.reste.ligne" : "aide.reste.colonne")
                                + " "
                                + LangManager.get("aide.reste.numero") + " "
                                + (this.index + 1) + " !"
                );
                this.nbUtilisation++;
                return this.getCost(nbAides);

            default:
                this.aideTextuel = new AideTextuel(
                        this.titre,
                        this.explication + "\n"
                                + LangManager.get("aide.reste.position") + " "
                                + LangManager.get(this.isLigne ? "aide.reste.ligne" : "aide.reste.colonne")
                                + " "
                                + LangManager.get("aide.reste.numero") + " "
                                + (this.index + 1) + "!"
                );

                // Ajout d'effets visuels sur toute la ligne ou colonne
                if (this.isLigne) {
                    for (int colonne = 0; colonne < taille; colonne++) {
                        this.aideVisuel.add(
                                new EffetVisuel(TypeEffect.CASE_NEGATIVE, this.index, colonne, "")
                        );
                    }
                } else {
                    for (int ligne = 0; ligne < taille; ligne++) {
                        this.aideVisuel.add(
                                new EffetVisuel(TypeEffect.CASE_NEGATIVE, ligne, this.index, "")
                        );
                    }
                }

                this.nbUtilisation++;
                return this.getCost(nbAides);
        }
    }

    /**
     * Génère une grille d'exemple pour illustrer la technique "Reste".
     *
     * @return une grille d'exemple ou une grille vide si le niveau est introuvable
     */
    public Grille getGrilleExemple() {
        Niveau niveauExemple = SaveManager.chargerNiveau("exemple_Reste");

        if (niveauExemple == null) {
            return new Grille(null);
        }

        Grille grille = new Grille(niveauExemple);

        // Remplissage automatique pour l'exemple
        grille.autoRemplissage();

        // Réinitialisation du compteur pour la démonstration
        this.nbUtilisation = -1;

        load(grille, 0);

        return grille;
    }
}