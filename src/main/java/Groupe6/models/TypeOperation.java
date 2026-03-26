package Groupe6.models;

/**
 * Énumération des types d'opérations mathématiques possibles pour une ZoneCalcul.
 * 
 * Valeurs:
 * - ADDITION (+): somme des cellules = valeurCible
 * - SOUSTRACTION (-): première valeur (décroissante) - reste = valeurCible
 * - MULTIPLICATION (*): produit des cellules = valeurCible
 * - DIVISION (/): première valeur (décroissante) / reste = valeurCible
 * - AUCUNE (""):  cas spécial, une seule cellule doit égaler la cible
 * 
 * Stocke aussi le symbole pour affichage dans la grille.
 */
public enum TypeOperation {
    // ====== OPÉRATIONS MATHÉMATIQUES ======
    ADDITION("+"),              // Somme
    SOUSTRACTION("-"),          // Soustraction décroissante
    MULTIPLICATION("*"),        // Produit
    DIVISION("/"),              // Division décroissante
    AUCUNE("");                 // Zone avec une seule cellule (pas d'opération)

    private final String symbole;  // Symbole pour l'affichage (+, -, *, /)

    /**
     * Constructeur de l'opération
     * 
     * @param symbole le symbole de l'opération
     */
    TypeOperation(String symbole) {
        this.symbole = symbole;
    }

    /**
     * Retourne le symbole de l'opération
     * 
     * @return le symbole de l'opération
     */
    public String getSymbole() {
        return symbole;
    }

    /**
     * Retourne le symbole de l'opération
     * 
     * @return le symbole de l'opération
     */
    @Override
    public String toString() {
        return symbole;
    }
}
