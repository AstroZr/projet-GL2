package Groupe6.etats;

/**
 * Énumération des états possibles de l'application (machine à états).
 * Mémorise l'état courant et le précédent pour permettre retours ou transitions.
 *
 * @author Lounol72
 * @version 1.0
 * @since 2026-01-28
 */
public enum EtatJeu {
    START,
    MENU,
    GRILLE,
    PARAMETRES,
    ASTUCES,
    SELECTION,
    QUITTER;

    private static EtatJeu etatActuel = START;
    private static EtatJeu etatPrecedent = START;
    

    /** Retourne l’état actif juste avant le changement courant. */
    public static EtatJeu getEtatPrecedent() {
        return etatPrecedent;
    }

    /** Retourne l’état actuellement affiché. */
    public static EtatJeu getEtatActuel() {
        return etatActuel;
    }

    /**
     * Change l'état courant ; l'ancien état est conservé dans etatPrecedent.
     *
     * @param etat Nouvel état actif
     */
    public static void setEtatActuel(EtatJeu etat) {
        etatPrecedent = etatActuel;
        etatActuel = etat;
    }
}
