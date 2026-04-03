package Groupe6.aide.techniques;

import Groupe6.aide.AideAbstract;
import Groupe6.aide.AideTextuel;
import Groupe6.aide.AideVisuel;
import Groupe6.aide.EffetVisuel;
import Groupe6.aide.TypeEffect;
import Groupe6.models.Grille;
import Groupe6.models.Cellule;
import Groupe6.models.ZoneCalcul;
import Groupe6.save.Niveau;
import Groupe6.save.SaveManager;
import Groupe6.utilz.LangManager;

import java.util.ArrayList;
import java.util.List;

public class BlocageUnique extends AideAbstract {

    private int cibleLigne = -1;
    private int cibleColonne = -1;
    private ZoneCalcul cibleZone = null;

    public BlocageUnique() {
        super(6);
        refreshTexts();
    }

    private void refreshTexts() {
        this.titre = LangManager.get("aide.blocageunique.titre");
        this.description = LangManager.get("aide.blocageunique.description");
    }

    @Override
    public boolean check(Grille grille) {
        if (this.nbUtilisation >= 3)
            return false;

        this.cibleLigne = -1;
        this.cibleColonne = -1;
        this.cibleZone = null;

        int taille = grille.getTaille();

        for (ZoneCalcul zone : grille.getListeZones()) {
            List<Cellule> vides = new ArrayList<>();
            for (Cellule c : zone.getListeCellules()) {
                if (c.estVide()) {
                    vides.add(c);
                }
            }
            if (vides.isEmpty())
                continue;

            List<List<Integer>> mathAffectations = zone.trouverCombinaisons(taille);
            List<List<Integer>> validAffectations = new ArrayList<>();

            for (List<Integer> affectation : mathAffectations) {
                if (estCombinaisonValideGrille(grille, vides, affectation)) {
                    validAffectations.add(affectation);
                }
            }

            if (validAffectations.size() > 0) {
                // Chercher si une cellule vide prend tout le temps la même valeur sur
                // l'ensemble des combinaisons valides
                for (int i = 0; i < vides.size(); i++) {
                    int val = validAffectations.get(0).get(i);
                    boolean same = true;
                    for (List<Integer> affectation : validAffectations) {
                        if (affectation.get(i) != val) {
                            same = false;
                            break;
                        }
                    }

                    if (same) {
                        this.cibleZone = zone;
                        this.cibleLigne = vides.get(i).getLigne();
                        this.cibleColonne = vides.get(i).getColonne();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean estCombinaisonValideGrille(Grille grille, List<Cellule> vides, List<Integer> affectation) {
        int taille = grille.getTaille();
        for (int i = 0; i < vides.size(); i++) {
            Cellule cell = vides.get(i);
            int valeur = affectation.get(i);

            // Vérifier par rapport à la grille actuelle (cellules non vides)
            for (int k = 0; k < taille; k++) {
                if (grille.getCellule(cell.getLigne(), k).getValeur() == valeur)
                    return false;
                if (grille.getCellule(k, cell.getColonne()).getValeur() == valeur)
                    return false;
            }

            // Vérifier les conflits internes à cette combinaison
            for (int j = 0; j < i; j++) {
                Cellule other = vides.get(j);
                if (other.getLigne() == cell.getLigne() || other.getColonne() == cell.getColonne()) {
                    if (affectation.get(j) == valeur)
                        return false;
                }
            }
        }
        return true;
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
                        this.description + " " + LangManager.get("aide.blocageunique.position") + " "
                                + String.valueOf(this.cibleLigne + 1));
                this.nbUtilisation++;
                return this.getCost(nbAides);

            default:
                this.aideTextuel = new AideTextuel(this.titre, this.description + "\n"
                        + LangManager.get("aide.blocageunique.position") + " " + (this.cibleLigne + 1));

                if (this.cibleZone != null) {
                    for (Cellule c : this.cibleZone.getListeCellules()) {
                        if (c.getLigne() != this.cibleLigne || c.getColonne() != this.cibleColonne) {
                            this.aideVisuel.add(
                                    new EffetVisuel(TypeEffect.CASE_POSITIVE, c.getLigne(), c.getColonne(),
                                            new String()));
                        }
                    }
                }

                this.aideVisuel.add(
                        new EffetVisuel(TypeEffect.CASE_NEGATIVE, this.cibleLigne, this.cibleColonne, new String()));

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
        Niveau niveauExemple = SaveManager.chargerNiveau("exemple_BlocageUnique");

        if (niveauExemple == null) {
            return new Grille(null);
        }

        Grille grille = new Grille(niveauExemple);

        // On utilise le pre-remplissage pour avoir des chiffres
        grille.autoRemplissage();

        this.nbUtilisation = -1;
        load(grille, 0);

        return grille;
    }
}
