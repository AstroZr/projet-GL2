package Groupe6.aide;

import Groupe6.aide.techniques.*;
import Groupe6.models.Grille;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class AideManager{
    private List<Aide> aides;
    private Aide aide;
    private int cost;

    public AideManager(){
        this.aides = new ArrayList<>();
        this.aides.add(new SingletonTechnique());
        this.aides.add(new ResteTechnique());
    }

    public boolean call(Grille grille){
        Iterator<Aide> iteratorAide = this.aides.iterator();
        Aide aide = this.aides.get(0);
        while (iteratorAide.hasNext() && !aide.check(grille)){
            aide = iteratorAide.next();
        }

        if (aide.check(grille)){
            this.cost = aide.load(grille, this.aides.size());
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
        return this.aides.size();
    }

    public int[] saveVector(){
        int[] vector = new int[this.aides.size()];
        for (int i = 0; i < this.aides.size(); i++){
            vector[i] = this.aides.get(i).getNbUtilisation();
        }
        return vector;
    }
}