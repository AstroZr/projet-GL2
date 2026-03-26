package Groupe6.save;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import Groupe6.aide.AideManager;
import Groupe6.models.Cellule;

/**
 * Conteneur de données pour une partie sauvegardée.
 * 
 * Respons abilités:
 * - Mémoriser l'état complet d'une partie en cours (grille, progrédition)
 * - Êatre sérialisée/désérialisée en JSON par SaveManager (GSON)
 * - Support du checkpoint/restore: rechargement d'une partie antérieure
 * 
 * Sauvegardes par niveau:
 * - saveGame/001/facile1.json
 * - saveGame/001/facile2.json
 * - saveGame/002/moyen1.json, etc.
 * 
 * Structure:
 * - matriceCellules: grille N×N avec valeurs actuelles de chaque cellule
 * - historique: list de coups joués pour undo/redo
 * - tempsEcoule: temps total en ms depuis début
 * - nbAidesUtilisees: comptage des hints par type
 * 
 * GSON Serialization:
 * - Pas de constructeur par défaut (présent pour GSON)
 * - Champs publics ou getters/setters pour accés GSON
 */
public class PartieSauvegardee {
    // ====== ÉTAT DE LA GRILLE ======
    private transient AideManager aideManager;       // Exclu JSON: contient des interfaces non désérialisables par défaut
    private Cellule[][] matriceCellules;  // Grille N×N avec valeurs actuelles
    
    // ====== HISTORIQUE & TEMPS ======
    private List<int[]> historique;        // Enregistrement de tous les coups [ligne, col, ancVal, nouvelleVal, actionType]
    private long tempsEcoule;              // Temps total écoulé en millisecondes
    
    // ====== AIDS TRACKING ======
    private Map<Integer, Integer> nbAidesUtilisees;  // Comptage des hints utilisés par type

    public PartieSauvegardee() {
        this.historique = new ArrayList<>();
        this.nbAidesUtilisees = new HashMap<>();
        this.aideManager = AideManager.getInstance();
    }

    public PartieSauvegardee(Cellule[][] matriceCellules, List<int[]> historique, long tempsEcoule) {
        this.matriceCellules = matriceCellules;
        this.historique = historique;
        this.tempsEcoule = tempsEcoule;
        this.aideManager = AideManager.getInstance();
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