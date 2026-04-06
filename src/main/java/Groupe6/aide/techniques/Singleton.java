package Groupe6.aide.techniques;

import Groupe6.aide.AideAbstract;
import Groupe6.aide.AideTextuel;
import Groupe6.aide.AideVisuel;
import Groupe6.aide.EffetVisuel;
import Groupe6.aide.TypeEffect;
import Groupe6.models.Grille;
import Groupe6.models.Cellule;
import Groupe6.utilz.LangManager;

import java.util.List;
import java.util.Iterator;

/**
 * Implémentation d'une aide basée sur la technique du "Singleton".
 * <p>
 * Cette technique consiste à identifier une cellule ne possédant
 * qu'un seul candidat possible (zone de calcul de taille 1)
 * et dont la valeur n'est pas encore fixée.
 * </p>
 */
public class Singleton extends AideAbstract {

    /** Ligne de la cellule cible */
    private int ligneCellule = -1;

    /** Colonne de la cellule cible */
    private int colonneCellule = -1;

    /**
     * Constructeur de l'aide Singleton.
     * <p>
     * Initialise l'identifiant et les textes localisés.
     * </p>
     */
    public Singleton() {
        super(0); // identifiant de l'aide Singleton
        refreshTexts();
    }

    /**
     * Met à jour les textes (titre, description, explication)
     * selon la langue courante.
     */
    @Override
    public void refreshTexts() {
        this.titre = LangManager.get("aide.singleton.titre");
        this.description = LangManager.get("aide.singleton.description");
        this.explication = LangManager.get("aide.singleton.explication");
    }

    /**
     * Retourne le nombre maximal d'utilisations.
     *
     * @return 3 utilisations maximum
     */
    @Override
    public int getMaxUtilisation() {
        return 3;
    }

    /**
     * Indique si l'aide a été utilisée trop de fois.
     *
     * @return {@code true} si le nombre d'utilisations est supérieur ou égal à 3
     */
    @Override
    public boolean isOverUsed() {
        return (this.nbUtilisation >= 3);
    }

    /**
     * Vérifie s'il existe une cellule avec un seul candidat possible.
     * <p>
     * Parcourt les cellules de la grille et recherche une cellule vide
     * dont la zone de calcul contient exactement un élément.
     * </p>
     *
     * @param grille la grille à analyser
     * @return {@code true} si une telle cellule est trouvée, {@code false} sinon
     */
    @Override
    public boolean check(Grille grille) {
        List<Cellule> cellules = grille.getListeCellules();

        Iterator<Cellule> iterator = cellules.iterator();
        Cellule cellule = cellules.get(0);

        while (iterator.hasNext()
                && !(cellule.getZoneCalcul().getListeCellules().size() == 1
                && cellule.getValeur() == 0)) {
            cellule = iterator.next();
        }

        if (cellule.getZoneCalcul().getListeCellules().size() == 1
                && cellule.getValeur() == 0) {

            this.ligneCellule = cellule.getLigne();
            this.colonneCellule = cellule.getColonne();
            return true;
        }

        return false;
    }

    /**
     * Applique l'aide sur la grille.
     * <p>
     * Le contenu est identique pour les premières utilisations,
     * puis une indication visuelle est ajoutée.
     * </p>
     *
     * @param grille la grille cible
     * @param nbAides le nombre total d'aides
     * @return le coût de l'aide
     */
    @Override
    public int load(Grille grille, int nbAides) {
        refreshTexts();

        // Vérification de sécurité si la cible n'a pas été initialisée
        if (ligneCellule == -1 || colonneCellule == -1) {
            if (!this.check(grille)) {
                return 0;
            }
        }

        this.aideVisuel = new AideVisuel();

        switch (this.nbUtilisation) {

            case 0:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);
                this.nbUtilisation++;
                return this.getCost(nbAides);

            case 1:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);
                this.nbUtilisation++;
                return this.getCost(nbAides);

            default:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);

                this.aideVisuel.add(
                        new EffetVisuel(
                                TypeEffect.CASE_NEGATIVE,
                                this.ligneCellule,
                                this.colonneCellule,
                                ""
                        )
                );

                this.nbUtilisation++;
                return this.getCost(nbAides);
        }
    }
}