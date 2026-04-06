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

/**
 * Technique "Blocage Unique" :
 * permet d'identifier une cellule dont la valeur est déterminée
 * par l'ensemble des combinaisons valides de sa zone.
 */
public class BlocageUnique extends AideAbstract {

    /** Ligne de la cellule cible */
    private int cibleLigne = -1;

    /** Colonne de la cellule cible */
    private int cibleColonne = -1;

    /** Zone de calcul associée à la cellule cible */
    private ZoneCalcul cibleZone = null;

    /**
     * Constructeur de l'aide BlocageUnique.
     */
    public BlocageUnique() {
        super(6);
        refreshTexts();
    }

    /**
     * Met à jour les textes en fonction de la langue courante.
     */
    @Override
    public void refreshTexts() {
        this.titre = LangManager.get("aide.blocageunique.titre");
        this.description = LangManager.get("aide.blocageunique.description");
        this.explication = LangManager.get("aide.blocageunique.explication");
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
     * Vérifie s'il existe une cellule dont la valeur est unique
     * dans toutes les combinaisons valides de sa zone.
     *
     * @param grille la grille à analyser
     * @return true si une cellule correspondante est trouvée
     */
    @Override
    public boolean check(Grille grille) {
        this.cibleLigne = -1;
        this.cibleColonne = -1;
        this.cibleZone = null;

        int taille = grille.getTaille();

        // Parcours de toutes les zones de la grille
        for (ZoneCalcul zone : grille.getListeZones()) {

            // Liste des cellules vides dans la zone
            List<Cellule> vides = new ArrayList<>();
            for (Cellule c : zone.getListeCellules()) {
                if (c.estVide()) {
                    vides.add(c);
                }
            }

            // Si aucune case vide, on passe à la zone suivante
            if (vides.isEmpty())
                continue;

            // Récupération des combinaisons possibles pour la zone
            List<List<Integer>> mathAffectations = zone.trouverCombinaisons(taille);
            List<List<Integer>> validAffectations = new ArrayList<>();

            // Filtrage des combinaisons valides par rapport à la grille
            for (List<Integer> affectation : mathAffectations) {
                if (estCombinaisonValideGrille(grille, vides, affectation)) {
                    validAffectations.add(affectation);
                }
            }

            // Analyse des combinaisons valides
            if (!validAffectations.isEmpty()) {

                // Recherche d'une cellule avec une valeur constante
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

    /**
     * Vérifie si une combinaison est valide dans la grille.
     *
     * @param grille la grille
     * @param vides les cellules vides
     * @param affectation la combinaison à tester
     * @return true si la combinaison est valide
     */
    private boolean estCombinaisonValideGrille(Grille grille, List<Cellule> vides, List<Integer> affectation) {
        int taille = grille.getTaille();

        for (int i = 0; i < vides.size(); i++) {

            Cellule cell = vides.get(i);
            int valeur = affectation.get(i);

            // Vérification des lignes et colonnes
            for (int k = 0; k < taille; k++) {
                if (grille.getCellule(cell.getLigne(), k).getValeur() == valeur)
                    return false;

                if (grille.getCellule(k, cell.getColonne()).getValeur() == valeur)
                    return false;
            }

            // Vérification des conflits internes à la combinaison
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
                return this.getCost(nbAides);

            case 1:
                this.aideTextuel = new AideTextuel(
                        this.titre,
                        this.explication + "\n"
                                + LangManager.get("aide.blocageunique.position") + " "
                                + (this.cibleLigne + 1)
                );
                this.nbUtilisation++;
                return this.getCost(nbAides);

            default:
                this.aideTextuel = new AideTextuel(
                        this.titre,
                        this.explication + "\n"
                                + LangManager.get("aide.blocageunique.position") + " "
                                + (this.cibleLigne + 1)
                );

                // Ajout des effets visuels sur la zone
                if (this.cibleZone != null) {
                    for (Cellule c : this.cibleZone.getListeCellules()) {
                        if (c.getLigne() != this.cibleLigne || c.getColonne() != this.cibleColonne) {
                            this.aideVisuel.add(
                                    new EffetVisuel(TypeEffect.CASE_POSITIVE, c.getLigne(), c.getColonne(), "")
                            );
                        }
                    }
                }

                // Mise en évidence de la cellule cible
                this.aideVisuel.add(
                        new EffetVisuel(TypeEffect.CASE_NEGATIVE, this.cibleLigne, this.cibleColonne, "")
                );

                this.nbUtilisation++;
                return this.getCost(nbAides);
        }
    }

    /**
     * Génère une grille d'exemple pour illustrer la technique "BlocageUnique".
     *
     * @return une grille d'exemple ou une grille vide si le niveau est introuvable
     */
    public Grille getGrilleExemple() {
        Niveau niveauExemple = SaveManager.chargerNiveau("exemple_BlocageUnique");

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
