package Groupe6.aide;

import Groupe6.models.Grille;

/**
 * Représente une aide pouvant être utilisée dans le jeu.
 * <p>
 * Une aide peut être textuelle ou visuelle et possède un identifiant unique
 * ainsi qu’un compteur d’utilisations. Elle peut vérifier si elle est applicable
 * à une {@link Grille} donnée et ensuite être chargée si la vérification est valide.
 */
public interface Aide {

    /**
     * Retourne l'identifiant unique de l'aide.
     *
     * @return l'identifiant de l'aide
     */
    int getId();

    /**
     * Retourne le nombre de fois où cette aide a été utilisée.
     *
     * @return le nombre d'utilisations de l'aide
     */
    int getNbUtilisation();

    /**
     * Retourne l'aide sous forme textuelle.
     *
     * @return l'objet {@link AideTextuel} contenant l'aide textuelle
     */
    AideTextuel getAideTextuel();

    /**
     * Retourne l'aide sous forme visuelle.
     *
     * @return l'objet {@link AideVisuel} contenant l'aide visuelle
     */
    AideVisuel getAideVisuel();

    /**
     * Vérifie si l'aide peut être appliquée à la grille donnée.
     *
     * @param grille la grille sur laquelle vérifier l'applicabilité de l'aide
     * @return {@code true} si l'aide peut être appliquée, {@code false} sinon
     */
    boolean check(Grille grille);

    /**
     * Applique l'aide sur la grille.
     * <p>
     * Cette méthode doit être appelée uniquement après que {@link #check(Grille)}
     * ait retourné {@code true}.
     *
     * @param grille la grille sur laquelle appliquer l'aide
     * @param nbAides le nombre d'aides actuellement disponibles
     * @return le nouveau nombre d'aides restantes après utilisation
     */
    int load(Grille grille, int nbAides);
}