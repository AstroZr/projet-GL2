package Groupe6.aide.techniques;

import Groupe6.aide.AideAbstract;
import Groupe6.aide.AideTextuel;
import Groupe6.aide.AideVisuel;
import Groupe6.aide.EffetVisuel;
import Groupe6.aide.TypeEffect;
import Groupe6.models.Grille;
import Groupe6.models.Cellule;
import Groupe6.utilz.LangManager;

import java.util.List;
import java.util.Iterator;

public class Singleton extends AideAbstract {
    private int ligneCellule = -1;
    private int colonneCellule = -1;

    public Singleton() {
        super(0); // 0 is the id du singleton
        refreshTexts();
    }

    private void refreshTexts() {
        this.titre = LangManager.get("aide.singleton.titre");
        this.description = LangManager.get("aide.singleton.description");
        this.explication = LangManager.get("aide.singleton.explication");
    }

    @Override
    public int getMaxUtilisation() {
        return 3;
    }

    @Override
    public boolean isOverUsed() {
        return (this.nbUtilisation >= 3);
    }

    @Override
    public boolean check(Grille grille) {
        List<Cellule> cellules = grille.getListeCellules();

        Iterator<Cellule> iterator = cellules.iterator();
        Cellule cellule = cellules.get(0);
        while (iterator.hasNext()
                && !(cellule.getZoneCalcul().getListeCellules().size() == 1 && cellule.getValeur() == 0)) {
            cellule = iterator.next();
        }

        if (cellule.getZoneCalcul().getListeCellules().size() == 1 && cellule.getValeur() == 0) {
            this.ligneCellule = cellule.getLigne();
            this.colonneCellule = cellule.getColonne();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int load(Grille grille, int nbAides) {
        refreshTexts();
        if (ligneCellule == -1 || ligneCellule == -1) {
            if (!this.check(grille)) {
                return 0;
            }
        }

        this.aideVisuel = new AideVisuel();

        switch (this.nbUtilisation) {
            case 0:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);
                this.nbUtilisation++;
                return this.getCost(nbAides);

            case 1:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);
                this.nbUtilisation++;
                return this.getCost(nbAides);

            default:
                this.aideTextuel = new AideTextuel(this.titre, this.explication);
                this.aideVisuel.add(new EffetVisuel(TypeEffect.CASE_NEGATIVE, this.ligneCellule, this.colonneCellule,
                        new String()));
                this.nbUtilisation++;
                return this.getCost(nbAides);
        }
    }
}