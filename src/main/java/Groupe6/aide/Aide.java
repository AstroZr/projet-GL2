package Groupe6.aide;

import Groupe6.models.Grille;

/**
 * Représente une aide utilisable dans le jeu.
 * <p>
 * Une aide peut être textuelle et/ou visuelle, possède un nombre d'utilisations
 * limité (ou illimité), et peut être appliquée sur une {@link Grille}.
 * </p>
 */
public interface Aide {

    /**
     * Rafraîchit les textes associés à l'aide (titre, description, etc.).
     * <p>
     * Méthode optionnelle : l’implémentation par défaut ne fait rien.
     * </p>
     */
    default void refreshTexts() {}

    /**
     * Retourne l'identifiant unique de l'aide.
     *
     * @return l'identifiant de l'aide
     */
    int getId();

    /**
     * Retourne le nombre de fois où l'aide a été utilisée.
     *
     * @return le nombre d'utilisations
     */
    int getNbUtilisation();

    /**
     * Définit le nombre d'utilisations de l'aide.
     *
     * @param nbUtilisation le nouveau nombre d'utilisations
     */
    void setNbUtilisation(int nbUtilisation);

    /**
     * Retourne l'aide sous forme textuelle.
     *
     * @return l'objet {@link AideTextuel}
     */
    AideTextuel getAideTextuel();

    /**
     * Retourne l'aide sous forme visuelle.
     *
     * @return l'objet {@link AideVisuel}
     */
    AideVisuel getAideVisuel();

    /**
     * Vérifie si l'aide peut être appliquée à la grille donnée.
     *
     * @param grille la grille cible
     * @return {@code true} si l'aide est applicable, {@code false} sinon
     */
    boolean check(Grille grille);

    /**
     * Retourne le titre de l'aide.
     *
     * @return le titre de l'aide
     */
    String getTitre();

    /**
     * Retourne la description de l'aide.
     *
     * @return la description de l'aide
     */
    String getDescription();

    /**
     * Indique si l'aide a dépassé son nombre maximal d'utilisations.
     *
     * @return {@code true} si l'aide est surutilisée, {@code false} sinon
     */
    boolean isOverUsed();

    /**
     * Applique l'aide sur la grille.
     * <p>
     * Cette méthode doit être appelée uniquement après que {@link #check(Grille)}
     * ait retourné {@code true}.
     * </p>
     *
     * @param grille la grille cible
     * @param nbAides le nombre d'aides disponibles avant utilisation
     * @return le nombre d'aides restantes après utilisation
     */
    int load(Grille grille, int nbAides);

    /**
     * Retourne le nombre maximal d'utilisations de l'aide.
     * <p>
     * Par défaut, retourne {@code -1} pour indiquer une utilisation illimitée.
     * </p>
     *
     * @return le nombre maximal d'utilisations, ou {@code -1} si illimité
     */
    default int getMaxUtilisation() { return -1; }
}