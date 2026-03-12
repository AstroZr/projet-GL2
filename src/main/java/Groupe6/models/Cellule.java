package Groupe6.models;

import java.util.List;

public class Cellule {
    private final int ligne; // ligne de la cellule
    private final int colonne; // colonne de la cellule
    private int valeur; // valeur de la cellule
    private boolean estModifiable; // si on est en mode candidat
    private boolean estSelectionnee; // si la cellule est selectionnee
    private boolean estValide; // si la cellule est valide
    private boolean estErreur; // si la cellule est en erreur
    private transient ZoneCalcul zoneCalcul; // zone de calcul de la cellule
    private List<Integer> listeCandidat = new java.util.ArrayList<>();

    /**
     * Constructeur d'une cellule.
     * 
     * @param ligne   L'index de la ligne (0-based)
     * @param colonne L'index de la colonne (0-based)
     */
    public Cellule(int ligne, int colonne) {
        this.ligne = ligne;
        this.colonne = colonne;
        this.valeur = 0; // 0 indidique une cellule vide
        this.estModifiable = true;
        this.estSelectionnee = false;
        this.estValide = true;
        this.estErreur = false;
    }

    /**
     * Vérifier si la cellule est vide
     * 
     * @return true si la valeur est 0
     */
    public boolean estVide() {
        return valeur == 0;
    }

    // === GETTERS ET SETTERS ===
    /**
     * Retourne la ligne de la cellule
     * 
     * @return la ligne de la cellule
     */
    public int getLigne() {
        return ligne;
    }

    /**
     * Retourne la colonne de la cellule
     * 
     * @return la colonne de la cellule
     */
    public int getColonne() {
        return colonne;
    }

    /**
     * Retourne la valeur de la cellule
     * 
     * @return la valeur de la cellule
     */
    public int getValeur() {
        return valeur;
    }

    /**
     * Définit la valeur de la cellule
     * 
     * @param valeur la valeur à définir
     */
    public void setValeur(int valeur) {
        this.valeur = valeur;
    }

    /**
     * Vérifie si la cellule est modifiable
     * 
     * @return true si la cellule est modifiable, false sinon
     */
    public boolean estModifiable() {
        return estModifiable;
    }

    /**
     * Définit si la cellule est modifiable
     * 
     * @param estModifiable la valeur à définir
     */
    public void setEstModifiable(boolean estModifiable) {
        this.estModifiable = estModifiable;
    }

    /**
     * Vérifie si la cellule est sélectionnée
     * 
     * @return true si la cellule est sélectionnée, false sinon
     */
    public boolean estSelectionnee() {
        return estSelectionnee;
    }

    /**
     * Définit si la cellule est sélectionnée
     * 
     * @param estSelectionnee la valeur à définir
     */
    public void setEstSelectionnee(boolean estSelectionnee) {
        this.estSelectionnee = estSelectionnee;
    }

    /**
     * Vérifie si la cellule est valide
     * 
     * @return true si la cellule est valide, false sinon
     */
    public boolean estValide() {
        return estValide;
    }

    /**
     * Définit si la cellule est valide
     * 
     * @param estValide la valeur à définir
     */
    public void setEstValide(boolean estValide) {
        this.estValide = estValide;
    }

    /**
     * Vérifie si la cellule est en erreur
     * 
     * @return true si la cellule est en erreur, false sinon
     */
    public boolean estErreurDuplique() {
        return estErreur;
    }

    /**
     * Définit si la cellule est en erreur
     * 
     * @param estErreur la valeur à définir
     */
    public void setEstErreurDuplique(boolean estErreur) {
        this.estErreur = estErreur;
    }

    /**
     * Retourne la zone de calcul de la cellule
     * 
     * @return la zone de calcul de la cellule
     */
    public ZoneCalcul getZoneCalcul() {
        return zoneCalcul;
    }

    /**
     * Définit la zone de calcul de la cellule
     * 
     * @param zoneCalcul la valeur à définir
     */
    public void setZoneCalcul(ZoneCalcul zoneCalcul) {
        this.zoneCalcul = zoneCalcul;
    }

    /**
     * guetteur pour une liste de candidat
     * 
     * @return la liste de candidat d une cellule 
     */
    public List<Integer> getListeCandidat() {
        return listeCandidat;
    }
}
