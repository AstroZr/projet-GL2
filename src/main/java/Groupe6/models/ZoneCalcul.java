package Groupe6.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente une zone de calcul contenant plusieurs cellules devant respecter une opération mathématique.
 * 
 * Responsabilités:
 * - Gérer un groupe de cellules liées par une contrainte mathématique
 * - Vérifier que l'opération est satisfaite: valeurCible = op(cell1, cell2, ...)
 * - Support des 4 opérations: +, -, *, / avec gestion spéciale (ex: soustraction décroissante)
 * - Accepte zones incomplètes (retourne true) ; valide seulement quand complètes
 * 
 * Exemple: Zone avec target=6, operation=+, cells=[2,3,1] => 2+3+1=6 ✓
 */
public class ZoneCalcul {
    // ====== CELLULES ======
    private final List<Cellule> listeCellules;  // Cellules appartenant à cette zone
    
    // ====== CONTRAINTE MATHÉMATIQUE ======
    private final int valeurCible;              // Valeur que l'opération doit atteindre (target sum/product/etc)
    private final TypeOperation typeOperation;  // Type d'opération (+, -, *, /)

    /**
     * Constructeur d'une zone de calcul.
     * 
     * @param valeurCible   La valeur que l'opération doit atteindre
     * @param typeOperation Le type d'opération (+, -, *, /)
     */
    public ZoneCalcul(int valeurCible, TypeOperation typeOperation) {
        this.listeCellules = new ArrayList<>();
        this.valeurCible = valeurCible;
        this.typeOperation = typeOperation;
    }

    public ZoneCalcul(ZoneCalcul zone){
        this.listeCellules = zone.getListeCellules();
        this.valeurCible = zone.getValeurCible();
        this.typeOperation = zone.getTypeOperation();
    }

    /**
     * Ajoute une cellule à la zone.
     * 
     * @param cellule La cellule à ajouter
     */
    public void ajouterCellule(Cellule cellule) {
        listeCellules.add(cellule);
        cellule.setZoneCalcul(this);
    }

    /**
     * Vérifie si la zone est actuellement valide.
     * 
     * @return true si la zone est valide (ou incomplète), false si l'opération est
     *         incorrecte
     */
    public boolean verifierMaths() {
        // Collecter les valeurs non nulles
        List<Integer> valeurs = new ArrayList<>();
        for (Cellule c : listeCellules) {
            if (!c.estVide()) {
                valeurs.add(c.getValeur());
            }
        }

        // On valide seulement quand toutes les cellules de la zone sont remplies.
        if (valeurs.size() < listeCellules.size()) {
            return true;
        }

        // Validation selon l'opération
        switch (typeOperation) {
            case ADDITION:
                return valeurs.stream().mapToInt(Integer::intValue).sum() == valeurCible;

            case SOUSTRACTION:
                // Pour la soustraction, on fait le premier moins les autres.
                Collections.sort(valeurs, Collections.reverseOrder()); // Tri décroissant sinon on peut avoir des
                                                                       // résultats négatifs
                int premier = valeurs.get(0);
                for (int i = 1; i < valeurs.size(); i++) {
                    premier -= valeurs.get(i);
                }
                return (premier) == valeurCible;

            case MULTIPLICATION:
                return valeurs.stream().mapToInt(Integer::intValue).reduce(1, (a, b) -> a * b) == valeurCible;

            case DIVISION:
                // Pour la division, on fait le premier divisé par les autres.
                Collections.sort(valeurs, Collections.reverseOrder()); // Tri croissant sinon on peut avoir des
                                                                       // résultats négatifs
                int premierDiv = valeurs.get(0);
                for (int i = 1; i < valeurs.size(); i++) {
                    premierDiv /= valeurs.get(i);
                }
                return (premierDiv) == valeurCible;

            case AUCUNE:
                // Cas d'une seule case avec le chiffre donné
                return valeurs.get(0) == valeurCible;

            default:
                return false;
        }
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

    /**
     * Explore les combinaisons .
     * 
     * @param index     L index de la cellule vide que l on traite actuellement
     * @param max       Valeur maximale
     * @param resultats Liste accumulant le combinaison valides trouve
     */
    private void rechercherRecursive(List<Cellule> vides, int index, int max, List<List<Integer>> resultats) {
        // Cas de base : toutes les cellules vides ont une valeur de test
        if (index == vides.size()) {
            if (verifierMaths()) {
                List<Integer> combinaison = new ArrayList<>();
                for (Cellule c : vides) {
                    combinaison.add(c.getValeur());
                }
                resultats.add(combinaison);
            }
            return;
        }

        Cellule celluleCourante = vides.get(index);

        for (int i = 1; i <= max; i++) {
            // On simule le remplissage
            celluleCourante.setValeur(i);

            // appel recursif pour la cellule suivante
            rechercherRecursive(vides, index + 1, max, resultats);

            // On vide la cellule
            celluleCourante.setValeur(0);
        }
    }

    /**
     * trouver les Combinaisons de touche
     * 
     * @param max Valeur maximale
     */
    public List<List<Integer>> trouverCombinaisons(int max) {
        List<Cellule> vides = new ArrayList<>();
        for (Cellule c : listeCellules) {
            if (c.estVide()) {
                vides.add(c);
            }
        }

        List<List<Integer>> resultats = new ArrayList<>();
        rechercherRecursive(vides, 0, max, resultats);
        return resultats;
    }

}
