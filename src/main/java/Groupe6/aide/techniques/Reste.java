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


public class Reste extends AideAbstract {
    private boolean isLigne; // if false, isLigne est une colonne
    private int index = -1; // indice de la ligne ou colonne

    public Reste() {
        super(1);
        refreshTexts();
    }

    private void refreshTexts() {
        this.titre = LangManager.get("aide.reste.titre");
        this.description = LangManager.get("aide.reste.description");
    }

    @Override
    public boolean check(Grille grille) {
        if (this.nbUtilisation >= 3)
            return false;
        this.index = -1;

        int taille = grille.getTaille();

        int ligne = 0;
        int colonne = 0;
        while (ligne < taille && this.index == -1) {
            int count = 0;
            for (colonne = 0; colonne < taille; colonne++) {
                if (!grille.getCellule(ligne, colonne).estVide()) {
                    count++;
                }
            }
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
        while (colonne < taille && this.index == -1) {
            int count = 0;
            for (ligne = 0; ligne < taille; ligne++) {
                if (!grille.getCellule(ligne, colonne).estVide()) {
                    count++;
                }
            }
            if (count == taille - 1) {
                this.isLigne = false;
                this.index = colonne;
            }

            colonne++;
        }

        return (this.index != -1) ? true : false;
    }

    @Override
    public int load(Grille grille, int nbAides) {
        refreshTexts();
        int taille = grille.getTaille(); // par sécurité

        this.aideVisuel = new AideVisuel();

        switch (this.nbUtilisation) {
            case 0:
                this.aideTextuel = new AideTextuel(this.titre, this.description);
                this.nbUtilisation++;
                return this.getCost(nbAides);

            case 1:
                this.aideTextuel = new AideTextuel(this.titre,
                        this.description + " " + LangManager.get("aide.reste.position") + " "
                                + LangManager.get(this.isLigne ? "aide.reste.ligne" : "aide.reste.colonne") + " "
                                + LangManager.get("aide.reste.numero") + " " + String.valueOf(this.index + 1) + " !");
                this.nbUtilisation++;
                return this.getCost(nbAides);

            default:
                this.aideTextuel = new AideTextuel(this.titre,
                        this.description + " " + LangManager.get("aide.reste.position") + " "
                                + LangManager.get(this.isLigne ? "aide.reste.ligne" : "aide.reste.colonne") + " "
                                + LangManager.get("aide.reste.numero") + " " + String.valueOf(this.index + 1) + "!");
                if (this.isLigne) {
                    for (int colonne = 0; colonne < taille; colonne++) {
                        this.aideVisuel
                                .add(new EffetVisuel(TypeEffect.CASE_NEGATIVE, this.index, colonne, new String()));
                    }
                } else {
                    for (int ligne = 0; ligne < taille; ligne++) {
                        this.aideVisuel.add(new EffetVisuel(TypeEffect.CASE_NEGATIVE, ligne, this.index, new String()));
                    }
                }
                this.nbUtilisation++;
                return this.getCost(nbAides);
        }
    }

    /**
     * Génère une grille d'exemple de taille 4 pour voir la technique du Reste.
     * 
     * @return Une grille initialisée d'exemple pour le Reste.
     */
    public Grille getGrilleExemple() {
        Niveau niveauExemple = SaveManager.chargerNiveau("exemple_reste");
        
        if (niveauExemple == null) {
            return new Grille(null);
        }

        Grille grille = new Grille(niveauExemple);

        // Simulation : 3 cases pleines sur la ligne 0 pour que 'Reste' s'applique
        grille.getCellule(0, 0).setValeur(1);
        grille.getCellule(0, 1).setValeur(2);
        grille.getCellule(0, 2).setValeur(3);

        return grille;
    }
}
