package Groupe6.aide;

import Groupe6.aide.techniques.Reste;
import Groupe6.aide.techniques.Singleton;
import Groupe6.aide.techniques.UniqueCacheeLigne;
import Groupe6.aide.techniques.UniqueCacheeColonne;
import Groupe6.aide.techniques.BlocageUnique;
import Groupe6.models.Grille;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

public class AideManager {
    private static AideManager instance = null;
    private List<Aide> aides;
    private Aide aide;
    private int cost;

    private AideManager() {
        this.aides = new ArrayList<>();
        this.aides.add(new Singleton());
        this.aides.add(new Reste());
        this.aides.add(new UniqueCacheeLigne());
        this.aides.add(new UniqueCacheeColonne());
        this.aides.add(new BlocageUnique());
    }

    public static AideManager getInstance() {
        if (instance == null) {
            instance = new AideManager();
        }
        return instance;
    }

    public boolean call(Grille grille) {
        Iterator<Aide> iteratorAide = this.aides.iterator();
        Aide aide = this.aides.get(0);
        while (iteratorAide.hasNext() && !aide.check(grille)) {
            aide = iteratorAide.next();
        }

        if (aide.check(grille)) {
            this.cost = aide.load(grille, this.aides.size());
            this.aide = aide;
            return true;
        }
        return false;
    }

    public Aide getAide() {
        return this.aide;
    }

    public int getCost() {
        return this.cost;
    }

    public int getNbAides() {
        return this.aides.size();
    }

    public List<Aide> getAides() {
        return java.util.Collections.unmodifiableList(this.aides);
    }

    public Map<Integer, Integer> getNBUtilisations() {
        Map<Integer, Integer> dicoNbUtilisation = new HashMap<>();
        for (Aide aide : this.aides) {
            dicoNbUtilisation.put(aide.getId(), aide.getNbUtilisation());
        }
        return dicoNbUtilisation;
    }

    public void setNBUtilisations(Map<Integer, Integer> dicoNbUtilisation) {
        if (dicoNbUtilisation == null)
            return;
        for (Aide aide : this.aides) {
            Integer n = dicoNbUtilisation.get(aide.getId());
            aide.setNbUtilisation(n != null ? n : 0);
        }
    }

    public void setNBUtilisationsZero() {
        for (Aide aide : this.aides) {
            aide.setNbUtilisation(0);
        }
    }

    public int[] saveVector() {
        int[] vector = new int[this.aides.size()];
        for (int i = 0; i < this.aides.size(); i++) {
            vector[i] = this.aides.get(i).getNbUtilisation();
        }
        return vector;
    }

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