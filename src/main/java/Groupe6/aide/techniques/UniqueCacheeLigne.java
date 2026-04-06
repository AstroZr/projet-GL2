package Groupe6.aide.techniques;

import Groupe6.aide.AideAbstract;
import Groupe6.aide.AideTextuel;
import Groupe6.aide.AideVisuel;
import Groupe6.aide.EffetVisuel;
import Groupe6.aide.TypeEffect;
import Groupe6.models.Grille;
import Groupe6.models.Cellule;
import Groupe6.utilz.LangManager;

/**
 * Implémentation d'une aide basée sur la technique du "Unique caché en ligne".
 * <p>
 * Cette technique consiste à identifier une valeur manquante dans une ligne
 * qui ne peut être placée que dans une seule case possible.
 * </p>
 * <p>
 * L'aide fonctionne en plusieurs étapes :
 * <ul>
 *     <li>Détection d'une valeur manquante dans une ligne</li>
 *     <li>Identification des positions possibles pour cette valeur</li>
 *     <li>Validation si une seule position est possible</li>
 * </ul>
 * </p>
 */
public class UniqueCacheeLigne extends AideAbstract {

    /** Indice de la ligne cible */
    private int cibleLigne = -1;

    /** Indice de la colonne cible */
    private int cibleColonne = -1;

    /**
     * Constructeur de l'aide.
     * <p>
     * Initialise l'identifiant et les textes localisés.
     * </p>
     */
    public UniqueCacheeLigne() {
        super(4);
        refreshTexts();
    }

    /**
     * Met à jour les textes (titre, description, explication)
     * en fonction de la langue courante.
     */
    @Override
    public void refreshTexts() {
        this.titre = LangManager.get("aide.uniquecacheeligne.titre");
        this.description = LangManager.get("aide.uniquecacheeligne.description");
        this.explication = LangManager.get("aide.uniquecacheeligne.explication");
    }

    /**
     * Retourne le nombre maximal d'utilisations de cette aide.
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
     * Vérifie si un "unique caché en ligne" existe dans la grille.
     * <p>
     * Parcourt chaque ligne et recherche une valeur manquante
     * qui ne peut être placée que dans une seule colonne.
     * </p>
     *
     * @param grille la grille à analyser
     * @return {@code true} si une position unique est trouvée, {@code false} sinon
     */
    @Override
    public boolean check(Grille grille) {
        this.cibleLigne = -1;
        this.cibleColonne = -1;

        int taille = grille.getTaille();

        for (int i = 0; i < taille; i++) {

            // Marque les valeurs déjà présentes dans la ligne
            boolean[] presentsLigne = new boolean[taille + 1];
            for (int j = 0; j < taille; j++) {
                int val = grille.getCellule(i, j).getValeur();
                if (val != 0) {
                    presentsLigne[val] = true;
                }
            }

            // Recherche des valeurs manquantes
            for (int valeur = 1; valeur <= taille; valeur++) {

                if (!presentsLigne[valeur]) {

                    int posPossibles = 0;
                    int dernierePos = -1;

                    // Recherche des positions valides pour cette valeur
                    for (int j = 0; j < taille; j++) {
                        Cellule cell = grille.getCellule(i, j);

                        if (cell.estVide()) {

                            boolean presentColonne = false;

                            // Vérifie si la valeur est déjà dans la colonne
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

                    // Une seule position possible → unique caché
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

    /**
     * Applique l'aide sur la grille.
     * <p>
     * Le contenu de l'aide évolue selon le nombre d'utilisations :
     * <ul>
     *     <li>1ère utilisation : explication générale</li>
     *     <li>2ème utilisation : indication de la ligne</li>
     *     <li>3ème utilisation : indication visuelle précise</li>
     * </ul>
     * </p>
     *
     * @param grille la grille cible
     * @param nbAides le nombre total d'aides
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
                        this.explication + "\n" +
                        LangManager.get("aide.uniquecacheeligne.position") + " " +
                        (this.cibleLigne + 1)
                );
                this.nbUtilisation++;
                return this.getCost(nbAides);

            default:
                this.aideTextuel = new AideTextuel(
                        this.titre,
                        this.explication + "\n" +
                        LangManager.get("aide.uniquecacheeligne.position") + " " +
                        (this.cibleLigne + 1)
                );

                this.aideVisuel.add(
                        new EffetVisuel(
                                TypeEffect.CASE_NEGATIVE,
                                this.cibleLigne,
                                this.cibleColonne,
                                new String()
                        )
                );

                this.nbUtilisation++;
                return this.getCost(nbAides);
        }
    }
}