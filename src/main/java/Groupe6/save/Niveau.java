package Groupe6.save;

import java.util.List;

import Groupe6.models.Cellule;
import Groupe6.models.ZoneCalcul;

public class Niveau {

    private String id;
    private int taille;
    private Cellule[][] matriceCellules;
    private List<ZoneCalcul> listeZones;
    private int[][] matriceCorrection;

    public Niveau() {
    }

    public Niveau(String id, int taille, Cellule[][] matriceCellules, List<ZoneCalcul> listeZones, int[][] matriceCorrection) {
        this.id = id;
        this.taille = taille;
        this.matriceCellules = matriceCellules;
        this.listeZones = listeZones;
        this.matriceCorrection = matriceCorrection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTaille() {
        return taille;
    }

    public void setTaille(int taille) {
        this.taille = taille;
    }

    public Cellule[][] getMatriceCellules() {
        return matriceCellules;
    }

    public void setMatriceCellules(Cellule[][] matriceCellules) {
        this.matriceCellules = matriceCellules;
    }

    public List<ZoneCalcul> getListeZones() {
        return listeZones;
    }

    public void setListeZones(List<ZoneCalcul> listeZones) {
        this.listeZones = listeZones;
    }

    public int[][] getMatriceCorrection() {
        return matriceCorrection;
    }

}