package Groupe6.models;

import java.util.List;

public class ZoneCalcul {
    List<Cellule> listeCellules; // Liste des cellules de la zone de calcul
    int valeurCible; // Valeur cible de la zone de calcul
    TypeOperation typeOperation; // Type d'opération de la zone de calcul

    /**
     * Constructeur
     * 
     * @param valeurCible
     * @param typeOperation
     */
    public ZoneCalcul(int valeurCible, TypeOperation typeOperation) {
        this.valeurCible = valeurCible;
        this.typeOperation = typeOperation;
    }

    /**
     * Ajouter une cellule à la zone de calcul
     * 
     * @param cellule
     * @return void
     */
    public void ajouterCellule(Cellule cellule) {
        listeCellules.add(cellule);
    }

    /**
     * Vérifier si la zone de calcul est valide
     * 
     * @return boolean
     */
    public boolean verifierMaths() {
        return true;
    }

    /**
     * Obtenir la liste des cellules de la zone de calcul
     * 
     * @return List<Cellule>
     */
    public List<Cellule> getListeCellules() {
        return listeCellules;
    }

    /**
     * Obtenir la valeur cible de la zone de calcul
     * 
     * @return int
     */
    public int getValeurCible() {
        return valeurCible;
    }

    /**
     * Obtenir le type d'opération de la zone de calcul
     * 
     * @return TypeOperation
     */
    public TypeOperation getTypeOperation() {
        return typeOperation;
    }
}
