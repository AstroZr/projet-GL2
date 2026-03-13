package Groupe6.aide;

import java.util.List;
import java.util.ArrayList;

public class AideBuilder{
    private List<Aide> aides;

    AideBuilder(int nbAides){
        this.aides = new ArrayList<>();
        for (int i = 0; i < nbAides; i++){
            this.aides.add(null);
        }
    }
}