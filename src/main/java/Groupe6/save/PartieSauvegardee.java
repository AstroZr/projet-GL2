package Groupe6.save;

import java.util.ArrayList;
import java.util.List;

import Groupe6.models.Cellule;

public class PartieSauvegardee {

    private Cellule[][] matriceCellules;
    private List<int[]> historique;
    private long tempsEcoule;

    public PartieSauvegardee() {
        this.historique = new ArrayList<>();
    }

    public PartieSauvegardee(Cellule[][] matriceCellules, List<int[]> historique, long tempsEcoule) {
        this.matriceCellules = matriceCellules;
        this.historique = historique;
        this.tempsEcoule = tempsEcoule;
    }

    public Cellule[][] getMatriceCellules() {
        return matriceCellules;
    }

    public void setMatriceCellules(Cellule[][] matriceCellules) {
        this.matriceCellules = matriceCellules;
    }

    public List<int[]> getHistorique() {
        return historique;
    }

    public void setHistorique(List<int[]> historique) {
        this.historique = historique;
    }

    public long getTempsEcoule() {
        return tempsEcoule;
    }

    public void setTempsEcoule(long tempsEcoule) {
        this.tempsEcoule = tempsEcoule;
    }
}