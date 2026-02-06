package Groupe6.models;

/**
 * Enumération des types d'opérations possibles pour une zone de calcul.
 */
public enum TypeOperation {
    ADDITION("+"),
    SOUSTRACTION("-"),
    MULTIPLICATION("*"),
    DIVISION("/"),
    AUCUNE("");

    private final String symbole;

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
