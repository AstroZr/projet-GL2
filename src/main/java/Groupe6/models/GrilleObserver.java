package Groupe6.models;

/**
 * Pattern Observer : interface pour écouter les changements de la grille.
 * 
 * Utilisé par:
 * - VueGrille: redessiner la grille à chaque changement
 * - SoundManager: jouer un son à chaque mouvement
 * - Tout composant UI qui affiche l'état de la grille
 * 
 * Méthode appelée par Grille.notifierObservateurs() après chaque action
 * (ajout chiffre, suppression, undo/redo, validation, etc.)
 */
public interface GrilleObserver {
    /**
     * Appelé lorsque l'état interne de la grille change (valeur, doublons, validation, sélection, etc.).
     * Le listener doit alors mettre à jour son affichage/logique.
     */
    void onGrilleChanged();
}
