package Groupe6.aide.techniques;

import Groupe6.aide.AideAbstract;
import Groupe6.aide.AideTextuel;
import Groupe6.aide.AideVisuel;
import Groupe6.aide.EffetVisuel;
import Groupe6.aide.TypeEffect;
import Groupe6.models.Grille;
import Groupe6.models.Cellule;
import Groupe6.utilz.LangManager;

import java.util.Set;
import java.util.HashSet;

/**
 * Technique "Candidat Unique" :
 * permet de détecter une cellule dont la valeur peut être
 * déterminée de manière unique à partir de sa ligne et de sa colonne.
 */
public class CandidatUnique extends AideAbstract {

    /** Ligne de la cellule cible */
    private int ligneCellule = -1;

    /** Colonne de la cellule cible */
    private int colonneCellule = -1;

    /**
     * Constructeur de l'aide CandidatUnique.
     */
    public CandidatUnique() {
        super(7);
        refreshTexts();
    }

    /**
     * Met à jour les textes en fonction de la langue courante.
     */
    @Override
    public void refreshTexts() {
        this.titre = LangManager.get("aide.candidatunique.titre");
        this.description = LangManager.get("aide.candidatunique.description");
        this.explication = LangManager.get("aide.candidatunique.explication");
    }

    /**
     * Détermine la valeur possible unique d'une cellule en fonction
     * des valeurs présentes dans sa ligne et sa colonne.
     *
     * @param grid la matrice de cellules
     * @param gridSize la taille de la grille
     * @param ligneCellule la ligne de la cellule
     * @param colonneCellule la colonne de la cellule
     * @return la valeur possible unique, ou -1 si aucune
     */
    private int checkCellule(Cellule[][] grid, int gridSize, int ligneCellule, int colonneCellule) {
        Set<Integer> valeurs = new HashSet<>();

        // Collecte des valeurs présentes dans la colonne
        for (int ligne = 0; ligne < gridSize; ligne++) {
            if (ligne != ligneCellule) {
                valeurs.add(grid[ligne][colonneCellule].getValeur());
            }
        }

        // Collecte des valeurs présentes dans la ligne
        for (int colonne = 0; colonne < gridSize; colonne++) {
            if (colonne != colonneCellule) {
                valeurs.add(grid[ligneCellule][colonne].getValeur());
            }
        }

        // Vérifie si toutes les valeurs sont présentes sauf une
        if (valeurs.size() == gridSize) {
            for (int k = 1; k <= gridSize; k++) {
                if (!valeurs.contains(k))
                    return k;
            }
        }

        return -1;
    }

    /**
     * Indique si l'aide a été utilisée trop de fois.
     *
     * @return true si le nombre d'utilisations est supérieur ou égal à 3
     */
    @Override
    public boolean isOverUsed() {
        return (this.nbUtilisation >= 3);
    }

    /**
     * Recherche une cellule avec une valeur déterminée uniquement
     * par les contraintes de sa ligne et de sa colonne.
     *
     * @param grille la grille à analyser
     * @return true si une cellule correspondante est trouvée
     */
    @Override
    public boolean check(Grille grille) {
        Cellule[][] grid = grille.getMatriceCellules();
        int gridSize = grille.getTaille();
        int missingVal = -1;

        int ligne = 0;
        int colonne = 0;

        // Parcours de la grille
        for (ligne = 0; ligne < gridSize && missingVal == -1; ligne++) {
            for (colonne = 0; colonne < gridSize && missingVal == -1; colonne++) {

                // Recherche d'une cellule vide
                if (grid[ligne][colonne].getValeur() == 0) {
                    missingVal = checkCellule(grid, gridSize, ligne, colonne);
                }
            }
        }

        // Si une valeur est trouvée, on mémorise la position
        if (missingVal != -1) {
            this.ligneCellule = ligne - 1;
            this.colonneCellule = colonne - 1;
            return true;
        }

        return false;
    }

    /**
     * Applique l'aide sur la grille.
     *
     * @param grille la grille
     * @param nbAides nombre d'aides
     * @return le coût de l'aide
     */
    @Override
    public int load(Grille grille, int nbAides) {
        refreshTexts();

        this.aideVisuel = new AideVisuel();

        switch (this.nbUtilisation) {

            case 0:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);
                this.nbUtilisation++;
                break;

            case 1:
                this.aideTextuel = new AideTextuel(
                        this.titre,
                        this.explication + "\n"
                                + LangManager.get("aide.candidatunique.colonne") + " "
                                + (colonneCellule + 1) + " "
                                + LangManager.get("aide.candidatunique.ligne") + " "
                                + (ligneCellule + 1)
                );
                this.nbUtilisation++;
                break;

            default:
                this.aideTextuel = new AideTextuel(
                        this.titre,
                        this.explication + "\n"
                                + LangManager.get("aide.candidatunique.colonne") + " "
                                + (colonneCellule + 1) + " "
                                + LangManager.get("aide.candidatunique.ligne") + " "
                                + (ligneCellule + 1)
                );

                // Mise en évidence de la cellule cible
                this.aideVisuel.add(
                        new EffetVisuel(TypeEffect.CASE_NEGATIVE, ligneCellule, colonneCellule, "")
                );

                this.nbUtilisation++;
                break;
        }

        return this.getCost(nbAides);
    }
}