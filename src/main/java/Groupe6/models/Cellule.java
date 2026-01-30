package Groupe6.models;

public class Cellule {
    int ligne; // ligne de la cellule
    int colonne; // colonne de la cellule
    int valeur; // valeur de la cellule
    boolean estModifiable; // si on est en mode candidat
    boolean estSelectionnee; // si la cellule est selectionnee
    boolean estValide; // si la cellule est valide
    boolean estErreur; // si la cellule est en erreur
    ZoneCalcul zoneCalcul; // zone de calcul de la cellule

    /**
     * Constructeur
     * 
     * @param ligne
     * @param colonne
     */
    public Cellule(int ligne, int colonne) {
        this.ligne = ligne;
        this.colonne = colonne;
    }

    /**
     * Vérifier si la cellule est valide
     * 
     * @return boolean
     */
    public boolean estValide() {
        return estValide;
    }
}
