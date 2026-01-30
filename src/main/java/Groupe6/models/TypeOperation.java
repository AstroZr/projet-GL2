package Groupe6.models;

public enum TypeOperation {
    ADDITION("+"),
    SOUSTRACTION("-"),
    MULTIPLICATION("*"),
    DIVISION("/");

    private final String symbole;

    TypeOperation(String symbole) {
        this.symbole = symbole;
    }

    public String getSymbole() {
        return symbole;
    }
}
