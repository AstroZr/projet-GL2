package Groupe6.models;

/**
 * Interface pour les observateurs de la grille.
 */
public interface GrilleObserver {
    /**
     * Appelé lorsque l'état de la grille change.
     */
    void onGrilleChanged();
}
