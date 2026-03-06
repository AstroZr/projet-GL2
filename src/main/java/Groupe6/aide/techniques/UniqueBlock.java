package Groupe6.aide.techniques;

import Groupe6.aide.Aide;
import Groupe6.models.Grille;
import Groupe6.models.ZoneCalcul;
import Groupe6.models.Cellule;

import java.util.List;
import java.util.Iterator;

public class UniqueBlock extends Aide{


    public UniqueBlock(){
        super(0); // 0 is the id of the UniqueBlock technique
    }

    public boolean check(Grille grille){
        List<Cellule> cellules = grille.getListeCellules();

        Iterator<Cellule> iterator = cellules.iterator();
        Cellule cellule = cellules.get(0);
        while (iterator.hasNext() && !(cellule.getZoneCalcul().getListeCellules().size() == 1 && cellule.getValeur() == 0)){
            cellule = iterator.next();
        }

        return (cellule.getZoneCalcul().getListeCellules().size() == 1 && cellule.getValeur() == 0) ? true : false;
    }
}