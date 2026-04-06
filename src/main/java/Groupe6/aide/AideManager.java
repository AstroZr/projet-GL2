package Groupe6.aide;

import Groupe6.aide.techniques.Reste;
import Groupe6.aide.techniques.Singleton;
import Groupe6.aide.techniques.UniqueCacheeLigne;
import Groupe6.aide.techniques.UniqueCacheeColonne;
import Groupe6.aide.techniques.BlocageUnique;
import Groupe6.aide.techniques.CandidatUnique;
import Groupe6.aide.techniques.NakedN;
import Groupe6.models.Grille;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Gestionnaire des aides disponibles dans le jeu.
 * <p>
 * Cette classe suit le pattern Singleton et centralise :
 * <ul>
 *     <li>la liste des aides disponibles</li>
 *     <li>la sélection et l'application d'une aide</li>
 *     <li>la gestion des utilisations et des coûts</li>
 * </ul>
 * </p>
 */
public class AideManager {

    /** Instance unique du gestionnaire */
    private static AideManager instance = null;

    /** Liste des aides disponibles */
    private List<Aide> aides;

    /** Dernière aide utilisée */
    private Aide aide;

    /** Coût de la dernière aide utilisée */
    private int cost;

    /**
     * Constructeur privé (pattern Singleton).
     * <p>
     * Initialise la liste des aides disponibles.
     * </p>
     */
    private AideManager() {
        this.aides = new ArrayList<>();
        this.aides.add(new Singleton());
        this.aides.add(new Reste());
        this.aides.add(new UniqueCacheeLigne());
        this.aides.add(new UniqueCacheeColonne());
        this.aides.add(new BlocageUnique());
        this.aides.add(new CandidatUnique());
        this.aides.add(new NakedN(8, 2));
        this.aides.add(new NakedN(9, 3));
    }

    /**
     * Retourne l'instance unique du gestionnaire.
     *
     * @return l'instance de {@code AideManager}
     */
    public static AideManager getInstance() {
        if (instance == null) {
            instance = new AideManager();
        }
        return instance;
    }

    /**
     * Recherche et applique la première aide valide sur la grille.
     * <p>
     * Une aide est appliquée si :
     * <ul>
     *     <li>{@link Aide#check(Grille)} retourne {@code true}</li>
     *     <li>elle n'a pas dépassé son nombre maximal d'utilisations</li>
     * </ul>
     * </p>
     * <p>
     * Si toutes les aides valides sont surutilisées, les compteurs sont réinitialisés
     * puis la recherche est relancée.
     * </p>
     *
     * @param grille la grille sur laquelle appliquer une aide
     * @return {@code true} si une aide a été appliquée, {@code false} sinon
     */
    public boolean call(Grille grille) {
        for (Aide a : this.aides) {
            if (a.check(grille) && !a.isOverUsed()) {
                this.cost = a.load(grille, this.aides.size());
                this.aide = a;
                return true;
            }
        }

        boolean hasOverusedValidAide = false;
        for (Aide a : this.aides) {
            if (a.check(grille)) {
                hasOverusedValidAide = true;
                break;
            }
        }

        if (hasOverusedValidAide) {
            for (Aide a : this.aides) {
                a.setNbUtilisation(0);
            }
            return call(grille);
        }

        return false;
    }

    /**
     * Retourne la dernière aide utilisée.
     *
     * @return l'aide utilisée, ou {@code null} si aucune
     */
    public Aide getAide() {
        return this.aide;
    }

    /**
     * Retourne le coût de la dernière aide utilisée.
     *
     * @return le coût
     */
    public int getCost() {
        return this.cost;
    }

    /**
     * Retourne le nombre total d'aides disponibles.
     *
     * @return le nombre d'aides
     */
    public int getNbAides() {
        return this.aides.size();
    }

    /**
     * Retourne une vue non modifiable de la liste des aides.
     *
     * @return une liste non modifiable des aides
     */
    public List<Aide> getAides() {
        return java.util.Collections.unmodifiableList(this.aides);
    }

    /**
     * Retourne un dictionnaire associant chaque aide à son nombre d'utilisations.
     *
     * @return une map (id -> nombre d'utilisations)
     */
    public Map<Integer, Integer> getNBUtilisations() {
        Map<Integer, Integer> dicoNbUtilisation = new HashMap<>();
        for (Aide aide : this.aides) {
            dicoNbUtilisation.put(aide.getId(), aide.getNbUtilisation());
        }
        return dicoNbUtilisation;
    }

    /**
     * Définit les nombres d'utilisations à partir d'un dictionnaire.
     *
     * @param dicoNbUtilisation map (id -> nombre d'utilisations)
     */
    public void setNBUtilisations(Map<Integer, Integer> dicoNbUtilisation) {
        if (dicoNbUtilisation == null)
            return;

        for (Aide aide : this.aides) {
            Integer n = dicoNbUtilisation.get(aide.getId());
            aide.setNbUtilisation(n != null ? n : 0);
        }
    }

    /**
     * Réinitialise le nombre d'utilisations de toutes les aides à zéro.
     */
    public void setNBUtilisationsZero() {
        for (Aide aide : this.aides) {
            aide.setNbUtilisation(0);
        }
    }

    /**
     * Sauvegarde les nombres d'utilisations sous forme de vecteur.
     *
     * @return un tableau contenant les utilisations de chaque aide
     */
    public int[] saveVector() {
        int[] vector = new int[this.aides.size()];
        for (int i = 0; i < this.aides.size(); i++) {
            vector[i] = this.aides.get(i).getNbUtilisation();
        }
        return vector;
    }

    /**
     * Restaure les nombres d'utilisations à partir d'un vecteur.
     * <p>
     * Si le vecteur est plus court que la liste des aides,
     * seules les premières aides sont mises à jour.
     * </p>
     *
     * @param vector le vecteur des utilisations
     */
    public void restoreVector(int[] vector) {
        if (vector == null) {
            return;
        }

        int n = Math.min(vector.length, this.aides.size());
        for (int i = 0; i < n; i++) {
            Aide aide = this.aides.get(i);
            if (aide instanceof AideAbstract) {
                ((AideAbstract) aide).setNbUtilisation(vector[i]);
            }
        }

        this.aide = null;
        this.cost = 0;
    }
}