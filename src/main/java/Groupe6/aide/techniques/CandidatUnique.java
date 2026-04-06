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

public class CandidatUnique extends AideAbstract {
    private int ligneCellule = -1;
    private int colonneCellule = -1;

    public CandidatUnique() {
        super(7);
        refreshTexts();
    }

    @Override
    public void refreshTexts() {
        this.titre = LangManager.get("aide.candidatunique.titre");
        this.description = LangManager.get("aide.candidatunique.description");
        this.explication = LangManager.get("aide.candidatunique.explication");
    }

    private int checkCellule(Cellule[][] grid, int gridSize, int ligneCellule, int colonneCellule) {
        Set<Integer> valeurs = new HashSet<>();

        for (int ligne = 0; ligne < gridSize; ligne++) {
            if (ligne != ligneCellule) {
                valeurs.add(grid[ligne][colonneCellule].getValeur());
            }
        }

        for (int colonne = 0; colonne < gridSize; colonne++) {
            if (colonne != colonneCellule) {
                valeurs.add(grid[ligneCellule][colonne].getValeur());
            }
        }

        if (valeurs.size() == gridSize) { // because of the 0 is in the list
            for (int k = 1; k <= gridSize; k++) {
                if (!valeurs.contains(k))
                    return k;
            }
        }
        return -1;
    }

    @Override
    public boolean isOverUsed() {
        return (this.nbUtilisation >= 3);
    }

    @Override
    public boolean check(Grille grille) {
        Cellule[][] grid = grille.getMatriceCellules();
        int gridSize = grille.getTaille();
        int missingVal = -1;

        int ligne = 0;
        int colonne = 0;
        for (ligne = 0; ligne < gridSize && missingVal == -1; ligne++) {
            for (colonne = 0; colonne < gridSize && missingVal == -1; colonne++) {
                if (grid[ligne][colonne].getValeur() == 0) {
                    missingVal = checkCellule(grid, gridSize, ligne, colonne);
                }
            }
        }

        if (missingVal != -1) {
            this.ligneCellule = ligne - 1;
            this.colonneCellule = colonne - 1;
            return true;
        }
        return false;
    }

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
                this.aideTextuel = new AideTextuel(this.titre,
                        this.explication + "\n" + LangManager.get("aide.candidatunique.colonne") + " "
                                + String.valueOf(colonneCellule + 1) + " " +
                                LangManager.get("aide.candidatunique.ligne") + " " + String.valueOf(ligneCellule + 1));

                this.nbUtilisation++;
                break;

            default:
                this.aideTextuel = new AideTextuel(this.titre,
                        this.explication + "\n" + LangManager.get("aide.candidatunique.colonne") + " "
                                + String.valueOf(colonneCellule + 1) + " " +
                                LangManager.get("aide.candidatunique.ligne") + " " + String.valueOf(ligneCellule + 1));

                this.aideVisuel
                        .add(new EffetVisuel(TypeEffect.CASE_NEGATIVE, ligneCellule, colonneCellule, new String()));

                this.nbUtilisation++;
                break;
        }

        return this.getCost(nbAides);
    }
}
