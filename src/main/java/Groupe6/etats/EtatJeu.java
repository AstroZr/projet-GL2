package Groupe6.etats;

/**
 * Enumération des états du jeu.
 * 
 * @author Lounol72
 * @version 1.0
 * @since 2026-01-28
 * 
 * FONCTIONNALITÉS :
 * - Gère les états du jeu
 * - Gère l'état actuel et l'état précédent du jeu
 * 
 * ARCHITECTURE :
 * - Enumération des états du jeu
 * - Gère l'état actuel et l'état précédent du jeu
 */
public enum EtatJeu {
    /** État de démarrage du jeu */
    START,
    MENU,
    GRILLE,
    PARAMETRES,
    ASTUCES,
    SELECTION;

    private static EtatJeu etatActuel = START;
    private static EtatJeu etatPrecedent = START;
    

    /**
     * Récupère l'état précédent du jeu.
     * 
     * @return L'état précédent du jeu
     */
    public static EtatJeu getEtatPrecedent() {
        return etatPrecedent;
    }

    /**
     * Récupère l'état actuel du jeu.
     * 
     * @return L'état actuel du jeu
     */
    public static EtatJeu getEtatActuel() {
        return etatActuel;
    }

    /**
     * Définit l'état actuel du jeu.
     * 
     * @param etat L'état à définir
     */
    public static void setEtatActuel(EtatJeu etat) {
        etatPrecedent = etatActuel;
        etatActuel = etat;
    }
}
