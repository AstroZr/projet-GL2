package Groupe6.aide.techniques;

import Groupe6.aide.AideAbstract;
import Groupe6.aide.AideTextuel;
import Groupe6.aide.AideVisuel;
import Groupe6.aide.EffetVisuel;
import Groupe6.aide.TypeEffect;
import Groupe6.models.Grille;
import Groupe6.models.Cellule;
import Groupe6.models.ZoneCalcul;
import Groupe6.utilz.LangManager;

import java.util.*;

public class NakedN extends AideAbstract {

    private final int n; // taille du subset cherché (2 = pair, 3 = triple, etc.)

    private List<Cellule> groupe = new ArrayList<>();
    private List<Cellule> impact = new ArrayList<>();
    private Set<Integer> unionCandidats = new HashSet<>();

    public NakedN(int id, int n) {
        super(id);
        this.n = n;
        refreshTexts();
    }

    @Override
    public void refreshTexts() {
        this.titre = LangManager.get("aide.nakedn.titre") + String.valueOf(n);
        this.description = LangManager.get("aide.difficiles") + LangManager.get("aide.nakedn.description");
        this.explication = LangManager.get("aide.nakedn.explication");
    }

    @Override
    public int getMaxUtilisation() {
        return 3;
    }

    @Override
    public boolean isOverUsed() {
        return this.nbUtilisation >= 3;
    }

    // ===================== UTILITAIRES =====================

    private Set<Integer> getCandidats(Cellule cellule, Grille grille) {
        int taille = grille.getTaille();
        Set<Integer> dejaPresents = new HashSet<>();

        for (int colonne = 0; colonne < taille; colonne++) {
            Cellule c = grille.getCellule(cellule.getLigne(), colonne);
            if (!c.estVide()) {
                dejaPresents.add(c.getValeur());
            }
        }

        for (int ligne = 0; ligne < taille; ligne++) {
            Cellule c = grille.getCellule(ligne, cellule.getColonne());
            if (!c.estVide()) {
                dejaPresents.add(c.getValeur());
            }
        }

        Set<Integer> candidats = new HashSet<>();
        for (int v = 1; v <= taille; v++)
            if (!dejaPresents.contains(v))
                candidats.add(v);

        ZoneCalcul zone = cellule.getZoneCalcul();
        if (zone != null)
            candidats.retainAll(getValeursPossiblesPourCellule(zone, cellule, taille));

        return candidats;
    }

    private Set<Integer> getValeursPossiblesPourCellule(ZoneCalcul zone, Cellule cellule, int taille) {
        int index = zone.getListeCellules().indexOf(cellule);
        Set<Integer> possibles = new HashSet<>();
        for (List<Integer> combo : zone.trouverCombinaisons(taille))
            if (index < combo.size())
                possibles.add(combo.get(index));
        return possibles;
    }

    /**
     * Génère toutes les combinaisons de taille `taille` depuis une liste de
     * cellules.
     * Ex : [A, B, C] taille 2 → [[A,B], [A,C], [B,C]]
     */
    private List<List<Cellule>> combinaisons(List<Cellule> cellules, int taille) {
        List<List<Cellule>> result = new ArrayList<>();
        combiner(cellules, taille, 0, new ArrayList<>(), result);
        return result;
    }

    private void combiner(List<Cellule> cellules, int taille, int debut,
            List<Cellule> courante, List<List<Cellule>> result) {
        if (courante.size() == taille) {
            result.add(new ArrayList<>(courante));
            return;
        }
        for (int i = debut; i < cellules.size(); i++) {
            courante.add(cellules.get(i));
            combiner(cellules, taille, i + 1, courante, result);
            courante.remove(courante.size() - 1);
        }
    }

    // ===================== CHECK / LOAD =====================

