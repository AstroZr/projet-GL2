package Groupe6.aide;

import Groupe6.aide.techniques.*;
import Groupe6.models.Grille;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class AideManager{
    private List<Aide> aides;

    public AideManager(){
        this.aides = new ArrayList<>();
        this.aides.add(new UniqueBlock());
    }

    public Aide call(Grille grille){
        Iterator<Aide> iteratorAide = this.aides.iterator();
        Aide aide = this.aides.get(0);
        while (iteratorAide.hasNext() && !aide.check(grille)){
            aide = iteratorAide.next();
        }
        if (aide.check(grille)){
            aide.load(grille);
            return aide;
        }
        return null;
    }
}
