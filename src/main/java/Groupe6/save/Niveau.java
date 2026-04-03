package Groupe6.save;

import java.util.List;

import Groupe6.models.Cellule;
import Groupe6.models.ZoneCalcul;

/**
 * Modèle de données pour un niveau/puzzle CalcuDoku.
 * 
 * Resp onsabilités:
 * - Mémoriser l'ennoncé du puzzle (zones, cellules poussière, solution)
 * - Être chargé à partir de JSON (saveGame/niveaux/facile1.json, etc.)
 * - Être stocké en mémoire avec référence partagée avec Grille
 * 
 * Contenu:
 * - id: identifiant unique ("facile1", "moyen2", "difficile3", etc.)
 * - taille: dimension N de la grille N×N (généralement 4, 5 ou 6)
 * - matriceCellules: grille avec cellules poussière (values = 0 ou pré-remplies)
 * - listeZones: contraintes mathématiques (zones de calcul)
 * - matriceCorrection: grille solution (pour vérification)
 * 
 * Sérialisation JSON:
 * - Chargé par SaveManager.chargerNiveau(idNiveau)
 * - Contient toute l'information de base du puzzle
 */
public class Niveau {

    // ====== IDENTIFICATION ======
    
    /** Identifiant unique du niveau (ex: "facile1", "moyen2", "difficile3"). */
    private String id;
    
    // ====== DIMENSIONS ======
    
    /** Dimension N de la grille N×N. */
    private int taille;
    
    // ====== ÉNONCÉ ======
    
    /** Grille de base contenant les cellules de l'énoncé (souvent de valeur 0 pour commencer). */
    private Cellule[][] matriceCellules;
    
    /** Liste des zones de calcul qui définissent les contraintes mathématiques de la grille. */
    private List<ZoneCalcul> listeZones;
    
    // ====== SOLUTION ======
    
    /** Grille contenant les valeurs correctes (solution) pour permettre la vérification. */
    private int[][] matriceCorrection;

    /**
     * Constructeur par défaut.
     * Indispensable pour permettre à GSON de désérialiser l'objet depuis le fichier JSON.
     */
    public Niveau() {
    }

    /**
     * Constructeur paramétré d'un niveau.
     * * @param id                L'identifiant unique du niveau.
     * @param taille            La dimension N de la grille (N×N).
     * @param matriceCellules   La matrice initiale des cellules.
     * @param listeZones        La liste des zones de calcul associées à la grille.
     * @param matriceCorrection La matrice d'entiers représentant la solution correcte.
     */
    public Niveau(String id, int taille, Cellule[][] matriceCellules, List<ZoneCalcul> listeZones, int[][] matriceCorrection) {
        this.id = id;
        this.taille = taille;
        this.matriceCellules = matriceCellules;
        this.listeZones = listeZones;
        this.matriceCorrection = matriceCorrection;
    }

    /**
     * Obtient l'identifiant du niveau.
     * @return L'identifiant unique (ex: "facile1").
     */
    public String getId() {
        return id;
    }

    /**
     * Définit l'identifiant du niveau.
     * @param id Le nouvel identifiant.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtient la dimension de la grille.
     * @return La taille N (pour une grille N×N).
     */
    public int getTaille() {
        return taille;
    }

    /**
     * Définit la dimension de la grille.
     * @param taille La nouvelle taille N.
     */
    public void setTaille(int taille) {
        this.taille = taille;
    }

    /**
     * Obtient la matrice des cellules de l'énoncé.
     * @return Un tableau 2D de {@link Cellule}.
     */
    public Cellule[][] getMatriceCellules() {
        return matriceCellules;
    }

    /**
     * Définit la matrice des cellules de l'énoncé.
     * @param matriceCellules Le nouveau tableau 2D de {@link Cellule}.
     */
    public void setMatriceCellules(Cellule[][] matriceCellules) {
        this.matriceCellules = matriceCellules;
    }

    /**
     * Obtient la liste des zones de calcul de la grille.
     * @return Une liste d'objets {@link ZoneCalcul}.
     */
    public List<ZoneCalcul> getListeZones() {
        return listeZones;
    }

    /**
     * Définit la liste des zones de calcul.
     * @param listeZones La nouvelle liste de contraintes mathématiques.
     */
    public void setListeZones(List<ZoneCalcul> listeZones) {
        this.listeZones = listeZones;
    }

    /**
     * Obtient la matrice de correction (solution).
     * @return Un tableau 2D d'entiers contenant les valeurs correctes.
     */
    public int[][] getMatriceCorrection() {
        return matriceCorrection;
    }

}