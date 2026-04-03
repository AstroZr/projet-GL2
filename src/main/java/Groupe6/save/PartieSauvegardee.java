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
    
    /** * Gestionnaire d'aides. 
     * Exclu de la sérialisation JSON (transient) car il contient des interfaces et 
     * des logiques non désérialisables directement.
     */
    private transient AideManager aideManager;

    /** Matrice N×N représentant l'état actuel de la grille avec les valeurs saisies par le joueur. */
    private Cellule[][] matriceCellules;
    
    // ====== HISTORIQUE & TEMPS ======
    
    /** Historique des coups joués pour la fonctionnalité Undo/Redo. 
     * Format typique d'un coup : [ligne, colonne, ancienneValeur, nouvelleValeur].
     */
    private List<int[]> historique;

    /** Temps de jeu total écoulé sur cette partie, en millisecondes. */
    private long tempsEcoule;
    
    // ====== AIDES TRACKING ======
    
    /** Comptage du nombre d'utilisations pour chaque type d'aide (Clé: ID Type, Valeur: Quantité). */
    private Map<Integer, Integer> nbAidesUtilisees;

    /**
     * Constructeur par défaut.
     * Indispensable pour la désérialisation via GSON. Initialise les collections à vide
     * et récupère l'instance du gestionnaire d'aides.
     */
    public PartieSauvegardee() {
        this.historique = new ArrayList<>();
        this.nbAidesUtilisees = new HashMap<>();
        this.aideManager = AideManager.getInstance();
    }

    /**
     * Constructeur paramétré utilisé lors de la création d'une nouvelle sauvegarde depuis le jeu.
     * * @param matriceCellules L'état actuel des cellules de la grille.
     * @param historique      La liste des coups joués jusqu'à présent.
     * @param tempsEcoule     Le temps total passé sur cette grille en millisecondes.
     */
    public PartieSauvegardee(Cellule[][] matriceCellules, List<int[]> historique, long tempsEcoule) {
        this.matriceCellules = matriceCellules;
        this.historique = historique;
        this.tempsEcoule = tempsEcoule;
        this.aideManager = AideManager.getInstance();
        this.nbAidesUtilisees = this.aideManager.getNBUtilisations();
    }

    /**
     * Retourne la matrice des cellules sauvegardée.
     * * @return Un tableau 2D de {@link Cellule} représentant la grille.
     */
    public Cellule[][] getMatriceCellules() {
        return matriceCellules;
    }

    /**
     * Définit la matrice des cellules.
     * * @param matriceCellules Le nouveau tableau 2D de {@link Cellule}.
     */
    public void setMatriceCellules(Cellule[][] matriceCellules) {
        this.matriceCellules = matriceCellules;
    }

    /**
     * Retourne l'historique des coups joués.
     * * @return Une liste de tableaux d'entiers représentant les actions du joueur.
     */
    public List<int[]> getHistorique() {
        return historique;
    }

    /**
     * Définit l'historique des coups joués.
     * * @param historique La nouvelle liste des coups.
     */
    public void setHistorique(List<int[]> historique) {
        this.historique = historique;
    }

    /**
     * Retourne le temps écoulé sur la partie.
     * * @return Le temps de jeu en millisecondes.
     */
    public long getTempsEcoule() {
        return tempsEcoule;
    }

    /**
     * Définit le temps écoulé sur la partie.
     * * @param tempsEcoule Le temps de jeu en millisecondes.
     */
    public void setTempsEcoule(long tempsEcoule) {
        this.tempsEcoule = tempsEcoule;
    }

    /**
     * Retourne les statistiques d'utilisation des aides de jeu.
     * * @return Une Map associant l'identifiant d'un type d'aide à son nombre d'utilisations.
     */
    public Map<Integer, Integer> getNbAidesUtilisees() {
        return nbAidesUtilisees;
    }
}