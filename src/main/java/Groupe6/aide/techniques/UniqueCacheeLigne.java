package Groupe6.aide.techniques;

import Groupe6.aide.AideAbstract;
import Groupe6.aide.AideTextuel;
import Groupe6.aide.AideVisuel;
import Groupe6.aide.EffetVisuel;
import Groupe6.aide.TypeEffect;
import Groupe6.models.Grille;
import Groupe6.models.Cellule;
import Groupe6.utilz.LangManager;

public class UniqueCacheeLigne extends AideAbstract {
    private int cibleLigne = -1;
    private int cibleColonne = -1;

    public UniqueCacheeLigne() {
        super(4);
        refreshTexts();
    }

    private void refreshTexts() {
        this.titre = LangManager.get("aide.uniquecacheeligne.titre");
        this.description = LangManager.get("aide.uniquecacheeligne.description");
    }

    @Override
    public int getMaxUtilisation() {
        return 3;
    }

    @Override
    public boolean check(Grille grille) {
        if (this.nbUtilisation >= 3)
            return false;
        this.cibleLigne = -1;
        this.cibleColonne = -1;
        int taille = grille.getTaille();

        for (int i = 0; i < taille; i++) {
            boolean[] presentsLigne = new boolean[taille + 1];
            for (int j = 0; j < taille; j++) {
                int val = grille.getCellule(i, j).getValeur();
                if (val != 0) {
                    presentsLigne[val] = true;
                }
            }

            for (int valeur = 1; valeur <= taille; valeur++) {
                if (!presentsLigne[valeur]) {
                    // C'est un chiffre manquant. Combien de cases peuvent l'accueillir ?
                    int posPossibles = 0;
                    int dernierePos = -1;

                    for (int j = 0; j < taille; j++) {
                        Cellule cell = grille.getCellule(i, j);
                        if (cell.estVide()) {
                            // Vérifier si la colonne j contient déjà cette valeur
                            boolean presentColonne = false;
                            for (int k = 0; k < taille; k++) {
                                if (grille.getCellule(k, j).getValeur() == valeur) {
                                    presentColonne = true;
                                    break;
                                }
                            }
                            if (!presentColonne) {
                                posPossibles++;
                                dernierePos = j;
                            }
                        }
                    }

                    if (posPossibles == 1) {
                        this.cibleLigne = i;
                        this.cibleColonne = dernierePos;
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
                this.aideTextuel = new AideTextuel(this.titre, this.description);
                this.nbUtilisation++;
                return this.getCost(nbAides);

            case 1:
                this.aideTextuel = new AideTextuel(this.titre,
                        this.description + " " + LangManager.get("aide.candidatuniqueligne.position") + " "
                                + String.valueOf(this.cibleLigne + 1));
                this.nbUtilisation++;
                return this.getCost(nbAides);

            default:
                this.aideTextuel = new AideTextuel(this.titre, this.description + "\n"
                        + LangManager.get("aide.candidatuniqueligne.position") + " " + (this.cibleLigne + 1));
                this.aideVisuel.add(
                        new EffetVisuel(TypeEffect.CASE_NEGATIVE, this.cibleLigne, this.cibleColonne, new String()));
                this.nbUtilisation++;
                return this.getCost(nbAides);
        }
    }
}
