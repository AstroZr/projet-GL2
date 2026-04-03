package Groupe6.save;

/**
 * Conteneur de données pour les préférences d'un joueur.
 * * Responsabilités:
 * - Mémoriser les paramètres personnels du joueur
 * - Être sérialisée/désérialisée en JSON par SaveManager (GSON)
 * - Être chargée au démarrage et mise en cache en RAM
 * * Emmagasine:
 * - Nom/pseudo du joueur
 * - Langue de l'interface ("fr", "en")
 * - Volumes: effets sonores et musique (0-100)
 * - Préférence de thème (0=clair, 1=foncé, 2=catppuccin)
 * * Sauvegarde:
 * - saveGame/001/settings.json (pour joueur 001)
 * - saveGame/002/settings.json (pour joueur 002), etc.
 * * Flux:
 * 1. Connexion: SaveManager.chargerParametres(nomJoueur) → ParametresJoueur
 * 2. Utilisation: Game.parametres charge/affiche ces valeurs
 * 3. Changement: Parametres.java met à jour puis appelle SaveManager.sauvegarder()
 */
public class ParametresJoueur {

    // ====== IDENTIFICATION ======
    /** Nom ou pseudo du joueur. */
    private String nomJoueur;
    
    // ====== LOCALISATION ======
    /** Code de la langue ("fr", "en", etc.). */
    private String language;
    
    // ====== AUDIO ======
    /** Volume des effets sonores (0-100). */
    private int volumeEffet;
    
    /** Volume de la musique (0-100). */
    private int volumeMusique;
    
    // ====== THÈME VISUEL ======
    /** Choix du thème : 0=clair, 1=foncé, 2=catppuccin. */
    private int modeSombre;
    
    // ====== AIDE DE JEU ======
    /** Détermine s'il faut afficher ou masquer les possibilités d'équations au survol. */
    private boolean afficherPossibilites;
    
    /** Détermine s'il faut afficher ou masquer les cases en rouge en cas de doublon. */
    private boolean afficherErreurDouble;

    /**
     * Constructeur par défaut nécessaire pour la désérialisation GSON.
     */
    public ParametresJoueur() {
    }

    /**
     * Constructeur complet pour initialiser les paramètres d'un joueur.
     * * @param nomJoueur     Le nom ou pseudo du joueur.
     * @param language      Le code de la langue.
     * @param volumeEffet   Le volume des effets sonores (0-100).
     * @param volumeMusique Le volume de la musique (0-100).
     * @param modeSombre    L'identifiant du thème visuel.
     */
    public ParametresJoueur(String nomJoueur, String language, int volumeEffet, int volumeMusique, int modeSombre) {
        this.nomJoueur = nomJoueur;
        this.language = language;
        this.volumeEffet = volumeEffet;
        this.volumeMusique = volumeMusique;
        this.modeSombre = modeSombre;
        this.afficherPossibilites = false;  // Par défaut, masquer les possibilités
        this.afficherErreurDouble = false;  // Par défaut, ne pas afficher les doublons
    }

    /**
     * @return Le nom ou pseudo du joueur.
     */
    public String getNomJoueur() {
        return nomJoueur;
    }

    /**
     * @param nomJoueur Le nouveau nom ou pseudo du joueur.
     */
    public void setNomJoueur(String nomJoueur) {
        this.nomJoueur = nomJoueur;
    }

    /**
     * @return Le code de la langue.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language Le nouveau code de la langue.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return Le volume actuel des effets sonores.
     */
    public int getVolumeEffet() {
        return volumeEffet;
    }

    /**
     * @param volumeEffet Le nouveau volume des effets sonores.
     */
    public void setVolumeEffet(int volumeEffet) {
        this.volumeEffet = volumeEffet;
    }

    /**
     * @return Le volume actuel de la musique.
     */
    public int getVolumeMusique() {
        return volumeMusique;
    }

    /**
     * @param volumeMusique Le nouveau volume de la musique.
     */
    public void setVolumeMusique(int volumeMusique) {
        this.volumeMusique = volumeMusique;
    }

    /**
     * @return L'identifiant du thème visuel.
     */
    public int getModeSombre() {
        return modeSombre;
    }

    /**
     * @param modeSombre Le nouvel identifiant du thème visuel.
     */
    public void setModeSombre(int modeSombre) {
        this.modeSombre = modeSombre;
    }

    /**
     * @return true si les possibilités d'équations sont affichées, false sinon.
     */
    public boolean isAfficherPossibilites() {
        return afficherPossibilites;
    }

    /**
     * @param afficherPossibilites true pour afficher les possibilités, false pour les masquer.
     */
    public void setAfficherPossibilites(boolean afficherPossibilites) {
        this.afficherPossibilites = afficherPossibilites;
    }

    /**
     * @return true si les erreurs de doublons s'affichent en rouge, false sinon.
     */
    public boolean isAfficherErreurDouble() {
        return afficherErreurDouble;
    }

    /**
     * @param afficherErreurDouble true pour afficher les erreurs, false pour les masquer.
     */
    public void setAfficherErreurDouble(boolean afficherErreurDouble) {
        this.afficherErreurDouble = afficherErreurDouble;
    }
}