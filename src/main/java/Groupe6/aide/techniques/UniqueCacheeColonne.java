package Groupe6.aide.techniques;

import Groupe6.aide.AideAbstract;
import Groupe6.aide.AideTextuel;
import Groupe6.aide.AideVisuel;
import Groupe6.aide.EffetVisuel;
import Groupe6.aide.TypeEffect;
import Groupe6.models.Grille;
import Groupe6.models.Cellule;
import Groupe6.utilz.LangManager;

public class UniqueCacheeColonne extends AideAbstract {
    private int cibleLigne = -1;
    private int cibleColonne = -1;

    public UniqueCacheeColonne() {
        super(5);
        refreshTexts();
    }

    private void refreshTexts() {
        this.titre = LangManager.get("aide.uniquecacheecolonne.titre");
        this.description = LangManager.get("aide.uniquecacheecolonne.description");
        this.explication = LangManager.get("aide.uniquecacheecolonne.explication");
    }

    @Override
    public int getMaxUtilisation() {
        return 3;
    }

    @Override
    public boolean isOverUsed() {
        return (this.nbUtilisation >= 3);
    }

    @Override
    public boolean check(Grille grille) {
        this.cibleLigne = -1;
        this.cibleColonne = -1;
        int taille = grille.getTaille();

        for (int j = 0; j < taille; j++) {
            boolean[] presentsColonne = new boolean[taille + 1];
            for (int i = 0; i < taille; i++) {
                int val = grille.getCellule(i, j).getValeur();
                if (val != 0) {
                    presentsColonne[val] = true;
                }
            }

            for (int valeur = 1; valeur <= taille; valeur++) {
                if (!presentsColonne[valeur]) {
                    int posPossibles = 0;
                    int dernierePos = -1;

                    for (int i = 0; i < taille; i++) {
                        Cellule cell = grille.getCellule(i, j);
                        if (cell.estVide()) {
                            boolean presentLigne = false;
                            for (int k = 0; k < taille; k++) {
                                if (grille.getCellule(i, k).getValeur() == valeur) {
                                    presentLigne = true;
                                    break;
                                }
                            }
                            if (!presentLigne) {
                                posPossibles++;
                                dernierePos = i;
                            }
                        }
                    }

                    if (posPossibles == 1) {
                        this.cibleLigne = dernierePos;
                        this.cibleColonne = j;
                        return true;
                    }
                }
            }
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
                return this.getCost(nbAides);

            case 1:
                this.aideTextuel = new AideTextuel(this.titre,
                        this.explication + "\n" + LangManager.get("aide.uniquecacheecolonne.position") + " "
                                + String.valueOf(this.cibleColonne + 1));
                this.nbUtilisation++;
                return this.getCost(nbAides);

            default:
                this.aideTextuel = new AideTextuel(this.titre, this.explication + "\n"
                        + LangManager.get("aide.uniquecacheecolonne.position") + " " + (this.cibleColonne + 1));
                this.aideVisuel.add(
                        new EffetVisuel(TypeEffect.CASE_NEGATIVE, this.cibleLigne, this.cibleColonne, new String()));
                this.nbUtilisation++;
                return this.getCost(nbAides);
        }
    }
}
