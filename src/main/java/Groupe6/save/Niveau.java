package Groupe6.save;

import java.util.List;

import Groupe6.models.Cellule;
import Groupe6.models.ZoneCalcul;

/**
 * Modèle de données pour un niveau/puzzle CalcuDoku.
 * * Resp onsabilités:
 * - Mémoriser l'ennoncé du puzzle (zones, cellules poussière, solution)
 * - Être chargé à partir de JSON (saveGame/niveaux/facile1.json, etc.)
 * - Être stocké en mémoire avec référence partagée avec Grille
 * * Contenu:
 * - id: identifiant unique ("facile1", "moyen2", "difficile3", etc.)
 * - taille: dimension N de la grille N×N (généralement 4, 5 ou 6)
 * - matriceCellules: grille avec cellules poussière (values = 0 ou
 * pré-remplies)
 * - listeZones: contraintes mathématiques (zones de calcul)
 * - matriceCorrection: grille solution (pour vérification)
 * * Sérialisation JSON:
 * - Chargé par SaveManager.chargerNiveau(idNiveau)
 * - Contient toute l'information de base du puzzle
 */
public class Niveau {

    // ====== IDÉNTIFICATION ======
    /** Identifiant unique du niveau ("facile1", "moyen2", etc.). */
    private String id;

    // ====== DIMENSIONS ======
    /** Dimension N de la grille N×N. */
    private int taille;

    // ====== ÉNONCÉ ======
    /** Grille avec cellules poussière (puzzle à résoudre). */
    private Cellule[][] matriceCellules;

    /** Zones de calcul (contraintes mathématiques). */
    private List<ZoneCalcul> listeZones;

    // ====== SOLUTION ======
    /** Grille solution (pour vérification/correction). */
    private int[][] matriceCorrection;

    /** Grille pré-remplie (pour aide/ajouter automatiquement des chiffres). */
    private int[][] matricePreRemplie;

    /**
     * Constructeur par défaut nécessaire pour la désérialisation GSON.
     */
    public Niveau() {
    }

    /**
     * Constructeur complet pour initialiser un niveau.
     * * @param id                Identifiant unique du niveau.
     * @param taille            Taille de la grille (N).
     * @param matriceCellules   Matrice des cellules initiales.
     * @param listeZones        Liste des zones de calcul.
     * @param matriceCorrection Matrice contenant la solution attendue.
     * @param matricePreRemplie Matrice contenant les chiffres pré-remplis pour l'aide.
     */
    public Niveau(String id, int taille, Cellule[][] matriceCellules, List<ZoneCalcul> listeZones,
            int[][] matriceCorrection, int[][] matricePreRemplie) {
        this.id = id;
        this.taille = taille;
        this.matriceCellules = matriceCellules;
        this.listeZones = listeZones;
        this.matriceCorrection = matriceCorrection;
        this.matricePreRemplie = matricePreRemplie;
    }

    /**
     * @return L'identifiant du niveau.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id Le nouvel identifiant du niveau.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return La taille de la grille.
     */
    public int getTaille() {
        return taille;
    }

    /**
     * @param taille La nouvelle taille de la grille.
     */
    public void setTaille(int taille) {
        this.taille = taille;
    }

    /**
     * @return La matrice des cellules du puzzle.
     */
    public Cellule[][] getMatriceCellules() {
        return matriceCellules;
    }

    /**
     * @param matriceCellules La nouvelle matrice de cellules.
     */
    public void setMatriceCellules(Cellule[][] matriceCellules) {
        this.matriceCellules = matriceCellules;
    }

    /**
     * @return La liste des zones de calcul.
     */
    public List<ZoneCalcul> getListeZones() {
        return listeZones;
    }

    /**
     * @param listeZones La nouvelle liste de zones de calcul.
     */
    public void setListeZones(List<ZoneCalcul> listeZones) {
        this.listeZones = listeZones;
    }

    /**
     * @return La matrice de correction.
     */
    public int[][] getMatriceCorrection() {
        return matriceCorrection;
    }

    /**
     * @return La matrice pré-remplie pour l'aide.
     */
    public int[][] getMatricePreRemplie() {
        return matricePreRemplie;
    }
}