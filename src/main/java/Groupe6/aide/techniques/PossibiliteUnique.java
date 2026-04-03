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

public class PossibiliteUnique extends AideAbstract {
    private int ligneCellule = -1;
    private int colonneCellule = -1;

    public PossibiliteUnique(){
        super(6);
        refreshTexts();
    }

    private void refreshTexts() {
        this.titre = LangManager.get("aide.possibiliteunique.titre");
        this.description = LangManager.get("aide.possibiliteunique.description");
    }
    
    private boolean checkCellule(Cellule[][] grid, int gridSize, int ligneCellule, int colonneCellule){
        Set<Integer> valeurs = new HashSet<>();

        for (int ligne = 0; ligne < gridSize; ligne++){
            if (ligne != ligneCellule){
                valeurs.add(grid[ligne][colonneCellule].getValeur());
            }
        }
        
        for (int colonne = 0; colonne < gridSize; colonne++){
            if (colonne != colonneCellule){
                valeurs.add(grid[ligneCellule][colonne].getValeur());
            }
        }
        return (valeurs.size() == gridSize); // beacause of the 0 is in the list
    }

    @Override
    public boolean isOverUsed(){
        return (this.nbUtilisation >= 3);
    }

    @Override
    public boolean check(Grille grille) {        
        Cellule[][] grid = grille.getMatriceCellules();
        int gridSize = grille.getTaille();
        boolean finded = false;

        int ligne = 0;
        int colonne = 0;
        for (ligne = 0; ligne < gridSize && !finded; ligne++){
            for (colonne = 0; colonne < gridSize && !finded; colonne++){
                if (grid[ligne][colonne].getValeur() == 0){
                    finded = checkCellule(grid, gridSize, ligne, colonne);
                }
            }
        }

        if (finded){
            this.ligneCellule = ligne - 1;
            this.colonneCellule = colonne - 1;
            return true;
        }
        return false;
    }

    @Override
    public int load(Grille grille, int nbAides) {
        refreshTexts();
        int taille = grille.getTaille();

        this.aideVisuel = new AideVisuel();

        switch (this.nbUtilisation) {
            case 0:
                this.aideTextuel = new AideTextuel(this.titre, this.description);
                this.nbUtilisation++;
                break;

            case 1:
                this.aideTextuel = new AideTextuel(this.titre,
                        this.description + " " + LangManager.get("aide.possibiliteunique.colonne") + " " + String.valueOf(colonneCellule + 1) + " " +
                        LangManager.get("aide.possibiliteunique.ligne") + " " + String.valueOf(ligneCellule + 1) + " !");
                
                this.nbUtilisation++;
                break;

            default:
                this.aideTextuel = new AideTextuel(this.titre,
                        this.description + " " + LangManager.get("aide.possibiliteunique.colonne") + " " + String.valueOf(colonneCellule + 1) + " " +
                        LangManager.get("aide.possibiliteunique.ligne") + " " + String.valueOf(ligneCellule + 1) + " !");
                
                this.aideVisuel.add(new EffetVisuel(TypeEffect.CASE_NEGATIVE, ligneCellule, colonneCellule, new String()));
                
                this.nbUtilisation++;
                break;
            }

        return this.getCost(nbAides);
    }
}
