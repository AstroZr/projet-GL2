package Groupe6.aide.techniques;

import Groupe6.aide.AideAbstract;
import Groupe6.aide.AideTextuel;
import Groupe6.aide.AideVisuel;
import Groupe6.aide.EffetVisuel;
import Groupe6.aide.TypeEffect;
import Groupe6.models.Grille;
import Groupe6.models.Cellule;

import java.util.List;
import java.util.Iterator;

public class Singleton extends AideAbstract{
    private int ligneCellule = -1;
    private int colonneCellule = -1;

    public Singleton(){
        super(0); // 0 is the id du singleton
        this.titre = "Technique du singleton";
        this.description = "Lorsqu'il y a une zone de calcul qui ne possède qu'une seule case, il n'y a qu'une seule possibilité !";
    }

    @Override
    public boolean check(Grille grille){
        if (this.nbUtilisation >= 3){
            return false;
        }
        
        List<Cellule> cellules = grille.getListeCellules();

        Iterator<Cellule> iterator = cellules.iterator();
        Cellule cellule = cellules.get(0);
        while (iterator.hasNext() && !(cellule.getZoneCalcul().getListeCellules().size() == 1 && cellule.getValeur() == 0)){
            cellule = iterator.next();
        }

        if (cellule.getZoneCalcul().getListeCellules().size() == 1 && cellule.getValeur() == 0){
            this.ligneCellule = cellule.getLigne();
            this.colonneCellule = cellule.getColonne();
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public int load(Grille grille, int nbAides) {
        if (ligneCellule == -1 || ligneCellule == -1){
            if (!this.check(grille)){
                return 0;
            }
        }

        this.aideVisuel = new AideVisuel();

        switch (this.nbUtilisation){
            case 0:
                this.aideTextuel = new AideTextuel(this.titre, this.description);
                this.nbUtilisation++;
                return this.getCost(nbAides);
            
            case 1:
                this.aideTextuel = new AideTextuel(this.titre, this.description + "\n Le chiffre à inscrire est : " + grille.getCellule(this.ligneCellule, this.colonneCellule).getZoneCalcul().getValeurCible() + "!");
                this.nbUtilisation++;
                return this.getCost(nbAides);
            
            default:
                this.aideTextuel = new AideTextuel(this.titre, this.description + "\n Le chiffre à inscrire est : " + grille.getCellule(this.ligneCellule, this.colonneCellule).getZoneCalcul().getValeurCible() + "!");
                this.aideVisuel.add(new EffetVisuel(TypeEffect.CASE_NEGATIVE, this.ligneCellule, this.colonneCellule, new String()));
                return this.getCost(nbAides);
        }
    }
}