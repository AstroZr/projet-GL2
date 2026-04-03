package Groupe6.save;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import Groupe6.aide.AideManager;
import Groupe6.models.Cellule;

/**
 * Conteneur de données pour une partie sauvegardée.
 * * Respons abilités:
 * - Mémoriser l'état complet d'une partie en cours (grille, progrédition)
 * - Être sérialisée/désérialisée en JSON par SaveManager (GSON)
 * - Support du checkpoint/restore: rechargement d'une partie antérieure
 * * Sauvegardes par niveau:
 * - saveGame/001/facile1.json
 * - saveGame/001/facile2.json
 * - saveGame/002/moyen1.json, etc.
 * * Structure:
 * - matriceCellules: grille N×N avec valeurs actuelles de chaque cellule
 * - historique: list de coups joués pour undo/redo
 * - tempsEcoule: temps total en ms depuis début
 * - nbAidesUtilisees: comptage des hints par type
 * * GSON Serialization:
 * - Pas de constructeur par défaut (présent pour GSON)
 * - Champs publics ou getters/setters pour accés GSON
 */
public class PartieSauvegardee {
    // ====== ÉTAT DE LA GRILLE ======
    
    /** Exclu JSON: contient des interfaces non désérialisables par défaut. */
    private transient AideManager aideManager;       
    
    /** Grille N×N avec valeurs actuelles de chaque cellule. */
    private Cellule[][] matriceCellules;  
    
    // ====== HISTORIQUE & TEMPS ======
    
    /** Enregistrement de tous les coups [ligne, col, ancVal, nouvelleVal, actionType]. */
    private List<int[]> historique;        
    
    /** Temps total écoulé en millisecondes. */
    private long tempsEcoule;              
    
    // ====== AIDS TRACKING ======
    
    /** Comptage des hints utilisés par type. */
    private Map<Integer, Integer> nbAidesUtilisees;  

    /**
     * Constructeur par défaut nécessaire pour la désérialisation par GSON.
     */
    public PartieSauvegardee() {
        this.historique = new ArrayList<>();
        this.nbAidesUtilisees = new HashMap<>();
        this.aideManager = AideManager.getInstance();
    }

    /**
     * Constructeur complet pour instancier une sauvegarde avant écriture JSON.
     * * @param matriceCellules L'état actuel des cellules de la grille.
     * @param historique      L'historique des coups joués.
     * @param tempsEcoule     Le temps de jeu écoulé en millisecondes.
     */
    public PartieSauvegardee(Cellule[][] matriceCellules, List<int[]> historique, long tempsEcoule) {
        this.matriceCellules = matriceCellules;
        this.historique = historique;
        this.tempsEcoule = tempsEcoule;
        this.aideManager = AideManager.getInstance();
        this.nbAidesUtilisees = this.aideManager.getNBUtilisations();
    }

    /**
     * @return L'état actuel de la grille N×N.
     */
    public Cellule[][] getMatriceCellules() {
        return matriceCellules;
    }

    /**
     * @param matriceCellules Le nouvel état de la grille.
     */
    public void setMatriceCellules(Cellule[][] matriceCellules) {
        this.matriceCellules = matriceCellules;
    }

    /**
     * @return L'historique des coups joués.
     */
    public List<int[]> getHistorique() {
        return historique;
    }

    /**
     * @param historique Le nouvel historique des coups.
     */
    public void setHistorique(List<int[]> historique) {
        this.historique = historique;
    }

    /**
     * @return Le temps total écoulé en millisecondes.
     */
    public long getTempsEcoule() {
        return tempsEcoule;
    }

    /**
     * @param tempsEcoule Le nouveau temps total écoulé en millisecondes.
     */
    public void setTempsEcoule(long tempsEcoule) {
        this.tempsEcoule = tempsEcoule;
    }

    /**
     * @return Le décompte des aides utilisées par type.
     */
    public Map<Integer, Integer> getNbAidesUtilisees() {
        return nbAidesUtilisees;
    }
}