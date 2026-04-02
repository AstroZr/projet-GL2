package Groupe6.save;

import java.util.List;

import Groupe6.models.Cellule;
import Groupe6.models.ZoneCalcul;

/**
 * Modèle de données pour un niveau/puzzle CalcuDoku.
 * 
 * Resp onsabilités:
 * - Mémoriser l'ennoncé du puzzle (zones, cellules poussière, solution)
 * - Êatre chargé à partir de JSON (saveGame/niveaux/facile1.json, etc.)
 * - Êatre stocké en mémoire avec référence partagée avec Grille
 * 
 * Contenu:
 * - id: identifiant unique ("facile1", "moyen2", "difficile3", etc.)
 * - taille: dimension N de la grille N×N (généralement 4, 5 ou 6)
 * - matriceCellules: grille avec cellules poussière (values = 0 ou
 * pré-remplies)
 * - listeZones: contraintes mathématiques (zones de calcul)
 * - matriceCorrection: grille solution (pour vérification)
 * 
 * Sérialisation JSON:
 * - Chargé par SaveManager.chargerNiveau(idNiveau)
 * - Contient toute l'information de base du puzzle
 */
public class Niveau {

    // ====== IDÉNTIFICATION ======
    private String id; // Identifiant unique du niveau ("facile1", "moyen2", etc.)

    // ====== DIMENSIONS ======
    private int taille; // Dimension N de la grille N×N

    // ====== ÉNONCÉ ======
    private Cellule[][] matriceCellules; // Grille avec cellules poussière (puzzle à résoudre)
    private List<ZoneCalcul> listeZones; // Zones de calcul (contraintes mathématiques)

    // ====== SOLUTION ======
    private int[][] matriceCorrection; // Grille solution (pour vérification/correction)
    private int[][] matricePreRemplie; // Grille pré-remplie (pour aide/ajouter automatiquement des chiffres)

    public Niveau() {
    }

    public Niveau(String id, int taille, Cellule[][] matriceCellules, List<ZoneCalcul> listeZones,
            int[][] matriceCorrection, int[][] matricePreRemplie) {
        this.id = id;
        this.taille = taille;
        this.matriceCellules = matriceCellules;
        this.listeZones = listeZones;
        this.matriceCorrection = matriceCorrection;
        this.matricePreRemplie = matricePreRemplie;
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

    public int[][] getMatricePreRemplie() {
        return matricePreRemplie;
    }
}