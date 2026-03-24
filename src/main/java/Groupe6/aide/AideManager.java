package Groupe6.aide;

import Groupe6.aide.techniques.*;
import Groupe6.models.Grille;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

public class AideManager{
    private static List<Aide> aides;
    private Aide aide;
    private int cost;

    public AideManager(){
        AideManager.aides = new ArrayList<>();
        AideManager.aides.add(new Singleton());
        AideManager.aides.add(new Reste());
    }

    public boolean call(Grille grille){
        Iterator<Aide> iteratorAide = AideManager.aides.iterator();
        Aide aide = AideManager.aides.get(0);
        while (iteratorAide.hasNext() && !aide.check(grille)){
            aide = iteratorAide.next();
        }

        if (aide.check(grille)){
            this.cost = aide.load(grille, AideManager.aides.size());
            this.aide = aide;
            return true;
        }

        return false;
    }

    public Aide getAide(){
        return this.aide;
    }

    public int getCost(){
        return this.cost;
    }

    public int getNbAides(){
        return AideManager.aides.size();
    }

    public static Map<Integer, Integer> getNBUtilisations(){
        Map<Integer, Integer> dicoNbUtilisation = new HashMap<>();
        for (Aide aide : AideManager.aides){
            dicoNbUtilisation.put(aide.getId(), aide.getNbUtilisation());
        }
        return dicoNbUtilisation;
    }

    public static void setNBUtilisations(Map<Integer, Integer> dicoNbUtilisation){
        if (dicoNbUtilisation == null){
            setNBUtilisationsZero();
            return;
        }

        for (Aide aide : AideManager.aides){
            aide.setNbUtilisation(dicoNbUtilisation.getOrDefault(aide.getId(), 0));
        }
    }

    public static void setNBUtilisationsZero(){
        for (Aide aide : AideManager.aides){
            aide.setNbUtilisation(0);
        }
    }   

    public int[] saveVector(){
        int[] vector = new int[AideManager.aides.size()];
        for (int i = 0; i < AideManager.aides.size(); i++){
            vector[i] = AideManager.aides.get(i).getNbUtilisation();
        }
        return vector;
    }

    public void restoreVector(int[] vector){
        if (vector == null){
            return;
        }

        int n = Math.min(vector.length, this.aides.size());
        for (int i = 0; i < n; i++){
            Aide aide = this.aides.get(i);
            if (aide instanceof AideAbstract){
                ((AideAbstract) aide).setNbUtilisation(vector[i]);
            }
        }

        this.aide = null;
        this.cost = 0;
    }
}