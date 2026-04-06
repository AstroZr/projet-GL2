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

/**
 * Technique "Naked N" :
 * permet d'identifier un groupe de N cellules contenant exactement N candidats différents.
 * <p>
 * Ces candidats ne peuvent apparaître que dans ces cellules, ce qui permet
 * de les éliminer des autres cellules de la zone.
 * </p>
 */
public class NakedN extends AideAbstract {

    /** Taille du subset recherché (ex : 2 = paire, 3 = triplet) */
    private final int n;

    /** Groupe de cellules formant le Naked N */
    private List<Cellule> groupe = new ArrayList<>();

    /** Cellules impactées par l'élimination des candidats */
    private List<Cellule> impact = new ArrayList<>();

    /** Union des candidats du groupe */
    private Set<Integer> unionCandidats = new HashSet<>();

    /**
     * Constructeur de l'aide NakedN.
     *
     * @param id identifiant de l'aide
     * @param n taille du groupe recherché
     */
    public NakedN(int id, int n) {
        super(id);
        this.n = n;
        refreshTexts();
    }

    /**
     * Met à jour les textes en fonction de la langue courante.
     */
    @Override
    public void refreshTexts() {
        this.titre = LangManager.get("aide.nakedn.titre") + String.valueOf(n);
        this.description = LangManager.get("aide.difficiles") + LangManager.get("aide.nakedn.description");
        this.explication = LangManager.get("aide.nakedn.explication");
    }

    /**
     * Indique si l'aide a été utilisée trop de fois.
     *
     * @return true si le nombre d'utilisations est supérieur ou égal à 3
     */
    @Override
    public boolean isOverUsed() {
        return this.nbUtilisation >= 3;
    }

    /**
     * Récupère les candidats possibles pour une cellule en fonction
     * de sa ligne, de sa colonne et de sa zone.
     *
     * @param cellule la cellule
     * @param grille la grille
     * @return ensemble des candidats possibles
     */
    private Set<Integer> getCandidats(Cellule cellule, Grille grille) {
        int taille = grille.getTaille();
        Set<Integer> dejaPresents = new HashSet<>();

        // Valeurs présentes dans la colonne
        for (int colonne = 0; colonne < taille; colonne++) {
            Cellule c = grille.getCellule(cellule.getLigne(), colonne);
            if (!c.estVide()) {
                dejaPresents.add(c.getValeur());
            }
        }

        // Valeurs présentes dans la ligne
        for (int ligne = 0; ligne < taille; ligne++) {
            Cellule c = grille.getCellule(ligne, cellule.getColonne());
            if (!c.estVide()) {
                dejaPresents.add(c.getValeur());
            }
        }

        // Candidats possibles
        Set<Integer> candidats = new HashSet<>();
        for (int v = 1; v <= taille; v++)
            if (!dejaPresents.contains(v))
                candidats.add(v);

        // Filtrage par zone
        ZoneCalcul zone = cellule.getZoneCalcul();
        if (zone != null)
            candidats.retainAll(getValeursPossiblesPourCellule(zone, cellule, taille));

        return candidats;
    }

    /**
     * Vérifie si une combinaison est valide pour une cellule donnée.
     *
     * @param zone la zone de calcul
     * @param cellule la cellule concernée
     * @param taille taille de la grille
     * @return ensemble des valeurs possibles
     */
    private Set<Integer> getValeursPossiblesPourCellule(ZoneCalcul zone, Cellule cellule, int taille) {
        int index = zone.getListeCellules().indexOf(cellule);
        Set<Integer> possibles = new HashSet<>();

        for (List<Integer> combo : zone.trouverCombinaisons(taille))
            if (index < combo.size())
                possibles.add(combo.get(index));

        return possibles;
    }

    /**
     * Génère toutes les combinaisons possibles d'une liste de cellules.
     *
     * @param cellules liste des cellules
     * @param taille taille des combinaisons
     * @return liste des combinaisons
     */
    private List<List<Cellule>> combinaisons(List<Cellule> cellules, int taille) {
        List<List<Cellule>> result = new ArrayList<>();
        combiner(cellules, taille, 0, new ArrayList<>(), result);
        return result;
    }

    /**
     * Fonction récursive de génération des combinaisons.
     */
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

    /**
     * Détecte un groupe Naked N dans une unité (ligne ou colonne).
     *
     * @param grille la grille
     * @param unit liste de cellules de la ligne ou colonne
     * @return true si un groupe est trouvé
     */
    private boolean detect(Grille grille, List<Cellule> unit) {

        // Association cellule -> candidats
        Map<Cellule, Set<Integer>> candidatsMap = new LinkedHashMap<>();

        for (Cellule c : unit)
            if (c.estVide())
                candidatsMap.put(c, getCandidats(c, grille));

        List<Cellule> vides = new ArrayList<>(candidatsMap.keySet());

        if (vides.size() <= n)
            return false;

        // Test des combinaisons de taille n
        for (List<Cellule> groupe : combinaisons(vides, n)) {

            Set<Integer> unionCandidats = new HashSet<>();
            for (Cellule c : groupe)
                unionCandidats.addAll(candidatsMap.get(c));

            // Condition Naked N
            if (unionCandidats.size() == n) {

                List<Cellule> impact = new ArrayList<>();

                // Recherche des cellules impactées
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

                    if (needed) {
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

    /**
     * Recherche un Naked N dans toutes les lignes et colonnes.
     *
     * @param grille la grille
     * @return true si une configuration est trouvée
     */
    @Override
    public boolean check(Grille grille) {
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

    /**
     * Applique l'aide sur la grille.
     *
     * @param grille la grille
     * @param nbAides nombre d'aides
     * @return le coût de l'aide
     */
    @Override
    public int load(Grille grille, int nbAides) {
        refreshTexts();

        this.aideVisuel = new AideVisuel();
        this.aideTextuel = new AideTextuel(this.titre, this.explication);

        switch (this.nbUtilisation) {

            case 0:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);
                break;

            case 1:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);
                break;

            default:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);

                // Ajout des candidats positifs dans le groupe
                for (Cellule cellule : this.groupe) {
                    List<Integer> candidats = cellule.getListeCandidat();

                    for (int numero : this.unionCandidats) {
                        if (!candidats.contains(numero)) {
                            grille.ajouterCandidat(cellule.getLigne(), cellule.getColonne(), numero);

                            this.aideVisuel.add(
                                    new EffetVisuel(TypeEffect.CANDIDAT_POSITIF,
                                            cellule.getLigne(),
                                            cellule.getColonne(),
                                            String.valueOf(numero))
                            );
                        }
                    }
                }

                // Suppression des candidats dans les cellules impactées
                for (Cellule cellule : this.impact) {
                    List<Integer> candidats = cellule.getListeCandidat();

                    for (int numero : this.unionCandidats) {
                        if (candidats.contains(numero)) {
                            this.aideVisuel.add(
                                    new EffetVisuel(TypeEffect.CANDIDAT_NEGATIF,
                                            cellule.getLigne(),
                                            cellule.getColonne(),
                                            String.valueOf(numero))
                            );
                        }
                    }
                }
        }

        this.nbUtilisation++;
        return this.getCost(nbAides);
    }
}