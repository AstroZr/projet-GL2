package Groupe6.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente une zone de calcul contenant plusieurs cellules
 * devant respecter une opération mathématique.
 */
public class ZoneCalcul {
    private final List<Cellule> listeCellules; // Liste des cellules de la zone de calcul
    private final int valeurCible; // Valeur cible de la zone de calcul
    private final TypeOperation typeOperation; // Type d'opération de la zone de calcul

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
                Collections.sort(valeurs); // Tri croissant sinon on peut avoir des résultats négatifs
                int premier = valeurs.get(0);
                int reste = 0;
                for (int i = 1; i < valeurs.size(); i++) {
                    reste += valeurs.get(i);
                }
                return (premier - reste) == valeurCible;

            case MULTIPLICATION:
                return valeurs.stream().mapToInt(Integer::intValue).reduce(1, (a, b) -> a * b) == valeurCible;

            case DIVISION:
                // Pour la division, on fait le premier divisé par les autres.
                Collections.sort(valeurs); // Tri croissant sinon on peut avoir des résultats négatifs
                int premierDiv = valeurs.get(0);
                int diviseur = 1;
                for (int i = 1; i < valeurs.size(); i++) {
                    diviseur *= valeurs.get(i);
                }
                return (premierDiv / diviseur) == valeurCible;

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
     * calcule toutes les combinaisons possibles de chiffres manquants pour completer la zone de calcul
     * @return Une liste de listes chaque sous liste represente une combinaison valide de chiffres p our remplir les cellules vides
     */
    public List<List<Integer>> trouverCombinaisons(int max) {
        List<Cellule> vides = new ArrayList<>();
        List<Integer> remplis = new ArrayList<>();
        
        for (Cellule c : listeCellules) {
            if (c.estVide()) vides.add(c);
            else remplis.add(c.getValeur());
        }

        List<List<Integer>> resultats = new ArrayList<>();
        rechercherRecursive(vides.size(), remplis, new ArrayList<>(), max, resultats);
        return resultats;
    }

    /**
     * explorer combinaisons de chiffre
     * @param remplis Liste des valeurs deja presentes dans la zo ne
     * @param courant Liste temporaire des chiffres testes lors de la recursion
     * @param max Valeur maximale autorisee
     * @param resultats Liste accumulant les combinaisons valides trouve
     */
    private void rechercherRecursive(int nbVides, List<Integer> remplis, List<Integer> courant, int max, List<List<Integer>> resultats) {
        if (courant.size() == nbVides) {
            List<Integer> test = new ArrayList<>(remplis);
            test.addAll(courant);
            if (estValideLogique(test)) {
                resultats.add(new ArrayList<>(courant));
            }
            return;
        }
        for (int i = 1; i <= max; i++) {
            courant.add(i);
            rechercherRecursive(nbVides, remplis, courant, max, resultats);
            courant.remove(courant.size() - 1);
        }
    }

    /**
     * valide si une liste de valeurs respecte la regle mathematique definie par le type d'operation de la zone
     * @return true si le résultat correspond à la valeurCible ou false sinon
     */
    private boolean estValideLogique(List<Integer> valeurs) {
        switch (typeOperation) {
            case ADDITION:
                return valeurs.stream().mapToInt(Integer::intValue).sum() == valeurCible;
            case MULTIPLICATION:
                return valeurs.stream().mapToInt(Integer::intValue).reduce(1, (a, b) -> a * b) == valeurCible;
            case SOUSTRACTION:
                Collections.sort(valeurs, Collections.reverseOrder());
                int resS = valeurs.get(0);
                for (int i = 1; i < valeurs.size(); i++) resS -= valeurs.get(i);
                return resS == valeurCible;
            case DIVISION:
                Collections.sort(valeurs, Collections.reverseOrder());
                int resD = valeurs.get(0);
                for (int i = 1; i < valeurs.size(); i++) resD /= valeurs.get(i);
                return resD == valeurCible;
            case AUCUNE:
                return valeurs.size() == 1 && valeurs.get(0) == valeurCible;
            default: return false;
        }
    }
}