    private boolean detect(Grille grille, List<Cellule> unit) {
        // Calculer les candidats de chaque cellule vide
        Map<Cellule, Set<Integer>> candidatsMap = new LinkedHashMap<>();
        for (Cellule c : unit)
            if (c.estVide())
                candidatsMap.put(c, getCandidats(c, grille));

        List<Cellule> vides = new ArrayList<>(candidatsMap.keySet());

        // Pas assez de cellules pour former un subset de taille n
        if (vides.size() <= n)
            return false;

        // Tester toutes les combinaisons de n cellules
        for (List<Cellule> groupe : combinaisons(vides, n)) {

            // Union des candidats du groupe
            Set<Integer> unionCandidats = new HashSet<>();
            for (Cellule c : groupe)
                unionCandidats.addAll(candidatsMap.get(c));

            // Naked N : l'union contient exactement n valeurs
            if (unionCandidats.size() == n) {

                // Chercher les cellules hors groupe impactées
                List<Cellule> impact = new ArrayList<>();
                for (Cellule autre : vides) {
                    if (groupe.contains(autre))
                        continue;
                    Set<Integer> intersection = new HashSet<>(candidatsMap.get(autre));
                    intersection.retainAll(unionCandidats);
                    if (!intersection.isEmpty())
                        impact.add(autre);
                }

                if (!impact.isEmpty()) {
                    boolean needed = false;
                    for (Cellule cellule : groupe) {
                        if (!cellule.getListeCandidat().containsAll(unionCandidats)) {
                            needed = true;
                        }
                    }
                    if (needed == true) {
                        this.unionCandidats = unionCandidats;
                        this.groupe = groupe;
                        this.impact = impact;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean check(Grille grille) { // il faudra faire aussi la négation !!!
        int taille = grille.getTaille();

        for (int i = 0; i < taille; i++) {
            List<Cellule> unit = new ArrayList<>();
            for (int colonne = 0; colonne < taille; colonne++) {
                unit.add(grille.getCellule(i, colonne));
            }
            if (detect(grille, unit)) {
                return true;
            }

            unit = new ArrayList<>();
            for (int ligne = 0; ligne < taille; ligne++) {
                unit.add(grille.getCellule(ligne, i));
            }
            if (detect(grille, unit)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int load(Grille grille, int nbAides) {
        refreshTexts();
        this.aideVisuel = new AideVisuel();
        this.aideTextuel = new AideTextuel(this.titre, this.explication);

        /*
         * Debug
         * System.out.println("groupe : ");
         * for (Cellule c : this.groupe){
         * System.out.println("\tline : " + c.getLigne() + " column : " +
         * c.getColonne());
         * }
         * System.out.println("impact : ");
         * for (Cellule c : this.impact){
         * System.out.println("\tline : " + c.getLigne() + " column : " +
         * c.getColonne());
         * }
         * System.out.println("unionCandidats : ");
         * for (Integer i : this.unionCandidats){
         * System.out.print(i + ", ");
         * }
         * System.out.println("\n\n\n");
         */

        switch (this.nbUtilisation) {
            case 0:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);
                break;

            case 1:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);
                break;

            default:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);

                for (Cellule cellule : this.groupe) {
                    List<Integer> candidats = cellule.getListeCandidat();
                    for (int numero : this.unionCandidats) {
                        if (!candidats.contains(numero)) {
                            grille.ajouterCandidat(cellule.getLigne(), cellule.getColonne(), numero);
                            this.aideVisuel.add(new EffetVisuel(TypeEffect.CANDIDAT_POSITIF, cellule.getLigne(),
                                    cellule.getColonne(), String.valueOf(numero)));
                        }
                    }
                }

                for (Cellule cellule : this.impact) {
                    List<Integer> candidats = cellule.getListeCandidat();
                    for (int numero : this.unionCandidats) {
                        if (candidats.contains(numero)) {
                            this.aideVisuel.add(new EffetVisuel(TypeEffect.CANDIDAT_NEGATIF, cellule.getLigne(),
                                    cellule.getColonne(), String.valueOf(numero)));
                        }
                    }
                }
        }

        this.nbUtilisation++;
        return this.getCost(nbAides);
    }
}
