package Groupe6.save;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import Groupe6.aide.AideManager;
import Groupe6.models.Cellule;

public class PartieSauvegardee {
    private AideManager aideManager;
    private Cellule[][] matriceCellules;
    private List<int[]> historique;
    private long tempsEcoule;
    private Map<Integer, Integer> nbAidesUtilisees;

    public PartieSauvegardee() {
        this.historique = new ArrayList<>();
        this.nbAidesUtilisees = new HashMap<>();
        this.aideManager = AideManager.getInstance();
    }

    public PartieSauvegardee(Cellule[][] matriceCellules, List<int[]> historique, long tempsEcoule) {
        this.matriceCellules = matriceCellules;
        this.historique = historique;
        this.tempsEcoule = tempsEcoule;
        this.nbAidesUtilisees = this.aideManager.getNBUtilisations();
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

    public Map<Integer, Integer> getNbAidesUtilisees() {
        return nbAidesUtilisees;
    }
}